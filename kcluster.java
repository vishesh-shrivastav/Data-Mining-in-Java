
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

public class kcluster {

	static int [] seeds = {1,3,5,7,9,11,13,15,17,19,21,23,25,27,29,31,33,35,37,39};
	static int trials;
	static int number_of_clusters;
	//static String filename = "/home/shriv070/K_means/freq.csv";
	static String filename = "freq.csv";
	static String classfile;
	public static int[][] c_matrix;
	static HashMap<Integer, ArrayList<Integer>> new_clusters = new HashMap<Integer, ArrayList<Integer>>();
	static HashMap<Integer, HashMap<Integer, Double>> data = new HashMap<Integer, HashMap<Integer, Double>>();
	static ArrayList<String> unique_topics = new ArrayList<String>();

	//String filename = "C:\\Users\\Shrivatav\\Desktop\\UMN\\Homework\\Introduction to Data Mining\\CHW2\\freq.csv";

	public static void main(String[] args) throws Exception {
		int t[][] = new int[2][3];
		//System.out.println(t.length);
		String inputfile = args[0];
		String cf = args[1];
		classfile = args[2];

		number_of_clusters = Integer.parseInt(args[3]);
		trials = Integer.parseInt(args[4]);
		String outputfile = args[3];
		double val = 0.0, sse= 0.0, i2 = 0.0;

		for(int i = 0; i < trials; i++){
			if(cf.equals("SSE")){
				HashMap<Integer, ArrayList<Integer>> clusters = getClusters(inputfile, seeds[i]);
				sse = reassign(clusters);
				if(val == 0.0){
					val = sse;
				}
				else if (val>sse) {
					val = sse;
				}
			}
			else if(cf.equals("I2")){
				HashMap<Integer, ArrayList<Integer>> clusters = getClusters(inputfile, seeds[i]);
				val = reassignCosine(clusters);
				if(val<i2)
					val = i2;
			}


		}
		System.out.println("Value of Objective Criterion Function:"+ val);
		System.out.println("Value of Objective Confusion Matrix:"+ c_matrix);

		FileWriter fw= null;
        fw = new FileWriter(new File(outputfile));

        for (int k : new_clusters.keySet()){
        	for(int i : new_clusters.get(k)){
        		fw.write(i + "," + k);
                fw.write("\n");
        	}
        }
        fw.close();

        fw = new FileWriter(new File("matrix"));
        fw.write("ClusterNo.");
        Iterator<String> iter = unique_topics.iterator();
        while(iter.hasNext()){
            fw.write(", "+iter.next());
        }
        fw.write("\n");
        for(int j = 0; j<c_matrix.length;j++){
            fw.write(j+", ");
            for(int k = 0; k < unique_topics.size(); k++){
            	fw.write(", "+c_matrix[j][k]);
            }
            fw.write("\n");
        }
        fw.close();

		//HashMap<Integer, ArrayList<Integer>> clusters = getClusters(filename);
		//reassign(clusters);
		//reassignCosine(clusters);
	}

	//Function to compute Euclidean distance between two points
	public static double getDistance(HashMap<Integer, Double> point1, HashMap<Integer, Double> point2){

		List<Integer> union_keys = new ArrayList<Integer>(point1.keySet());
		List<Integer> union_keys_1 = new ArrayList<Integer>(point2.keySet());

		for(int bar : union_keys_1){
			if (! union_keys.contains(bar)){
				union_keys.add(bar);
			}
		}

		double sum = 0.0;
		for (int item : union_keys){
			if(! point1.keySet().contains(item)){
				Double b = point2.get(item);
				sum += b * b;
			}
			else if(! point2.keySet().contains(item)){
				Double a = point1.get(item);
				sum += a * a;
			}
			else{
				Double a = point1.get(item);
				Double b = point2.get(item);
				sum += (a - b) * (a - b);
			}
		}
		double dist = Math.pow(sum,0.5);
		return dist;
	}

