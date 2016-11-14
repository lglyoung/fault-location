package deltadebugging;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import common.Util;

public class RI {
	/**
	 * RI错误定位方法
	 * @param valuesOfEachParam 如{3, 4, 5}表示第0个参数可以取3个值，第1个参数可以取4个值，第2个参数可以取5个值
	 * @param ftcs 失效测试用例集
	 * @param ptcs 通过测试用例集
	 * @param extraTcs 附加测试用例集(记得去重，又不能用Set<int[]>去重，因为int[]是对象)
	 * @param faultSchemas 故障模式集
	 */
	public static void ri(int[] valuesOfEachParam, List<int[]> ftcs, 
			List<int[]> ptcs, List<int[]> extraTcs, List<int[]> faultSchemas) {
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
				List<Integer> changedParams = genChangedParams(ftc, relatedParams);
				observedParams = changedParams;
				extraTc = Util.genExtraTc(valuesOfEachParam, ftc, changedParams);
			} while(!Util.isFailTc(extraTc, ftcs, ptcs));
			Util.addNotRepeatIntArray(Util.genFaultSchema(ftc, relatedParams), faultSchemas);
		}
	}
	
	/**
	 * 返回与故障无关的参数
	 * @param ftc 失效测试用例
	 * @param relatedParams 与故障相关的参数
	 * @return List<Integer> 
	 */
	public static List<Integer> genChangedParams(int[] ftc, List<Integer> relatedParams) {
		Set<Integer> allParams = new HashSet<Integer>();
		for(int i = 0; i < ftc.length; i++) {
			allParams.add(i);
		}
		allParams.removeAll(relatedParams);
		return new ArrayList<Integer>(allParams);
	}
}
