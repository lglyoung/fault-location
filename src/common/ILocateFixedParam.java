package common;

import java.util.List;

public interface ILocateFixedParam {
	/**
	 * 查找故障模式的某一个固定参数
	 * @param vs 种子测试用例（即已知的失效测试用例）
	 * @param s 每个元素与对应的参数的值的个数一一对应
	 * @param k 表示参数的个数
	 * @param cfree 不引发故障的参数集合
	 * @param interaction 引发故障的参数集合
	 * @param allFtcs SUT的所有失效测试用例集
	 * @param extraTcs 定位过程中产生的附加测试用例集
	 * @return
	 */
	public int locateFixedParam(int[] vs, int[] s, int k, List<Integer> cfree, List<Integer> interaction,
			List<int[]> allFtcs, List<int[]> extraTcs);
}
