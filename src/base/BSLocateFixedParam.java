package base;

import java.util.ArrayList;
import java.util.List;

import common.Util;

/**
 * 二分法查找故障模式的某一个固定参数
 * @author lglyoung
 *
 */
public class BSLocateFixedParam implements ILocateFixedParam {

	@Override
	public int locateFixedParam(int[] vs, int[] s, int k, List<Integer> cfree, List<Integer> interaction,
			List<int[]> allFtcs, List<int[]> extraTcs) {
		//获取候选参数集合
		List<Integer> ccand = Util.getCcand(k, cfree, interaction);
		
		//固定interaction参数，生成一个附加测试用例，如果是失效测试用例，那么直接返回-1
		List<Integer> changedParams = new ArrayList<Integer>(cfree);
		changedParams.addAll(ccand);
		int[] extraTc = Util.genExtraTc(s, vs, changedParams);
		extraTcs.add(extraTc);
		if (ccand.size() == 0 || Util.isFailTc(extraTc, allFtcs, null)) return -1; 
		
		//二分定位
		List<List<Integer>> groups = null;
		while (ccand.size() > 1) {
			//将候选参数分为两组
			groups = Util.paramGroups(ccand, 2);
			
			//生成附加测试用例
			changedParams.clear();
			changedParams.addAll(cfree);
			changedParams.addAll(groups.get(0));
			extraTc = Util.genExtraTc(s, vs, changedParams);
			extraTcs.add(extraTc);
			ccand.clear();
			if (!Util.isFailTc(extraTc, allFtcs, null)) {
				ccand.addAll(groups.get(0));
			} else {
				ccand.addAll(groups.get(1));
				cfree.addAll(groups.get(0));
			}
		}
		return ccand.get(0);
	}
	
}
