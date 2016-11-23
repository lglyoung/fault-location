package locatefault;

import java.util.ArrayList;
import java.util.List;

import common.ILocateFault;
import common.Util;

public class IterAIFL implements ILocateFault {

	@Override
	public void locateFault(int[] valuesOfEachParam, List<int[]> allFtcs, List<int[]> ftcs, List<int[]> ptcs, List<int[]> extraTcs,
			List<int[]> faultSchemas) {
		int tcLen = valuesOfEachParam.length;								//测试用例的长度
		List<int[]> scheSetT1 = Util.genScheSet(ftcs);
		List<int[]> scheSetT2 = Util.genScheSet(ptcs);
		List<int[]> preSuspSet = Util.arrDiffSet(scheSetT1, scheSetT2);		//前一次的可疑模式集
		
		List<int[]> curSuspSet = new ArrayList<int[]>();
		
		//目前暂未考虑"达到某个阈值时退出"这种情况
		for (int i = 1; i < tcLen && preSuspSet.size() != curSuspSet.size(); i++) {
			//生成附加测试用例集
			List<int[]> at = Util.genAT(ftcs, valuesOfEachParam);
			
			//保存附加测试用例
			extraTcs.addAll(at);
			
			//根据暴力实验得到的失效测试用例集，将附加测试用例集分成通过测试用例集和失效测试用例集
			List<int[]> at2 = Util.arrDiffSet(at, allFtcs);			//获取通过测试用例集
			List<int[]> scheSetAt2 = Util.genScheSet(at2);
			curSuspSet = Util.arrDiffSet(preSuspSet, scheSetAt2);
			preSuspSet = curSuspSet;
		}
		
		//返回可疑的极小故障模式集
		faultSchemas.addAll(curSuspSet.size() == 0 ? preSuspSet : curSuspSet);
		
		//对附加测试用例集进行去重
		Util.delRepeat(extraTcs);
	}	
	
}
