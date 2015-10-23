package frameWork;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import gnu.trove.map.hash.THashMap;

public class FeatureInfo implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	//name, count
	public THashMap<String,Integer> MainCounter = new THashMap<String,Integer>();
	//name, ordered ID
	public THashMap<String,Integer> MainCounterSorted = new THashMap<String,Integer>();
	//ordered count, name
	Map<Integer, String> desSortedMap = new TreeMap<Integer, String>(new Descending());

	public int SmoothingLimit=100;
	private int maxID=Integer.MAX_VALUE;
	
	public void Update(String key){
		if(MainCounter.contains(key)){
			MainCounter.put(key, MainCounter.get(key)+1);
		}else{
			MainCounter.put(key, 1);
		}
	}
	
	public void printMain(int Position, String filename){
		try {
			PrintWriter writer = new PrintWriter(filename);

			for (Map.Entry<Integer, String> entry : desSortedMap.entrySet()) {
				maxID=MainCounterSorted.get(entry.getValue());
				writer.write(entry.getValue()+"@@"+(maxID+Position)+"\n");
				if(entry.getKey()<SmoothingLimit){
					//maxID=MainCounterSorted.get(entry.getValue());
					break;
				}
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void PrepareFeatures(int minSmoothingLimit){
		for (Map.Entry<String, Integer> entry : MainCounter.entrySet()) {
			System.out.println(entry.getValue());
			if(minSmoothingLimit>entry.getValue()){
				//find a way to remove this entry later?
				//MainCounter.remove(entry.getKey());
				continue;
			}
			desSortedMap.put(entry.getValue(), entry.getKey());
		}
		
		int count=0;
		for (Map.Entry<Integer, String> entry : desSortedMap.entrySet()) {
			count++;
			MainCounterSorted.put(entry.getValue(), count);
		}
	}
	
	public int GetPreparedFeature(String RawFeature){
		int Result=0;
		if(MainCounterSorted.contains(RawFeature)){
			Integer ID=MainCounterSorted.get(RawFeature);
			if(ID<=maxID){
				Result=ID;
			}
		}
		return Result;
	}
	
	public int GetProcessedFeature(String Feature){
		int Result=0;
		int ID=Integer.parseInt(Feature);
		if(ID<=maxID){
			Result=ID;
		}

		return Result;
	}
	
	public int SetMaxID(){
		for (Map.Entry<Integer, String> entry : desSortedMap.entrySet()) {
			maxID=MainCounterSorted.get(entry.getValue());
			if(entry.getKey()<SmoothingLimit){
				
				//maxID=MainCounterSorted.get(entry.getValue());
				break;
			}
		}
		
		System.out.println(maxID);
		return maxID;
	}
	
	public int GetMaxID(){
		return maxID;
	}
	
	public class Descending implements Comparator<Integer> , java.io.Serializable{
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int compare(Integer o1, Integer o2) {
			return o2.compareTo(o1);
		}
	}
}
