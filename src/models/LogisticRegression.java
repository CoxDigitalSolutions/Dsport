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
	
	public float  Train(float RealValue, float [] FeatureVector){
		float prediction=predictInputLayer(FeatureVector);
		
		prediction=UtilMath.sigmoid(prediction);
		
		float Error=prediction-RealValue;
		
		updateInputLayer(FeatureVector,Error);

		return prediction;
	}

	public float  Train(float RealValue, float learningRate, int [] FeatureVector){
		float prediction=predictInputLayer(FeatureVector);
		
		prediction=UtilMath.sigmoid(prediction);
		
		float Error=prediction-RealValue;
		
		updateInputLayer(FeatureVector,Error);

		return prediction;
	}

	
	private void updateInputLayer(int[] FeatureVector, float Error){
		//inputLayer[0]-=learningRate*Error*weight;
		for(int j=0;j<FeatureVector.length;j++){
			inputLayer[FeatureVector[j]+1]-=learningRate*Error;
		}
	}
	
	private void updateInputLayer(float[] FeatureVector, float Error){
		//inputLayer[0]-=learningRate*Error*weight;
		for(int j=1;j<inputLayer.length;j++){
			inputLayer[j]-=learningRate*Error*FeatureVector[j-1];
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
	public float Train(float RealValue, int[] FeatureVector) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int StopCalcuations() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float TrainBoosted(float RealValue, float residual, int[] FeatureVector) {
		// TODO Auto-generated method stub
		return 0;
	}


}