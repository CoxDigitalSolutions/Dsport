package Boosting;

import frameWork.DataPreparer;
import models.BaseModel;
import models.DecisionTree;

public class BoostedTree  extends BaseModel implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DecisionTree [] TreeList;
	public DataPreparer dataPreparer;
	public int minLeafCount=1;
	public int maxDepth=6;
	int currentTree=0;
	public int [] fields;
	
	public void init(int num_trees,int minLeafCount,DataPreparer dataPreparer, int InputTotal){
		TreeList=new DecisionTree [num_trees];
		this.minLeafCount=minLeafCount;
		this.dataPreparer=dataPreparer;
		for(int i=0;i<TreeList.length;i++){
			TreeList[i]=new DecisionTree();
			
			TreeList[i].minLeafCount=minLeafCount;
			TreeList[i].maxDepth=maxDepth;
			TreeList[i].fields=fields;
			TreeList[i].Init(InputTotal);
		}
	}
	
	public float Train(float RealValue, int[] FeatureVector) {
		float residual=0;
		for(int i=0;i<currentTree;i++){
			residual+=TreeList[i].predict(FeatureVector);
		}
		TreeList[currentTree].Train(RealValue, FeatureVector,residual);
		return 0;
	}
	
	public int CalculateCost() {
		int cost=TreeList[currentTree].StopCalcuations();

		if(cost<0 ){
			currentTree++;
			System.out.println("tree count="+ currentTree);
		}
		
		if(currentTree==TreeList.length){
			return -1;
		}
		return 1;
	}

	@Override
	public float predict(int[] FeatureVector) {
		float prediction=0;

		for(int i=0;i<currentTree;i++){
			float thisPrediction=TreeList[i].predict(FeatureVector);
			prediction+=thisPrediction;
		}
		return prediction;
	}

	@Override
	public int StopCalcuations() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float TrainBoosted(float RealValue, float residual, int[] FeatureVector, int ID) {
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
