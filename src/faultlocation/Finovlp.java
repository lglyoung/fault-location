package faultlocation;

import java.util.ArrayList;
import java.util.List;

import common.ILocateFixedParam;
import common.IStrategy;
import common.Util;

/**
 * 基于FIC(或FIC_BS)的非重叠的多故障定位方法
 * @author lglyoung
 *
 */
public class Finovlp implements IStrategy {
	private ILocateFixedParam lfp;	//接口：定位故障模式的一个固定参数
	
	public Finovlp(ILocateFixedParam lfp) {
		this.lfp = lfp;
	}

	@Override
	public void faultLocating(int[] valuesOfEachParam, List<int[]> allFtcs, List<int[]> ftcs, List<int[]> ptcs,
			List<int[]> extraTcs, List<int[]> faultSchemas) {
		List<Integer> interaction = null;
		for (int[] ftc : ftcs) {
			List<Integer> relatiedParams = new ArrayList<Integer>();
			int[] extraTc = null;
			while (true) {
				extraTc = Util.genExtraTc(valuesOfEachParam, ftc, relatiedParams);
				extraTcs.add(extraTc);
				if (!Util.isFailTc(extraTc, ftcs, null)) break;
				interaction = Fic(ftc, valuesOfEachParam, valuesOfEachParam.length, 
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
	
	/**
	 * FIC故障定位算法
	 * @param vs 种子测试用例（即已知的失效测试用例）
	 * @param s 每个元素与对应的参数的值的个数一一对应
	 * @param k 表示参数的个数
	 * @param ctabu FIC定位出来的新的故障模式的非固定参数的集合
	 * @param lfp 接口：定位故障模式的一个固定参数
	 * @return List<Integer> 故障模式的固定参数集合
	 */
	public List<Integer> Fic(int[] vs, int[] s, int k, List<Integer> ctabu, 
			List<int[]> allFtcs, List<int[]> extraTcs, ILocateFixedParam lfp) {
		List<Integer> interaction = new ArrayList<Integer>();
		List<Integer> Cfree = new ArrayList<Integer>(ctabu);
		int param = -1;		//故障模式的一个固定参数
		while (true) {
			param = lfp.locateFixedParam(vs, s, k, Cfree, interaction, allFtcs, extraTcs);
			if (param == -1) break;
			interaction.add(param);
		}
		return interaction;
	}	
}
