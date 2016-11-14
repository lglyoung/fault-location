package entries;

import java.util.ArrayList;
import java.util.List;

import faultlocation.SchemaTreeStrategy;

public class SchemaNode {
	private int[] testcase;
	private List<SchemaNode> parents = new ArrayList<SchemaNode>();
	private List<SchemaNode> children = new ArrayList<SchemaNode>();
	private String state;
	private boolean isInDeque;	//用来标识相同的子节点是否进栈或队列
	
	public SchemaNode() {
		super();
	}
	
	public SchemaNode(int[] testcase) {
		super();
		this.testcase = testcase;
		this.state = SchemaTreeStrategy.UNKNOW;
		this.isInDeque = false;
	}
	
	/**
	 * 添加一个父亲节点
	 * @param parent
	 * @return
	 */
	public boolean addParent(SchemaNode parent) {
		return parents.add(parent);
	}
	
	/**
	 * 添加一个孩子节点
	 * @param child
	 * @return
	 */
	public boolean addChild(SchemaNode child) {
		return children.add(child);
	}
	
	public List<SchemaNode> getParents() {
		return parents;
	}

	public List<SchemaNode> getChildren() {
		return children;
	}

	public int[] getTestcase() {
		return testcase;
	}
	public void setTestcase(int[] testcase) {
		this.testcase = testcase;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}

	public boolean isInDeque() {
		return isInDeque;
	}

	public void setInDeque(boolean isInDeque) {
		this.isInDeque = isInDeque;
	}
}
