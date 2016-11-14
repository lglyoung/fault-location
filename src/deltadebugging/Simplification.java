package deltadebugging;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import common.Util;

public class Simplification {
	/**
	 * simplification错误定位方法
	 * @param valuesOfEachParam 如{3, 4, 5}表示第0个参数可以取3个值，第1个参数可以取4个值，第2个参数可以取5个值
	 * @param ftcs 失效测试用例集
	 * @param ptcs 通过测试用例集
	 * @param extraTcs 附加测试用例集(记得去重，又不能用Set<int[]>去重，因为int[]是对象)
	 * @param faultSchemas 故障模式集
	 */
	public static void simplify(int[] valuesOfEachParam, List<int[]> ftcs, 
			List<int[]> ptcs, List<int[]> extraTcs, List<int[]> faultSchemas) {
		
		List<Integer> initUnchangeParams = new ArrayList<Integer>();
		for(int i = 0; i < valuesOfEachParam.length; i++) {
			initUnchangeParams.add(i);
		}
		
		//遍历所有的失效测试用例
		for(int[] ftc : ftcs) {	
			List<Integer> unchangedParams = new ArrayList<Integer>(initUnchangeParams);
			int g = 2;
			
			//当unchangeParams.size()不是  < 2
			while(unchangedParams.size() >= 2) {
				List<List<Integer>> groups = Util.paramGroups(unchangedParams, g);
				boolean isFtc = false;
				for(int i = 0; i < groups.size(); i++) {
					List<Integer> changedParams = groups.get(i);
					int[] extraTc = Util.genExtraTc(valuesOfEachParam, ftc, changedParams);
					
					//保存附加测试用例
					Util.addNotRepeatIntArray(extraTc, extraTcs);
					
					isFtc = Util.isFailTc(extraTc, ftcs, ptcs);
					if(isFtc) {
						unchangedParams = updateUnchangedParams(unchangedParams, changedParams);
						g = Math.max(g-1, 2);
						break;
					}
				}
				if(!isFtc) {
					if(g == unchangedParams.size()) {
						break;
					} else {
						g = Math.min(g*2, unchangedParams.size());
					}
				}
			}
			
			//当unchangeParams.size() < 2
			Util.addNotRepeatIntArray(Util.genFaultSchema(ftc, unchangedParams), faultSchemas);
		}
	}
		
	/**
	 * 从unchangedParams中删除changedParams
	 * @param unchangedParams 未被修改的参数
	 * @param changedParams 被修改的参数
	 * @return 返回新的未被修改的参数
	 */
	public static List<Integer> updateUnchangedParams(List<Integer> unchangedParams, List<Integer> changedParams) {
		HashSet<Integer> s = new HashSet<Integer>(unchangedParams);	
		s.removeAll(changedParams);
		List<Integer> newUnchangedParams = new ArrayList<Integer>(s);
		return newUnchangedParams;
	}
	
}
