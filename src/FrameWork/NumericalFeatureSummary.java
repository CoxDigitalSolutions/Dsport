package frameWork;

import java.util.Map;
import java.util.TreeMap;


public class NumericalFeatureSummary  implements java.io.Serializable{
	/**
	 * 
	 */
	public int myID=0;
	private static final long serialVersionUID = 1L;
	final int AbsValue=1;
	final int Count=2;
	int Type=2;

	public TreeMap<Double, Stats> AbsValueMap = new TreeMap<Double, Stats>();
	double [] AbsValueMapFloat;
	public TreeMap<Double, Stats> CountMap = new TreeMap<Double, Stats>();
	double [] CountMapFloat;
	public double[] IDToValue;
	
	public void ReduceMap(int maxReduce, int goal, int ReduceLimit){
		ReduceMap(maxReduce, goal, ReduceLimit,AbsValueMap,1);
		ReduceMap(maxReduce, goal, ReduceLimit,CountMap,2);
	}
	private void ReduceMap(int maxReduce, int goal, int ReduceLimit,TreeMap<Double, Stats> desSortedMap, int type){
		while(desSortedMap.size()>ReduceLimit){
			ReduceMap(maxReduce, goal,desSortedMap, type);
		}
	}
	
	public int ValueToPosition(double value){
		if(Type==1){
			return ValueToPosition(value,AbsValueMapFloat);
			//return ValueToPosition(value,AbsValueMap);
		}else if(Type==2){
			return ValueToPosition(value,CountMapFloat);
			//return ValueToPosition(value,CountMap);
		}else{
			System.out.println("Error getting value from NumericalFeatureSummary");
			return -1;
		}
	}
	
	private int ValueToPosition(double value,double [] DoubleMap){

		int pos=DoubleMap.length/2;
		int diff=pos;
		double currentVal=DoubleMap[pos];
		int mixLimit=100;
		if(diff<mixLimit){
			for(int i=0;i<DoubleMap.length;i++){
				if(value<=DoubleMap[i]){
					return i;
				}
			}
		}

		while(true){
			if(value==currentVal){
				return pos;
			}
			diff/=2;

			if(value<currentVal){
				pos-=diff;
				
				if(pos-diff<10){
					pos=0;diff=0;
				}
				if(diff<mixLimit){
					for(int i=pos-diff;i<DoubleMap.length;i++){
						if(value<=DoubleMap[i]){
							return i;
						}
					}
				}
			}else if(value>currentVal){
				if(diff<mixLimit){
					for(int i=pos-(diff/2)-2;i<DoubleMap.length;i++){
						if(value<=DoubleMap[i]){
							return i;
						}
					}
				}
				pos+=diff;
			}
			currentVal=DoubleMap[pos];
		}

	}
	
	private int ValueToPosition(double value,TreeMap<Double, Stats> desSortedMap){
		for (Map.Entry<Double, Stats> entry : desSortedMap.entrySet()) {
			//System.out.println(entry.getKey()+"="+entry.getValue().position);
		}
		Double key=desSortedMap.floorKey(value);
		if(key==null){
			return 0;
		}
		return desSortedMap.get(key).position;
	}
	
	private double PositionToValue(int Position){
		return IDToValue[Position];
	}
	
	public void AddTargetValue(double value){

		AddTargetValue(value,AbsValueMap);
		AddTargetValue(value,CountMap);
	}
	
	private void AddTargetValue(double value,TreeMap<Double, Stats> desSortedMap){
		
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
		AbsValueMapFloat=Finalize(AbsValueMap);
		CountMapFloat=Finalize(CountMap);

	}
	
	private double [] Finalize(TreeMap<Double, Stats> desSortedMap){
		int count=0;
		double [] DoubleMap=new double[desSortedMap.size()];

		for (Map.Entry<Double, Stats> entry : desSortedMap.entrySet()) {
			entry.getValue().position=count;
			DoubleMap[count]=entry.getKey();
			count++;
		}
		return DoubleMap;
	}
	
	private void ReduceMap(int maxReduce, int goal){
		ReduceMap(maxReduce,goal,AbsValueMap,1);
		ReduceMap(maxReduce,goal,CountMap,2);
	}
	
	private void ReduceMap(int maxReduce, int goal,TreeMap<Double, Stats> desSortedMap, int type){
		Map<Double, Stats[]> CostMap= getCostMap(desSortedMap,type);


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
	
	private Map<Double, Stats[]> getCostMap(TreeMap<Double, Stats> desSortedMap, int type){
		Map<Double, Stats[]> CostMap = new TreeMap<Double, Stats[]>();

		Stats prevStats=null;

		int count=0;
		for (Map.Entry<Double, Stats> entry : desSortedMap.entrySet()) {
			if(count>0){
				Stats currentStats=entry.getValue();

				double cost=0;
				if(type==1){
					cost=CostFunctionAbsDiff(currentStats,prevStats);
				}else if(type==2){
					cost=CostFunctionCount(currentStats,prevStats);
				}else{
					return null;
				}
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
	
	private void mergeStats(Stats s1,Stats s2,TreeMap<Double, Stats> desSortedMap){
		desSortedMap.remove(s1.min);
		desSortedMap.remove(s2.min);
		s1.min=Math.min(s1.min, s2.min);
		s1.max=Math.max(s1.max, s2.max);
		s1.count+=s2.count;
		s1.sum+=s2.sum;
		
		desSortedMap.put(s1.min, s1);
	}
	
	

	
	private void IncrementValue(double value, double key,TreeMap<Double, Stats> desSortedMap){
		Stats tStats=desSortedMap.get(key);
		tStats.count+=1;
		tStats.sum+=value;

		desSortedMap.put(key, tStats);
	}
	
	private void AddNewValue(double value,TreeMap<Double, Stats> desSortedMap){
		Stats tStats=new Stats();
		tStats.count=1;
		tStats.max=value;
		tStats.min=value;
		desSortedMap.put(value, tStats);
	}
	
	private double CostFunctionAbsDiff(Stats prev,Stats curr){
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