	// Function to compute Euclidean SSE for a clustering
	public static double getSSE(HashMap<Integer, ArrayList<Integer>> clust, HashMap<Integer, HashMap<Integer, Double>> centr){

		double net_sse = 0.0;

		for(int i = 0; i < number_of_clusters; i++){
			double cluster_sse = 0.0;
			double bb = 0.0;

			if(clust.get(i) != null){
				ArrayList<Integer> points = new ArrayList<Integer>(clust.get(i));
				for (int xx : points){
					cluster_sse += getDistance(data.get(xx), centr.get(i));
				}}

			net_sse += cluster_sse;
		}
		return net_sse;

	}

	// Function to compute Euclidean centroid for a cluster
		public static HashMap<Integer, HashMap<Integer, Double>> getCentroid(HashMap<Integer, ArrayList<Integer>> clus){
			HashMap<Integer, HashMap<Integer, Double>> centroid = new HashMap<Integer, HashMap<Integer, Double>>();

			//Find union of keys, stored as key_list
			for (int k : clus.keySet()){
				List<Integer> key_list = new ArrayList<Integer>();
				for (int i : clus.get(k)){
					if (! key_list.contains(i)) {
						key_list.add(i);
					}
				}

				HashMap<Integer, Double> v_pair = new HashMap<Integer, Double>();

				for (int k1 : key_list){
					double sum = 0.0;
					for (int m : data.keySet()){
						if (data.get(m).containsKey(k1)){
							sum += data.get(m).get(k1);
						}
					}
				double cent = sum/data.size();
				v_pair.put(k1, cent);
				}

				centroid.put(k, v_pair);

			}
			return centroid;

		}


	//Function to compute cosine distance between two points
		public static double getCosineDistance(HashMap<Integer, Double> point1, HashMap<Integer, Double> point2){

			double num = 0.0;
			double denom = 0.0;
			for (int item : point2.keySet()){
				if( point1.keySet().contains(item)){
					num += ((point1.get(item) * point2.get(item)));
			}
				denom += point2.get(item) * point2.get(item);

		}
			double dist = num/(Math.pow(denom,0.5));
			return dist;

		}

		// Function to compute cosine I2 for a clustering
		public static double getI2(HashMap<Integer, ArrayList<Integer>> clust, HashMap<Integer, HashMap<Integer, Double>> centr){

			double net_i2 = 0.0;
			double cluster_i2_new = 0.0;

			for (int i : centr.keySet()){
				double cluster_i2 = 0.0;
				HashMap<Integer, Double> points = centr.get(i);
				for (int l : points.keySet()){
					double ee = points.get(l);
					cluster_i2 +=  ee * ee;
				}
				cluster_i2_new = Math.pow(cluster_i2,0.5);

				net_i2 += cluster_i2_new;
			}

			return net_i2;

		}

		// Function to compute Cosine centroid for a cluster
		public static HashMap<Integer, HashMap<Integer, Double>> getCosineCentroid(HashMap<Integer, ArrayList<Integer>> clus){
			HashMap<Integer, HashMap<Integer, Double>> centroid = new HashMap<Integer, HashMap<Integer, Double>>();

			//Find union of keys, stored as key_list
			for (int k : clus.keySet()){
				List<Integer> key_list = new ArrayList<Integer>();
				for (int i : clus.get(k)){
					if (! key_list.contains(i)) {
						key_list.add(i);
					}
				}

				HashMap<Integer, Double> v_pair = new HashMap<Integer, Double>();

				for (int k1 : key_list){
					double sum = 0.0;
					for (int m : data.keySet()){
						if (data.get(m).containsKey(k1)){
							sum += data.get(m).get(k1);
						}
					}
				double cent = sum;
				v_pair.put(k1, cent);
				}

				centroid.put(k, v_pair);

			}
			return centroid;

		}


