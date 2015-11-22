package Boosting;

import frameWork.DataPreparer;
import frameWork.ModelThreadingDiskBinary;

public class Booster {
	public BoostedModel [] BoostedModels;
	public ModelTrainer [] ModelTrainers;
	
	public float [] predictions;
	public int [] ModelCount;
	public float [] predictionsValid;
	public int [] ModelCountValid;
	int modelsTrained=0;
	float eta=0.09F;
	
	//SamplesPerThread, threads,FilePath,startPoint, endPoint,dataPreparer)
	
	public int validationSamplesPerThread;
	public int validationThreads;
	public String validationFilePath;
	public int validationStartPoint;
	public int validationEndPoint;
	public DataPreparer validationDataPreparer;
	
	public ModelThreadingDiskBinary ModelThreading;
	
	public void init(int sampleCount,int sampleCountValid){
		predictions=new float[sampleCount];
		ModelCount=new int[sampleCount];
		predictionsValid=new float[sampleCountValid];
		ModelCountValid=new int[sampleCountValid];
	}
	
	public float GetLatestPrediction(int sample,String Features){
		for(int i=ModelCount[sample];i<modelsTrained;i++){
			predictions[sample]+=BoostedModels[i].predict(Features);
			ModelCount[sample]++;
		}
		return predictions[sample];
	}
	
	public float GetLatestPredictionValid(int sample,String Features){
		for(int i=ModelCountValid[sample];i<modelsTrained;i++){
			predictionsValid[sample]+=BoostedModels[i].predict(Features);
			ModelCountValid[sample]++;
		}
		return predictionsValid[sample];
	}
	
	public float GetLatestPrediction(int sample,int [] Features, int [] Positions){
		for(int i=ModelCount[sample];i<modelsTrained;i++){
			predictions[sample]+=BoostedModels[i].predict(Features,Positions)*eta;
			ModelCount[sample]++;
		}
		return predictions[sample];
	}
	
	public float GetLatestPredictionValid(int sample,int [] Features, int [] Positions){
		for(int i=ModelCountValid[sample];i<modelsTrained;i++){
			predictionsValid[sample]+=BoostedModels[i].predict(Features,Positions)*eta;
			ModelCountValid[sample]++;
		}
		return predictionsValid[sample];
	}
	
	public float GetLatestPredictionClean(int sample,int [] Features, int [] Positions){
		for(int i=0;i<modelsTrained;i++){
			predictionsValid[sample]+=BoostedModels[i].predict(Features,Positions)*eta;
			ModelCountValid[sample]++;
		}
		return predictionsValid[sample];
	}
	
	public void train(){
		ModelThreading.verbose=true;
		ModelThreading.booster=this;
		ModelThreading.validation=true;;
		for(int i=0;i<BoostedModels.length;i++){
			double startTime = System.currentTimeMillis();
			ModelTrainers[i].booster=this;
			BoostedModels[i].model.Init();
			ModelTrainers[i].TrainModel(BoostedModels[i]);
			modelsTrained++;
			double endTime = System.currentTimeMillis();
			double secondsTaken=(double) ((endTime - startTime)/1000);
			System.out.println("done training model:"+i+" time taken="+secondsTaken);
			try {
				if(ModelThreading!=null){
					startTime = System.currentTimeMillis();
					//System.out.print(validationStartPoint);
					ModelThreading.subSample=1;
					ModelThreading.train(null, validationSamplesPerThread, validationThreads,validationFilePath,validationStartPoint, validationEndPoint,validationDataPreparer);
					endTime = System.currentTimeMillis();
					secondsTaken=(double) ((endTime - startTime)/1000);
					System.out.println("done predicting model:"+i+" time taken="+secondsTaken);
				}
			} catch (InterruptedException e) {
				System.out.println("error validating");
				e.printStackTrace();
			}

			BoostedModels[i].model.Cleanup();
		}
	}	
}
