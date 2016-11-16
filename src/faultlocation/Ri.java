package faultlocation;

import java.util.ArrayList;
import java.util.List;

import common.IStrategy;
import common.Isolation;
import common.Util;

public class Ri implements IStrategy {

	@Override
	public void faultLocating(int[] valuesOfEachParam, List<int[]> allFtcs, List<int[]> ftcs, List<int[]> ptcs,
			List<int[]> extraTcs, List<int[]> faultSchemas) {
		List<Integer> params = new ArrayList<Integer>();
		for(int i = 0; i < valuesOfEachParam.length; i++) {
			params.add(i);
		}
		for(int i = 0; i < ftcs.size(); i++) {
			int[] ftc = ftcs.get(i);
			List<Integer> observedParams = new ArrayList<Integer>(params);
			List<Integer> relatedParams = new ArrayList<Integer>();
			int[] extraTc = null;
			do{
				int relatedParam = Isolation.isolate(ftc, observedParams, 
						valuesOfEachParam, ftcs, ptcs, extraTcs);
				relatedParams.add(relatedParam);
				List<Integer> changedParams = Util.genChangedParams(ftc, relatedParams);
				observedParams = changedParams;
				extraTc = Util.genExtraTc(valuesOfEachParam, ftc, changedParams);
			} while(!Util.isFailTc(extraTc, allFtcs, null));
			Util.addNotRepeatIntArray(Util.genFaultSchema(ftc, relatedParams), faultSchemas);
		}
	}
	
}
