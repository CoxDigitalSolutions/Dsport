package models;


public class TreeNode implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int ID=0;
	boolean isRoot=false;
	int Categories;
	double [][] CategoricalG;
	double [][] CategoricalH;
	int  [][] CategoricalCount;
	
	double  [] GlobalG;
	double  [] GlobalH;
	int [] GlobalCount;
	
	double  GlobalGFinal=0;
	double  GlobalHFinal=0;
	int GlobalCountFinal=0;
	
	float prediction;
	int count1=0;
	int count2=0;

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
	
	TreeStats treeStats=new TreeStats();
	
	int [] fields;
	String direction="";
	
	public void InitCategorical(int inputNodes){
		Categories=inputNodes;
		
		CategoricalG = new double[treeStats.Threads][Categories];
		CategoricalH = new double[treeStats.Threads][Categories];
		CategoricalCount = new int[treeStats.Threads][Categories];

		GlobalG= new double[treeStats.Threads];
		GlobalH= new double[treeStats.Threads];
		GlobalCount = new int[treeStats.Threads];

	}
	
	public void InsertCatFeature(int ID, float RealValue,float residual,int ThreadID){

		CategoricalG[ThreadID][ID]+=calculateG(RealValue,residual);
		CategoricalH[ThreadID][ID]+=calculateH(RealValue,residual);
		CategoricalCount[ThreadID][ID]++;
		
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
			}else{
				
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
			int LeftResult=-1;
			if(!FinalLeft){
				LeftResult=leftChild.CalculateCost();
			}
			if(LeftResult==-1){
				leftChild=null;
				FinalLeft=true;
				CompletedLeft=true;
			}else if(LeftResult==-2){
				CompletedLeft=true;
			}
			int RightResult=-1;
			if(!FinalRight){
				RightResult=rightChild.CalculateCost();
			}
			
			if(RightResult==-1){
				rightChild=null;
				FinalRight=true;
				CompletedRight=true;
			}else if(RightResult==-2){
				CompletedRight=true;
			}
			
			if(FinalRight && FinalLeft){
				leafNode=true;
			}

			if(CompletedRight && CompletedLeft){
				return -2;
			}
			
			return 0;
		}
		if(Completed){
			return -2;
		}
		for(int i=0;i<GlobalG.length;i++){
			GlobalGFinal+=GlobalG[i];
		}
		for(int i=0;i<GlobalH.length;i++){
			GlobalHFinal+=GlobalH[i];
		}
		for(int i=0;i<GlobalCount.length;i++){
			GlobalCountFinal+=GlobalCount[i];

		}


		prediction=(float) (-GlobalGFinal/GlobalHFinal);


		float maxGain=Float.MIN_VALUE;
		boolean ValidChild=false;
		float predictionRight=0;
		float predictionLeft=0;
		float countLF=0;
		
		for(int i=0;i<fields.length;i++){
			//System.out.println(i+"="+fields[i]);
		}

		int fieldPos=0;
		int fieldSum=0;
		double [] fieldsG=new double[fields.length];
		double [] fieldsH=new double[fields.length];
		int [] fieldsCount=new int[fields.length];


		for(int i=0;i<CategoricalG[0].length;i++){

			if(i>fieldSum+fields[fieldPos]){
				//System.out.println(i);
				//System.out.println(fieldPos+"="+fieldSum);
				fieldSum+=fields[fieldPos];
				
				fieldPos++;
				
			}
			int sumCategoricalCount=0;
			for(int j=0;j<CategoricalG.length;j++){
				sumCategoricalCount+=CategoricalCount[j][i];
			}
			
			if(sumCategoricalCount==0){
				continue;
			}
			
			//float GL=CategoricalG[i];
			double Tsum=0;
			for(int j=0;j<CategoricalG.length;j++){
				fieldsG[fieldPos]+=CategoricalG[j][i];
			}
			double GL=fieldsG[fieldPos];

			//float HL=CategoricalH[i];
			for(int j=0;j<CategoricalH.length;j++){
				fieldsH[fieldPos]+=CategoricalH[j][i];
			}

			double HL=fieldsH[fieldPos];
			if(HL==0 || GlobalHFinal-HL==0){
				//continue;
			}
			for(int j=0;j<CategoricalCount.length;j++){
				fieldsCount[fieldPos]+=CategoricalCount[j][i];
			}
			int countL=fieldsCount[fieldPos];
			float GR=(float) (GlobalGFinal-GL);
			float HR=(float) (GlobalHFinal-HL);
			

			int countR=GlobalCountFinal-countL;
			
			double scoreLeft=GL*GL/HL;
			double scoreRight=GR*GR/HR;
			double scoreTot=(GL+GR)*(GL+GR)/(HL+HR);
			double gain=scoreTot-scoreLeft-scoreRight;
			gain=gain*-1;

			//System.out.println("Feature="+i+" Field="+fieldPos+" gain="+gain);
			if(gain>maxGain && gain>0 && countL>minLeafCount && countR>minLeafCount){
				maxGain=(float) gain;
				topFeature=i;
				topField=fieldPos;
				predictionRight=-GR/HR;
				predictionLeft=(float) (-GL/HL);
				ValidChild=true;
				countLF=countL;
			}
			
		}

		/*
		System.out.println("direction=" + direction);
		System.out.println("prediction=" + (-GlobalGFinal/GlobalHFinal));
		System.out.println("predictionLeft=" + predictionLeft);
		System.out.println("predictionRight=" + predictionRight);
		System.out.println("GlobalGFinal=" + GlobalGFinal);
		System.out.println("GlobalHFinal=" + GlobalHFinal);
		System.out.println("GlobalCountFinal=" + GlobalCountFinal);
		System.out.println("count=" + GlobalCountFinal);
		System.out.println("countLF=" + countLF);
		System.out.println("topFeature=" + topFeature);
		System.out.println("topField=" + topField);
		System.out.println("maxGain=" + maxGain);
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		leftChild.ID=treeStats.LeafCount+0;
		
		rightChild=CreateChild();
		rightChild.prediction=predictionRight;
		rightChild.direction+="-right";
		rightChild.depth=this.depth+1;
		rightChild.ID=treeStats.LeafCount+1;
		
		treeStats.LeafCount+=2;
		
		leafNode=false;

		return 0;
	}
	
	public void UpdateCatFeatures(int [] FeatureVector, float RealValue,float residual, int ThreadID){
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
				if(fieldPos==1 && FeatureID<1116){
					//System.out.println(FeatureID+" - "+ topFeature+" - "+ fieldPos+" - "+ topField);
				}
				if(FeatureID<=topFeature && fieldPos==topField){
					//System.out.println("left");
					left=true;
					break;
				}
			}
			if(left){
				//if(FinalLeft){
				if(CompletedLeft){
					return;
				}
				leftChild.UpdateCatFeatures(FeatureVector, RealValue,residual,ThreadID);
			}else{
				//if(FinalRight){
				if(CompletedRight){
					return;
				}
				rightChild.UpdateCatFeatures(FeatureVector, RealValue,residual,ThreadID);
			}
			return;
		}

		GlobalG[ThreadID]+=calculateG(RealValue,residual);
		GlobalH[ThreadID]+=calculateH(RealValue,residual);
		GlobalCount[ThreadID]++;

		for(int i=0;i<FeatureVector.length;i++){
			if(i==0){
				count1++;
			}
			if(i==1){
				count2++;
			}
			InsertCatFeature(FeatureVector[i],RealValue,residual,ThreadID);

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
		Child.treeStats=treeStats;
		Child.InitCategorical(Categories);
		Child.minLeafCount=minLeafCount;
		Child.fields=fields;
		Child.direction=direction;
		

		return Child;
	}
	
	public void PrintTree(){
		System.out.println(depth+" : Feature="+ topFeature + "topField="+ topField + " prediction="+prediction);
		if(leftChild!=null){
			leftChild.PrintTree();
		}
		if(rightChild!=null){
			rightChild.PrintTree();
		}
	}
	
	public void ClearData(){
		CategoricalG=null;
		CategoricalH=null;
		CategoricalCount=null;
		if(leftChild!=null){
			leftChild.ClearData();
		}
		if(rightChild!=null){
			rightChild.ClearData();
		}
	}
}