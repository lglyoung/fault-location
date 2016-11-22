package common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
		Set<String> extraTcsStrSet = new HashSet<String>();	//用Set<String>去重附加测试用例集
		List<Integer> unrelatedParams = new ArrayList<Integer>();
		
		while(observedParams.size() != 1) {
			List<List<Integer>> groups = Util.paramGroups(observedParams, 2);
			
			//changedParams目的是用来生成附加测试 用例
			List<Integer> changedParams = new ArrayList<Integer>(unrelatedParams); 
			changedParams.addAll(groups.get(0));
			
			int[] extraTc = Util.genExtraTc(valuesOfEachParam, ftc, changedParams);
			
			//保存附加测试用例
			extraTcsStrSet.add(Util.intArrayToStr(extraTc));
			
			if(Util.isFailTc(extraTc, ftcs, ptcs)) {
				observedParams = groups.get(1);
				unrelatedParams.addAll(groups.get(0));
			} else {
				observedParams = groups.get(0);
			}
		}
		extraTcs.addAll(Util.strScheSetToIntArrayList(extraTcsStrSet));
		return observedParams.get(0);
	}
}
