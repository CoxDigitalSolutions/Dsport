package frameWork;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import gnu.trove.map.hash.THashMap;

public class FeatureInfo implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int myID=0;
	NumericalFeatureSummary FeatureSummary=new NumericalFeatureSummary();
	int Type=1;
	
	final int SMOOTHEDCATEGORICAL=1;
	final int INTEGER=2;
	final int FLOAT=3;
	
	
	int NumericalType=3;
	
	
	//name, count
	public THashMap<String,Integer> MainCounter = new THashMap<String,Integer>();
	//name, ordered ID
	public THashMap<String,Integer> MainCounterSorted = new THashMap<String,Integer>();
	//ordered count, name
	Map<Integer, String[]> desSortedMap = new TreeMap<Integer, String[]>(new Descending());

	public int SmoothingLimit=100;
	private int maxID=Integer.MAX_VALUE;
	
	public void Finalize(){
		FeatureSummary.Finalize();
	}
	
	public void Update(String key){

		if(NumericalType>1){
			try{
				double Dval=Double.parseDouble(key);
				
				FeatureSummary.AddTargetValue(Dval);
				if(Dval % 1!=0){
					
					NumericalType=2;
				}


			}catch (Exception e){
				//if(myID==11){
					System.out.println(myID+" key="+key);
				//}
				NumericalType=1;
			}
		}
		if(MainCounter.contains(key)){
			MainCounter.put(key, MainCounter.get(key)+1);
		}else{
			MainCounter.put(key, 1);
		}
	}
	
	public void printMain(int Position, String filename){
		try {
			PrintWriter writer = new PrintWriter(filename);

			for (Map.Entry<Integer, String[]> entry : desSortedMap.entrySet()) {
				maxID=MainCounterSorted.get(entry.getValue());
				writer.write(entry.getValue()+"@@"+(maxID+Position)+"\n");
				if(entry.getKey()<SmoothingLimit){
					break;
				}
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void PrepareFeatures(int minSmoothingLimit){
		Iterator<Entry<String, Integer>> iter = MainCounter.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String,Integer> entry = iter.next();
			if(minSmoothingLimit>entry.getValue()){
				iter.remove();
				continue;
			}
			if(desSortedMap.containsKey(entry.getValue())){
				String [] currStringArr=desSortedMap.get(entry.getValue());
				String [] tStringArr=new String[currStringArr.length+1];
				for(int i=0;i<currStringArr.length;i++){
					tStringArr[i]=currStringArr[i];
				}
				tStringArr[tStringArr.length-1]=entry.getKey();
				desSortedMap.put(entry.getValue(), tStringArr);
			}else{
				String [] tStringArr=new String[1];
				tStringArr[0]=entry.getKey();
				desSortedMap.put(entry.getValue(), tStringArr);
			}
		}
		
		int count=0;
		for (Map.Entry<Integer, String[]> entry : desSortedMap.entrySet()) {
			String [] tStringArr=entry.getValue();
			for(int i=0;i<entry.getValue().length;i++){
				count++;
				MainCounterSorted.put(tStringArr[i], count);
			}
		}
	}
	
	public int GetPreparedFeature(String RawFeature){
		int Result=0;
		if(NumericalType==1){
			if(MainCounterSorted.contains(RawFeature)){
				Integer ID=MainCounterSorted.get(RawFeature);
				if(ID<=maxID){
					Result=ID;
				}
			}
		}else if(NumericalType>=2){
			Result=Integer.parseInt(RawFeature);
			
		}
		return Result;
	}
	
	public int GetProcessedFeature(String FeatureS){
		int Result=0;
		int Feature=Integer.parseInt(FeatureS);
		if(Type==SMOOTHEDCATEGORICAL){
			int ID=0;
			
			if(NumericalType==1){
				ID=Feature;
			}else if(NumericalType>=2){
				ID=FeatureSummary.ValueToPosition(Feature);
				
			}

			if(ID<=maxID){
				Result=ID;
			}
			
		}else {
			System.out.println("wtf!!");
		}
		return Result;
	}
	
	public int GetProcessedFeatureInt(int Feature){
		
		int Result=0;
		if(Type==SMOOTHEDCATEGORICAL){
			int ID=0;
			
			if(NumericalType==1){
				ID=Feature;
			}else if(NumericalType>=2){
				ID=FeatureSummary.ValueToPosition(Feature);
				
			}
			
			if(ID<=maxID){
				Result=ID;
			}
			//System.out.println(Result + " Feature="+Feature + " maxID="+maxID +"ID="+ID);
		}else if(Type==INTEGER){
			
		}

		return Result;
	}
	
	public int SetMaxID(){
		for (Map.Entry<Integer, String[]> entry : desSortedMap.entrySet()) {
			String [] tStringArr=entry.getValue();
			for(int i=0;i<tStringArr.length;i++){
				maxID=MainCounterSorted.get(tStringArr[i]);
				if(entry.getKey()<SmoothingLimit){
					break;
				}
			}
		}
		if(NumericalType>=2){
			maxID=FeatureSummary.CountMap.size();
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
