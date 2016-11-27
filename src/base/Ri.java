package base;

import java.util.ArrayList;
import java.util.List;

import common.Util;

/**
 * ri差异定位算法
 * @author lglyoung
 *
 */
public class Ri implements IDeltaDebug {

	@Override
	public List<Integer> dd(int[] valuesOfEachParam, int[] ftc, List<int[]> allFtcs, List<int[]> ptcs,
			List<int[]> extraTcs) {
		//初始化关注模式
		List<Integer> observedParams = new ArrayList<Integer>();
		for(int i = 0; i < valuesOfEachParam.length; i++) {
			observedParams.add(i);
		}
		
		//保存与故障模式相关的固定参数
		List<Integer> relatedParams = new ArrayList<Integer>();
		int[] extraTc = null;
		do{
			int relatedParam = Util.isolate(ftc, observedParams, 
					valuesOfEachParam, allFtcs, null, extraTcs);
			relatedParams.add(relatedParam);
			List<Integer> changedParams = Util.genChangedParams(ftc, relatedParams);
			observedParams = changedParams;
			extraTc = Util.genExtraTc(valuesOfEachParam, ftc, changedParams);
		} while(!Util.isFailTc(extraTc, allFtcs, null));
		
		return relatedParams;
	}
	
}
