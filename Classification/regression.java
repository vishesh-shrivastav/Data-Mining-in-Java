import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class regression {	
	
	static List<Float> w_vector = new ArrayList<Float>();
	static HashMap<Integer, Integer> id_topic_of_target_data = new HashMap<Integer, Integer>();
	static HashMap<Integer, Integer> id_topic_of_predictor_data = new HashMap<Integer, Integer>();
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		/*
		String validation = "//home//shriv070//classification//mnist_validation.csv";
		String train = "//home//shriv070//classification//mnist_train.csv";
		String test = "//home//shriv070//classification//mnist_test.csv";
		String out_file = "//home//shriv070//classification//output_ridge.csv";
		String weights = "//home//shriv070//classification//weights.csv";
		*/
		
		String train = args[0];
		String validation = args[1];
		String test = args[2];
		String out_file = args[3];
		String weights = args[4];
		
		
		HashMap<Integer, List<Float>> validation_data = getData(validation);
		HashMap<Integer, List<Float>> test_data = getData(test);
		HashMap<Integer, List<Float>> data_combined = getData_combined(train, validation);
		HashMap<Integer, Integer> id_topic_of_train_data = new HashMap<Integer, Integer>();
		id_topic_of_train_data = get_topics(validation);
		
		//Normalise the data before doing Ridge Regression
		HashMap<Integer, List<Float>> normalised_validation_data = normaliseData(validation_data);
		HashMap<Integer, List<Float>> normalised_combined_data = normaliseData(data_combined);
		HashMap<Integer, List<Float>> normalised_test_data = normaliseData(test_data);
		
		List<Float> lambda_list = new ArrayList<Float>();
		lambda_list.add((float)100);
		lambda_list.add((float)500);
		lambda_list.add((float)1000);
		
		id_topic_of_target_data = get_topics(validation);
		
		//Initialise weight vector as a zero vector
		for(int i = 0; i < validation_data.get(i).size(); i++) {
			w_vector.add((float) 0);
		}
		
		//Create y vectors for all 10 classes
		List<List<Integer>> list_of_ys = new ArrayList<List<Integer>>();
		
		list_of_ys = get_y_models(validation_data, id_topic_of_train_data);
		
		//HashMap to store the newly assigned class of each data point
		float max_accu = (float) 0;
		float max_lambda = (float) 0;
		for (int i = 0; i < lambda_list.size(); i ++) {
			
		HashMap<Integer, Integer> new_classes_assigned = doRR(normalised_validation_data, normalised_validation_data, list_of_ys, lambda_list.get(i));
		float accu = getAccuracy(new_classes_assigned, id_topic_of_target_data);
		System.out.println("Accuracy for lambda " + lambda_list.get(i) + " : " + accu);
		
		if(accu > max_accu) {
			max_accu = accu;
			max_lambda = lambda_list.get(i);
		}
		}
		
		System.out.println("Highest accuracy: " + max_accu + ", best lambda: " + max_lambda);
		
		id_topic_of_target_data.clear();
		id_topic_of_target_data = get_topics(test);
		
		id_topic_of_predictor_data = get_topics_combined(train, validation);
		
		//System.out.println(data_combined.size());
		
		list_of_ys.clear();
		
		//Repopulate list_of_ys
		list_of_ys = get_y_models(data_combined, id_topic_of_predictor_data);
		
		//For both lambda and 2 * lambda
		
		HashMap<Integer, Integer> n_c_a = doRR(normalised_combined_data, normalised_test_data, list_of_ys, max_lambda);
		float accu_lambda = getAccuracy(n_c_a, id_topic_of_target_data);
		System.out.println("Accuracy on combined for lambda: " + accu_lambda);
		HashMap<Integer, Integer> n_c_a1 = doRR(normalised_combined_data, normalised_test_data, list_of_ys, 2*max_lambda);
		float accu_2lambda = getAccuracy(n_c_a1, id_topic_of_target_data);
		System.out.println("Accuracy on combined for twice lambda: " + accu_2lambda);
		
		// Write predicted classes to output file
		// Assume default encoding.
        FileWriter fileWriter = new FileWriter(out_file);

        // Always wrap FileWriter in BufferedWriter.
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		
		float final_lambda = 0;
		float max_accuracy = 0;
		if(accu_lambda >= accu_2lambda) {
			final_lambda = max_lambda;
			max_accuracy = accu_lambda;
			for(int i = 0; i < n_c_a.size(); i++) {
				bufferedWriter.write(Integer.toString(n_c_a.get(i)));
				bufferedWriter.write("\n");
		}
		}
		else {
			max_accuracy = accu_2lambda;
			final_lambda = 2*max_lambda;
			for(int i = 0; i < n_c_a1.size(); i++) {
				bufferedWriter.write(Integer.toString(n_c_a1.get(i)));
				bufferedWriter.write("\n");
		}
		}
		bufferedWriter.close();
		
		List<List<Float>> bw = get_best_weights(normalised_combined_data, list_of_ys, final_lambda);
		
		//Write best weights to weights file
		FileWriter fw = new FileWriter(weights);
        // Always wrap FileWriter in BufferedWriter.
        BufferedWriter bwriter = new BufferedWriter(fw);
        
        for(int i = 0; i < bw.size(); i++) {
        	for(int j = 0; j < normalised_combined_data.get(0).size(); j++){
        	bwriter.write(Float.toString(bw.get(i).get(j)) + ",");
        	}
        	bwriter.write("\n");
        }
        bwriter.close();
		System.out.println("ACCURACY: " + max_accuracy);
	}
	
	//Function to populate y-models for given data
	public static List<List<Integer>> get_y_models(HashMap<Integer, List<Float>> d, HashMap<Integer, Integer> c){
		List<List<Integer>> list_of_y_models = new ArrayList<List<Integer>>();
		
		for (int i = 0; i < 10; i ++) {
			List<Integer> y_ = new ArrayList<Integer>();
			for(int j : d.keySet()) {
				if (c.get(j) == i) {
					y_.add(1);
				}
				else {
					y_.add(-1);
				}
			}
			list_of_y_models.add(y_);
		}
		return list_of_y_models;
		
	}
	
	//Function to get best weights
	public static List<List<Float>> get_best_weights(HashMap<Integer, List<Float>> data1, List<List<Integer>> list_of_y, float lambda){
		List<List<Float>> best_weights	= new ArrayList<List<Float>>();
		for(int i = 0; i < list_of_y.size(); i ++) {
			List<Integer> y_at_i = list_of_y.get(i);
			List<Float> best_w = new ArrayList<Float>();
			//System.out.println("Iteration = " + i);
			best_w = get_best_w(w_vector, data1, y_at_i , lambda);
			best_weights.add(best_w);
		}
		return best_weights;
		
	}
	
	//Function to do RR
	public static HashMap<Integer, Integer> doRR(HashMap<Integer, List<Float>> data1, HashMap<Integer, List<Float>> data2, List<List<Integer>> list_of_y, float lambda) {
		
		//We need a list of weight vectors for each y
				List<List<Float>> list_of_ws = new ArrayList<List<Float>>();
				
				for(int i = 0; i < list_of_y.size(); i ++) {
					List<Integer> y_at_i = list_of_y.get(i);
					List<Float> best_w = new ArrayList<Float>();
					//System.out.println("Iteration = " + i);
					best_w = get_best_w(w_vector, data1, y_at_i , lambda);
					list_of_ws.add(best_w);
				}
		
		//Matrix multiplication of data and each weight vector and store it in a list of lists
				List<List<Float>> predictions = new ArrayList<List<Float>>();
				
				for (int i = 0; i < list_of_ws.size(); i++) {
					List<Float> x_w = new ArrayList<Float>();
					x_w = multiply_x_and_w(data2, list_of_ws.get(i));
					predictions.add(x_w);
				}		
		
		//List to store the max values of Xw among all Xws for each data point
				List<Float> max_values = new ArrayList<Float>();
				List<Integer> new_classes_list = new ArrayList<Integer>();
				for(int i = 0; i < predictions.get(0).size(); i++) {
					max_values.add((float)0);
					new_classes_list.add(0);
				}
				
		//HashMap to store the newly assigned class of each data point
				HashMap<Integer, Integer> new_classes_assigned = new HashMap<Integer, Integer>();
				
				for(int i = 0; i < predictions.size(); i++) {
					for(int j = 0; j < predictions.get(0).size(); j++) {
						float alpha = predictions.get(i).get(j);
						if ( alpha > max_values.get(j)) {
							max_values.set(j, alpha);
							new_classes_list.set(j, i);
						}
					}
				}
		//new_classes_list has values of classes assigned to each data point, which are the indices of the points
		//We create a HashMap from this list
				for(int i = 0; i < new_classes_list.size(); i++) {
					new_classes_assigned.put(i, new_classes_list.get(i));
				}		
		return new_classes_assigned;
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
		
		//Function to get w
		public static List<List<Float>> get_w(HashMap<Integer, List<Float>> Data, List<Float> w_vector, List<Integer> y, float lambd) {
			
			List<Float> original_w = new ArrayList<Float>();
			
			for(float f: w_vector) {
				original_w.add(f);
			}
			//System.out.println("original vector in getw"+original_w);
			List<Float> new_w_vector = new ArrayList<Float>();
			List<List<Float>> result = new ArrayList<List<Float>>();
			
			// m ka loop
			for (int m = 0; m < Data.get(1).size(); m++) {
				//populate Xm and X_minus_m
				List<Float> Xm = new ArrayList<Float>();
				HashMap<Integer, List<Float>> X_minus_m = new HashMap<Integer, List<Float>>();
				for (int k : Data.keySet()) {
					//Populate Xm
					Xm.add(Data.get(k).get(m));
					//Populate X_minus_m
					List<Float> all_except_m = new ArrayList<Float>();
					for(int alpha = 0; alpha < Data.get(k).size(); alpha ++) {
						if(alpha != m) {
							all_except_m.add(Data.get(k).get(alpha));
						}
						X_minus_m.put(k, all_except_m);
				}
				}
			
				List<Float> w_minus_m = new ArrayList<Float>();
				for(int beta = 0; beta < w_vector.size(); beta++) {
					if(beta != m) {
						w_minus_m.add(w_vector.get(beta));
					}
				}
				
				//Calculate numerator
				float numerator = 0;
				
				//Multiply X_minus_m and w_minus_m
				List<Float> product_of_X_minus_m_and_w_minus_m = new ArrayList<Float>();
				
				for (int q : X_minus_m.keySet()) {
					float sum = 0;
					for (int w = 0; w < X_minus_m.get(q).size(); w++) {
						{
							sum += X_minus_m.get(q).get(w) * w_minus_m.get(w);
						}
					}
					product_of_X_minus_m_and_w_minus_m.add(sum);
				}
				
				//System.out.println("Size of product_of_X_minus_m_and_w_minus_m: " + product_of_X_minus_m_and_w_minus_m.size());
				
				//System.out.println("Size of y:" + y.size());
				//Subtract product_of_X_minus_m_and_w_minus_m from y
				List<Float> diff = new ArrayList<Float>();
				
				for(int e = 0; e < y.size(); e++) {
					diff.add(y.get(e) - product_of_X_minus_m_and_w_minus_m.get(e));
				}
				
				//System.out.println("Size of diff:" + diff.size());
				
				//Multiply diff by X_transpose to get the numerator
				for (int g = 0; g < Xm.size(); g++) {
					numerator += (Xm.get(g) * diff.get(g));
				}
				
				//System.out.println("numerator: "+ numerator);
				//Calculate denominator
				/*float transpose_product = 0;
				
				for (float item : Xm) {
					transpose_product += item * item;
				}*/
				
				//Since we have taken normalised form of the data, the denominator will only be lambda
				float denominator = lambd;
				Float w_of_m = numerator / denominator;
				w_vector.set(m, w_of_m);
				new_w_vector.add(w_of_m);
			}
			//System.out.println("Modified original vector is "+original_w);
			//System.out.println("Changed new vector is "+new_w_vector);
			result.add(new_w_vector);
			result.add(original_w);
			
			return result;
		}
		
		//Function to get best_w
		public static List<Float> get_best_w(List<Float> old_w, HashMap<Integer, List<Float>> data, List<Integer> y, float lambda){	
			List<List<Float>> res = get_w(data, old_w, y, lambda);
			List<Float> new_w = res.get(0);
			List<Float> org_w = res.get(1);
			
			//System.out.println("old: " + org_w);
			//System.out.println("new: " + new_w);
			float obj_old = objective_function(data, org_w, y, (float) 0.01);
			float obj_new = objective_function(data, new_w, y, (float) 0.01);
			
			float difff = (obj_old - obj_new);
			float score = difff/obj_old;	
			//System.out.println("obj_func for old_w: " + obj_old);
			//System.out.println("obj_func for new_w: " + obj_new);
			//System.out.println("Diff: " + difff);
			if(score < 0.0001) {
				//System.out.println("score: " + score);
				return new_w;
			}
			else {
			get_best_w(new_w, data, y, lambda);
			}
			return new_w;
		}
		
		/*public static List<Float> get_best_w(List<Float> old_w, HashMap<Integer, List<Float>> data, List<Integer> y, float lambda){
			List<Float> new_w = get_w(data, old_w, y, lambda);
			List<Float> new_w1 = new ArrayList<Float>();
			List<Float> new_w2 = new ArrayList<Float>();
			int flag = 0;
			float score = (objective_function(data, old_w, y, (float) 0.01) - objective_function(data, new_w, y, (float) 0.01))/objective_function(data, old_w, y, (float) 0.01);	
			while(score >= 0.0001) {
				flag = 1;
				new_w = get_w(data, new_w, y, lambda);
				new_w1 = new_w;
				new_w2 = get_w(data, new_w1, y, lambda);
				score = (objective_function(data, new_w1, y, (float) 0.01) - objective_function(data, new_w2, y, (float) 0.01))/objective_function(data, new_w1, y, (float) 0.01);
			}
			if(flag == 1) {
				System.out.println("min_score: " + score);
			}
			return new_w1;
		}*/
		
		//Generic function to multiply a HashMap X and a List w
		public static List<Float> multiply_x_and_w(HashMap<Integer, List<Float>> Data, List<Float> w){
			//Calculate X into w
			List<Float> X_into_w = new ArrayList<Float>();
			
			// Change data into 2-D array before multiplying
			float[][] x_matrix = new float[Data.size()][Data.get(1).size()]; 
			
			for(int i = 0; i < Data.size(); i++) {
				for(int j = 0; j < Data.get(i).size(); j++) {
					x_matrix[i][j] = Data.get(i).get(j);
				}
			}
			
			//Multiply
			for(int i = 0; i < x_matrix.length; i++) {
				float c = 0;
				for(int j = 0; j < w.size(); j++) {
					c += x_matrix[i][j] * w.get(j);
					//System.out.println("i: "+i + " j: "+j +  " c:" +c);
					}
				X_into_w.add(c);
			}
			return X_into_w;
		}
		
		//Objective function calculation
		public static float objective_function(HashMap<Integer, List<Float>> Data, List<Float> w, List<Integer> y, float lambd) {	
			
			//Calculate X into w
			List<Float> X_into_w = new ArrayList<Float>();
			
			X_into_w = multiply_x_and_w(Data, w);
			
			List<Float> first_term = new ArrayList<Float>();
			
			for(int i = 0; i < X_into_w.size(); i++) {
				first_term.add(X_into_w.get(i) - y.get(i));
			}
			
			float mag1 = 0;
			float mag2 = 0;
			
			//Magnitude of first_term
			for(int i = 0; i < first_term.size(); i++) {
				mag1 += Math.pow(first_term.get(i),2);
			}
			//System.out.println("mag1: " + mag1);
			//Magnitude of second term
			for(int j = 0; j < w.size(); j++) {
				mag2 += Math.pow(w.get(j), 2);
			}
			//System.out.println("mag2: " + mag2);
			return (mag1 + lambd * mag2);
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
		
		//Generic function to normalise a vector
		public static List<Float> normalise(List<Float>input_vector) {
			List<Float> output_vector = new ArrayList<Float>();
			float magnitude = (float) 0;
			for(int i = 0; i < input_vector.size(); i++) {
				magnitude += input_vector.get(i) * input_vector.get(i);
			}
			
			float sqrt_mag = (float)Math.sqrt(magnitude);
			
			for(int i = 0; i < input_vector.size(); i++) {
				output_vector.add(input_vector.get(i) / sqrt_mag);
			}
			return output_vector;
		}
		
		//Function to normalise a HashMap column wise
		public static HashMap<Integer, List<Float>> normaliseData(HashMap<Integer, List<Float>> data) {
			HashMap<Integer, List<Float>> normalised_data = new HashMap<Integer, List<Float>>();
			List<Float> normalised_vector = new ArrayList<Float>();
			for(int m = 0; m < data.get(1).size(); m++) {
				List<Float> Xm = new ArrayList<Float>();
				for (int k : data.keySet()) {
					Xm.add(data.get(k).get(m));
				}
				normalised_vector = normalise(Xm);
				
				for(int j : data.keySet()) {
					if(normalised_data.containsKey(j)){
						normalised_data.get(j).add(normalised_vector.get(j));
					}
					else {
						normalised_data.put(j, new ArrayList<Float>());
						normalised_data.get(j).add(normalised_vector.get(j));
					}
				}
				
			}
		return normalised_data;
		}
}