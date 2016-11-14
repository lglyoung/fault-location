package deltadebugging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import common.Util;

public class SRI {
	/**
	 * SRI错误定位方法
	 * @param valuesOfEachParam 如{3, 4, 5}表示第0个参数可以取3个值，第1个参数可以取4个值，第2个参数可以取5个值
	 * @param ftcs 失效测试用例集
	 * @param ptcs 通过测试用例集
	 * @param extraTcs 附加测试用例集(记得去重，又不能用Set<int[]>去重，因为int[]是对象)
	 * @param faultSchemas 故障模式集
	 */
	public static void sri(int[] valuesOfEachParam, List<int[]> ftcs, List<int[]> ptcs, 
			List<int[]> extraTcs, List<int[]> faultSchemas) {
		for(int i = 0; i < ftcs.size(); i++) {
			int[] ftc = ftcs.get(i);
			List<Integer> relatedParams = new ArrayList<Integer>();
			int[] extraTc = null;
			do{
				List<Integer> observedParams = genObservedParams(valuesOfEachParam, ftc, relatedParams, ptcs);
				int relatedParam = Isolation.isolate(ftc, observedParams, valuesOfEachParam, ftcs, ptcs, extraTcs);
				relatedParams.add(relatedParam);
				List<Integer> changedParams = RI.genChangedParams(ftc, relatedParams);
				extraTc = Util.genExtraTc(valuesOfEachParam, ftc, changedParams);
			}while(!Util.isFailTc(extraTc, ftcs, ptcs));
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
	public static List<Integer> genObservedParams(int[] valuesOfEachParam, int[] ftc, List<Integer> relatedParams, List<int[]> ptcs) {
		List<Integer> observedParams = new ArrayList<Integer>();
		
		//求出"覆盖表通过测试用例集"中含有relatedParams的所有通过测试用例集
		List<int[]> candidatePtcs = new ArrayList<int[]>();
		if(ptcs != null) {
			boolean isInclude = true;
			for(int i = 0; i < ptcs.size(); i++) {
				int[] tmpTc = ptcs.get(i);
				for(int j = 0; i < relatedParams.size(); j++) {
					if(ftc[j] != tmpTc[j]) {
						isInclude = false;
					}
				}
				if(isInclude) {
					candidatePtcs.add(tmpTc);
				}
				isInclude = true;
			}
		}

		//求candidatePtcs中与ftc相似度最大的tc
		int maxDegreeOfSimilary = 0;	//当前最大相似度
		int[] TPass = null;				//所对应的测试用例
		for(int[] tmpTc : candidatePtcs) {
			int s = Util.degreeOfSimilary(ftc, tmpTc);	//获取相似度
			if(s > maxDegreeOfSimilary) {
				maxDegreeOfSimilary = s;
				TPass = tmpTc;
			}
		}
		
		//生成关注模式
		if(TPass == null) {
			TPass = Util.genExtraTc(valuesOfEachParam, ftc, RI.genChangedParams(ftc, relatedParams));
		}
		for(int i = 0; i < ftc.length; i++) {
			if(ftc[i] != TPass[i]) {
				observedParams.add(i);
			}
		}
		
		return observedParams;
	}

	/**
	 * 返回覆盖表中所有的通过测试用例
	 * @param ftcs 失效测试用例集
	 * @param coverTable 覆盖表
	 * @return List<int[]>
	 */
	public static List<int[]> genCoverTablePtcs(List<int[]> ftcs, List<int[]> coverTable) {
		List<int[]> coverTablePtcs = new ArrayList<int[]>();
		boolean isFtc = false;
		for(int[] tmpTc : coverTable) {
			for(int[] tmpFtc : ftcs) {
				if(Arrays.equals(tmpTc, tmpFtc)) {
					isFtc = true;
					break;
				}
			}
			if(!isFtc) {
				coverTablePtcs.add(tmpTc);
			}
			isFtc = false;
		}
		return coverTablePtcs;
	}
	

}
