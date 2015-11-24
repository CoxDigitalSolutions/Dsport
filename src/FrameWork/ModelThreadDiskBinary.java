package frameWork;

import java.io.FileInputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import Boosting.BoostedModel;
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
   InitBRBinary BRthread;
   private DataPreparer dataPreparer;
   boolean verbose=false;
   public String location="";
   public Booster booster;
   public boolean Validation=false;
   public int Diff=0;
   public int seed=1;
   public float subSample=1;
   public BoostedModel boostedModel;

   
   public ModelThreadDiskBinary(BaseModel BaseModel,int rounds, int ID,int TotalThreads,String File,int minID,int maxID,InitBRBinary BRthread,DataPreparer dataPreparer, boolean verbose)
   {
      this.BaseModel = BaseModel;
      this.rounds = rounds;
      this.ID=ID;
      this.RoundCost=0;
      this.TotalThreads=TotalThreads;
      this.minID=minID;
      this.maxID=maxID;
      this.File=File;
      this.inChannel=BRthread.inChannel;
      this.buffer=BRthread.buffer;
      this.dataPreparer=dataPreparer;
      this.verbose=verbose;
      this.BRthread=BRthread;
      
   }
	
	public void run(){
	  try {
		  


	  
	  int i=0;
	  float PrintTime=1.0F;
	  float temp=PrintTime;

	  
	  AdTotCountRound=0;
	 
	  int Size=maxID-minID;
	  int Pos=BRthread.StartPosition;

			  //(int) minID+(Size/TotalThreads*ID);

	  PrintWriter writer=null;
	  if(location.length()>0){
		  writer = new PrintWriter(location+"-"+ID,"UTF-8");
	  }
	  
	  rounds+=Diff;
	  
	  Random rand=new Random();
	  rand.setSeed(seed);
	  
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
	  	float RealValue=0;
	  	int sampleID=0;
	  	

	  	
	  	if(location.length()>0){
	  		sampleID=dataPreparer.GetIDBinary(inChannel,buffer);
	  	}else{

	  		RealValue=dataPreparer.GetPredictionBinary(inChannel,buffer);
	  	}
	  	
	  	float RandNum=rand.randf(0, 1, 5);
	  	if(RandNum>subSample){
		    i++;
	  		continue;
	  	}

		float result=0;
			if(location.length()>0){
				if(booster==null){
					int [] UsedFatures=dataPreparer.GetFeaturesFromInt(tempLFV[0],tempLFV[1]);
					result = BaseModel.predict(UsedFatures);
					writer.write(sampleID+","+result+"\n");
				}else{
					
					float residual=booster.GetLatestPredictionClean(Pos, tempLFV[0],tempLFV[1]);
					//float WriterResult=(float) UtilMath.exp(residual)-1;
					writer.write(sampleID+","+residual+"\n");
				}
			}else{
				if(booster==null){
					int [] UsedFatures=dataPreparer.GetFeaturesFromInt(tempLFV[0],tempLFV[1]);
					result = BaseModel.Train(RealValue ,UsedFatures);
	
				}else{
					// need to make sure Pos is correct
					if(Validation){

						result=booster.GetLatestPredictionValid(Pos, tempLFV[0],tempLFV[1]);

					}else{


						float residual=booster.GetLatestPrediction(Pos, tempLFV[0],tempLFV[1]);
						int [] UsedFatures=dataPreparer.GetFeaturesFromInt(tempLFV[0],tempLFV[1]);
						float TransformedRealValue=boostedModel.targetTransform.TransformTarget(RealValue);
						float TransformedResidual=boostedModel.targetTransform.TransformTarget(residual);

						result = BaseModel.TrainBoosted(TransformedRealValue, TransformedResidual, UsedFatures, ID);
					}
				}
			}


		float tempC=0;
		
		//result=(float) UtilMath.exp(result)-1;
		//RealValue=(float) UtilMath.exp(RealValue)-1;
		if(result>0){
			//System.out.println(result +" - "+ RealValue);
		}
		float tempNum=(result-RealValue)/RealValue;

		tempC=tempNum*tempNum;
		RoundCost+=tempC;
		

		
		AdTotCountRound++;

	     i++;
      }
      //System.out.println(ID+ " End=" + Pos + " inChannelPos="+inChannel.position()+" bufferPos="+buffer.position());

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
