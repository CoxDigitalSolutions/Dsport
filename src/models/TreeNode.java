package models;

import frameWork.DataPreparer;

public class TreeNode implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int Categories;
	float [] CategoricalG;
	float [] CategoricalH;
	int  [] CategoricalCount;
	
	float  GlobalG=0;
	float  GlobalH=0;
	int GlobalCount=0;;
	
	float prediction;


	int topFeature=-1;
	int topField=-1;

	boolean Completed=false;
	boolean leafNode=true;
	boolean FinalLeft=false;
	boolean FinalRight=false;
	boolean CompletedLeft=false;
	boolean CompletedRight=false;

	TreeNode leftChild;
	TreeNode rightChild;
	int minLeafCount=1;
	int depth=0;
	
	int [] fields;
	String direction="";
	
	public void InitCategorical(int inputNodes){
		Categories=inputNodes;
		CategoricalG = new float[Categories];
		CategoricalH = new float[Categories];
		CategoricalCount = new int[Categories];

	}
	
	public void InsertCatFeature(int ID, float RealValue,float residual){
		CategoricalG[ID]+=calculateG(RealValue,residual);
		CategoricalH[ID]+=calculateH(RealValue,residual);
		CategoricalCount[ID]++;
		
	}
	
	public float predict(int [] FeatureVector){
		if(leafNode){

			return prediction;

		}
		
		boolean left=false;
		int fieldPos=0;
		for(int i=0;i<FeatureVector.length;i++){
			int FeatureID=FeatureVector[i];
			if(!(FeatureID<fields[fieldPos])){
				fieldPos++;
			}

			if(FeatureID<=topFeature && fieldPos==topField){
				//System.out.println(FeatureID+" - "+ topFeature+" - "+ fieldPos+" - "+ topField);
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
		if(Completed){
			return -2;
		}



		prediction=(-GlobalG/GlobalH);
		//System.out.println("prediction="+prediction);

		float maxGain=Float.MIN_VALUE;
		boolean ValidChild=false;
		float predictionRight=0;
		float predictionLeft=0;
		//float countLF=0;
		
		for(int i=0;i<fields.length;i++){
			//System.out.println(i+"="+fields[i]);
		}

		int fieldPos=0;
		int fieldSum=0;
		float [] fieldsG=new float[fields.length];
		float [] fieldsH=new float[fields.length];
		int [] fieldsCount=new int[fields.length];
		
		for(int i=0;i<CategoricalG.length;i++){

			if(i>fieldSum+fields[fieldPos]){
				//System.out.println(i);
				fieldSum+=fields[fieldPos];
				fieldPos++;
			}
			if(CategoricalCount[i]==0){
				continue;
			}
			//float GL=CategoricalG[i];
			fieldsG[fieldPos]+=CategoricalG[i];
			float GL=fieldsG[fieldPos];
			//float HL=CategoricalH[i];
			fieldsH[fieldPos]+=CategoricalH[i];
			float HL=fieldsH[fieldPos];
			if(HL==0 || GlobalH-HL==0){
				continue;
			}
			fieldsCount[fieldPos]+=CategoricalCount[i];
			int countL=fieldsCount[fieldPos];
			float GR=GlobalG-GL;
			float HR=GlobalH-HL;
			

			int countR=GlobalCount-countL;

			
			float scoreLeft=GL*GL/HL;
			float scoreRight=GR*GR/HR;
			float scoreTot=(GL+GR)*(GL+GR)/(HL+HR);
			float gain=scoreTot-scoreLeft-scoreRight;
			gain=gain*-1;


			if(gain>maxGain && countL>minLeafCount && countR>minLeafCount){
				maxGain=gain;
				topFeature=i;
				topField=fieldPos;
				predictionRight=-GR/HR;
				predictionLeft=-GL/HL;
				ValidChild=true;
				//countLF=countL;
			}
			
		}
		
		/*
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
		
		 */
		/*
		System.out.println("direction=" + direction);
		System.out.println("prediction=" + (-GlobalG/GlobalH));
		System.out.println("predictionLeft=" + predictionLeft);
		System.out.println("predictionRight=" + predictionRight);
		System.out.println("GlobalG=" + GlobalG);
		System.out.println("GlobalH=" + GlobalH);
		System.out.println("count=" + GlobalCount);
		System.out.println("countLF=" + countLF);
		System.out.println("topFeature=" + topFeature);
		System.out.println("maxGain=" + maxGain);
		*/
		
		CategoricalG=null;
		CategoricalH=null;
		CategoricalCount=null;
		Completed=true;
		
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
		leftChild.direction+="-left";
		leftChild.depth=this.depth+1;
		
		rightChild=CreateChild();
		rightChild.prediction=predictionRight;
		rightChild.direction+="-right";
		rightChild.depth=this.depth+1;
		
		leafNode=false;

		return 0;
	}
	
	public void UpdateCatFeatures(int [] FeatureVector, float RealValue,float residual){
		//if(FinalLeft&&FinalRight){
		if(CompletedLeft&&CompletedRight){
			return;
		}
		

		if(!leafNode){
			boolean left=false;
			int fieldPos=0;
			for(int i=0;i<FeatureVector.length;i++){
				int FeatureID=FeatureVector[i];
				if(!(FeatureID<fields[fieldPos])){
					fieldPos++;
				}

				if(FeatureID<=topFeature && fieldPos==topField){
					//System.out.println(FeatureID+" - "+ topFeature+" - "+ fieldPos+" - "+ topField);
					left=true;
					break;
				}
			}
			if(left){
				//if(FinalLeft){
				if(CompletedLeft){
					return;
				}
				leftChild.UpdateCatFeatures(FeatureVector, RealValue,residual);
			}else{
				//if(FinalRight){
				if(CompletedRight){
					return;
				}
				rightChild.UpdateCatFeatures(FeatureVector, RealValue,residual);
			}
			return;
		}

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
		Child.InitCategorical(Categories);
		Child.minLeafCount=minLeafCount;
		Child.fields=fields;
		Child.direction=direction;

		return Child;
	}
}