package FrameWork;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class InitBR extends Thread{
	

   public BufferedReader br;
   private int ID;
   private int TotalThreads;
   private String File;
   private int maxID;
   private int minID;
   
   public InitBR(BufferedReader br,int minID,int maxID,int ID,int TotalThreads,String File)
   {
	  this.br=br;
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
	  //br = new BufferedReader(new FileReader(File));
	  br = new BufferedReader(
			   new InputStreamReader(
	                   new FileInputStream(File), "UTF8"));
	  int StartPosition=(int) minID+(Size/TotalThreads*ID);
	  while(counter<StartPosition && (br.readLine()) != null) {
		  counter++;
	  }

	  }catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
