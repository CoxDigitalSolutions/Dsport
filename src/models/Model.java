package models;

public interface Model {
	float predict(int [] FeatureVector);
	float Train(float RealValue, int [] FeatureVector );
	float TrainBoosted(float RealValue, float residual, int [] FeatureVector );
	public int StopCalcuations();
}
