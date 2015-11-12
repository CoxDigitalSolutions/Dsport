package Boosting;

import frameWork.DataPreparer;
import frameWork.ModelThreadingDiskBinary;

public class Booster {
	public BoostedModel [] BoostedModels;
	public ModelTrainer [] ModelTrainers;
	
	float [] predictions;
	int [] ModelCount;
	int modelsTrained=0;
	
	
	//SamplesPerThread, threads,FilePath,startPoint, endPoint,dataPreparer)
	
	public int validationSamplesPerThread;
	public int validationThreads;
	public String validationFilePath;
	public int validationStartPoint;
	public int validationEndPoint;
	public DataPreparer validationDataPreparer;
	
	public ModelThreadingDiskBinary ModelThreading;
	
	public void init(int sampleCount){
		predictions=new float[sampleCount];
		ModelCount=new int[sampleCount];
	}
	
	public float GetLatestPrediction(int sample,String Features){
		for(int i=ModelCount[sample];i<modelsTrained;i++){
			predictions[sample]+=BoostedModels[i].predict(Features);
			ModelCount[sample]++;
		}
		return predictions[sample];
	}
	
	public float GetLatestPrediction(int sample,int [] Features, int [] Positions){
		
		for(int i=ModelCount[sample];i<modelsTrained;i++){
			predictions[sample]+=BoostedModels[i].predict(Features,Positions);
			ModelCount[sample]++;
		}
		return predictions[sample];
	}
	
	public void train(){
		ModelThreading.verbose=true;
		ModelThreading.booster=this;
		ModelThreading.validation=true;;
		for(int i=0;i<BoostedModels.length;i++){
			double startTime = System.currentTimeMillis();
			ModelTrainers[i].booster=this;
			ModelTrainers[i].TrainModel(BoostedModels[i].model,BoostedModels[i].dataPreparer);
			modelsTrained++;
			double endTime = System.currentTimeMillis();
			double secondsTaken=(double) ((endTime - startTime)/1000);
			System.out.println("done training model:"+i+" time taken="+secondsTaken);
			try {
				if(ModelThreading!=null){
					startTime = System.currentTimeMillis();
					ModelThreading.train(null, validationSamplesPerThread, validationThreads,validationFilePath,validationStartPoint, validationEndPoint,validationDataPreparer);
					endTime = System.currentTimeMillis();
					secondsTaken=(double) ((endTime - startTime)/1000);
					System.out.println("done predicting model:"+i+" time taken="+secondsTaken);
				}
			} catch (InterruptedException e) {
				System.out.println("error validating");
				e.printStackTrace();
			}
		}
	}	
}
