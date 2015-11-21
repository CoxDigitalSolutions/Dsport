package models;

import gnu.trove.map.hash.THashMap;

public class DecisionTree  extends BaseModel implements java.io.Serializable{
	/**
	 * 
	 */
	
	
	
	private static final long serialVersionUID = 1L;
	public TreeNode root;
	public int minLeafCount=1;
	public int maxDepth=6;
	public int CurrentDepth=0;
	public int [] fields;
	TreeStats treeStats=new TreeStats();
	static THashMap<Integer,TreeNode> Nodes = new THashMap<Integer,TreeNode>();
	public int Threads=1;
	
	@Override
	public float predict(int[] FeatureVector) {
		// TODO Auto-generated method stub
		return root.predict(FeatureVector);
	}


	public float Train(float RealValue, int[] FeatureVector,float residual) {
		//root.UpdateCatFeatures(FeatureVector, RealValue,residual);
		return 0;
	}
	
	public void Init(int inputNodes) {
		root=new TreeNode();
		root.minLeafCount=minLeafCount;
		treeStats.LeafCount=1;
		treeStats.Threads=Threads;
		root.treeStats=treeStats;
		root.InitCategorical(inputNodes);
		root.fields=fields;
		root.isRoot=true;
		

	}

	public int StopCalcuations() {
		int cost=root.CalculateCost();
		CurrentDepth++;
		if(cost<0 || maxDepth<=CurrentDepth){
			//root.PrintTree();
			root.ClearData();
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
	public float TrainBoosted(float RealValue, float residual, int[] FeatureVector, int ThreadID) {
		root.UpdateCatFeatures(FeatureVector, RealValue,residual, ThreadID);

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