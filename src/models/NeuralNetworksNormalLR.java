package models;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import frameWork.UtilMath;

public class NeuralNetworksNormalLR extends BaseModel implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private float[][] inputLayer;
	private float[][] outputLayer;
	private float[][] GinputLayer;
	private float[][] GoutputLayer;
	private float[][] AinputLayer;
	private float[][] AoutputLayer;
	
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
		int EndNeuron=0;
		if(EndNodePosition==-1){
			EndNeuron=FeatureVector[FeatureVector.length-1]-this.OffSetEndNode;
		}else{
			EndNeuron=FeatureVector[EndNodePosition]-this.OffSetEndNode;
		}
		
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
		
		float prediction=predictOutputLayer(EndNeuron,hiddenResult);
		
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

	
	public float  Train(float RealValue, int [] FeatureVector){
		int EndNeuron=0;
		if(EndNodePosition==-1){
			EndNeuron=FeatureVector[FeatureVector.length-1]-this.OffSetEndNode;
		}else{
			EndNeuron=FeatureVector[EndNodePosition]-this.OffSetEndNode;
		}
		
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
		
		float prediction=predictOutputLayer(EndNeuron,hiddenResult);
		
		prediction=UtilMath.sigmoid(prediction);
		
		float ErrorsEnd=prediction-RealValue;
		
		float [] HiddenError=updateOutputLayer( EndNeuron, ErrorsEnd,hiddenResult);
		
		updateInputLayer(shortFeatureVector,HiddenError);

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
	private void updateInputLayer(int[] FeatureVector, float[] HiddenError){
		for(int j=0;j<FeatureVector.length;j++){
			float [] G=GinputLayer[FeatureVector[j]+1];
			float [] A=AinputLayer[FeatureVector[j]+1];
			float [] V=inputLayer[FeatureVector[j]+1];
			for(int i=0;i<inputLayer[FeatureVector[j]+1].length;i++){
				
				float Err=HiddenError[i];//+V[i]*0.0001F;

				V[i]-=learningRate*Err;
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
	private float[] updateOutputLayer(int EndNeuron,float ErrorsEnd,float [] hiddenResult){
		float [] HiddenError=new float [hiddenResult.length];
		float[] G=GoutputLayer[EndNeuron];
		float[] A=AoutputLayer[EndNeuron];
		float[] V=outputLayer[EndNeuron];
		for(int i=1;i<outputLayer[EndNeuron].length;i++){
			outputLayer[EndNeuron][i]-=learningRate*ErrorsEnd*hiddenResult[i-1]/1000;
			float H=hiddenResult[i-1];
			HiddenError[i-1]+=H*H*(1-H)*(1-H)*outputLayer[EndNeuron][i]*ErrorsEnd;
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
	private float predictOutputLayer(int EndNeuron,float[] hiddenResult){
		float prediction=outputLayer[EndNeuron][0];
		for(int i=0;i<hiddenResult.length;i++){
			prediction+=hiddenResult[i]*outputLayer[EndNeuron][i+1];
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
	public void createOutputLayer(int outputNodes, int hiddenNodes){
		outputLayer=new float[outputNodes][hiddenNodes+1];
		GoutputLayer=new float[outputNodes][hiddenNodes+1];
		AoutputLayer=new float[outputNodes][hiddenNodes+1];
		for(int i=0;i<outputLayer.length;i++){
			for(int j=0;j<outputLayer[i].length;j++){
				outputLayer[i][j]=UtilMath.random()*0.1F;
			}
		}
	}
	
	/**
	 * Prints the weights of the NN
	 *
	 *
	 * @param filename name of file to print to
	 * @return count of weights printed
	 */
	public int printWeights(String filename){
		int weightcounter=0;
		PrintWriter writer;
		try {
			writer = new PrintWriter(filename, "UTF-8");
			
			//prints the input layer
			writer.append("Layer="+0+"\n");
			for(int j=0;j<inputLayer.length;j++){
				writer.append("input="+j+"\n");
				for(int w=0;w<inputLayer[j].length;w++){
					writer.append(w+","+inputLayer[j][w]+"\n");
					weightcounter++;
				}
			}
			
			//prints the output layer
			writer.append("Layer="+1+"\n");
			for(int j=0;j<outputLayer.length;j++){
				writer.append("output="+j+"\n");
				for(int w=0;w<outputLayer[j].length;w++){
					writer.append(w+","+outputLayer[j][w]+"\n");
					weightcounter++;
				}
			}
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// this should never fail
			e.printStackTrace();
		}
		return weightcounter;
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