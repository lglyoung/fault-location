package faultlocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
			
			//不重叠的故障模式的相关参数集合
			Set<Integer> allRelatedParamSet = new HashSet<Integer>();	
					
			//这个do while的作用是定位多个不重叠的故障模式 2016.11.18 by lglyoung
			boolean hasMuliSche = false;
			do {	
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
				
				//判断是否存在多个不重叠的故障模式
				//2016.11.18 by lglyoung
				if (Util.notContainsAnyOneOf(allRelatedParamSet, relatedParams)) {
					allRelatedParamSet.addAll(relatedParams);
					extraTc = Util.genExtraTc(valuesOfEachParam, ftc, relatedParams);
					extraTcs.add(extraTc);					
				} else {
					break;	//如果relatedParams中有一个元素已经出现在allRelatedParamSet，则退出循环
				}
				hasMuliSche = Util.isFailTc(extraTc, allFtcs, null);
				if (hasMuliSche) {
					ftc = extraTc;
				}
			} while (hasMuliSche);
		}
		
		//再对总的附加测试用例集进行去重
		Util.delRepeat(extraTcs);
	}
	
}
