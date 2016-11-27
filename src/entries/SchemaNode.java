package entries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	
	
}
