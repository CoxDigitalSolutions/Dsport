package Boosting;

public class Booster {
	public BoostedModel [] BoostedModels;
	public ModelTrainer [] ModelTrainers;
	
	float [] predictions;
	int [] ModelCount;
	int modelsTrained=0;
	
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
	
	public void train(){
		for(int i=0;i<BoostedModels.length;i++){
			double startTime = System.currentTimeMillis();
			ModelTrainers[i].booster=this;
			ModelTrainers[i].TrainModel(BoostedModels[i].model,BoostedModels[i].dataPreparer);
			double endTime = System.currentTimeMillis();
			double secondsTaken=(double) ((endTime - startTime)/1000);
			System.out.println("done training model:"+i+" time taken="+secondsTaken);
		}
	}	
}
