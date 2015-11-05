package models;

import frameWork.DataPreparer;

public class TreeNode implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	float [] CategoricalG;
	float [] CategoricalH;
	int  [] CategoricalCount;
	
	float  GlobalG=0;
	float  GlobalH=0;
	int GlobalCount=0;;
	
	float prediction;


	int topFeature=-1;
	int count=0;
	
	boolean leafNode=true;
	boolean FinalLeft=false;
	boolean FinalRight=false;
	boolean CompletedLeft=false;
	boolean CompletedRight=false;

	TreeNode leftChild;
	TreeNode rightChild;
	int minLeafCount=1;
	
	public DataPreparer dataPreparer;
	int [] fields;
	
	public void InitCategorical(int inputNodes, int splits){
		CategoricalG = new float[inputNodes];
		CategoricalH = new float[inputNodes];
		CategoricalCount = new int[inputNodes];

	}
	
	public void InsertCatFeature(int ID, float RealValue,float residual){
		CategoricalG[ID]+=calculateG(RealValue,residual);
		CategoricalH[ID]+=calculateH(RealValue,residual);
		CategoricalCount[ID]++;
		
	}
	
	public float predict(int [] FeatureVector){
		//System.out.println("prediction");
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
			return leftChild.predict(FeatureVector);
		}else{
			return rightChild.predict(FeatureVector);
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
		



		prediction=(-GlobalG/GlobalH);

		float maxGain=Float.MIN_VALUE;
		boolean ValidChild=false;
		float predictionRight=0;
		float predictionLeft=0;
		float countLF=0;
		
		for(int i=0;i<fields.length;i++){
			System.out.println(i+"="+fields[i]);
		}
		
		for(int i=0;i<CategoricalG.length;i++){
			float GL=CategoricalG[i];
			float HL=CategoricalH[i];
			float GR=GlobalG-CategoricalG[i];
			float HR=GlobalH-CategoricalH[i];
			
			int countL=CategoricalCount[i];
			int countR=GlobalCount-CategoricalCount[i];
			
			
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
				countLF=countL;
			}
			
		}
		
		/*
		System.out.println("prediction=" + (-GlobalG/GlobalH));
		System.out.println("GlobalG=" + GlobalG);
		System.out.println("GlobalH=" + GlobalH);
		System.out.println("count=" + GlobalCount);
		System.out.println(GlobalCount);
		System.out.println("topFeature=" + topFeature);
		*/
		if(!ValidChild){
			return -1;
		}
		

		
		
		/*
		System.out.println("maxGain="+maxGain);
		System.out.println("topFeature="+topFeature);
		System.out.println("countLF="+countLF);
		 */
		
		


		leftChild=CreateChild();
		leftChild.prediction=predictionLeft;
		
		rightChild=CreateChild();
		rightChild.prediction=predictionRight;
		
		leafNode=false;

		return 0;
	}
	
	public void UpdateCatFeatures(int [] FeatureVector, float RealValue,float residual){
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
				leftChild.UpdateCatFeatures(FeatureVector, RealValue,residual);
			}else{
				if(FinalRight){
					return;
				}
				rightChild.UpdateCatFeatures(FeatureVector, RealValue,residual);
			}
			return;
		}
		count++;
		GlobalG+=calculateG(RealValue,residual);
		GlobalH+=calculateH(RealValue,residual);
		GlobalCount++;
		for(int i=0;i<FeatureVector.length;i++){
			InsertCatFeature(FeatureVector[i],RealValue,residual);
		}
	}
	
	public float calculateG(float RealValue,float residual){
		return (residual-RealValue);
	}
	
	public float calculateH(float RealValue,float residual){
		return 1;
	}
	
	public TreeNode CreateChild(){
		TreeNode Child=new TreeNode();
		Child.InitCategorical(CategoricalG.length, this.dataPreparer.TargetSummary.desSortedMap.size());
		Child.dataPreparer=this.dataPreparer;
		Child.minLeafCount=minLeafCount;
		Child.fields=fields;

		return Child;
	}
}