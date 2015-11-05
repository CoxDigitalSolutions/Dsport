package models;

import frameWork.DataPreparer;

public class DecisionTree  extends BaseModel implements java.io.Serializable{
	/**
	 * 
	 */
	
	
	
	private static final long serialVersionUID = 1L;
	public DataPreparer dataPreparer;
	public TreeNode root;
	public int minLeafCount=1;
	public int maxDepth=6;
	public int CurrentDepth=0;
	int [] fields;
	
	@Override
	public float predict(int[] FeatureVector) {
		// TODO Auto-generated method stub
		return root.predict(FeatureVector);
	}


	public float Train(float RealValue, int[] FeatureVector,float residual) {
		root.UpdateCatFeatures(FeatureVector, RealValue,residual);
		return 0;
	}
	
	public void Init(DataPreparer dataPreparer, int inputNodes) {
		root=new TreeNode();
		this.dataPreparer=dataPreparer;
		root.minLeafCount=minLeafCount;
		root.InitCategorical(inputNodes, this.dataPreparer.TargetSummary.desSortedMap.size());
		root.dataPreparer=this.dataPreparer;
		root.fields=fields;
	}

	public int CalculateCost() {
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
}
