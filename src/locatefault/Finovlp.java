package locatefault;

import java.util.ArrayList;
import java.util.List;

import common.ILocateFault;
import common.ILocateFixedParam;
import common.Util;

public class Finovlp implements ILocateFault {
	private ILocateFixedParam lfp;
	
	public Finovlp(ILocateFixedParam lfp) {
		this.lfp = lfp;
	}
	
	@Override
	public void locateFault(int[] valuesOfEachParam, List<int[]> allFtcs, List<int[]> ftcs, List<int[]> ptcs,
			List<int[]> extraTcs, List<int[]> faultSchemas) {
		for (int i = 0; i < ftcs.size(); i++) {
			int[] ftc = ftcs.get(i);
			List<Integer> relatiedParams = new ArrayList<Integer>();
			int[] extraTc = null;
			List<Integer> interaction = null;
			while (true) {
				extraTc = Util.genExtraTc(valuesOfEachParam, ftc, relatiedParams);
				extraTcs.add(extraTc);
				if (!Util.isFailTc(extraTc, allFtcs, null)) break;
				interaction = Util.fic(ftc, valuesOfEachParam, valuesOfEachParam.length, 
						relatiedParams, allFtcs, extraTcs, lfp);
				relatiedParams.addAll(interaction);
				faultSchemas.add(Util.genFaultSchema(ftc, interaction));
				if (interaction.size() == 0) break; 
			}
		}
		
		//对extraTcs, faultSchemas进行去重
		Util.delRepeat(extraTcs);
		Util.delRepeat(faultSchemas);
	}
}
