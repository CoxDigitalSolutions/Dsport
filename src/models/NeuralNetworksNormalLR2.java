package models;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import frameWork.UtilMath;

public class NeuralNetworksNormalLR2 extends BaseModel implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private float[][] inputLayer;
	private float[] outputLayer;
	private float[][] GinputLayer;
	private float[] GoutputLayer;
	private float[][] AinputLayer;
	private float[] AoutputLayer;
	
	private float decayRate=0.95F;
	private float constantAdded =(float) Math.pow(Math.E, -30);
	public int OffSetEndNode=0;
	public int EndNodePosition=-1;
	public int PosAdjustment=0;
	
	/**
	 * Predict 1 sample
	 *
	 *
	 * @param FeatureVector array of active input nodes
	 * @param EndNeuron which end neuron to be used
	 * @return prediction the predicted CTR
	 */
	public float predict(int [] FeatureVector){

		
		float[] hiddenResult=predictInputLayer(FeatureVector);
		
		float prediction=predictOutputLayer(hiddenResult);
		
		prediction=UtilMath.sigmoid(prediction);
		
		return prediction;
	}

	/**
	 * Trains 1 sample
	 *
	 *
	 * @param RealValue the actual value for this sample
	 * @param FeatureVector array of active input nodes
	 * @param weight the weight of this sample
	 * @param EndNeuron which end neuron to be used
	 * @return prediction the predicted CTR
	 */

	
	public float  Train(float RealValue, int [] FeatureVector, int weight){

		
		float[] hiddenResult=predictInputLayer(FeatureVector);
		
		float prediction=predictOutputLayer(hiddenResult);
		
		prediction=UtilMath.sigmoid(prediction);
		
		float ErrorsEnd=prediction-RealValue;
		
		float [] HiddenError=updateOutputLayer(  weight, ErrorsEnd,hiddenResult);
		
		updateInputLayer(FeatureVector,HiddenError,weight);

		return prediction;
	}
	
	public float[]  GetHiddenValues(int [] FeatureVector){
		
		int [] shortFeatureVector=new int[FeatureVector.length-1];
		int count=0;
		for(int i=0;i<FeatureVector.length;i++){
			if(i==EndNodePosition){
				continue;
			}
			if(i>EndNodePosition){
				shortFeatureVector[count]=FeatureVector[i]-this.PosAdjustment;
			}else{
				shortFeatureVector[count]=FeatureVector[i];
			}
			count++;
		}
		
		float[] hiddenResult=predictInputLayer(shortFeatureVector);
		return hiddenResult;
	}
	

	/**
	 * Update input layer
	 *
	 *
	 * @param FeatureVector array of active input nodes
	 * @param HiddenError the error in the hidden layer
	 * @param weight to decide the weighting of the sample
	 */
	private void updateInputLayer(int[] FeatureVector, float[] HiddenError, float weight){
		for(int j=0;j<FeatureVector.length;j++){
			float [] G=GinputLayer[FeatureVector[j]+1];
			float [] A=AinputLayer[FeatureVector[j]+1];
			float [] V=inputLayer[FeatureVector[j]+1];
			for(int i=0;i<inputLayer[FeatureVector[j]+1].length;i++){
				
				float Err=HiddenError[i];//+V[i]*0.0001F;

				V[i]-=learningRate*Err*weight;
			}
		}
	}

	/**
	 * Update output layer
	 *
	 *
	 * @param EndNeuron which end node to use
	 * @param weight to decide the weighting of the sample
	 * @param ErrorsEnd the error at the end node
	 * @param hiddenResult results in the hidden layer
	 * @return Errors in the hidden layer
	 */
	private float[] updateOutputLayer( float weight,float ErrorsEnd,float [] hiddenResult){
		float [] HiddenError=new float [hiddenResult.length];
		float[] G=GoutputLayer;
		float[] A=AoutputLayer;
		float[] V=outputLayer;
		for(int i=1;i<outputLayer.length;i++){
			outputLayer[i]-=learningRate*ErrorsEnd*hiddenResult[i-1]*weight/1000;
			float H=hiddenResult[i-1];
			HiddenError[i-1]+=H*H*(1-H)*(1-H)*outputLayer[i]*ErrorsEnd;
		}
		
		return HiddenError;
	}

	/**
	 * Predict output layer
	 *
	 *
	 * @param EndNeuron which end node to use
	 * @param hiddenResult results in the hidden layer
	 * @return Predicted CTR
	 */
	private float predictOutputLayer(float[] hiddenResult){
		float prediction=outputLayer[0];
		for(int i=0;i<hiddenResult.length;i++){
			prediction+=hiddenResult[i]*outputLayer[i+1];
		}
		
		return prediction;
	}

	/**
	 * Predict input layer
	 *
	 *
	 * @param FeatureVector array of active input nodes
	 * @return result prediction of hidden nodes
	 */
	private float[] predictInputLayer(int [] FeatureVector){
		float[] hiddenResult=new float[inputLayer[0].length];
		float[] BiasLayer=inputLayer[0];
		for(int i=0;i<hiddenResult.length;i++){
			hiddenResult[i]=BiasLayer[i];
		}
		for(int j=0;j<FeatureVector.length;j++){
			float [] ThisLayer=inputLayer[FeatureVector[j]+1];
			for(int i=0;i<inputLayer[FeatureVector[j]+1].length;i++){
				hiddenResult[i]+=ThisLayer[i];
			}
		}
		for(int i=0;i<hiddenResult.length;i++){
			hiddenResult[i]=UtilMath.sigmoid(hiddenResult[i]);
		}

		return hiddenResult;
	}

	/**
	 * Create input layer
	 *
	 *
	 * @param inputNodes the number of nodes in the input layer
	 * @param hiddenNodes the number of nodes in the hidden layer
	 */
	public void createInputLayer(int inputNodes, int hiddenNodes){
		inputLayer=new float[inputNodes+1][hiddenNodes];
		GinputLayer=new float[inputNodes+1][hiddenNodes];
		AinputLayer=new float[inputNodes+1][hiddenNodes];
		for(int i=0;i<inputLayer.length;i++){
			for(int j=0;j<inputLayer[i].length;j++){
				inputLayer[i][j]=UtilMath.random()*0.1F;
			}
		}
	}
	
	/**
	 * Creates the output layer of the NN
	 *
	 *
	 * @param outputNodes the number of nodes in the output layer
	 * @param hiddenNodes the number of nodes in the hidden layer
	 */
	public void createOutputLayer(int hiddenNodes){
		outputLayer=new float[hiddenNodes+1];
		GoutputLayer=new float[hiddenNodes+1];
		AoutputLayer=new float[hiddenNodes+1];
		for(int i=0;i<outputLayer.length;i++){
				outputLayer[i]=UtilMath.random()*0.1F;
		}
	}
	
	

}