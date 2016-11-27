package locatefault;

import java.util.ArrayList;
import java.util.List;

import base.ILocateFault;
import base.ILocateFixedParam;
import common.Util;

/**
 * FIC定位单个故障模式
 * @author lglyoung
 *
 */
public class Fic implements ILocateFault {
	private ILocateFixedParam lfp;
	
	public Fic(ILocateFixedParam lfp) {
		this.lfp = lfp;
	}

	@Override
	public void locateFault(int[] valuesOfEachParam, List<int[]> allFtcs, List<int[]> ftcs, List<int[]> ptcs,
			List<int[]> extraTcs, List<int[]> faultSchemas) {
		for (int i = 0; i < ftcs.size(); i++) {
			List<Integer> interaction = Util.fic(ftcs.get(i), valuesOfEachParam, valuesOfEachParam.length, 
					new ArrayList<Integer>(), allFtcs, extraTcs, lfp);
			faultSchemas.add(Util.genFaultSchema(ftcs.get(i), interaction));
		}
	}
}
