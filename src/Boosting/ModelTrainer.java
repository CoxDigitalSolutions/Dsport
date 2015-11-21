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
	public int Diff=0;
	int [] StartingPositions;
	
	
	public Booster booster;
	ModelThreadingDiskBinary ModelThreading=new ModelThreadingDiskBinary();
	
	
	public void TrainModel(BoostedModel boostedModel){
		
		boostedModel.dataPreparer.setUsedFeatures(boostedModel.UsedFeatures);
		for(int i=0;i<Rounds || ModelDecidedEnd;i++){
			double startTime = System.currentTimeMillis();
    		try {
    			ModelThreading.seed=boostedModel.seed;
    			ModelThreading.subSample=boostedModel.subSample;
				ModelThreading.train(boostedModel.model, SamplesPerThread, threads,FilePath,startPoint, endPoint,boostedModel.dataPreparer);
			} catch (InterruptedException e) {
				System.out.println("failed training model");
				e.printStackTrace();
			}

    		int result=boostedModel.model.StopCalcuations();
    		double endTime = System.currentTimeMillis();
			double secondsTaken=(double) ((endTime - startTime)/1000);
			//System.out.println("round:"+i+" time taken="+secondsTaken);
    		if(result==-1){
    			break;
    		}
		}
	}
	
	public void SetPosition(DataPreparer dataPreparer){
		ModelThreading.SetPositions(SamplesPerThread, threads,FilePath,startPoint, endPoint,dataPreparer);
	}
	public void Init(){
		ModelThreading.verbose=false;
		ModelThreading.booster=booster;
		ModelThreading.Diff=Diff;
	}

}
