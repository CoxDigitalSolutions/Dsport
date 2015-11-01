package frameWork;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class DataPreparer implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public FeatureInfo[] FeatureInfo;
	String Delimiter=",";
	String SubDelimiter="\t";
	int [] ColumnSizes;
	int [] usedFeatures;

	public TargetSummary TargetSummary=new TargetSummary();
	
	public void init(int FeatureCount){
		FeatureInfo=new FeatureInfo[FeatureCount];
		for(int i=0;i<FeatureInfo.length;i++){
			FeatureInfo[i]=new FeatureInfo();
		}
	}
	
	public void Finalize(){
		TargetSummary.Finalize();
	}
	
	public void Update(String [] RawFeatures){
		for(int i=0;i<RawFeatures.length;i++){
			if(RawFeatures[i].contains(SubDelimiter)){
				String [] values=RawFeatures[i].split(SubDelimiter);
				for(int j=0;j<values.length;j++){
					FeatureInfo[i].Update(values[j]);
				}
			}else{
				FeatureInfo[i].Update(RawFeatures[i]);
			}
		}
	}
	
	public void PrepareFeatures(int minSmoothingLimit){
		for(int i=0;i<FeatureInfo.length;i++){
			FeatureInfo[i].PrepareFeatures(minSmoothingLimit);
			FeatureInfo[i].SetMaxID();
		}
	}
	
	public void SetSmoothingLimit(int minSmoothingLimit){
		ColumnSizes=new int[FeatureInfo.length];
		for(int i=0;i<FeatureInfo.length;i++){
			FeatureInfo[i].SmoothingLimit=minSmoothingLimit;
			ColumnSizes[i] = FeatureInfo[i].SetMaxID();
		}
	}
	
	public String GetPreparedString(String [] RawFeatures){
		String Result="";
		for(int i=0;i<RawFeatures.length;i++){
			if(RawFeatures[i].contains(SubDelimiter)){
				String [] values=RawFeatures[i].split(SubDelimiter);
				int added=0;
				for(int j=0;j<values.length;j++){
					int ReturnedFeature=FeatureInfo[i].GetPreparedFeature(values[j]);
					if(ReturnedFeature!=0){
						Result+=ReturnedFeature+SubDelimiter;
						added++;
					}
				}
				if(added==0){
					Result+="0";
				}else{
					Result=Result.substring(0,Result.length()-1);
				}
			}else{
				Result+=FeatureInfo[i].GetPreparedFeature(RawFeatures[i]);
			}
			Result+=",";
		}
		//remove last comma as it's not needed
		Result=Result.substring(0,Result.length()-1);
		return Result;
	}
	
	public int [] GetFeatures(String FeaturesString){
		//System.out.println(FeaturesString);
		String [] values=FeaturesString.split(Delimiter);
		
		if(values.length!=FeatureInfo.length){
			System.out.println("FeatureString has wrong number of features :" + FeaturesString);
		}
		
		int SubDelimiterCount= FeaturesString.length() - FeaturesString.replace(SubDelimiter, "").length();
		
		int [] FeatureList=new int [FeatureInfo.length+SubDelimiterCount];
		int Position=0;
		int counter=0;
		for(int i=0;i<usedFeatures.length;i++){
			if(values[usedFeatures[i]].contains(SubDelimiter)){
			//if(values[i].contains(SubDelimiter)){
				
				System.out.println("got subDelimiter!!!");
				String [] valuesSub=values[usedFeatures[i]].split(SubDelimiter);
				//String [] valuesSub=values[i].split(SubDelimiter);
				int Added=0;
				for(int j=0;j<valuesSub.length;j++){
					
					int temp=Position+FeatureInfo[usedFeatures[i]].GetProcessedFeature(valuesSub[j]);
					if(temp!=Position){
							FeatureList[counter]=temp;
							counter++;
							Added++;
					}
				}
				if(Added==0){
					FeatureList[counter]=Position;
					counter++;
				}
				
			}else{
				int temp=Position+FeatureInfo[usedFeatures[i]].GetProcessedFeature(values[usedFeatures[i]]);
				//int temp=Position+FeatureInfo[usedFeatures[i]].GetProcessedFeature(values[i]);
				//System.out.println(usedFeatures[i]+" val="+values[usedFeatures[i]]+"  temp="+temp+ "  pos=" + Position);

				FeatureList[counter]=temp;

				counter++;
			}
			
			Position+=FeatureInfo[usedFeatures[i]].GetMaxID()+1;

		}
		
		int [] FinalFeatureList=new int [counter];
		for(int i=0;i<counter;i++){
			FinalFeatureList[i]=FeatureList[i];
		}
		
		return FinalFeatureList;
	}
	
	public void setUsedFeatures(int []usedFeatures){
		if(usedFeatures==null){
			this.usedFeatures= new int [FeatureInfo.length];
			for(int i=0;i<FeatureInfo.length;i++){
				this.usedFeatures[i]=i;
			}
		}else{
			this.usedFeatures=usedFeatures;
		}
	}

	public int GetTotalFeatureCount() {
		int TotalFeatureCount=0;
		for(int i=0;i<usedFeatures.length;i++){
			TotalFeatureCount+=ColumnSizes[usedFeatures[i]]+1;
		}
		
		return TotalFeatureCount;
	}
	
	public int PrintTotalFeatureCount(String File) {
		int TotalFeatureCount=0;
		try {
			PrintWriter writer = new PrintWriter(File+"-Main");

			for(int i=0;i<usedFeatures.length;i++){
				writer.write(i+","+TotalFeatureCount + "\n");
				FeatureInfo[usedFeatures[i]].printMain(TotalFeatureCount,File+"-"+i);
				
				TotalFeatureCount+=ColumnSizes[usedFeatures[i]]+1;
				
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return TotalFeatureCount;
	}

	public int[] GetColumns() {
		int [] Columns= new int [usedFeatures.length];
		for(int i=0;i<usedFeatures.length;i++){
			Columns[i]=ColumnSizes[usedFeatures[i]]+1;
		}
		return Columns;
	}
}
