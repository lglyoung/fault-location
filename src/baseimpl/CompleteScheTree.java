package baseimpl;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import base.ICreateScheTree;
import common.Util;
import entries.SchemaNode;
import locatefault.Trt;


/**
 * 完整的关系树
 * @author lglyoung
 *
 */
public class CompleteScheTree implements ICreateScheTree {

	@Override
	public SchemaNode create(int[] ftc, List<int[]> ptcs) {
		//创建关系树
		SchemaNode head = createSchemaTree(ftc);

		//初始化
		initTrt(head, ptcs);
		
		return head;
	}
	
	/**
	 * 根据一个失效测试用例生成模式树
	 * @param ftc
	 * @return SchemaNode 树的头节点
	 */
	public SchemaNode createSchemaTree(int[] ftc) {
		SchemaNode headNode = new SchemaNode(ftc);				//创建树的根节点
		headNode.setState(Trt.FAIL);								
		Deque<SchemaNode> stack = new ArrayDeque<SchemaNode>(Trt.STACK_INIT_SIZE);	//栈
		Map<SchemaNode, SchemaNode> map = new HashMap<SchemaNode, SchemaNode>();	//保存已经生成的节点，目的是去重，用Map而不用Set的目的是方便取到已经存在的节点	
		stack.push(headNode);
		while(!stack.isEmpty()) {
			SchemaNode popNode = stack.pop();
			map.put(popNode, popNode);								//保存已经生成的节点
			//如果当前弹出模式长度不为1，则生成子节点
			if (Util.schemaLen(popNode.getSche()) > 1) {
				List<SchemaNode> subNodes = genSubNodes(popNode);	//获取当前弹出节点的直接子模式
				
				//将生成的节点的直接子节点压栈，如果已经生成，则不压栈
				for(int i = 0; i < subNodes.size(); i++) {
					SchemaNode tmpNode = subNodes.get(i);
					if (!map.containsKey(tmpNode)) { 	//未生成该子节点
						stack.push(tmpNode);
					} else {						//已生成该子节点
						//将tmpNode指向之前已经创建的节点对象
						tmpNode = map.get(tmpNode);
					}
					//更新当前节点的子节点列表
					popNode.addDirectChild(tmpNode);
					
					//更新当前子节点的父节点列表
					tmpNode.addDirectParent(popNode);
				}
			}
		}
		return headNode;
	}

	/**
	 * 生成直接子模式(子节点)
	 * @param node
	 * @return List<SchemaNode>
	 */
	private List<SchemaNode> genSubNodes(SchemaNode node) {
		List<SchemaNode> subNodes = new ArrayList<SchemaNode>();
		int[] sche = node.getSche();
		
		//如果模式长度为1，则不生成子模式
		if (Util.schemaLen(sche) == 1) return subNodes;
		
		for(int i = 0; i < sche.length; i++) {
			int value = sche[i];
			if(value != -1) {
				int[] subSchema = new int[sche.length];
				System.arraycopy(sche, 0, subSchema, 0, sche.length);
				subSchema[i] = -1;
				subNodes.add(new SchemaNode(subSchema));
			}
		}
		return subNodes;
	}
	
	/**
	 * 根据ptcs，来初始化关系树
	 * @param head 根节点
	 * @param ptcs 组合测试的通过测试用例集
	 */
	private void initTrt(SchemaNode head, List<int[]> ptcs) {
		Deque<SchemaNode> stack = new ArrayDeque<SchemaNode>(Trt.STACK_INIT_SIZE);	//栈
		stack.push(head);
		SchemaNode popNode = null;			//指向弹出的节点
		List<SchemaNode> tmpNodes = null;	//指向父模式列表或者子模式列表
		Map<SchemaNode, SchemaNode> map = new HashMap<SchemaNode, SchemaNode>();	//保存已经生成的节点，目的是去重，用Map而不用Set的目的是方便取到已经存在的节点	

		while (!stack.isEmpty()) {
			popNode = stack.pop();
			map.put(popNode, popNode);
			
			//初始化
			if (!popNode.getState().equals(Trt.PASS)) {
				for (int[] tmpptc : ptcs) {
					if (isMatchSche(tmpptc, popNode)) {
						popNode.setState(Trt.PASS);		//设置当前节点的状态
						Trt.updateOthersState(popNode);	//更新子节点的状态
						break;
					}
				}				
			}
			
			tmpNodes = popNode.getDirectChildren();
			for (int i = tmpNodes.size() - 1; i >= 0; i--) {
				if (!map.containsKey(tmpNodes.get(i))) stack.push(tmpNodes.get(i));
			}
		}
	}
	
	/**
	 * 测试用例是否匹配到当前模式
	 * @return
	 */
	private boolean isMatchSche(int[] tc, SchemaNode node) {
		int[] sche = node.getSche();
		for (int i = 0; i < tc.length; i++) {
			if (tc[i] != sche[i] && sche[i] != -1) return false;
		}
		return true;
	}
}
