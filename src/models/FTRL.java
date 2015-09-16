package models;

import java.util.Objects;

import FrameWork.UtilMath;

public class FTRL  extends BaseModel implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String US = "_";
	
	public int numFeatures = (int) Math.pow(2, 28);
	public float α = 0.1F; // learning rate
	
	private float[] w;
    private float[] z;
    private float[] η;
    
    
    public int interactions = 2; // degree of automatically generated feature interactions
    public float λ1 = 1.0F; // L1-Regularization
    public float λ2 = 1.0F; // L2-Regularization
    public float β = 1.0F;
    
    public long numSamplesSeen = 0;
    
    public void init(){
    	η = new float[numFeatures];
        w = new float[numFeatures];
        z = new float[numFeatures];
    }
    
    public float predict(int [] FeatureVector){
    	FeatureVector=features(FeatureVector);
    	return predictProba(FeatureVector);
    }
    
    public float Train(float RealValue, int [] FeatureVector, int weight){
    	FeatureVector=features(FeatureVector);
    	float prediction= predictProba(FeatureVector);
    	update(FeatureVector,prediction,RealValue);
    	return prediction;
    }
    
    public float predictProba(int[] x) {

        float wTx = 0.0F;
        for (int i = 0; i < x.length; ++i) {
            int xi = x[i];
            int sign = z[xi] < 0 ? -1 : 1;
            if (sign * z[xi] < λ1) {
                w[xi] = 0.0F;
            } else {
                w[xi] = -1.0F / ((β + UtilMath.fastSqrt(η[xi])) / α + λ2) * (z[xi] - sign * λ1);
            }

            wTx += w[xi];
        }
        return UtilMath.sigmoid(wTx);
    }
    
    public void update(int[] x, float p, float y) {
        float gi = p - y;
        float g2 = gi * gi;

        for (int i = 0; i < x.length; ++i) {
            int xi = x[i];
            float si = 1.0F / α * (UtilMath.fastSqrt(η[xi] + g2) - UtilMath.fastSqrt(η[xi]));
            z[xi] = z[xi] + gi - si * w[xi];
            η[xi] += g2;
        }
    }
    
    public int[] features(int[] parts) {
        if (interactions < 1 && interactions > 4) {
            throw new IllegalArgumentException("interactions must be either 1, 2 or 3");
        }

        int size = numFeatures(parts, interactions);

        int[] featureIndices = new int[size];
        int ix = 0;


        if (interactions >= 1) {
            for (int i = 0; i < parts.length; i++) {
                int hash = Objects.hash(i, US, parts[i]);
                featureIndices[ix++] = Math.abs(hash) % numFeatures;
            }
        }

        if (interactions >= 2) {
            for (int i = 0; i < parts.length; i++) {
                for (int j = i + 1; j < parts.length; j++) {
                    int hash = Objects.hash(i, US, j, US, parts[i], US, parts[j]);
                    featureIndices[ix++] = Math.abs(hash) % numFeatures;
                }
            }
        }
        if (interactions >= 3) {
            for (int i = 0; i < parts.length; i++) {
                for (int j = i + 1; j < parts.length; j++) {
                    for (int k = j + 1; k < parts.length; k++) {
                        int hash = Objects.hash(i, US, j, US, k, US, parts[i], US, parts[j], US, parts[k]);
                        featureIndices[ix++] = Math.abs(hash) % numFeatures;
                    }
                }
            }
        }

        if (featureIndices.length != ix) {
            throw new IllegalStateException("post-condition violated: pre-allocated array does not match the number of features");
        }
        return featureIndices;
    }
    
    public static int numFeatures(int[] parts, int interactions) {
        int ix = parts.length;

        if (interactions >= 2) {
            int n = parts.length - 1;
            ix += (n * (n + 1)) / 2;
        }

        if (interactions >= 3) {
            int n = parts.length;
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    ix += n - (j + 1);
                }
            }
        }
        return ix;
    }
}
