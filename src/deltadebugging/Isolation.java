package deltadebugging;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import common.Util;

public class Isolation {
	
	/**
	 * 获取一个与故障相关的参数所在的索引位置
	 * @param ftc 
	 * @param observedParams 关注模式
	 * @param valuesOfEachParam 如{3, 4, 5}表示第0个参数可以取3个值，第1个参数可以取4个值，第2个参数可以取5个值
	 * @param ftcs 失效测试用例集
	 * @param ptcs 通过测试用例集
	 * @param extraTcs 附加测试用例集(记得去重，又不能用Set<int[]>去重，因为int[]是对象)
	 * @return int 与故障相关的参数所在的索引位置
	 */
	public static int isolate(int[] ftc, List<Integer> observedParams, int[] valuesOfEachParam, List<int[]> ftcs, 
			List<int[]> ptcs, List<int[]> extraTcs) {
		if((observedParams == null) || (observedParams.size() == 0)) {
			return -1;
		}
		
		List<Integer> unrelatedParams = new ArrayList<Integer>();
		
		while(observedParams.size() != 1) {
			List<List<Integer>> groups = Util.paramGroups(observedParams, 2);
			
			//changedParams目的是用来生成附加测试 用例
			List<Integer> changedParams = new ArrayList<Integer>(unrelatedParams); 
			changedParams.addAll(groups.get(0));
			
			int[] extraTc = Util.genExtraTc(valuesOfEachParam, ftc, changedParams);
			
			//保存附加测试用例
			Util.addNotRepeatIntArray(extraTc, extraTcs);
			
			if(Util.isFailTc(extraTc, ftcs, ptcs)) {
				observedParams = groups.get(1);
				unrelatedParams.addAll(groups.get(0));
			} else {
				observedParams = groups.get(0);
			}
		}
		return observedParams.get(0);
	}
	
	@Test
	public void test() {
		List<Integer> observedParams = new ArrayList<Integer>();
		observedParams.add(0);
		observedParams.add(1);
		observedParams.add(2);
		observedParams.add(3);
		int[] valuesOfEachParam = {2, 2, 2, 2, 2};
		int[] ftc1 = {0, 0, 0, 1, 0};
		int[] ftc2 = {0, 1, 0, 1, 0};
		int[] ftc3 = {1, 0, 0, 0, 0};
		int[] ftc4 = {1, 0, 1, 0, 0};
		
		List<int[]> ftcs = new ArrayList<int[]>();
		ftcs.add(ftc1);
		ftcs.add(ftc2);
		ftcs.add(ftc3);
		ftcs.add(ftc4);
		List<int[]> extraTcs = new ArrayList<int[]>();
		System.out.println(Isolation.isolate(ftc4, observedParams, valuesOfEachParam, ftcs, null, extraTcs));
	}
}
