package faultlocation;

import java.util.List;

public class Context {
	private IStrategy strategy;
	
	public Context(IStrategy strategy) {
		super();
		this.strategy = strategy;
	}

	public void setStrategy(IStrategy strategy) {
		this.strategy = strategy;
	}
	
	public void faultLocating(int[] valuesOfEachParam, List<int[]> allFtcs, List<int[]> ftcs, 
			List<int[]> ptcs, List<int[]> extraTcs, List<int[]> faultSchemas) {
		this.strategy.faultLocating(valuesOfEachParam, allFtcs, ftcs, ptcs, extraTcs, faultSchemas);
	}
	
}
