package baseimpl;

import java.util.List;

import base.ILocateFixedParam;
import common.Util;

public class LocateFixedParam implements ILocateFixedParam {

	@Override
	public int locateFixedParam(int[] vs, int[] s, int k, List<Integer> cfree, List<Integer> interaction,
			List<int[]> allFtcs, List<int[]> extraTcs) {
		List<Integer> ccand = Util.getCcand(k, cfree, interaction);
		
		int[] extraTc = null;				//附加测试用例
		for (Integer curParam : ccand) {
			cfree.add(curParam);
			extraTc = Util.genExtraTc(s, vs, cfree);
			extraTcs.add(extraTc);
			if (!Util.isFailTc(extraTc, allFtcs, null)) {
				cfree.remove(curParam);		//在前面生成附加测试用例的时候，已经将curParam加入到cfree中了，所以要删除
				return curParam;
			}
		}
		return -1;
	}
	
}
