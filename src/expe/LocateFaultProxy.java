package expe;

import java.util.List;

import common.ILocateFault;
import common.Util;

/**
 * 故障定位方法的代理类
 * @author lglyoung
 *
 */
public class LocateFaultProxy implements ILocateFault {
	private ILocateFault lf;
	
	public LocateFaultProxy(ILocateFault lf) {
		this.lf = lf;
	}
	
	@Override
	public void locateFault(int[] valuesOfEachParam, List<int[]> allFtcs, List<int[]> ftcs, List<int[]> ptcs,
			List<int[]> extraTcs, List<int[]> faultSchemas) {
		lf.locateFault(valuesOfEachParam, allFtcs, ftcs, ptcs, extraTcs, faultSchemas);
		
		//去重
		Util.delRepeat(extraTcs);
		Util.delRepeat(faultSchemas);
	}
	
}
