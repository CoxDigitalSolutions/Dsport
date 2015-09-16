package models;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import frameWork.Random;
import frameWork.UtilMath;

public class FieldAwereFactorizationMachine extends BaseModel implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public float[][][] Factors;
	private float[][][] FactorsGrad;
	public int [] fields;
	int factorCount=0;
	int fieldCount=0;
	public float lambda=0.0F;
	public Random random = new Random();
	public float L1=0;
	public float L2=0;


	public float predict(int [] FeatureVector){
		
		int[] FieldVector=new int[FeatureVector.length];
		
		for(int i=0;i<FieldVector.length;i++){
			FieldVector[i]=fields[FeatureVector[i]];
		}

		float prediction=predictFactors(FeatureVector,FieldVector);
		
		return CostFunction.Activation(prediction);
	}
	
	
	public float  Train(float RealValue, int [] FeatureVector, int weight){
		int [] feildSum=new int[fieldCount];
		int[] FieldVector=new int[FeatureVector.length];
		for(int i=0;i<FieldVector.length;i++){
			FieldVector[i]=fields[FeatureVector[i]];
			feildSum[fields[FeatureVector[i]]]++;
		}

		
		float prediction=predictFactors(FeatureVector,FieldVector);

		prediction=CostFunction.Activation(prediction);
		
		float Error=CostFunction.CalculateError(prediction, RealValue);
		
		Update(FeatureVector,FieldVector,Error,feildSum);
		
		return prediction;
	}

	private void Update(int[] FeatureVector, int [] FieldVector,float Error,int [] feildSum){
		for(int i=0;i<FeatureVector.length;i++){
			for(int f=i+1;f<FeatureVector.length;f++){
				if(FieldVector[f]==FieldVector[i]){
					continue;
				}
				float [] F1=Factors[FeatureVector[i]][FieldVector[f]];
				float [] F2=Factors[FeatureVector[f]][FieldVector[i]];
				float [] F1g=FactorsGrad[FeatureVector[i]][FieldVector[f]];
				float [] F2g=FactorsGrad[FeatureVector[f]][FieldVector[i]];
				
				float F1count=feildSum[FieldVector[f]];
				float F2count=feildSum[FieldVector[i]];

				for(int j=0;j<factorCount;j++){
					
					float T1=F1[j];
					float T2=F2[j];
					float sign1=1;
					if(T1<0){
						sign1=-1;
					}
					float sign2=1;
					if(T2<0){
						sign2=-1;
					}
					float g1= Error*T2+L1*T1+L2*T1*T1*sign1;
					float g2= Error*T1+L1*T2+L2*T2*T2*sign2;
					
					float T1g=F1g[j]+g1*g1;
					float T2g=F2g[j]+g2*g2;
					F1g[j]=T1g;
					F2g[j]=T2g;

					F1[j]=T1-UtilMath.rsqrt(T1g)*g1*learningRate/F1count;
					F2[j]=T2-UtilMath.rsqrt(T2g)*g2*learningRate/F2count;
				}
			}
		}
	}
	
	private float predictFactors(int [] FeatureVector, int [] FieldVector){
		float Result=0;
		for(int i=0;i<FeatureVector.length;i++){
			for(int f=i+1;f<FeatureVector.length;f++){
				if(FieldVector[f]==FieldVector[i]){
					continue;
				}
				float [] F1=Factors[FeatureVector[i]][FieldVector[f]];
				float [] F2=Factors[FeatureVector[f]][FieldVector[i]];
				float v=2*1*1;
				for(int j=0;j<factorCount;j++){
					float U=F1[j]*F2[j]*v;
					Result+=U;
				}
			}
		}
		return Result;
	}

	public void createFactors(int inputNodes,int fields,int factors){
		factorCount=factors;
		Factors=new float[inputNodes][fields][factorCount];
		FactorsGrad=new float[inputNodes][fields][factorCount];
		for(int i=0;i<Factors.length;i++){
			for(int j=0;j<Factors[i].length;j++){
				for(int f=0;f<Factors[i][j].length;f++){
					Factors[i][j][f]=(float) (UtilMath.random()*0.01);
					FactorsGrad[i][j][f]=1;
				}
			}
		}
	}
	
	public void createFields(int[] Fields,int inputNodes){
		fields=new int[inputNodes];
		fieldCount=Fields.length;
		int j=0;
		int sum=Fields[0];
		for(int i=0;i<inputNodes;i++){
			if(j+1<Fields.length && sum<=i){
				sum+=Fields[j+1];
				j++;
			}
			fields[i]=j;
		}
	}
	
	public void printWeights(String filename){
		try {
			PrintWriter writer = new PrintWriter(filename);


			for(int i=0;i<Factors.length;i++){
				String weight="";
				for(int j=0;j<Factors[i].length;j++){
					for(int f=0;f<Factors[i][j].length;f++){
						weight+=Factors[i][j][f]+",";
						
					}
					
				}
				weight=weight.substring(0,weight.length()-1);
				weight+="\n";
				writer.write(weight);
				//System.out.print(weight);
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}