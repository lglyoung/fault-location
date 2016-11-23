package locatefault;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import common.ILocateFault;
import common.Util;

public class Simplification implements ILocateFault {

	@Override
	public void locateFault(int[] valuesOfEachParam, List<int[]> allFtcs, List<int[]> ftcs, List<int[]> ptcs,
			List<int[]> extraTcs, List<int[]> faultSchemas) {
		
		//初始化关注模式
		List<Integer> initUnchangeParams = new ArrayList<Integer>();
		for(int i = 0; i < valuesOfEachParam.length; i++) {
			initUnchangeParams.add(i);
		}
		
		
		//遍历组合测试的所有失效测试用例
		for(int[] ftc : ftcs) {			
			//不重叠的故障模式的相关参数集合
			Set<Integer> allRelatedParamSet = new HashSet<Integer>();	
					
			//这个do while的作用是定位多个不重叠的故障模式 2016.11.21 by lglyoung
			boolean hasMuliSche = false;

			do {	
				List<Integer> unchangedParams = new ArrayList<Integer>(initUnchangeParams);
				int g = 2;
				int[] extraTc = null;
				
				//当unchangeParams.size()不是  < 2
				while(unchangedParams.size() >= 2) {
					List<List<Integer>> groups = Util.paramGroups(unchangedParams, g);
					boolean isFtc = false;
					for(int i = 0; i < groups.size(); i++) {
						List<Integer> changedParams = groups.get(i);
						extraTc = Util.genExtraTc(valuesOfEachParam, ftc, changedParams);
						
						//保存附加测试用例
						Util.addNotRepeatIntArray(extraTc, extraTcs);
						
						isFtc = Util.isFailTc(extraTc, allFtcs, null);
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
				
				//判断是否存在多个不重叠的故障模式
				//2016.11.21 by lglyoung
				if (Util.notContainsAnyOneOf(allRelatedParamSet, unchangedParams)) {
					allRelatedParamSet.addAll(unchangedParams);
					extraTc = Util.genExtraTc(valuesOfEachParam, ftc, unchangedParams);
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
	}
	
	/**
	 * 从unchangedParams中删除changedParams
	 * @param unchangedParams 未被修改的参数
	 * @param changedParams 被修改的参数
	 * @return 返回新的未被修改的参数
	 */
	private List<Integer> updateUnchangedParams(List<Integer> unchangedParams, List<Integer> changedParams) {
		HashSet<Integer> s = new HashSet<Integer>(unchangedParams);	
		s.removeAll(changedParams);
		List<Integer> newUnchangedParams = new ArrayList<Integer>(s);
		return newUnchangedParams;
	}
	

	
}
