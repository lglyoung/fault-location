package common;

import java.util.List;

public interface IStrategy {
	/**
	 * 错误定位方法
	 * @param valuesOfEachParam 如{3, 4, 5}表示第0个参数可以取3个值，第1个参数可以取4个值，第2个参数可以取5个值
	 * @param allFtcs SUT的所有失效测试用例集
	 * @param ftcs 组合测试的失效测试用例集
	 * @param ptcs 组合测试的通过测试用例集
	 * @param extraTcs 附加测试用例集(记得去重，又不能用Set<int[]>去重，因为int[]是对象)
	 * @param faultSchemas 故障模式集
	 */
	public void faultLocating(int[] valuesOfEachParam, List<int[]> allFtcs, List<int[]> ftcs, 
			List<int[]> ptcs, List<int[]> extraTcs, List<int[]> faultSchemas);
}
