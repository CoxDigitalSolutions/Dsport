package frameWork;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class InitBRBinary extends Thread{
	

   public FileChannel inChannel;
   public ByteBuffer buffer;
   public DataPreparer dataPreparer;
   private int ID;
   private int TotalThreads;
   private String File;
   private int maxID;
   private int minID;
   public int StartPosition;
   
   public InitBRBinary(FileChannel inChannel,ByteBuffer buffer,int minID,int maxID,int ID,int TotalThreads,String File,DataPreparer dataPreparer)
   {
	  this.inChannel=inChannel;
	  this.buffer=buffer;
	  this.dataPreparer=dataPreparer;
      this.ID=ID;
      this.TotalThreads=TotalThreads;
      this.maxID=maxID;
      this.File=File;
      this.minID=minID;
   }
	

	public void run(){
	  try {
	  
	  int counter=0;
	  
	  int Size=maxID-minID;

	  inChannel = new FileInputStream(File).getChannel();
	  buffer = ByteBuffer.allocateDirect(16384*2);
	  buffer.flip();
		
	  StartPosition=(int) minID+(Size/TotalThreads*ID);

	  while(counter<StartPosition ) {
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
	
	public void SetPositions(int minID,int maxID,int ID,int TotalThreads,String File,DataPreparer dataPreparer, long StartPositionsBufferinChannel, int StartPositionsBuffer){
		try {  
		  int Size=maxID-minID;

		  inChannel = new FileInputStream(File).getChannel();
		  long usedPostion=StartPositionsBufferinChannel;
		  if(StartPositionsBufferinChannel>16384*2){
			  usedPostion-=16384*2;
		  }
		  inChannel.position(usedPostion);

		  buffer = ByteBuffer.allocateDirect(16384*2);

		  inChannel.read(buffer);
		  buffer.flip();

		  buffer.position(StartPositionsBuffer);

		  StartPosition=(int) minID+(Size/TotalThreads*ID);
		}catch (Exception e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
		}
		  
	}
}
