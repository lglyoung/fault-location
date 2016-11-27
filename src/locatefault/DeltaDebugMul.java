package locatefault;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import base.IDeltaDebug;
import base.ILocateFault;
import common.Util;

/**
 * 定位多个非重叠的故障模式
 * @author lglyoung
 *
 */
public class DeltaDebugMul implements ILocateFault {
	private IDeltaDebug dd;
	
	public DeltaDebugMul(IDeltaDebug dd) {
		this.dd = dd;
	}
	
	@Override
	public void locateFault(int[] valuesOfEachParam, List<int[]> allFtcs, List<int[]> ftcs, List<int[]> ptcs,
			List<int[]> extraTcs, List<int[]> faultSchemas) {
		
		//遍历组合测试的所有失效测试用例
		for(int[] ftc : ftcs) {			
			//不重叠的故障模式的相关参数集合
			Set<Integer> allRelatedParamSet = new HashSet<Integer>();	
					
			//这个do while的作用是定位多个不重叠的故障模式 2016.11.21 by lglyoung
			boolean hasMuliSche = false;
			int[] extraTc = null;
			List<Integer> unchangedParams = null;

			do {	
				unchangedParams = dd.dd(valuesOfEachParam, ftc, allFtcs, ptcs, extraTcs);
				faultSchemas.add(Util.genFaultSchema(ftc, unchangedParams));
				
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
	
}
