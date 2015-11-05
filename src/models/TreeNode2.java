package models;

import frameWork.DataPreparer;

public class TreeNode2 implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int [][] CategoricalFeatures;
	
	float prediction;
	float residual;
	int [] distribution;
	int topFeature=-1;
	int count=0;
	
	boolean leafNode=true;
	boolean FinalLeft=false;
	boolean FinalRight=false;
	boolean CompletedLeft=false;
	boolean CompletedRight=false;

	TreeNode2 leftChild;
	TreeNode2 rightChild;
	int minLeafCount=500;
	
	public DataPreparer dataPreparer;
	
	public void InitCategorical(int inputNodes, int splits){
		CategoricalFeatures= new int[inputNodes][splits];
		distribution=new int[splits];
		residual=0.0F;
	}
	
	public void InsertCatFeature(int ID, int sValue){
		CategoricalFeatures[ID][sValue]++;
		
	}
	
	public float predict(int [] FeatureVector){
		if(leafNode){
			return prediction;

		}
		
		boolean left=false;
		for(int i=0;i<FeatureVector.length;i++){
			if(FeatureVector[i]==topFeature){
				left=true;
				break;
			}
		}
		if(left){
			if(FinalLeft){
				return prediction;
			}else{
				return leftChild.predict(FeatureVector);
			}
		}else{
			if(FinalRight){
				return prediction;
			}else{
				return rightChild.predict(FeatureVector);
			}
		}
	}
	
	public int CalculateCost(){
		if(!leafNode){
			int LeftResult=leftChild.CalculateCost();
			if(LeftResult==-1){
				FinalLeft=true;
				CompletedLeft=true;
			}else if(LeftResult==-2){
				CompletedLeft=true;
			}
			int RightResult=rightChild.CalculateCost();
			if(RightResult==-1){
				FinalRight=true;
				CompletedRight=true;
			}else if(RightResult==-2){
				CompletedRight=true;
			}

			if(CompletedRight && CompletedLeft){
				return -2;
			}
			
			return 0;
		}
		
		float gloablG=0;
		float gloablH=0;
		for(int i=0;i<distribution.length;i++){
			float value=dataPreparer.TargetSummary.PositionToValue(i);
			float count=distribution[i];
			gloablG+=calculateG(value)*count;
			gloablH+=calculateH(value)*count;
		}

		prediction=(-gloablG/gloablH);
		//System.out.println("prediction=" + (-gloablG/gloablH));
		
		float maxGain=Float.MIN_VALUE;
		boolean ValidChild=false;
		float predictionRight=0;
		float predictionLeft=0;

		
		for(int i=0;i<CategoricalFeatures.length;i++){
			float GL=0F;
			float HL=0F;
			float GR=0F;
			float HR=0F;
			
			int countL=0;
			int countR=0;
			
			
			for(int j=0;j<CategoricalFeatures[i].length;j++){
				if(distribution[j]<minLeafCount){
					continue;
				}
				
				float value=dataPreparer.TargetSummary.PositionToValue(j);
				float Catcount=CategoricalFeatures[i][j];
				float Totalcount=distribution[j];
				GL+=calculateG(value)*Catcount;
				HL+=calculateH(value)*Catcount;
				GR+=calculateG(value)*(Totalcount-Catcount);
				HR+=calculateH(value)*(Totalcount-Catcount);
				countL+=Catcount;
				countR+=Totalcount-Catcount;
			}
			float scoreLeft=GL*GL/HL;
			float scoreRight=GR*GR/HR;
			float scoreTot=(GL+GR)*(GL+GR)/(HL+HR);
			float gain=scoreTot-scoreLeft-scoreRight;
			gain=gain*-1;

			if(gain>maxGain && countL>minLeafCount && countR>minLeafCount){
				maxGain=gain;
				topFeature=i;
				predictionRight=-GR/HR;
				predictionLeft=-GL/HL;
				ValidChild=true;
			}
			
		}
		if(!ValidChild){
			return -1;
		}

		leftChild=CreateChild();
		leftChild.prediction=predictionLeft;
		
		rightChild=CreateChild();
		rightChild.prediction=predictionRight;
		
		leafNode=false;

		return 0;
	}
	
	public void UpdateCatFeatures(int [] FeatureVector, int sValue){
		if(FinalLeft&&FinalRight){
			return;
		}
		
		if(!leafNode){
			boolean left=false;
			for(int i=0;i<FeatureVector.length;i++){
				if(FeatureVector[i]==topFeature){
					left=true;
					break;
				}
			}
			if(left){
				if(FinalLeft){
					return;
				}
				leftChild.UpdateCatFeatures(FeatureVector, sValue);
			}else{
				if(FinalRight){
					return;
				}
				rightChild.UpdateCatFeatures(FeatureVector, sValue);
			}
			return;
		}
		count++;
		distribution[sValue]++;
		for(int i=0;i<FeatureVector.length;i++){
			InsertCatFeature(FeatureVector[i],sValue);
		}
	}
	
	public float calculateG(float RealValue){
		return 2*(residual-RealValue);
	}
	
	public float calculateH(float RealValue){
		return 2;
	}
	
	public TreeNode2 CreateChild(){
		TreeNode2 Child=new TreeNode2();
		Child.InitCategorical(CategoricalFeatures.length, this.dataPreparer.TargetSummary.desSortedMap.size());
		Child.dataPreparer=this.dataPreparer;
		Child.minLeafCount=minLeafCount;

		return Child;
	}
}
