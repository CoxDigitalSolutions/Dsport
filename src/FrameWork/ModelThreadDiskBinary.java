package frameWork;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import Boosting.Booster;
import models.BaseModel;

public class ModelThreadDiskBinary extends Thread{
	
   private BaseModel BaseModel;
   

   private int rounds;
   private int ID;
   private int TotalThreads;
   private int minID;
   private int maxID;
   private String File;
   public  float RoundCost;
   public int AdTotCountRound;
   FileChannel inChannel;
   ByteBuffer buffer;
   private DataPreparer dataPreparer;
   boolean verbose=false;
   public String location="";
   public Booster booster;
   public boolean Validation=false;


   
   public ModelThreadDiskBinary(BaseModel BaseModel,int rounds, int ID,int TotalThreads,String File,int minID,int maxID,FileChannel inChannel,ByteBuffer buffer,DataPreparer dataPreparer, boolean verbose)
   {
      this.BaseModel = BaseModel;
      this.rounds = rounds;
      this.ID=ID;
      this.RoundCost=0;
      this.TotalThreads=TotalThreads;
      this.minID=minID;
      this.maxID=maxID;
      this.File=File;
      this.inChannel=inChannel;
      this.buffer=buffer;
      this.dataPreparer=dataPreparer;
      this.verbose=verbose;
      
   }
	
	public void run(){
	  try {
		  


	  
	  int i=0;
	  float PrintTime=1.0F;
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
    	
    	  if(UtilByte.CheckUpdateBuffer(inChannel,buffer)==-1){
  			inChannel = new FileInputStream(File).getChannel();
  			buffer = ByteBuffer.allocateDirect(16384*2);
  			buffer.flip();
  			Pos=0;
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




		int [][] tempLFV=dataPreparer.GetFeatures(inChannel,buffer);
		
		//System.out.println(tempLFV.length);
	  	  if(tempLFV==null || tempLFV.length==0){
				inChannel = new FileInputStream(File).getChannel();
				buffer = ByteBuffer.allocateDirect(16384*2);
				buffer.flip();
				UtilByte.CheckUpdateBuffer(inChannel,buffer);
				Pos=0;
				continue;
	  	  }

		float RealValue=dataPreparer.GetPredictionBinary(inChannel,buffer);
		
		
		float result=0;
			if(location.length()>0){

				result = BaseModel.predict(tempLFV[0]);
				writer.write(RealValue+","+result+"\n");
			}else{
				if(booster==null){
					int [] UsedFatures=dataPreparer.GetFeaturesFromInt(tempLFV[0],tempLFV[1]);
					result = BaseModel.Train(RealValue ,UsedFatures);
	
				}else{
					// need to make sure Pos is correct
					if(Validation){
						result=booster.GetLatestPrediction(Pos, tempLFV[0],tempLFV[1]);

					}else{
						float residual=booster.GetLatestPrediction(Pos, tempLFV[0],tempLFV[1]);
						int [] UsedFatures=dataPreparer.GetFeaturesFromInt(tempLFV[0],tempLFV[1]);
						result = BaseModel.TrainBoosted(RealValue, residual, UsedFatures);
					}
				}
			}


		if(result!=0){
		
		float tempC=0;
		/*
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
		*/
		result=(float) UtilMath.exp(result)-1;
		RealValue=(float) UtilMath.exp(RealValue)-1;
		//System.out.println(result +" - "+ RealValue);
		float tempNum=(result-RealValue)/RealValue;

		tempC=tempNum*tempNum;
		RoundCost+=tempC;
		

		
		AdTotCountRound++;
		}
	     i++;
      }

	  if(location.length()>0){
		  writer.close();
	  }
	  
      if(verbose){
    	  //System.out.println("ThreadID=" +ID+" cost="+RoundCost/AdTotCountRound);
      }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();


		}
	}
}
