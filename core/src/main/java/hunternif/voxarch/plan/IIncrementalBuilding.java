package hunternif.voxarch.plan;

public interface IIncrementalBuilding {
	/** Perform one step of building. */
	void buildStep();
	boolean isDone();
}
