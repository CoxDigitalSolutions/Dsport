package frameWork;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

import models.BaseModel;

public class ModelThreadDisk extends Thread{
	
   private BaseModel BaseModel;

   private int rounds;
   private int ID;
   private int TotalThreads;
   private int minID;
   private int maxID;
   private String File;
   public  float RoundCost;
   public int AdTotCountRound;
   private BufferedReader br;
   private DataPreparer dataPreparer;
   boolean verbose=true;
   public String location="";


   
   public ModelThreadDisk(BaseModel BaseModel,int rounds, int ID,int TotalThreads,String File,int minID,int maxID,BufferedReader br,DataPreparer dataPreparer, boolean verbose)
   {
      this.BaseModel = BaseModel;
      this.rounds = rounds;
      this.ID=ID;
      this.RoundCost=0;
      this.TotalThreads=TotalThreads;
      this.minID=minID;
      this.maxID=maxID;
      this.File=File;
      this.br=br;
      this.dataPreparer=dataPreparer;
      this.verbose=verbose;
      
   }
	
	public void run(){
	  try {
		  

	  String sCurrentLine;
	  
	  int i=0;
	  float PrintTime=0.05F;
	  float temp=PrintTime;
	  float minLog=(float) (1*Math.pow(10, -15));

	  AdTotCountRound=0;

	  int Size=maxID-minID;
	  int Pos=(int) minID+(Size/TotalThreads*ID);

	  PrintWriter writer=null;
	  if(location.length()>0){
		  writer = new PrintWriter(location+"-"+ID,"UTF-8");
	  }
			  
      while(i<rounds)
      {
    	
    	if ((sCurrentLine = br.readLine()) == null || Pos>=maxID){
    		br.close();
    		br = new BufferedReader(new FileReader(File));
    		Pos=0;
    		continue;
    	}

    	if(sCurrentLine.length()<dataPreparer.GetColumns().length){
    		continue;
    	}

    	Pos++;
    	if(Pos<minID){
    		continue;
    	}

    	if(rounds*temp<i){
    		if(verbose){
	    		System.out.println("Thread " + ID + " :" + Math.round(temp*100) 
	    				+"%"+" RoundCost="+RoundCost/AdTotCountRound);
    		}
		
    		temp+=PrintTime;
    		RoundCost=0;
    		AdTotCountRound=0;
    	}

		
		String [] values=sCurrentLine.split(":");

		int [] tempLFV=dataPreparer.GetFeatures(values[0]);

		
		float RealValue=Float.parseFloat(values[1]);
		
		float result=0;
		//try{
			if(location.length()>0){

				result = BaseModel.predict(tempLFV);
				writer.write(values[1]+","+result+"\n");
			}else{
				result = BaseModel.Train(RealValue ,tempLFV);
			}
		/*}  catch (Exception e) {
			
			System.out.println("Error in ModelThreadDiskNew:");
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
			e.printStackTrace();
			continue;
		}*/

		if(RealValue==-1){
			RealValue=0;
		}
		
		float tempC=0;
		
		if(RealValue==0){
			if(result>=1-minLog){
				tempC=(float) Math.log(minLog);
			}else{
				tempC=(float) Math.log(1-result);
			}
		}else{
			if(result<=minLog){
				tempC=(float) Math.log(minLog);
			}else{
				tempC=(float) Math.log(result);
			}
		}
		
		
		float tempNum=(result-RealValue);

		tempC=tempNum*tempNum;
		RoundCost-=tempC;
		
		AdTotCountRound++;
		
	     i++;
      }

	  if(location.length()>0){
		  writer.close();
	  }
      if(verbose){
    	  System.out.println("ThreadID=" +ID+" cost="+RoundCost/AdTotCountRound);
      }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();


		}
	}
}
