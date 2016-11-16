package faultlocation;

import java.util.ArrayList;
import java.util.List;

import common.IStrategy;
import common.Isolation;
import common.Util;

public class Sri implements IStrategy {

	@Override
	public void faultLocating(int[] valuesOfEachParam, List<int[]> allFtcs, List<int[]> ftcs, List<int[]> ptcs,
			List<int[]> extraTcs, List<int[]> faultSchemas) {
		for(int i = 0; i < ftcs.size(); i++) {
			int[] ftc = ftcs.get(i);
			List<Integer> relatedParams = new ArrayList<Integer>();
			int[] extraTc = null;
			do{
				List<Integer> observedParams = genObservedParams(valuesOfEachParam, ftc, relatedParams, ptcs);
				int relatedParam = Isolation.isolate(ftc, observedParams, valuesOfEachParam, ftcs, ptcs, extraTcs);
				relatedParams.add(relatedParam);
				List<Integer> changedParams = Util.genChangedParams(ftc, relatedParams);
				extraTc = Util.genExtraTc(valuesOfEachParam, ftc, changedParams);
			}while(!Util.isFailTc(extraTc, allFtcs, null));
			Util.addNotRepeatIntArray(Util.genFaultSchema(ftc, relatedParams), faultSchemas);
		}
	}
	
	/**
	 * 根据覆盖表的通过测试用例集生成RSI的关注模式
	 * @param valuesOfEachParam 失效测试用例
	 * @param ftc 失效测试用例
	 * @param relatedParams 与故障相关的参数的索引集
	 * @param ptcs 通过测试用例集(覆盖表)
	 * @return List<Integer>
	 */
	private List<Integer> genObservedParams(int[] valuesOfEachParam, int[] ftc, List<Integer> relatedParams, List<int[]> ptcs) {
		List<Integer> observedParams = new ArrayList<Integer>();
		
		//求出"覆盖表通过测试用例集"中含有relatedParams的所有通过测试用例集
		List<int[]> candidatePtcs = new ArrayList<int[]>();
		boolean isInclude = true;
		for (int i = 0; i < ptcs.size(); i++) {
			int[] tmpTc = ptcs.get(i);
			for (int j = 0; j < relatedParams.size(); j++) {
				if (ftc[relatedParams.get(j)] != tmpTc[relatedParams.get(j)]) {
					isInclude = false;
					break;
				}
			}
			if (isInclude) {
				candidatePtcs.add(tmpTc);
			}
			isInclude = true;
		}

		//求candidatePtcs中与ftc相似度最大的tc
		int maxDegreeOfSimilary = 0;	//当前最大相似度
		int[] TPass = null;				//所对应的测试用例
		for (int[] tmpTc : candidatePtcs) {
			int s = Util.degreeOfSimilary(ftc, tmpTc);	//获取相似度
			if (s > maxDegreeOfSimilary) {
				maxDegreeOfSimilary = s;
				TPass = tmpTc;
			}
		}
		
		//生成关注模式
		if (TPass == null) {
			TPass = Util.genExtraTc(valuesOfEachParam, ftc, Util.genChangedParams(ftc, relatedParams));
		}
		for (int i = 0; i < ftc.length; i++) {
			if (ftc[i] != TPass[i]) {
				observedParams.add(i);
			}
		}
		
		return observedParams;
	}
	
}
