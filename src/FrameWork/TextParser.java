package FrameWork;

public class TextParser {
	
	public int[] Columns;
	public int[] FeatureVector;
	public int[] UsedFeatures;
	public float RN;


	public boolean parseString(String value){
		
		try
		{
			String []values=value.split("\t");
			int position=0;
			int CommaCount= value.length() - value.replace(",", "").length();
	
			int  [] tempFeatureVector=new int[values.length-1+CommaCount];
	
			
			int tempCounter=0;
			int UFcount=0;
			for(int i=0;i<values.length-1;i++){
				if(UsedFeatures!=null){
					
					while(UsedFeatures[UFcount]<i){
						UFcount++;
						if(UsedFeatures.length>=UFcount){
							break;
						}
					}
					if(UsedFeatures.length<=UFcount){
						break;
					}
					if(UsedFeatures[UFcount]!=i){
						continue;
					}
				}
				if(values[i].contains(",")){
					String[] tempSA=values[i].split(",");
					for(int j=0;j<tempSA.length;j++){
						try{
							tempFeatureVector[tempCounter]=(int) (position+Integer.parseInt(tempSA[j]));
						} catch (Exception e) {
							System.out.println(e.getMessage()+"\n"+e.getStackTrace());
							tempFeatureVector[tempCounter]=(int) (position+Integer.parseInt(tempSA[j].replaceAll("[^0-9.]", "")));
						}
						tempCounter++;
					}
				}else{
					
					try{
						tempFeatureVector[tempCounter]=(int) (position+Integer.parseInt(values[i]));
					} catch (Exception e) {
						System.out.println(e.getMessage()+"\n"+e.getStackTrace());
						tempFeatureVector[tempCounter]=(int) (position+Integer.parseInt(values[i].replaceAll("[^0-9.]", "")));
					}
					tempCounter++;
				}
	
				position+=Columns[i];
				
			}
			FeatureVector= new int [tempCounter];
			for(int i=0;i<tempCounter;i++){
				FeatureVector[i]=tempFeatureVector[i];
			}
	
			RN=Float.parseFloat(values[values.length-1]);
		} catch (Exception e) {
		    System.err.println("Error parsing string: " + value +" Error: " + e.getMessage());
		    return false;
		}
		return true;
	}
}
