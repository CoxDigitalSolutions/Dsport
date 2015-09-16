package models;

public interface Model {
	float predict(int [] FeatureVector);
	float Train(float RealValue, int [] FeatureVector, int weight );
}
