package FrameWork;

public class UtilMath {
	public static float sigmoid(float value){
		return (float) (1/(1+exp(-value)));
	}
	
	
	//update this to float version could be good
	public static float exp(float value){
		final long tmp = (long) (1512775 * value + 1072632447);
		return (float)Double.longBitsToDouble(tmp << 32);
	}
	
	public static float rsqrt(float value){
		return 1/fastSqrt(value);
	}
	
	public static float recipropal(float value){
		return 1/value;
	}
	
	public static float fastSqrt(float x) {
	    return Float.intBitsToFloat(532483686 + (Float.floatToRawIntBits(x) >> 1));
	}
	
	public static float random() {
	    return (float) Math.random();
	}
	
	public static float pow(float a, float b) {
	    return (float) Math.pow(a,b);
	}
}
