package Boosting;

import frameWork.DataPreparer;
import models.BaseModel;

public class BoostedModel {
	public BaseModel model;
	public DataPreparer dataPreparer;
	
	public float predict(String Features){
		String [] values=Features.split(":");
		int [] tempLFV=dataPreparer.GetFeatures(values[0]);
		
		float Result=model.predict(tempLFV);
		
		return Result;
	}
	
	public float predict(int [] Features, int [] Positions){
		int [] tempLFV=dataPreparer.GetFeaturesFromInt(Features,Positions);

		float Result=model.predict(tempLFV);
		return Result;
	}
}
