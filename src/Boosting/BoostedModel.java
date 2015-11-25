package Boosting;

import frameWork.BaseTargetTransform;
import frameWork.DataPreparer;
import frameWork.TargetTransform;
import models.BaseModel;

public class BoostedModel {
	public BaseModel model;
	public DataPreparer DataPreparer;
	public int seed=1;
	public int [] UsedFeatures;
	public float subSample=1;
	public float eta=1;
	public TargetTransform targetTransform=new BaseTargetTransform();
	
	public float predict(String Features){
		String [] values=Features.split(":");
		int [] tempLFV=DataPreparer.GetFeatures(values[0]);
		
		float Result=model.predict(tempLFV);
		Result=targetTransform.RevertTarget(Result);
		return Result;
	}
	
	public float predict(int [] Features, int [] Positions, float residual,float eta){

		int [] tempLFV=DataPreparer.GetFeaturesFromInt(Features,Positions,UsedFeatures);
		float Result=model.predict(tempLFV)*eta;

		Result+=targetTransform.TransformTarget(residual);
		
		Result=targetTransform.RevertTarget(Result);
		return Result-residual;
	}
	
	
}
