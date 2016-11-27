package base;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entries.SchemaNode;
import locatefault.Trt;

/**
 * 广度优先搜索的方法选择状态未知的节点
 * @author lglyoung
 *
 */
public class BFSSelectUnknowNode implements ISelectUnknowNode {

	@Override
	public List<SchemaNode> selectUnknowNode(Deque<SchemaNode> stack) {
		List<SchemaNode> unknowNodes = new ArrayList<>();
		Map<SchemaNode, SchemaNode> map = new HashMap<SchemaNode, SchemaNode>();	//保存已经生成的节点，目的是去重，用Map而不用Set的目的是方便取到已经存在的节点	
		SchemaNode pollNode = null;
		
		List<SchemaNode> tmpNodes = null;	
		while (!stack.isEmpty()) {
			pollNode = stack.poll();
			tmpNodes = pollNode.getDirectChildren();
			for (SchemaNode tmpNode : tmpNodes) {
				if (!map.containsKey(tmpNode)) {
					stack.offer(tmpNode);
					map.put(tmpNode, tmpNode);
				}
			}
			
			if (pollNode.getState().equals(Trt.UNKNOW)) {
				unknowNodes.add(pollNode);
				return unknowNodes;  
			}
		}
		return null;
	}
	
}
