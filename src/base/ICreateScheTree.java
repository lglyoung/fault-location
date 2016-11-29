package base;

import java.util.List;

import entries.SchemaNode;

/**
 * 创建并初始化关系模式树
 * @author lglyoung
 *
 */
public interface ICreateScheTree {

	public SchemaNode create(int[] ftc, int[] valuesOfEachParam, List<int[]> allFtcs, List<int[]> ftcs, List<int[]> ptcs, List<int[]> extraTcs,
			List<int[]> faultSchemas);
	
}
