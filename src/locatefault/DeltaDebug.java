package locatefault;

import java.util.List;

import common.IDeltaDebug;
import common.ILocateFault;
import common.Util;

/**
 * 定位单个故障模式
 * @author lglyoung
 *
 */
public class DeltaDebug implements ILocateFault {
	private IDeltaDebug dd;
	
	public DeltaDebug(IDeltaDebug dd) {
		this.dd = dd;
	}
	
	@Override
	public void locateFault(int[] valuesOfEachParam, List<int[]> allFtcs, List<int[]> ftcs, List<int[]> ptcs,
			List<int[]> extraTcs, List<int[]> faultSchemas) {
		
		//遍历组合测试的所有失效测试用例
		for(int[] ftc : ftcs) {		
			List<Integer> unchangedParams = dd.dd(valuesOfEachParam, ftc, allFtcs, ptcs, extraTcs);
			faultSchemas.add(Util.genFaultSchema(ftc, unchangedParams));
		}
	}
	
}
