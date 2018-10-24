import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
public class knn {

	static HashMap<Integer, Integer> id_topic_of_used_data = new HashMap<Integer, Integer>();
	static HashMap<Integer, Integer> id_topic_of_target_data = new HashMap<Integer, Integer>();

	public static void main (String[] args) throws FileNotFoundException, IOException {

		//String train = "//home//shriv070//Downloads//Classification//project_3//rep3//mnist_train.csv";
		//String test = "//home//shriv070//Downloads//Classification//project_3//rep3//mnist_validation.csv";

		//String classifier_name = "knn";
		String train = args[0];
		String validation = args[1];
		String test = args[2];
		String out_file = args[3];
		/*String train = "mnist_train.csv";
		String test = "mnist_test.csv";
		String validation = "mnist_validation.csv";
		String out_file = "output_knn.txt";*/

		HashMap<Integer, List<Float>> used_data = getData(train);
		HashMap<Integer, List<Float>> target_data = getData(validation);

		id_topic_of_used_data = get_topics(train);
		id_topic_of_target_data = get_topics(validation);

		List<Float> accuracy_list = new ArrayList<Float>();

		for (int h = 1; h <= 20; h++) {
			HashMap<Integer, Integer> new_classes = new HashMap<Integer, Integer>();
			new_classes	= getClass(used_data, target_data, h);
			//System.out.println("old classes: " + id_topic_of_test_data);
			//System.out.println("new classes: " + new_classes);
			float acc = getAccuracy(new_classes,id_topic_of_target_data);
			accuracy_list.add(acc);
			//System.out.println("Accuaracy for " + h + ": "  + acc);
		}

		//Find index of maximum accuracy (Best k)
		int best_k = 0;
		float max_accu = 0;
		for (int i = 0; i < accuracy_list.size(); i++) {
			if (accuracy_list.get(i) > max_accu) {
				max_accu = accuracy_list.get(i);
				best_k = i+1; //index + 1
			}
		}

		//Now we have the best value for k. We have to combine the train and validation data and use it
		//to predict classes for the train data

		//We empty our topic HashMaps and then reassign them
		id_topic_of_used_data.clear();
		id_topic_of_target_data.clear();

		id_topic_of_used_data = get_topics_combined(train, validation);
		id_topic_of_target_data = get_topics(test);

		used_data = getData_combined(train, validation);
		target_data = getData(test);

		HashMap<Integer, Integer> predicted_classes = getClass(used_data, target_data, best_k);

		// Write predicted classes to output file
		// Assume default encoding.
        FileWriter fileWriter = new FileWriter(out_file);

        // Always wrap FileWriter in BufferedWriter.
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		for(int i = 0; i < predicted_classes.size(); i++) {
			bufferedWriter.write(Integer.toString(predicted_classes.get(i)));
			bufferedWriter.write("\n");
		}

		bufferedWriter.close();

		System.out.println("ACCURACY: " + getAccuracy(predicted_classes, id_topic_of_target_data));
	}

	// Function to get class labels(topics) from the file
	public static HashMap<Integer, Integer> get_topics(String file) throws NumberFormatException, IOException {
		String line;
		HashMap<Integer, Integer> id_topic = new HashMap<Integer, Integer>();
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		int line_count = 0;
		while ((line = bufferedReader.readLine()) != null){
			String[] parts = line.split(",", 2);
			int class_label = Integer.parseInt(parts[0]);
			//System.out.println(line_count);
			id_topic.put(line_count, class_label);

			line_count += 1;
		}
		bufferedReader.close();
		return id_topic;

	}

	//Get topics from two files combined
		public static HashMap<Integer, Integer> get_topics_combined(String file1, String file2) throws NumberFormatException, IOException {
			String line;
			HashMap<Integer, Integer> id_topic = new HashMap<Integer, Integer>();
			FileReader fileReader1 = new FileReader(file1);
			BufferedReader bufferedReader1 = new BufferedReader(fileReader1);
			int line_count = 0;
			while ((line = bufferedReader1.readLine()) != null){
				String[] parts = line.split(",", 2);
				int class_label = Integer.parseInt(parts[0]);
				//System.out.println(line_count);
				id_topic.put(line_count, class_label);

				line_count += 1;
			}
			bufferedReader1.close();

			FileReader fileReader2 = new FileReader(file2);
			BufferedReader bufferedReader2 = new BufferedReader(fileReader2);
			while ((line = bufferedReader2.readLine()) != null){
				String[] parts = line.split(",", 2);
				int class_label = Integer.parseInt(parts[0]);
				//System.out.println(line_count);
				id_topic.put(line_count, class_label);

				line_count += 1;
			}
			bufferedReader2.close();
			return id_topic;
		}

