package base;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entries.SchemaNode;
import locatefault.Trt;

/**
 * 深度优先搜索待测节点
 * @author lglyoung
 *
 */
public class DFSSelectUnknowNode implements ISelectUnknowNode {

	@Override
	public List<SchemaNode> selectUnknowNode(Deque<SchemaNode> stack) {
		List<SchemaNode> unknowNodes = new ArrayList<>();
		SchemaNode pop = null;
		List<SchemaNode> nodes = null;
		Map<SchemaNode, SchemaNode> map = new HashMap<SchemaNode, SchemaNode>();	//保存已经生成的节点，目的是去重，用Map而不用Set的目的是方便取到已经存在的节点	

		while (!stack.isEmpty()) {
			pop = stack.pop();
			map.put(pop, pop);
			if (pop.getState().equals(Trt.UNKNOW)) {
				unknowNodes.add(pop);
			}
			nodes = pop.getDirectChildren();
			for (int i = nodes.size() - 1; i >= 0; i--) {
				if (!map.containsKey(nodes.get(i))) {
					stack.push(nodes.get(i));
				}
			}
			
			//如果选到待测节点，则返回。这个操作要在压栈之后
			if (unknowNodes.size() > 0) return unknowNodes;
		}
		return null;
	}

}
