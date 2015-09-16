package models;

public abstract class BaseModel implements Model{
	public float learningRate;
	public frameWork.CostFunctions CostFunction=new frameWork.CostFunctions();
}
