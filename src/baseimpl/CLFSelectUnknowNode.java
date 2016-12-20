package baseimpl;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import base.ISelectUnknowNode;
import common.Util;
import entries.SchemaNode;
import expe.Expe;
import locatefault.Trt;

/**
 * 组合测试长度优先搜索：比如4维的组合测试，那么先从长度为4的子模式节点开始搜索
 * @author lglyoung
 *
 */
public class CLFSelectUnknowNode implements ISelectUnknowNode {
	private boolean isFirst = true, flag = false;
	private SchemaNode head = null;
	ISelectUnknowNode lpSelectUnknowNode = new BFSSelectUnknowNode();

	@Override
	public List<SchemaNode> selectUnknowNode(Deque<SchemaNode> stack) {
		if (isFirst) {
			head = stack.peek();
		}
		isFirst = false;
		List<SchemaNode> unknowNodes = new ArrayList<>();
		Map<SchemaNode, SchemaNode> map = new HashMap<SchemaNode, SchemaNode>();	//保存已经生成的节点，目的是去重，用Map而不用Set的目的是方便取到已经存在的节点	
		SchemaNode pollNode = null;
		
		List<SchemaNode> tmpNodes = null;	
		while (!stack.isEmpty() && !flag) {
			pollNode = stack.poll();
			tmpNodes = pollNode.getDirectChildren();
			for (SchemaNode tmpNode : tmpNodes) {
				if (!map.containsKey(tmpNode)) {
					stack.offer(tmpNode);
					map.put(tmpNode, tmpNode);
				}
			}
			
			//如果模式节点的长度等于组合测试的维度
			int lenOfCt = Util.schemaLen(pollNode.getSche());
			if (lenOfCt == Expe.lenOfCt) {
				if (pollNode.getState().equals(Trt.UNKNOW)) {
					unknowNodes.add(pollNode);
					return unknowNodes;  
				}
			}
		}
		
		//如果组合测试长度的层遍历完，再调用LPSelectUnknowNode.java
		if (!flag) stack.push(head);
		flag = true;
		return lpSelectUnknowNode.selectUnknowNode(stack);
	}
	
}
