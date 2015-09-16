package models;

public abstract class BaseModel implements Model{
	public float learningRate;
	public FrameWork.CostFunctions CostFunction=new FrameWork.CostFunctions();
}
