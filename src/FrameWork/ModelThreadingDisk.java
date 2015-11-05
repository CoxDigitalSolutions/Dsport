package frameWork;

import java.io.BufferedReader;

import models.BaseModel;

public class ModelThreadingDisk {
	public boolean verbose= false;
	   public String location="";
	
	public void train(BaseModel BaseModel,int rounds,int threads, String File,int minID,int maxID,DataPreparer dataPreparer) throws InterruptedException{
		ModelThreadDisk[] ModelThreads= new ModelThreadDisk[threads];
		BufferedReader[] br =new BufferedReader[threads];
		InitBR[] BRthread = new InitBR[threads];
		for(int i=0;i<threads;i++){
			int ID=i;
			BRthread[ID]=new InitBR( br[ID],minID,maxID,ID,threads,File);
			BRthread[ID].start();
		}
		for(int i=0;i<threads;i++){
			BRthread[i].join();
		}
		
		for(int i=0;i<threads;i++){
			int ID=i;
			ModelThreads[ID]=new ModelThreadDisk(BaseModel,rounds,ID,threads,File,minID,maxID,BRthread[ID].br,dataPreparer,verbose);
			ModelThreads[ID].location=location;
			ModelThreads[ID].start();
		}
		
		int count=0;
		float cost=0;
		for(int i=0;i<threads;i++){
			int ID=i;
			ModelThreads[ID].join();
			cost+=ModelThreads[ID].RoundCost;
			count+=ModelThreads[ID].AdTotCountRound;
		}
		//System.out.println("Threads completed: cost="+cost/count);
	}
}
