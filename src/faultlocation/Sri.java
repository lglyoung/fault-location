package faultlocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import common.IStrategy;
import common.Isolation;
import common.Util;

public class Sri implements IStrategy {

	@Override
	public void faultLocating(int[] valuesOfEachParam, List<int[]> allFtcs, List<int[]> ftcs, List<int[]> ptcs,
			List<int[]> extraTcs, List<int[]> faultSchemas) {
		
		Set<String> extraTcsStrSet = new HashSet<String>();	//用Set+String去重附加测试用例集
		Set<String> scheStrSet = new HashSet<String>();
		
		for(int i = 0; i < ftcs.size(); i++) {
			int[] ftc = ftcs.get(i);
			
			//不重叠的故障模式的相关参数集合
			Set<Integer> allRelatedParamSet = new HashSet<Integer>();	
					
			//这个do while的作用是定位多个不重叠的故障模式 2016.11.18 by lglyoung
			boolean hasMuliSche = false;
			do {	
				List<Integer> relatedParams = new ArrayList<Integer>();
				int[] extraTc = null;
				do{
					List<Integer> observedParams = genObservedParams(valuesOfEachParam, ftc, relatedParams, ptcs);
					int relatedParam = Isolation.isolate(ftc, observedParams, valuesOfEachParam, ftcs, ptcs, extraTcs);
					relatedParams.add(relatedParam);
					List<Integer> changedParams = Util.genChangedParams(ftc, relatedParams);
					extraTc = Util.genExtraTc(valuesOfEachParam, ftc, changedParams);
				}while(!Util.isFailTc(extraTc, allFtcs, null));
				scheStrSet.add(Util.intArrayToStr(Util.genFaultSchema(ftc, relatedParams)));
				
				//判断是否存在多个不重叠的故障模式
				//2016.11.18 by lglyoung
				if (Util.notContainsAnyOneOf(allRelatedParamSet, relatedParams)) {
					allRelatedParamSet.addAll(relatedParams);
					extraTc = Util.genExtraTc(valuesOfEachParam, ftc, relatedParams);
					extraTcsStrSet.add(Util.intArrayToStr(extraTc));					
				} else {
					break;	//如果relatedParams中有一个元素已经出现在allRelatedParamSet，则退出循环
				}
				hasMuliSche = Util.isFailTc(extraTc, allFtcs, null);
				if (hasMuliSche) {
					ftc = extraTc;
				}
			} while (hasMuliSche);
		}
		
		//将extraTcsStrSet转换成List<int[]>
		extraTcs.addAll(Util.strScheSetToIntArrayList(extraTcsStrSet));
		faultSchemas.addAll(Util.strScheSetToIntArrayList(scheStrSet));
		
		//再对总的附加测试用例集进行去重
		Util.delRepeat(extraTcs);
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
