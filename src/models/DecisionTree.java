package models;

import frameWork.DataPreparer;

public class DecisionTree  extends BaseModel implements java.io.Serializable{
	/**
	 * 
	 */
	
	
	
	private static final long serialVersionUID = 1L;
	public DataPreparer dataPreparer;
	public TreeNode root;
	
	@Override
	public float predict(int[] FeatureVector) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float Train(float RealValue, int[] FeatureVector, int weight) {
		root.UpdateCatFeatures(FeatureVector, dataPreparer.TargetSummary.ValueToPosition(RealValue));
		return 0;
	}
	
	public void Init(DataPreparer dataPreparer, int inputNodes) {
		root=new TreeNode();
		this.dataPreparer=dataPreparer;
		root.InitCategorical(inputNodes, this.dataPreparer.TargetSummary.desSortedMap.size());
		root.dataPreparer=this.dataPreparer;
	}

	public void CalculateCost() {
		root.CalculateCost();
		
	}
}