		// Function to get Data from two files combined
		public static HashMap<Integer, List<Float>> getData_combined (String file1, String file2) throws FileNotFoundException,IOException{
				String line;
				HashMap<Integer, List<Float>> data = new HashMap<Integer, List<Float>>();
				//HashMap<Integer, Integer> id_topic = new HashMap<Integer, Integer>();
				FileReader fileReader1 = new FileReader(file1);
				BufferedReader bufferedReader1 = new BufferedReader(fileReader1);

				int line_count = 0;

				// Read data and store it as a HashMap
				while ((line = bufferedReader1.readLine()) != null){
					String[] parts = line.split(",", 2);
					String vect = parts[1];
					List<Float> vect_ = new ArrayList<Float>();
					String[] vect1 = vect.split(",");
					for (int i = 0; i < vect1.length; i++) {
						vect_.add(Float.parseFloat(vect1[i]));
					}
					data.put(line_count, vect_);
					line_count += 1;
				}
				bufferedReader1.close();

				FileReader fileReader2 = new FileReader(file2);
				BufferedReader bufferedReader2 = new BufferedReader(fileReader2);
				// Read data and store it as a HashMap
				while ((line = bufferedReader2.readLine()) != null){
					String[] parts = line.split(",", 2);
					String vect = parts[1];
					List<Float> vect_ = new ArrayList<Float>();
					String[] vect1 = vect.split(",");
					for (int i = 0; i < vect1.length; i++) {
						vect_.add(Float.parseFloat(vect1[i]));
					}
					data.put(line_count, vect_);
					line_count += 1;
				}
				bufferedReader2.close();
				return data;

			}

	// Function to get Data from the file
	public static HashMap<Integer, List<Float>> getData (String file) throws FileNotFoundException,IOException{
		String line;
		HashMap<Integer, List<Float>> data = new HashMap<Integer, List<Float>>();
		//HashMap<Integer, Integer> id_topic = new HashMap<Integer, Integer>();
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		int line_count = 0;

		// Read data and store it as a HashMap
		while ((line = bufferedReader.readLine()) != null){
			String[] parts = line.split(",", 2);
			String vect = parts[1];
			List<Float> vect_ = new ArrayList<Float>();
			String[] vect1 = vect.split(",");
			for (int i = 0; i < vect1.length; i++) {
				vect_.add(Float.parseFloat(vect1[i]));
			}
			data.put(line_count, vect_);
			line_count += 1;
		}
		bufferedReader.close();
		return data;

	}

	// Function to sort a HashMap by value
	public static Map sortByValue(Map unsortedMap) {
		Map sortedMap = new TreeMap(new ValueComparator(unsortedMap));
		sortedMap.putAll(unsortedMap);
		return sortedMap;
	}

	// Function to return first n elements of a TreeMap
	public static SortedMap<Integer, Float> putFirstEntries(int max, SortedMap<Integer, Float> source) {
		int count = 0;
		TreeMap<Integer, Float> target = new TreeMap<Integer, Float>();
		for (Map.Entry<Integer, Float> entry:source.entrySet()) {
			if (count >= max) break;

			target.put(entry.getKey(), entry.getValue());
			count++;
		}
		return target;
	}

