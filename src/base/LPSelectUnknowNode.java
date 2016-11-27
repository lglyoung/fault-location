package base;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import common.Util;
import entries.SchemaNode;
import locatefault.Trt;

/**
 * 最长路径法选择待测模式
 * @author lglyoung
 *
 */
public class LPSelectUnknowNode implements ISelectUnknowNode {

	/**
	 * 在当前实现下，stack当作队列来用
	 */
	@Override
	public List<SchemaNode> selectUnknowNode(Deque<SchemaNode> stack) {
		SchemaNode head = stack.peek();			//从栈中获取树的根节点
		List<SchemaNode> unknowNodes = null;	//保存长度最长的path
		List<List<SchemaNode>> allPaths = new ArrayList<List<SchemaNode>>();		//临时保存所有路径
		Map<SchemaNode, SchemaNode> map = new HashMap<SchemaNode, SchemaNode>();	//保存已经生成的节点，目的是去重，用Map而不用Set的目的是方便取到已经存在的节点	
		SchemaNode pollNode = null;
		
		List<SchemaNode> tmpNodes = null;	
		map.put(head, head);
		while (!stack.isEmpty()) {
			pollNode = stack.poll();
			
			//将状态未知的节点添加到path
			if (pollNode.getState().equals(Trt.UNKNOW)) addToPaths(allPaths, pollNode); 
			
			tmpNodes = pollNode.getDirectChildren();
			for (SchemaNode tmpNode : tmpNodes) {
				if (!map.containsKey(tmpNode)) {
					stack.offer(tmpNode);
					map.put(tmpNode, tmpNode);
				}
			}
			
		}
		
		//从List<List<SchemaNode>>中找出长度最长的路径
		int max = 0;
		for (List<SchemaNode> tmpPath : allPaths) {
			if (tmpPath.size() > max) {
				max = tmpPath.size();
				unknowNodes = tmpPath;
			}
		}
		stack.push(head);
		return unknowNodes;
	}

	/**
	 * 将节点添加到path中
	 * 思路：	如果当前路径的最后一个节点是当前节点的直接父节点，则添加到该路径。
	 * 		如果当前节点没有被加入到任意一条path，则创建一条新的path。
	 * @param allPaths
	 * @param node
	 */
	private void addToPaths(List<List<SchemaNode>> allPaths, SchemaNode node) {
		boolean isAdded = false;
		for (List<SchemaNode> tmpPath : allPaths) {
			if (isDirectChild(node, tmpPath.get(tmpPath.size()-1))) {
				isAdded = true;
				tmpPath.add(node);
			}
		}
		if (!isAdded) {
			List<SchemaNode> newPath = new ArrayList<SchemaNode>();
			newPath.add(node);
			allPaths.add(newPath);
		}
	}
	
	/**
	 * 判断a是否是b的直接子模式
	 * 思路：a的模式长度等于b的模式长度-1，且a是b的子模式
	 * @param a
	 * @param b
	 * @return
	 */
	private boolean isDirectChild(SchemaNode a, SchemaNode b) {
		int[] aSche = a.getSche();
		int[] bSche = b.getSche();
		int alen = Util.schemaLen(aSche);
		int blen = Util.schemaLen(bSche);
		int params = a.getSche().length;
		boolean isDirectChild = true;
		if (alen == (blen-1)) {
			for (int i = 0; i < params; i++) {
				if (bSche[i] != aSche[i] && aSche[i] != -1 && bSche[i] != -1) return false; 
				if (bSche[i] != aSche[i] && bSche[i] == -1) return false;
			}
		} else {
			isDirectChild = false;
		}
		return isDirectChild;
	}
	
	@Test
	public void test() {
		SchemaNode a = new SchemaNode(new int[] {2, -1, 3, 1});
		SchemaNode b = new SchemaNode(new int[] {2, 2, 3, -1});
		List<List<SchemaNode>> allPaths = new ArrayList<List<SchemaNode>>();		//临时保存所有路径
		addToPaths(allPaths, b);
		addToPaths(allPaths, a);
		System.out.println(allPaths);
	}
	
}
