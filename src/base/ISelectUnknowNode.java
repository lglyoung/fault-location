package base;

import java.util.Deque;
import java.util.List;

import entries.SchemaNode;

/**
 * 挑选状态未知的模式节点
 * @author lglyoung
 *
 */
public interface ISelectUnknowNode {
	/**
	 * 返回一个或多个状态未知的节点
	 * @param stack 
	 * @return List<SchemaNode> 之所以返回一个数组，目的是兼容最长路径搜索
	 */
	public List<SchemaNode> selectUnknowNode(Deque<SchemaNode> stack);
}
