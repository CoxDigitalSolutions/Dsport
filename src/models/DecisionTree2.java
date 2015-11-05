package models;

import frameWork.DataPreparer;

public class DecisionTree2  extends BaseModel implements java.io.Serializable{
	/**
	 * 
	 */
	
	
	
	private static final long serialVersionUID = 1L;
	public DataPreparer dataPreparer;
	public TreeNode root;
	public int minLeafCount=500;
	public int maxDepth=6;
	public int CurrentDepth=0;
	
	@Override
	public float predict(int[] FeatureVector) {
		// TODO Auto-generated method stub
		return root.predict(FeatureVector);
	}

	@Override
	public float Train(float RealValue, int[] FeatureVector) {
		root.UpdateCatFeatures(FeatureVector, dataPreparer.TargetSummary.ValueToPosition(RealValue),0.0F);
		return 0;
	}
	
	public void Init(DataPreparer dataPreparer, int inputNodes) {
		root=new TreeNode();
		this.dataPreparer=dataPreparer;
		root.minLeafCount=minLeafCount;
		root.InitCategorical(inputNodes, this.dataPreparer.TargetSummary.desSortedMap.size());
		root.dataPreparer=this.dataPreparer;
	}

	public int CalculateCost() {
		int cost=root.CalculateCost();
		CurrentDepth++;
		if(cost<0 || maxDepth<=CurrentDepth){
			return -1;
		}
		return 1;
	}
}