	// Function to get the data and assign initial cluster
	public static HashMap<Integer, ArrayList<Integer>> getClusters (String file_name, int seed) throws FileNotFoundException,IOException{
		String line;
		Random rnd_gen = new Random(seed);

		FileReader fileReader = new FileReader(file_name);
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		while ((line = bufferedReader.readLine()) != null){
			String[] parts = line.split(",",3);
			int article_id = Integer.parseInt(parts[0]);
			int dim = Integer.parseInt(parts[1]);
			Double val = Double.parseDouble(parts[2]);
			if (data.containsKey(article_id)) {
                data.get(article_id).put(dim,val);
            }
            else {
                data.put(article_id, new HashMap<Integer, Double>());
                data.get(article_id).put(dim, val);
            }
			}

		// Initialisation

			//Dictionary to store key and the clusters they are assigned to
			HashMap<Integer, ArrayList<Integer>> clusters = new HashMap<Integer, ArrayList<Integer>>();

			//Dictionary to store cluster - point pair
			//Assign every point to a random cluster
			for (int key1 : data.keySet()) {
				int rnd_val = rnd_gen.nextInt(number_of_clusters);
				if(clusters.containsKey(rnd_val)){
					clusters.get(rnd_val).add(key1);
				}
				else{
					clusters.put(rnd_val, new ArrayList<Integer>());
					clusters.get(rnd_val).add(key1);
				}
			}
		return clusters;
		}

	static int count = 0;

