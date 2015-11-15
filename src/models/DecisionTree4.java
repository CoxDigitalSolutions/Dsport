package models;

import frameWork.DataPreparer;

public class DecisionTree4  extends BaseModel implements java.io.Serializable{
	/**
	 * 
	 */
	
	
	
	private static final long serialVersionUID = 1L;
	public TreeNode3 root;
	public int minLeafCount=1;
	public int maxDepth=6;
	public int CurrentDepth=0;
	public int [] fields;
	
	
	// tree values
	int Categories;
	float [][] CategoricalG;
	float [][] CategoricalH;
	int  [][] CategoricalCount;
	
	@Override
	public float predict(int[] FeatureVector) {
		// TODO Auto-generated method stub
		return root.predict(FeatureVector);
	}


	public float Train(float RealValue, int[] FeatureVector,float residual) {
		root.UpdateCatFeatures(FeatureVector, RealValue,residual);
		return 0;
	}
	
	public void Init(int inputNodes) {
		Categories=inputNodes;


		

	}
	
	
	public void Init() {
		int trees=0;
		for(int i=0;i<=maxDepth;i++){
			trees+=Math.pow(2, i);
			System.out.println(trees);
		}
		CategoricalG = new float[trees][];
		CategoricalH = new float[trees][];
		CategoricalCount = new int[trees][];
		
		root=new TreeNode3();
		root.level=0;
		root.leftTrees=0;
		root.treeID=0;
		root.minLeafCount=minLeafCount;
		root.CategoricalCount=CategoricalCount;
		root.CategoricalG=CategoricalG;
		root.CategoricalH=CategoricalH;
		root.InitCategorical(Categories);
		root.fields=fields;
	}

	public int StopCalcuations() {
		int cost=root.CalculateCost();
		CurrentDepth++;
		if(cost<0 || maxDepth<=CurrentDepth){
			return -1;
		}
		return 1;
	}


	@Override
	public float Train(float RealValue, int[] FeatureVector) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public float TrainBoosted(float RealValue, float residual, int[] FeatureVector) {
		root.UpdateCatFeatures(FeatureVector, RealValue,residual);

		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void Cleanup() {
		CategoricalCount=null;
		CategoricalG=null;
		CategoricalH=null;
		// TODO Auto-generated method stub
		
	}
}
