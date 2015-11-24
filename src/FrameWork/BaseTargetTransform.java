package frameWork;

public class BaseTargetTransform extends  TargetTransform {

	@Override
	public float TransformTarget(float Target) {
		return Target;
	}

	@Override
	public float RevertTarget(float Target) {
		return Target;
	}
	
}
