import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class fptminer{

	public static int minsup = 0;

	public static String duplicate;

	static HashMap<String, Integer> global_map = new HashMap<String, Integer>();

	public static void main(String[] args) {
		long timeStart = System.currentTimeMillis();
		minsup = Integer.parseInt(args[0]);
		String minconf = args[1];
		String inputfile = args[2];
		String outputfile = args[3];

		duplicate = inputfile;

		HashMap<Integer, List<node>> good = node.getFPTreeMap();
		HashMap<Integer, Integer> fmap = getFreqmap();

		test2 obj = new test2();

		for (Integer k : good.keySet()) {
			global_map.put(Integer.toString(k), fmap.get(k));
			obj.getPattern(Integer.toString(k), good.get(k));
		}
		long timeEnd = System.currentTimeMillis();
		long timeDiff = timeEnd - timeStart;
		System.out.println((double)timeDiff/1000);

			//System.out.println(global_map);
        		//System.out.println(global_map.size());

try {
	FileWriter writer = new FileWriter(outputfile);

	for (String key : global_map.keySet()){
	writer.write(key + "|" + "{}" + "|" + global_map.get(key) + "|" + "-1");
	writer.write("\n");
	}
	//writer.flush();
	writer.close();
}
catch(Exception e){
		e.printStackTrace();
}
}

	static final HashMap<Integer, Integer> freq_hmap = new HashMap<Integer, Integer>();

	public static HashMap<Integer, ArrayList<Integer>> getMap() {
		//String filename = "C:\\Users\\Shrivatav\\Desktop\\UMN\\Homework\\Introduction to Data Mining"
        		//+ "\\CHW1\\Data\\small";
		//String filename = "C:\\Users\\Shrivatav\\Desktop\\UMN\\Homework\\Introduction to Data Mining"
        	//	+ "\\CHW1\\test_file.txt";
        String line;
        HashMap<Integer, ArrayList<Integer>> data_hmap = new HashMap<Integer, ArrayList<Integer>>();

        ArrayList<Integer> temp= new ArrayList<Integer>();
        try {
            FileReader fileReader = new FileReader(duplicate);
            // Wrap FileReader in BufferedReader
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(" ",2);
                int tid = Integer.parseInt(parts[0]);
                int item = Integer.parseInt(parts[1]);
                if (data_hmap.containsKey(tid)) {
                    data_hmap.get(tid).add(item);
                }
                else {
                    data_hmap.put(tid, new ArrayList<Integer>());
                    data_hmap.get(tid).add(item);
                }
                //System.out.println(data_hmap);
                if (freq_hmap.containsKey(item)){
                    freq_hmap.put(item, freq_hmap.get(item) + 1);
                }
                else {
                    freq_hmap.put(item, 1);
                }
                //System.out.println(freq_hmap);
        }
            //System.out.println(data_hmap);
            //System.out.println(freq_hmap);
        }
        catch(NumberFormatException ne)
        {
        	System.out.println("NumberFormatException");
        }
        catch(FileNotFoundException ex){
            System.out.println(
                    "Unable to open file '" +
                            duplicate + "'");
        }
        catch(IOException ex){
            System.out.println(
                    "Error reading file '"
                            + duplicate + "'");
        }
        //System.out.println(data_hmap);
        // Remove infrequent transactions
        //int minsup = 100;

        for (int key : data_hmap.keySet()) {
        	for (int a=0; a < data_hmap.get(key).size(); a++)
        	{
        		int x = freq_hmap.get(data_hmap.get(key).get(a));
        		//System.out.println(x<minsup);
        		if (x < minsup) {
        			//System.out.println("X");
        			data_hmap.get(key).remove(a);
        			a--;
        			//System.out.println(data_hmap);
        	}
        }
        //System.out.println(data_hmap);
        }
        // Sorting the Transaction HashMap
        for (int key1 : data_hmap.keySet()) {
        	for(int i=0; i < data_hmap.get(key1).size(); i++) {
        		for (int j=i+1; j < data_hmap.get(key1).size(); j++) {
        			if (freq_hmap.get(data_hmap.get(key1).get(j)) > freq_hmap.get(data_hmap.get(key1).get(i))) {

						int temporary = data_hmap.get(key1).get(j);
						data_hmap.get(key1).set(j,data_hmap.get(key1).get(i));
						data_hmap.get(key1).set(i,temporary);
					}
        		}

        	}

        }
        //System.out.println("data_hmap = " + data_hmap);

        return data_hmap;
	}

	static HashMap<Integer, Integer> getFreqmap(){
		return freq_hmap;
	}


    static class node {
	Integer ID;
	int count = 0;
	List<node> children= new ArrayList<node>();
	node parent;

	HashMap<Integer, ArrayList<Integer>> data_hash = new HashMap<Integer, ArrayList<Integer>>();

	public node(int data) {
		this.ID = data;
	}

	public List<node> getChildren() {
		return children;
	}

	public ArrayList<Integer> getChildrenID() {
		List<node> chldrn = getChildren();
		ArrayList<Integer> l = new ArrayList<Integer>();
		for (node child : chldrn) {
			l.add(child.getID());
		}
		return l;
	}

	public boolean addChild(node n) {
		boolean bool;
		if (this.getChildrenID().contains(n.getID())){
			//System.out.println("X");
			getNodeByID(n.getID()).count += 1;
			//n.count += 1;
			bool = false;
		}
		else {
			n.setParent(this);
	        this.children.add(n);
	        n.count = 1;
	        bool = true;
		}
		return bool;
	}

	private void setParent(node parent) {
		// TODO Auto-generated method stub
			this.parent = parent;

	}

	public int getID() {
        return ID;
    }

	public node getNodeByID(int id_value) {
		List<node> chldrn = getChildren();
		for (node item : chldrn) {
			if (item.ID == id_value) {
				return item;
			}
		}
		return null;
	}


public static HashMap<Integer, List<node>> getFPTreeMap() {
	node root = new node(-1);
	node header = root;


	HashMap<Integer, ArrayList<Integer>> map = getMap();
	HashMap<Integer, List<node>> fpTreeMap = new HashMap<Integer, List<node>>();

	for (int k : map.keySet()) {
		root=header;
		for(int b = 0; b < map.get(k).size(); b++) {
			node nx = new node(map.get(k).get(b));
			if (root.addChild(nx)) {
				if (fpTreeMap.containsKey(nx.ID)) {
					fpTreeMap.get(nx.ID).add(nx);
                }
                else {
                	fpTreeMap.put(nx.ID, new ArrayList<node>());
                	fpTreeMap.get(nx.ID).add(nx);
                }
			}
			root = root.getNodeByID(nx.ID);
		}
	}


	return fpTreeMap;
}

}


    static class test2 {

	//public static int minsup = 100;

	//Function to create Conditional FPTree Map
	public static HashMap<Integer, List<node>> getConditionalFPTreeMap(List<List> mainlist) {

		node root = new node(-1);
		node header = root;

		//HashMap<Integer, ArrayList<Integer>> map = FPTree.getMap();
		HashMap<Integer, List<node>> CondfpTreeMap = new HashMap<Integer, List<node>>();

		for (List<Integer> bar : mainlist) {
			root=header;
			for(int elem : bar) {
				node nx = new node(elem);
				if (root.addChild(nx)) {
					if (CondfpTreeMap.containsKey(nx.ID)) {
						CondfpTreeMap.get(nx.ID).add(nx);
	                }
	                else {
	                	CondfpTreeMap.put(nx.ID, new ArrayList<node>());
	                	CondfpTreeMap.get(nx.ID).add(nx);
	                }
				}
				root = root.getNodeByID(nx.ID);
			}
		}

		return CondfpTreeMap;
	}

	static void addNodeToList(node foo, List<Integer> n_list) {
		node p = foo.parent;
		if (p.getID() != -1) {
			n_list.add(p.ID);
			//System.out.println("pass");
			addNodeToList(p, n_list);
			//System.out.println("updated_list : " + n_list);
	}
}

	//Set<Integer> x = t.keySet();

	public static void getPattern(String t, List<node> list_node) {

		//HashMap<Integer, List<node>> good = node.getFPTreeMap();
		HashMap<Integer, Integer> fmap = getFreqmap();
        	//global_map.get(t).put(fmap.get(t));
			//System.out.println("K = :" + key);
			List<List> mainlist = new ArrayList<List>();
			List<node> n = list_node;
				for (node item : n) {
					//System.out.println("item = :" + item);
					List<Integer> l = new ArrayList<Integer>();
					addNodeToList(item, l);
					for (int i = 0; i < item.count; i++) {
					mainlist.add(l);
		}
				}

		//Sorting the list of lists
				for (List<Integer> bar : mainlist) {
		        	for(int i=0; i < bar.size(); i++) {
		        		for (int j=i+1; j < bar.size(); j++) {
		        			if (fmap.get(bar.get(j)) > fmap.get(bar.get(i))) {
								int temporary = bar.get(i);
								bar.set(i,bar.get(j));
								bar.set(j, temporary) ;
							}
		        		}
		        	}
		        }

		//HashMap for keeping counts of items in FPTree list of transactions
		HashMap<Integer, Integer> count_in_tree = new HashMap<Integer, Integer>();

		//Generating the HashMap
		for (List<Integer> bar : mainlist) {
			for (int item : bar) {
				if (count_in_tree.containsKey(item)){
					count_in_tree.put(item, count_in_tree.get(item) + 1);
                }
                else {
                	count_in_tree.put(item, 1);
                }
			}
		}

		//Deleting from mainList if count < minsup
		for (List<Integer> bar : mainlist) {
			//ListIterator<Integer> iter = bar.listIterator();
			//List<Integer> xx = new ArrayList<Integer>();
			List<Integer> xx = new ArrayList<Integer>(bar);
			for (Integer mm : xx) {
				//xx.add(item);
				if(count_in_tree.get(mm) < minsup) {
					bar.remove(bar.indexOf(mm));
				}
			}
		}

		//Generate conditional FP Tree from updated List of Lists
		HashMap<Integer, List<node>> new_tree = getConditionalFPTreeMap(mainlist);
		//System.out.println("Conditional FP Tree : " + new_tree);

		if (new_tree.isEmpty()){
			//System.out.println("Empty");
			return;
		}
		else {
		for (Integer elem : new_tree.keySet()) {
			global_map.put(Integer.toString(elem).concat(" ").concat(t), count_in_tree.get(elem));
			getPattern(Integer.toString(elem).concat(" ").concat(t), new_tree.get(elem));
			//getPattern(Integer.toString(elem), new_tree.get(elem));
		}
		}
}

}
}