	//Function to find class labels using KNN approach
	public static HashMap<Integer, Integer> getClass(HashMap<Integer, List<Float>> data_train, HashMap<Integer, List<Float>> data_test, Integer kn) {

		float dist = 0;
		HashMap<Integer, Integer> assigned_classes = new HashMap<Integer, Integer>();

		for (int k : data_test.keySet()) {

			HashMap<Integer, Float> distances = new HashMap<Integer, Float>(); // has the distance from cosine similarity

			for (int kk : data_train.keySet()) {
				dist = getCosine(data_test.get(k), data_train.get(kk));
				distances.put(kk, dist);
			}

			//System.out.println("the new distance dictionary is : "+distances);
			/*List<Float> distance_list = new ArrayList<Float>();
			List<Float> top_k_distances = new ArrayList<Float>();

			//Create List of distances
			for (int r : distances.keySet()) {
				distance_list.add(distances.get(r));
			}

			//Sort this list in descending order
			Collections.sort(distance_list,Collections.reverseOrder());

			//Find top-k
			top_k_distances = distance_list.subList(0, kn);*/

			//System.out.println("Top K distance list  : "+top_k_distances);

			//HashMap<Integer,Float> class_count = new HashMap<Integer,Float>();
			// get the key for the sorted distance
			/*for(int i = 0; i < top_k_distances.size(); i ++) {
				for (int j : distances.keySet()) {
					if ( top_k_distances.get(i) == distances.get(j)) {
						int cl = id_topic_of_train_data.get(j); // get class for the distance key
						//System.out.println("fOR ELEMENT IN distance list "+i+"___point is "+j+"..class being "+cl);
						if (class_count.containsKey(cl)) {
							class_count.put(cl,class_count.get(cl) + 1);
						}
						else {
							class_count.put(cl, (float) 1);
						}
					}
				}
			}*/
			//System.out.println("cc: " + class_count);

			//Sort the distances by value
			SortedMap<Integer, Float> sortedDistances = (SortedMap<Integer, Float>) sortByValue(distances);
			TreeMap<Integer, Float> top_k_= (TreeMap<Integer, Float>) putFirstEntries(kn,sortedDistances);

			// Highest occuring class in Top k
			//HashMap<Integer,Float> class_count = new HashMap<Integer,Float>();

			//HashMap to store weighted distances. Every index of this HashMap corresponds to a class label
			HashMap<Integer, Float> weighted = new HashMap<Integer, Float>();

			for (int i : top_k_.keySet()) {
				int xx = id_topic_of_used_data.get(i);
				float wd = getCosine(data_train.get(i),data_test.get(k));
				float weighted_distance = (1 / (1 - wd) * (1 - wd));
				if (weighted.containsKey(xx)) {
					weighted.put(xx,weighted.get(xx) + weighted_distance);
				}
				else {
					weighted.put(xx, weighted_distance);
				}
			}

			int maxKey = 0;
			float maxVal = 0;

			for ( int c : weighted.keySet()) {
				if (weighted.get(c) > maxVal) {
					maxKey = c;
					maxVal = weighted.get(c);
				}
			}


			/*Map.Entry<Integer, Float> maxEntry = null;
			for (Entry<Integer, Float> entry : class_count.entrySet())
			{
			    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
			    {
			        maxEntry = entry;
			    }
			}

			int maxIndex = maxEntry.getKey();*/
			//System.out.println("maxIndex: " + maxIndex);
			//System.out.println("class of maxIndex: " + id_topic_of_train_data.get(maxIndex));
			
			//The class to be assigned will be the index of the maximum distance
			assigned_classes.put(k, maxKey);
			//System.out.println("Assigned classes "+ assigned_classes);
		}
		return assigned_classes;
	}

	//Function to compute cosine between two points which are lists of integers
	public static float getCosine(List<Float> point1, List<Float> point2) {

		//Sum of squares
		int ss1 = 0;
		int ss2 = 0;

		for (int i=0; i < point1.size(); i++) {
			if(point1.get(i) != 0) {
			ss1 += Math.pow(point1.get(i), 2);
		}
		}

		for (int i=0; i < point2.size(); i++) {
			if (point2.get(i) != 0) {
			ss2 += (Math.pow(point2.get(i), 2));
		}
		}
		//Denominator = product of magnitudes
		float mag1 = (float) Math.sqrt(ss1);
		float mag2 = (float) Math.sqrt(ss2);
		float denom = mag1 * mag2;

		float num = 0;

		// Numerator = dot product
		for (int i = 0; i < point1.size(); i++) {
			if ( point1.get(i) != 0 && point2.get(i) != 0){{
			num += point1.get(i) * point2.get(i);
		}
		}
}
		float cosine = (float) (num/denom);
		return cosine;
	}

//Function to find accuracy between two class label HashMaps
public static float getAccuracy(HashMap<Integer, Integer> h1, HashMap<Integer, Integer> h2){

	int counter = 0;

	for (int m : h1.keySet()){
		if (h1.get(m) == h2.get(m)){
			counter += 1;
		}
	}

	float size = h1.size();
	float accuracy = (float)counter/size;

	return accuracy;

}
}

class ValueComparator implements Comparator {
	Map map;

	public ValueComparator(Map map) {
		this.map = map;
	}

	public int compare(Object keyA, Object keyB) {
		Comparable valueA = (Comparable) map.get(keyA);
		Comparable valueB = (Comparable) map.get(keyB);
		return valueB.compareTo(valueA);
	}
}
