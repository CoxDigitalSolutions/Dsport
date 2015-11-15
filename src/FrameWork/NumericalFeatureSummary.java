package frameWork;

import java.util.Map;
import java.util.TreeMap;


public class NumericalFeatureSummary  implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final int AbsValue=1;
	final int Count=2;
	int Type=1;

	public TreeMap<Double, Stats> AbsValueMap = new TreeMap<Double, Stats>();
	public TreeMap<Double, Stats> CountMap = new TreeMap<Double, Stats>();
	public double[] IDToValue;
	
	public void ReduceMap(int maxReduce, int goal, int ReduceLimit){
		ReduceMap(maxReduce, goal, ReduceLimit,AbsValueMap);
		ReduceMap(maxReduce, goal, ReduceLimit,CountMap);
	}
	public void ReduceMap(int maxReduce, int goal, int ReduceLimit,TreeMap<Double, Stats> desSortedMap){
		while(desSortedMap.size()>ReduceLimit){
			ReduceMap(maxReduce, goal,desSortedMap);
		}
	}
	
	public int ValueToPosition(double value){
		if(Type==1){
			return ValueToPosition(value,AbsValueMap);
		}else if(Type==2){
			return ValueToPosition(value,CountMap);
		}else{
			System.out.println("Error getting value from NumericalFeatureSummary");
			return -1;
		}
	}
	
	public int ValueToPosition(double value,TreeMap<Double, Stats> desSortedMap){

		Double key=desSortedMap.floorKey(value);
		return desSortedMap.get(key).position;
	}
	
	public double PositionToValue(int Position){
		return IDToValue[Position];
	}
	
	public void AddTargetValue(double value){
		AddTargetValue(value,AbsValueMap);
		AddTargetValue(value,CountMap);
	}
	
	public void AddTargetValue(double value,TreeMap<Double, Stats> desSortedMap){
		Double fKey=desSortedMap.floorKey(value);
		
		if(desSortedMap.isEmpty() || fKey==null){
			AddNewValue(value,desSortedMap);
		}else{
			if(desSortedMap.get(fKey).max>=value){
				IncrementValue(value,fKey,desSortedMap);
			}else{
				AddNewValue(value,desSortedMap);
			}
		}
	}
	
	public void Finalize(){
		Finalize(AbsValueMap);
		Finalize(CountMap);
	}
	
	public void Finalize(TreeMap<Double, Stats> desSortedMap){
		int count=0;
		IDToValue=new double[desSortedMap.size()];

		for (Map.Entry<Double, Stats> entry : desSortedMap.entrySet()) {
			entry.getValue().position=count;
			IDToValue[count]=entry.getValue().sum/entry.getValue().count;
			count++;
		}
	}
	
	void ReduceMap(int maxReduce, int goal){
		ReduceMap(maxReduce,goal,AbsValueMap);
		ReduceMap(maxReduce,goal,CountMap);
	}
	
	void ReduceMap(int maxReduce, int goal,TreeMap<Double, Stats> desSortedMap){
		Map<Double, Stats[]> CostMap = getCostMap(desSortedMap);
		int count=0;

		count=0;
		int size=desSortedMap.size();
		for (Map.Entry<Double, Stats[]> entry : CostMap.entrySet()) {
			Stats pStats=entry.getValue()[0];
			Stats cStats=entry.getValue()[1];
			
			if(desSortedMap.containsKey(pStats.min)&& desSortedMap.containsKey(cStats.min)){
				//System.out.println(entry.getKey()+"\t"+prevF+"\t"+currentF);
				desSortedMap.remove(cStats.min);
				desSortedMap.remove(pStats.min);
				pStats.min=Math.min(pStats.min, cStats.min);
				pStats.max=Math.max(pStats.max, cStats.max);
				pStats.count+=cStats.count;
				pStats.sum+=cStats.sum;
				

				desSortedMap.put(pStats.min, pStats);
				count++;
				if(count>=maxReduce || size-count<=goal){
					break;
				}
			}
		}
	}
	
	public Map<Double, Stats[]> getCostMap(TreeMap<Double, Stats> desSortedMap){
		Map<Double, Stats[]> CostMap = new TreeMap<Double, Stats[]>();

		Stats prevStats=null;

		int count=0;
		for (Map.Entry<Double, Stats> entry : desSortedMap.entrySet()) {
			if(count>0){
				Stats currentStats=entry.getValue();

				double cost=CostFunction(currentStats,prevStats);
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
	
	public void mergeStats(Stats s1,Stats s2,TreeMap<Double, Stats> desSortedMap){
		desSortedMap.remove(s1.min);
		desSortedMap.remove(s2.min);
		s1.min=Math.min(s1.min, s2.min);
		s1.max=Math.max(s1.max, s2.max);
		s1.count+=s2.count;
		s1.sum+=s2.sum;
		
		desSortedMap.put(s1.min, s1);
	}
	
	

	
	public void IncrementValue(double value, double key,TreeMap<Double, Stats> desSortedMap){
		Stats tStats=desSortedMap.get(key);
		tStats.count+=1;
		tStats.sum+=value;
		desSortedMap.put(key, tStats);
	}
	
	public void AddNewValue(double value,TreeMap<Double, Stats> desSortedMap){
		Stats tStats=new Stats();
		tStats.count=1;
		tStats.max=value;
		tStats.min=value;
		desSortedMap.put(value, tStats);
	}
	
	
	public double CostFunction(Stats prev,Stats curr){
		return CostFunctionCount( prev,curr);
	}
	
	public double CostFunctionAbsDiff(Stats prev,Stats curr){
		double currentF=curr.sum/curr.count;
		double currentC=curr.count;
		double prevF=prev.sum/prev.count;
		double prevC=prev.count;
		double avg=currentF*currentC+prevF*prevC;
		avg=avg/(prevC+currentC);
		return (currentF-avg)*currentC+(prevF-avg)*prevC;
	}
	
	
	public double CostFunctionCount(Stats prev,Stats curr){
		return Math.min(prev.count, curr.count);
	}
}