package frameWork;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;



public class TargetSummary  implements java.io.Serializable{
	static TreeMap<Float, Stats> desSortedMap = new TreeMap<Float, Stats>();
	
	public static void ReduceMap(int maxReduce, int goal, int ReduceLimit){
		if(desSortedMap.size()>ReduceLimit){
			ReduceMap(maxReduce, goal);
		}
	}
	
	public static void AddTargetValue(float value){
		Float fKey=desSortedMap.floorKey(value);
		
		if(desSortedMap.isEmpty() || fKey==null){
			AddNewValue(value);
		}else{
			if(desSortedMap.get(fKey).max>=value){
				IncrementValue(value,fKey);
			}else{
				AddNewValue(value);
			}
		}
	}
	
	static void ReduceMap(int maxReduce, int goal){
		Map<Float, Stats[]> CostMap = getCostMap();
		int count=0;

		count=0;
		int size=desSortedMap.size();
		for (Map.Entry<Float, Stats[]> entry : CostMap.entrySet()) {
			Stats pStats=entry.getValue()[0];
			Stats cStats=entry.getValue()[1];
			
			if(desSortedMap.containsKey(pStats.min)&& desSortedMap.containsKey(cStats.min)){
				//System.out.println(entry.getKey()+"\t"+prevF+"\t"+currentF);
				pStats.min=Math.min(pStats.min, cStats.min);
				pStats.max=Math.max(pStats.max, cStats.max);
				pStats.count+=cStats.count;
				pStats.sum+=cStats.sum;
				
				desSortedMap.remove(cStats.min);
				desSortedMap.put(pStats.min, pStats);
				

				count++;
				if(count>=maxReduce || size-count<=goal){
					break;
				}
			}
			
		}
	}
	
	static Map<Float, Stats[]> getCostMap(){
		Map<Float, Stats[]> CostMap = new TreeMap<Float, Stats[]>();
		
		Stats prevStats=null;

		int count=0;
		for (Map.Entry<Float, Stats> entry : desSortedMap.entrySet()) {
			if(count>0){
				Stats currentStats=entry.getValue();

				float cost=CostFunction(currentStats,prevStats);
				Stats [] TFloat=new Stats[2];
				TFloat[0]=prevStats;
				TFloat[1]=currentStats;
				CostMap.put(cost, TFloat);
			}
			prevStats=entry.getValue();
			count++;
		}
		return CostMap;
	}
	
	static void mergeStats(Stats s1,Stats s2){
		s1.min=Math.min(s1.min, s2.min);
		s1.max=Math.max(s1.max, s2.max);
		s1.count+=s2.count;
		s1.sum+=s2.sum;
		
		desSortedMap.remove(s2.min);
		desSortedMap.put(s1.min, s1);
	}
	
	

	
	static void IncrementValue(float value, float key){
		Stats tStats=desSortedMap.get(key);
		tStats.count+=1;
		tStats.sum+=value;
		desSortedMap.put(key, tStats);
	}
	
	static void AddNewValue(float value){
		Stats tStats=new Stats();
		tStats.count=1;
		tStats.max=value;
		tStats.min=value;
		desSortedMap.put(value, tStats);
	}
	
	
	static float CostFunction(Stats prev,Stats curr){
		float currentF=curr.sum/curr.count;
		float currentC=curr.count;
		float prevF=prev.sum/prev.count;
		float prevC=prev.count;
		float avg=currentF*currentC+prevF*prevC;
		avg=avg/(prevC+currentC);
		return (currentF-avg)*currentC+(prevF-avg)*prevC;
	}
	
	static class Descending implements Comparator<Float> , java.io.Serializable{
		private static final long serialVersionUID = 1L;

		@Override
		public int compare(Float o1, Float o2) {
			return o2.compareTo(o1);
		}
	}
}