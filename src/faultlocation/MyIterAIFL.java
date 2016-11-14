package faultlocation;

import java.util.ArrayList;
import java.util.List;

import common.Util;

public class MyIterAIFL implements IStrategy {

	@Override
	public void faultLocating(int[] valuesOfEachParam, List<int[]> allFtcs, List<int[]> ftcs, List<int[]> ptcs, List<int[]> extraTcs,
			List<int[]> faultSchemas) {
		int tcLen = valuesOfEachParam.length;								//测试用例的长度
		List<int[]> suspScheSet = new ArrayList<int[]>();
		
		//遍历失效测试用例集
		for (int[] tmpFtc : ftcs) {
			for (int[] tmpPtc : ptcs) {
				int[] tmpSche = Util.genMostLongSche(tmpFtc, tmpPtc);
				
				//如果tmpSche不存在suspSet集合中，那么就将tmpSche添加进去
				Util.addNotRepeatIntArray(tmpSche, suspScheSet);
			}
		}
	}	
	
}
