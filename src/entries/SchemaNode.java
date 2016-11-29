package entries;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.Util;
import locatefault.Trt;

/**
 * 关系树模型节点
 * @author lglyoung
 *
 */
public class SchemaNode {
	private int[] sche;
	private List<SchemaNode> directParents = new ArrayList<SchemaNode>();	//直接父亲节点
	private List<SchemaNode> directChildren = new ArrayList<SchemaNode>();	//直接孩子节点
	private String state;
	private int greedVal;				//节点的贪心值
	
	public SchemaNode(int[] sche) {
		super();
		this.sche = sche;
		this.state = Trt.UNKNOW;
	}
	
	/**
	 * 添加一个父亲节点
	 * @param parent
	 * @return
	 */
	public boolean addDirectParent(SchemaNode parent) {
		return directParents.add(parent);
	}
	
	/**
	 * 添加一个孩子节点
	 * @param child
	 * @return
	 */
	public boolean addDirectChild(SchemaNode child) {
		return directChildren.add(child);
	}
	
	public List<SchemaNode> getDirectParents() {
		return directParents;
	}

	public List<SchemaNode> getDirectChildren() {
		return directChildren;
	}

	public int[] getSche() {
		return sche;
	}

	public void setSche(int[] sche) {
		this.sche = sche;
	}

	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}

	@Override
	public int hashCode() {
		return Util.intArrayToStr(sche).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SchemaNode) {
			SchemaNode sn = (SchemaNode) obj;
			String str1 = Util.intArrayToStr(sche);
			String str2 = Util.intArrayToStr(sn.getSche());
			return str1.equals(str2);
		}
		return false;
	}

	@Override
	public String toString() {
		return Arrays.toString(sche) + " " + state;
	}

	public int getGreedVal() {
		return greedVal;
	}

	/**
	 * 设置贪心值
	 */
	public void setGreedVal() {
		greedVal = !state.equals(Trt.UNKNOW) ? -1 : calculateGreedVal(this);
	}
	
	/**
	 * 计算一个未知节点的贪心值
	 * @param node
	 * @return
	 */
	private int calculateGreedVal(SchemaNode node) {
		return Math.min(calculateUnknowNodes(node, true), calculateUnknowNodes(node, false));
	}
	
	/**
	 * 计算一个未知节点的未知父节点数或未知子节点数
	 * @param node
	 * @param isParent 为true，则计算的是父节点，否则计算子节点
	 * @return
	 */
	private int calculateUnknowNodes(SchemaNode node, boolean isParent) {
		Deque<SchemaNode> stack = new ArrayDeque<SchemaNode>(Trt.STACK_INIT_SIZE);
		stack.push(node);
		SchemaNode popNode = null, tmpNode = null;
		List<SchemaNode> nodes = null;
		Map<SchemaNode, SchemaNode> map = new HashMap<SchemaNode, SchemaNode>();	//保存已经生成的节点，目的是去重，用Map而不用Set的目的是方便取到已经存在的节点	
		map.put(node, node);
		int num = -1;
		while (!stack.isEmpty()) {
			popNode = stack.pop();
			
			//处理
			num++;
			
			nodes = isParent ? popNode.getDirectParents() : popNode.getDirectChildren();
			for (int i = nodes.size() - 1; i >= 0; i--) {
				tmpNode = nodes.get(i);
				if (!map.containsKey(tmpNode) && tmpNode.getState().equals(Trt.UNKNOW)) {
					stack.push(tmpNode);
					map.put(tmpNode, tmpNode);
				}
			}
			
		}
		return num;
	}

	
}
