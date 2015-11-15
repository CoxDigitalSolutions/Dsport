package models;

import frameWork.UtilMath;


public class FactorizationMachine extends BaseModel implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private float[] inputLayer;
	private float[][] Factors;
	int factorCount=0;
	
	public float predict(int [] FeatureVector){
		float prediction=predictInputLayer(FeatureVector);
		
		FactorResult FactorResult=predictFactors(FeatureVector);
		
		prediction+=FactorResult.prediction;
		
		return CostFunction.Activation(prediction);
	}
	
	public float  Train(float RealValue, int [] FeatureVector){
		float prediction=predictInputLayer(FeatureVector);
		
		FactorResult FactorResult=predictFactors(FeatureVector);
		
		prediction+=FactorResult.prediction;
		
		prediction=CostFunction.Activation(prediction);
		
		float Error=CostFunction.CalculateError(prediction, RealValue);
		
		updateInputLayer(FeatureVector,Error);
		updateFactors(FeatureVector,Error,FactorResult.sum);

		return prediction;
	}

	
	private void updateInputLayer(int[] FeatureVector, float Error){
		inputLayer[0]-=learningRate*Error;
		for(int j=0;j<FeatureVector.length;j++){
			inputLayer[FeatureVector[j]+1]-=learningRate*Error;
		}
	}
	
	private void updateFactors(int[] FeatureVector, float Error,  float [] sum){
		for(int j=0;j<factorCount;j++){
			for(int i=0;i<FeatureVector.length;i++){
				float grad=sum[j] * FeatureVector[i] - Factors[FeatureVector[i]][j] * FeatureVector[i] * FeatureVector[i];
				Factors[FeatureVector[i]][j]-=learningRate * Error * grad ;
			}
		}
	}

	private float predictInputLayer(int [] FeatureVector){
		float Result=inputLayer[0];
		for(int j=0;j<FeatureVector.length;j++){
			Result+=inputLayer[FeatureVector[j]+1];
		}
		return Result;
	}
	
	private FactorResult predictFactors(int [] FeatureVector){
		FactorResult FactorResult = new FactorResult();
		float Result=0;
		float [] sum=new float[factorCount];
		float [] sum_sqr=new float[factorCount];
		
		for(int j=0;j<factorCount;j++){
			sum[j]=0;
			sum_sqr[j]=0;
			for(int i=0;i<FeatureVector.length;i++){
				float d=Factors[FeatureVector[i]][j]*FeatureVector[i];
				sum[j]+=d;
				sum_sqr[j]+=d*d;
			}
			Result+=0.5 * ((sum[j] * sum[j]) - sum_sqr[j]);
		}
		FactorResult.sum=sum;
		FactorResult.prediction=Result;
		return FactorResult;
	}


	public void createInputLayer(int inputNodes){
		inputLayer=new float[inputNodes+1];
		for(int i=0;i<inputLayer.length;i++){
				inputLayer[i]=UtilMath.random()*0.00001F;
		}
	}
	
	public void createFactors(int inputNodes,int factors){
		Factors=new float[inputNodes][factors];
		factorCount=factors;
		for(int i=0;i<Factors.length;i++){
			for(int j=0;j<Factors[i].length;j++){
				Factors[i][j]=UtilMath.random()*0.00001F;
			}
		}
	}
	
	private class FactorResult{
		float prediction;
		float[] sum;
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

	@Override
	public void Init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Cleanup() {
		// TODO Auto-generated method stub
		
	}
}