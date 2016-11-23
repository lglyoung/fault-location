package common;

import java.util.List;

/**
 * 差异定位算法
 * @author lglyoung
 *
 */
public interface IDeltaDebug {
	public List<Integer> dd(int[] valuesOfEachParam, int[] ftc, List<int[]> allFtcs, List<int[]> ptcs,
			List<int[]> extraTcs);
}
