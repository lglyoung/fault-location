package base;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import common.Util;

public class Simplification implements IDeltaDebug {

	@Override
	public List<Integer> dd(int[] valuesOfEachParam, int[] ftc, List<int[]> allFtcs, List<int[]> ptcs,
			List<int[]> extraTcs) {
		//初始化关注模式
		List<Integer> unchangedParams = new ArrayList<Integer>();
		for(int i = 0; i < valuesOfEachParam.length; i++) {
			unchangedParams.add(i);
		}

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
				extraTcs.add(extraTc);
				
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
		
		return unchangedParams;
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
