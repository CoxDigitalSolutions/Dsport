package frameWork;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import Boosting.BoostedModel;
import Boosting.Booster;
import models.BaseModel;

public class ModelThreadingDiskBinary {
   public boolean verbose= false;
   public String location="";
   public Booster booster;
   public boolean validation=false;
   public boolean ValidTrain=false;
   public int Diff=0;
   long [] StartPositionsInChannel;
   int [] StartPositionsBuffer;
   int [] StartPositions;
	public int seed=1;
	public float subSample=1;
	public BoostedModel boostedModel;
	
	public double [] train(BaseModel BaseModel,int rounds,int threads, String File,int minID,int maxID,DataPreparer dataPreparer) throws InterruptedException{
		
		ModelThreadDiskBinary[] ModelThreads= new ModelThreadDiskBinary[threads];
		FileChannel[] inChannel =new FileChannel[threads];
		ByteBuffer[] buffer =new ByteBuffer[threads];
		InitBRBinary[] BRthread = new InitBRBinary[threads];
		//FileChannel inChannel,ByteBuffer buffer

		if(StartPositionsInChannel==null){
			for(int i=0;i<threads;i++){
				int ID=i;
				BRthread[ID]=new InitBRBinary( inChannel[ID],buffer[ID],minID,maxID,ID,threads,File,dataPreparer);
				BRthread[ID].start();
			}
			
			for(int i=0;i<threads;i++){
				BRthread[i].join();
			}
		}else{

			for(int i=0;i<threads;i++){
				int ID=i;
				BRthread[ID]=new InitBRBinary( inChannel[ID],buffer[ID],minID,maxID,ID,threads,File,dataPreparer);
				BRthread[ID].StartPosition=StartPositions[ID];
				BRthread[ID].SetPositions(minID,maxID,ID,threads,File,dataPreparer, StartPositionsInChannel[ID],StartPositionsBuffer[ID]);
			}
		}
		
		for(int i=0;i<threads;i++){
			int ID=i;

			ModelThreads[ID]=new ModelThreadDiskBinary(BaseModel,rounds,ID,threads,File,minID,maxID,BRthread[ID],dataPreparer,verbose);
			ModelThreads[ID].location=location;
			ModelThreads[ID].booster=booster;
			ModelThreads[ID].Validation=validation;
			ModelThreads[ID].ValidTrain=ValidTrain;
			ModelThreads[ID].seed=seed;
			ModelThreads[ID].subSample=subSample;
			ModelThreads[ID].boostedModel=boostedModel;
			if(i==threads-1){
				ModelThreads[ID].Diff=Diff;
			}
			ModelThreads[ID].start();
		}
		
		double count=0;
		double cost=0;

		for(int i=0;i<threads;i++){
			int ID=i;
			ModelThreads[ID].join();
			cost+=ModelThreads[ID].RoundCost;
			count+=ModelThreads[ID].AdTotCountRound;
		}
		
		if(verbose){
			System.out.println("Threads completed: cost="+Math.sqrt(cost/count));
		}
		double [] Result=new double[2];
		Result[0]=cost;
		Result[1]=count;
		

		
		return Result;
	}
	
	public void SetPositions(int rounds,int threads, String File,int minID,int maxID,DataPreparer dataPreparer){
		  try {
			  
			  int counter=0;
			  
			  int Size=maxID-minID;
			  StartPositionsInChannel=new long[threads];
			  StartPositionsBuffer=new int[threads];
			  StartPositions=new int [threads];
			  
			  FileChannel inChannel = new FileInputStream(File).getChannel();
			  ByteBuffer buffer = ByteBuffer.allocateDirect(16384*2);
			  buffer.flip();
				
			  int StartPosition=(int) minID;//+(Size/threads);
			  int currPos=0;
			  StartPositions[currPos]=minID;

			  
			  while(currPos<StartPositionsBuffer.length) {
				  if(counter==StartPosition){
					  StartPositionsInChannel[currPos]=inChannel.position();
					  StartPositionsBuffer[currPos]=buffer.position();
					  StartPositions[currPos]=counter;

					  currPos++;
					  StartPosition+=Size/threads;
				  }
				  if(dataPreparer.GetFeatures(inChannel, buffer)==null){
					  
					  System.out.println("ran out of Samples in InitBRBinary");
				  }else{
					  dataPreparer.GetPredictionBinary(inChannel, buffer);
					  //dataPreparer.GetIDBinary(inChannel, buffer);
				  }
				  
				  counter++;
			  }

			  }catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

}
