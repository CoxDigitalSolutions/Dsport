package frameWork;

import java.io.BufferedReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import Boosting.Booster;
import models.BaseModel;

public class ModelThreadingDiskBinary {
	public boolean verbose= false;
   public String location="";
   public Booster booster;
   public boolean validation=false;
	
	public double [] train(BaseModel BaseModel,int rounds,int threads, String File,int minID,int maxID,DataPreparer dataPreparer) throws InterruptedException{
		ModelThreadDiskBinary[] ModelThreads= new ModelThreadDiskBinary[threads];
		FileChannel[] inChannel =new FileChannel[threads];
		ByteBuffer[] buffer =new ByteBuffer[threads];
		InitBRBinary[] BRthread = new InitBRBinary[threads];
		//FileChannel inChannel,ByteBuffer buffer
		for(int i=0;i<threads;i++){
			int ID=i;
			BRthread[ID]=new InitBRBinary( inChannel[ID],buffer[ID],minID,maxID,ID,threads,File,dataPreparer);
			BRthread[ID].start();
		}
		for(int i=0;i<threads;i++){
			BRthread[i].join();
		}
		
		for(int i=0;i<threads;i++){
			int ID=i;
			ModelThreads[ID]=new ModelThreadDiskBinary(BaseModel,rounds,ID,threads,File,minID,maxID,BRthread[ID].inChannel,BRthread[ID].buffer,dataPreparer,verbose);
			ModelThreads[ID].location=location;
			ModelThreads[ID].booster=booster;
			ModelThreads[ID].Validation=validation;
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
}
