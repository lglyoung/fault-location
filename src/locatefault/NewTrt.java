package locatefault;

import java.util.List;

import base.ILocateFault;

/**
 * 改进版的关系树模型
 * 思路：（按层遍历）一边创建模式节点，一边判断该节点是否是健康模式，如果是健康模式，则不在往下创建子节点
 * @author lglyoung
 *
 */
public class NewTrt implements ILocateFault {

	@Override
	public void locateFault(int[] valuesOfEachParam, List<int[]> allFtcs, List<int[]> ftcs, List<int[]> ptcs,
			List<int[]> extraTcs, List<int[]> faultSchemas) {
		
	}
}
