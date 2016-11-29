package baseimpl;

import java.util.List;

import base.ICreateScheTree;
import entries.SchemaNode;

/**
 * 根据ptcs、已知的故障模式来创建修建后的关系树
 * @author lglyoung
 *
 */
public class CuttedScheTree implements ICreateScheTree {

	@Override 
	public SchemaNode create(int[] ftc, List<int[]> ptcs) {
		
		return null;
	}
	
}
