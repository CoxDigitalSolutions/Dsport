package models;

import frameWork.UtilMath;

public class LogisticRegression extends BaseModel implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private float[] inputLayer;
	
	public float predict(int [] FeatureVector){
		float prediction=predictInputLayer(FeatureVector);
		
		return UtilMath.sigmoid(prediction);
	}
	
	public float  Train(float RealValue, float [] FeatureVector, int weight){
		float prediction=predictInputLayer(FeatureVector);
		
		prediction=UtilMath.sigmoid(prediction);
		
		float Error=prediction-RealValue;
		
		updateInputLayer(FeatureVector,Error,weight);

		return prediction;
	}

	public float  Train(float RealValue, float learningRate, int [] FeatureVector, int weight){
		float prediction=predictInputLayer(FeatureVector);
		
		prediction=UtilMath.sigmoid(prediction);
		
		float Error=prediction-RealValue;
		
		updateInputLayer(FeatureVector,Error,weight);

		return prediction;
	}

	
	private void updateInputLayer(int[] FeatureVector, float Error, float weight){
		//inputLayer[0]-=learningRate*Error*weight;
		for(int j=0;j<FeatureVector.length;j++){
			inputLayer[FeatureVector[j]+1]-=learningRate*Error*weight;
		}
	}
	
	private void updateInputLayer(float[] FeatureVector, float Error, float weight){
		//inputLayer[0]-=learningRate*Error*weight;
		for(int j=1;j<inputLayer.length;j++){
			inputLayer[j]-=learningRate*Error*weight*FeatureVector[j-1];
		}
	}

	private float predictInputLayer(int [] FeatureVector){
		float Result=inputLayer[0];
		for(int j=0;j<FeatureVector.length;j++){
			Result+=inputLayer[FeatureVector[j]+1];
		}
		return Result;
	}
	
	private float predictInputLayer(float [] FeatureVector){
		float Result=inputLayer[0];
		for(int j=1;j<inputLayer.length;j++){
			//System.out.println(j+"="+inputLayer[j]);
			Result+=inputLayer[j]*FeatureVector[j-1];
		}
		return Result;
	}


	public void createInputLayer(int inputNodes){
		inputLayer=new float[inputNodes+1];
		for(int i=0;i<inputLayer.length;i++){
				inputLayer[i]=-4.5F;
		}
	}

	@Override
	public float Train(float RealValue, int[] FeatureVector, int weight) {
		// TODO Auto-generated method stub
		return 0;
	}


}