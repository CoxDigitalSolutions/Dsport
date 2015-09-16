package frameWork;

public class CostFunctions {
	public int Type=1;
	
	public float Activation(float prediction){
		if(Type==1){
			return UtilMath.sigmoid(prediction);
		}else if(Type==2){
			return prediction;
		}else{
			return 0.0F;
		}
	}
	
	
	public float CalculateError(float prediction, float RealValue){
		if(Type==1){
			return prediction-RealValue;
		}else if(Type==2){
			return prediction-RealValue;
		}else{
			return 0.0F;
		}
	}
	
	public float CalculateCost(float prediction, float RealValue){
		if(Type==1){
			if(RealValue==0){
				return(float) Math.log(1-prediction);
			}else{
				return(float) Math.log(prediction);
			}
		}else if(Type==2){
			return (prediction-RealValue)*(prediction-RealValue);
		}else{
			return 0.0F;
		}
	}
}
