package Boosting;

import frameWork.DataPreparer;
import frameWork.ModelThreadingDisk;
import frameWork.ModelThreadingDiskBinary;
import models.BaseModel;

public class ModelTrainer {
	public int threads;
	public int SamplesPerThread;
	public int startPoint;
	public int endPoint;
	public int Rounds;
	public boolean ModelDecidedEnd=false;
	public String FilePath;
	
	public Booster booster;
	ModelThreadingDiskBinary ModelThreading=new ModelThreadingDiskBinary();
	
	
	public void TrainModel(BaseModel model,DataPreparer dataPreparer){
		ModelThreading.verbose=false;
		ModelThreading.booster=booster;
		for(int i=0;i<Rounds || ModelDecidedEnd;i++){
			double startTime = System.currentTimeMillis();
    		try {
				ModelThreading.train(model, SamplesPerThread, threads,FilePath,startPoint, endPoint,dataPreparer);
			} catch (InterruptedException e) {
				System.out.println("failed training model");
				e.printStackTrace();
			}

    		int result=model.StopCalcuations();
    		double endTime = System.currentTimeMillis();
			double secondsTaken=(double) ((endTime - startTime)/1000);
			//System.out.println("round:"+i+" time taken="+secondsTaken);
    		if(result==-1){
    			break;
    		}
		}
	}

}
