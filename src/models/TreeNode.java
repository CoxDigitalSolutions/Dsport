package models;

import frameWork.DataPreparer;

public class TreeNode implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int [][] CategoricalFeatures;
	int [] splitPoint;
	
	float prediction;
	float residual;
	float [] distribution;
	
	boolean leafNode=false;
	TreeNode leftChild;
	TreeNode rightChild;
	
	public DataPreparer dataPreparer;
	
	public void InitCategorical(int inputNodes, int splits){
		CategoricalFeatures= new int[inputNodes][splits];
		splitPoint=new int[splits];
		residual=0.0F;
	}
	
	public void InsertCatFeature(int ID, int sValue){
		CategoricalFeatures[ID][sValue]++;
		splitPoint[sValue]++;
	}
	
	public float CalculateCost(){
		float gloablG=0;
		float gloablH=0;
		for(int i=0;i<splitPoint.length;i++){
			float value=dataPreparer.TargetSummary.PositionToValue(i);
			float count=splitPoint[i];
			gloablG+=calculateG(value)*count;
			gloablH+=calculateH(value)*count;
		}

		
		System.out.println("prediction=" + (-gloablG/gloablH));
		
		for(int i=0;i<CategoricalFeatures.length;i++){
			float GL=0F;
			float HL=0F;
			float GR=0F;
			float HR=0F;
			float count=0;
			float sum=0;
			for(int j=0;j<CategoricalFeatures[i].length;j++){
				float value=dataPreparer.TargetSummary.PositionToValue(j);
				float Catcount=CategoricalFeatures[i][j];
				float Totalcount=splitPoint[j];
				GL+=calculateG(value)*Catcount;
				HL+=calculateH(value)*Catcount;
				GR+=calculateG(value)*(Totalcount-Catcount);
				HR+=calculateH(value)*(Totalcount-Catcount);
				sum+=value*Catcount;
				count+=Catcount;
			}
			float scoreLeft=GL*GL/HL;
			float scoreRight=GR*GR/HR;
			float scoreTot=(GL+GR)*(GL+GR)/(HL+HR);
			float gain=scoreTot-scoreLeft-scoreRight;
			System.out.println("avg="+sum/count + "   count=" + count);
			System.out.println("ID=" + i+"  score left=" + scoreLeft + "  score right=" + scoreRight + "   gain=" + gain);
			
			
		}
		return 0.0F;
	}
	
	public void UpdateCatFeatures(int [] FeatureVector, int sValue){
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
}