	//Function to re-assign clusters
	public static double reassign(HashMap<Integer, ArrayList<Integer>> c) throws Exception{
	count+= 1;
	System.out.println(count);
	boolean hasConverged = false;
	List<Double> EntPur = new ArrayList<Double>();
	//Calculate the centroid of each cluster

	//Centroids stored as a HashMap, where key is the cluster id and value is a HashMap
	//which contains the dimension-average value pair

	HashMap<Integer, HashMap<Integer, Double>> centroid = getCentroid(c);
	//Function for Euclidean distance

	//int counter = 0;
	//Calculate distances from each point to all the centroids
	for (int yy: c.keySet()){
		for (int dd : data.keySet()){
			double dist = getDistance(data.get(dd), centroid.get(yy));
		}
	}

	for (int dd: data.keySet()){
		// distances is a HashMap that contains distance of key from each centroid
		//HashMap<Integer, Double> distances = new HashMap<Integer, Double>();
		//HashMap<Integer, Double> data_values = data.get(dd);
		double min_dist = 10000.0;
		int new_cluster = -1;
		for (int ck : centroid.keySet()){

			double dist = getDistance(data.get(dd), centroid.get(ck));

			if (dist < min_dist){
				min_dist = dist;
				new_cluster = ck;
			}

		}

		if (new_clusters.containsKey(new_cluster)){
			new_clusters.get(new_cluster).add(dd);
		}
		else{
			new_clusters.put(new_cluster, new ArrayList<Integer>());
			new_clusters.get(new_cluster).add(dd);
		}
	}

	HashMap<Integer, HashMap<Integer, Double>> n_centroid = getCentroid(new_clusters);

	double old_sse = getSSE(c,centroid);
	double new_sse = getSSE(new_clusters,n_centroid);

	//System.out.println("old clusters=" + c);
	//System.out.println("old sse=" + old_sse);
	//System.out.println("updated clusters=" + new_clusters);
	//System.out.println("new sse=" + new_sse);

	if (new_sse >= old_sse * (0.999)){
		hasConverged = true;
	}

	//hasConverged = mapsAreEqual(c, new_clusters);
	System.out.println(hasConverged);
	if(hasConverged){
		EntPur = getCM(new_clusters);
		//System.out.println("Enropy = " + EntPur.get(0) + " , " + "Purity = " + " , " + EntPur.get(1));
		return new_sse;
	}
	else{
		reassign(new_clusters);
		//System.out.println(count);
		//count += 1;

	}
	return new_sse;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Function to re-assign clusters
		public static double reassignCosine(HashMap<Integer, ArrayList<Integer>> c) throws Exception{
		count+= 1;
		System.out.println(count);
		boolean hasConverged = false;
		List<Double> EntPur = new ArrayList<Double>();
		//Calculate the centroid of each cluster
		//Centroids stored as a HashMap, where key is the cluster id and value is a HashMap
		//which contains the dimension-average value pair

		HashMap<Integer, HashMap<Integer, Double>> centroid = getCosineCentroid(c);

		//int counter = 0;
		//Calculate distances from each point to all the centroids
		for (int yy: c.keySet()){
			for (int dd : data.keySet()){
				double dist = getCosineDistance(data.get(dd), centroid.get(yy));
			}
		}

		for (int dd: data.keySet()){
			// distances is a HashMap that contains distance of key from each centroid
			//HashMap<Integer, Double> distances = new HashMap<Integer, Double>();
			//HashMap<Integer, Double> data_values = data.get(dd);
			double min_dist = 10000.0;
			int new_cluster = -1;
			for (int ck : centroid.keySet()){

				double dist = getCosineDistance(data.get(dd), centroid.get(ck));

				if (dist < min_dist){
					min_dist = dist;
					new_cluster = ck;
				}

			}

			if (new_clusters.containsKey(new_cluster)){
				new_clusters.get(new_cluster).add(dd);
			}
			else{
				new_clusters.put(new_cluster, new ArrayList<Integer>());
				new_clusters.get(new_cluster).add(dd);
			}
		}

		HashMap<Integer, HashMap<Integer, Double>> n_centroid = getCosineCentroid(new_clusters);

		double old_i2 = getI2(c,centroid);
		double new_i2 = getI2(new_clusters,n_centroid);

		//System.out.println("old clusters=" + c);
		//System.out.println("old i2=" + old_i2);
		//System.out.println("updated clusters=" + new_clusters);
		//System.out.println("new i2=" + new_i2);

		if (new_i2 >= old_i2 * (0.999)){
			hasConverged = true;
		}

		//hasConverged = mapsAreEqual(c, new_clusters);
		System.out.println(hasConverged);
		if(hasConverged){
			//System.out.println(count);
			EntPur = getCM(new_clusters);
			System.out.println("Entropy = " + EntPur.get(0) + " , " + "Purity = "  + EntPur.get(1));
			return new_i2;
		}
		else{
			reassignCosine(new_clusters);
			//System.out.println(count);
			//count += 1;

		}
		return new_i2;
		}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	// Entropy and Purity
	public static List<Double> getCM(HashMap<Integer, ArrayList<Integer>> new_clusters) throws NumberFormatException, IOException{

		//String c_file = /home/shriv070/Downloads/reuters21578.class;
		FileReader fileReader = new FileReader(classfile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String l;
		List<Double> e_and_p = new ArrayList<Double>();


		HashMap<Integer, String> articles_topics= new HashMap<Integer, String>();

		while ((l = bufferedReader.readLine()) != null){
			String[] parts = l.split(",",2);
			int a_id = Integer.parseInt(parts[0]);
			String topic = parts[1];
			articles_topics.put(a_id, topic);
			}

		//Get unique topics
		//ArrayList<String> unique_topics = new ArrayList<String>();

		for( int it : articles_topics.keySet()){
			if (! unique_topics.contains(articles_topics.get(it))) {
				unique_topics.add(articles_topics.get(it));
		}
		}

		c_matrix  = new int[number_of_clusters][unique_topics.size()];

		for(int i = 0 ; i < number_of_clusters; i++){
			if (new_clusters.get(i) != null){
			for(int j : new_clusters.get(i)){
				for ( int k = 0; k < unique_topics.size(); k ++)
				if ((articles_topics.get(j)!=null) && articles_topics.get(j).equals(unique_topics.get(k))){
					c_matrix[i][k] ++;
				}
			}}
		}

		double total = 0.0;
		double te = 0.0;
		for (int i = 0; i < number_of_clusters; i ++){
			int row_max = 0;
			double re = 0.0;
			for (int j = 0 ; j < 20; j ++){
				if(c_matrix[i][j] > row_max){
					row_max = c_matrix[i][j];
					double pi = (double)c_matrix[i][j];
					double pij = (double)new_clusters.get(i).size();
					if(pi != 0){
					re = re + (pi/pij) * Math.log(pi/pij)/Math.log(2);
				}
				}
			total += row_max;
			te += re;
			}
		}
		double purity = total/articles_topics.size();

		e_and_p.add(-te);
		e_and_p.add(purity);

		return e_and_p;


	}

	}
