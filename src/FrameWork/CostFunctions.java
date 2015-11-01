package frameWork;

public class CostFunctions {
	public int Type=1;
	
	public float Activation(float prediction){
		if(Type==1){
			return UtilMath.sigmoid(prediction);
		}else if(Type==2){
			return prediction;
		}else if(Type==3){
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
		}else if(Type==3){
			float RV=(float) Math.exp(RealValue*6.8060355F+3.8286414F);
			float P=(float) Math.exp(prediction*6.8060355F+3.8286414F);
			float cost=((P-RV)/RV)*((P-RV)/RV);
			if(P<RV){
				cost=cost*-1;
			}

			
			return cost;
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