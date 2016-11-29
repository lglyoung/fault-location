package baseimpl;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import base.ISelectUnknowNode;
import entries.SchemaNode;
import locatefault.Trt;

/**
 * 用贪心算法挑选状态未知的节点
 * 贪心值的计算：如果当前节点的状态已知，那么它的贪心值为-1，否则为min(未知子节点的总数，未知父节点的总数);
 * @author lglyoung
 *
 */
public class GreedSelectUnknowNode implements ISelectUnknowNode {

	@Override
	public List<SchemaNode> selectUnknowNode(Deque<SchemaNode> stack) {
		SchemaNode head = stack.peek();
		
		//更新节点的贪心值
		updateGreedVal(head);

		//挑选贪心值最大的未知节点
		int maxGreedVal = -1;
		SchemaNode popNode = null, tmpNode = null, maxNode = null;
		List<SchemaNode> nodes = null;
		Map<SchemaNode, SchemaNode> map = new HashMap<SchemaNode, SchemaNode>();	//保存已经生成的节点，目的是去重，用Map而不用Set的目的是方便取到已经存在的节点	
		map.put(head, head);
		while (!stack.isEmpty()) {
			popNode = stack.pop();
			
			//处理
			if (popNode.getGreedVal() > maxGreedVal) {
				maxGreedVal = popNode.getGreedVal();
				maxNode = popNode;
			}
			
			nodes = popNode.getDirectChildren();
			for (int i = nodes.size() - 1; i >= 0; i--) {
				tmpNode = nodes.get(i);
				if (!map.containsKey(tmpNode)) {
					stack.push(tmpNode);
					map.put(tmpNode, tmpNode);
				}
			}
			
		}
		
		//初始化栈
		stack.push(head);
		
		//返回结果
		List<SchemaNode> saveMaxNode = new ArrayList<SchemaNode>();
		if (maxNode != null) {
			saveMaxNode.add(maxNode);
			return saveMaxNode;
		}
		return null;
	}

	/**
	 * 更新节点的贪心值
	 * @param head
	 */
	private void updateGreedVal(SchemaNode head) {
		Deque<SchemaNode> stack = new ArrayDeque<SchemaNode>(Trt.STACK_INIT_SIZE);
		stack.push(head);
		SchemaNode popNode = null, tmpNode = null;
		List<SchemaNode> nodes = null;
		Map<SchemaNode, SchemaNode> map = new HashMap<SchemaNode, SchemaNode>();	//保存已经生成的节点，目的是去重，用Map而不用Set的目的是方便取到已经存在的节点	
		map.put(head, head);
		while (!stack.isEmpty()) {
			popNode = stack.pop();
			
			//处理
			popNode.setGreedVal();
			
			nodes = popNode.getDirectChildren();
			for (int i = nodes.size() - 1; i >= 0; i--) {
				tmpNode = nodes.get(i);
				if (!map.containsKey(tmpNode)) {
					stack.push(tmpNode);
					map.put(tmpNode, tmpNode);
				}
			}
			
		}
	}
}
