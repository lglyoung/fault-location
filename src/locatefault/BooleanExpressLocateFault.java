package locatefault;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import base.ILocateFault;
import common.Util;

/**
 * 布尔表达式故障定位方法
 * 思路：	1、队列中保存的都是故障模式
 * 		2、出列时，生成所有的直接子模式 && 判断是否是极小故障模式
 * 		3、入列时，要判断当前模式是否是故障模式。#如何判断#。如果所有的直接子模式都是健康模式，则保存该极小故障模式。
 * @author lglyoung
 *
 */
public class BooleanExpressLocateFault implements ILocateFault {

	@Override
	public void locateFault(int[] valuesOfEachParam, List<int[]> allFtcs, List<int[]> ftcs, List<int[]> ptcs,
			List<int[]> extraTcs, List<int[]> faultSchemas) {
		Set<String> ftcsSet = new HashSet<String>();
		ftcsSet.addAll(Util.intArrayListToStrScheSet(ftcs));
		Deque<int[]> queue = new ArrayDeque<int[]>(1024);
		for (int i = 0; i < ftcs.size(); i++) {
			int[] ftc = ftcs.get(i);
			List<int[]> passExtraTcs = new ArrayList<int[]>(ptcs);	//保存通过测试用例集+通过附加测试用例集
			boolean isMFS = true;			//如果有一个直接子模式不是健康模式，则isMFS = false;
			queue.clear();
			queue.offer(ftc);
			Map<String, Boolean> m = new HashMap<String, Boolean>();	//保存已经处理过的模式，值为true表示健康
			while (!queue.isEmpty()) {
				isMFS = true;
				int[] pollSche = queue.poll();
				List<int[]> directSubSches = Util.genDirectSubSchemas(pollSche);	//生成所有直接子模式
				for (int[] subSche : directSubSches) {
					String subScheStr = Util.intArrayToStr(subSche);
					if (!m.containsKey(subScheStr)) {
						m.put(subScheStr, true);
						boolean isPassSche = Util.isPassSche(subSche, passExtraTcs);
						
						//如果当前子模式无法通过已有的附加测试用例和通过测试用例来确定是健康模式，则生成附加测试用例，如果生成的附加测试用例是失效测试用例，则该模式一定是故障模式
						if (!isPassSche) {
							int[] extraTc = Util.genExtraTc(valuesOfEachParam, ftc, subSche);
							extraTcs.add(extraTc);		//保存附加测试用例
							if (Util.isFailTc(extraTc, allFtcs, null)) {
								queue.offer(subSche);	//入列	
								Util.addFailTc(extraTc, ftcs, ftcsSet);	//将发现到的新的附加测试用例添加到失效测试用例集中
								isMFS = false;			//只要有一个子模式是故障模式，那么当前出对列的故障模式就不是极小故障模式
								m.put(subScheStr, false);
							} else {
								passExtraTcs.add(extraTc);				
							}
						}
					} else {
						if (!m.get(subScheStr)) isMFS = false;
					}
				}
				
				//如果当前出对列的故障模式是极小故障模式，则保存
				if (isMFS) 
					faultSchemas.add(pollSche); 
				
			}
		}
	}
	
}
