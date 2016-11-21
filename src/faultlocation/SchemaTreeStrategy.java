package faultlocation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import common.IStrategy;
import common.Util;
import entries.SchemaNode;

public class SchemaTreeStrategy implements IStrategy {
	public static final String PASS = "pass";
	public static final String FAIL = "fail";
	public static final String UNKNOW = "unknow";

	@Override
	public void faultLocating(int[] valuesOfEachParam, List<int[]> allFtcs, List<int[]> ftcs, List<int[]> ptcs, List<int[]> extraTcs,
			List<int[]> faultSchemas) {
		for(int[] ftc : ftcs) {
			SchemaNode head = createSchemaTree(ftc);
			Deque<SchemaNode> queue = new ArrayDeque<SchemaNode>();
			while(true) {
				queue.offer(head);
				while(queue.size() > 0) {
					SchemaNode poll = queue.poll();
					poll.setInDeque(false); 	//将true重置为false
					//挑选待选模式
					if(poll.getState().equals(UNKNOW)) {
						int[] extraTc = genExtraTc(valuesOfEachParam, ftc, poll.getTestcase());
						//保存附加测试用例
						Util.addNotRepeatIntArray(extraTc, extraTcs);
						boolean isFailTc = Util.isFailTc(extraTc, allFtcs, null);
						if(isFailTc) {
							poll.setState(FAIL);
						} else {
							poll.setState(PASS);
							updateSubschemaState(poll);	//更新健康模式的子模式
						}
					}
					List<SchemaNode> pollChildren = poll.getChildren();
					for(SchemaNode node : pollChildren) {
						if(!node.isInDeque()) {
							queue.offer(node);
							node.setInDeque(true);
						}
					}
				}
				//报告最小故障模式
				List<SchemaNode> reportedMfs = reportMfs(head);	
				//验证最小故障模式
				List<SchemaNode> isNotMfs = new ArrayList<SchemaNode>();
				if(reportedMfs != null) {
					for(SchemaNode node : reportedMfs) {
						int[] extraTc = genExtraTc(valuesOfEachParam, ftc, node.getTestcase());
						if(!Util.isFailTc(extraTc, ftcs, ptcs)) {
							isNotMfs.add(node);
						}
					}
				}
				//更新父模式为未知状态
				if(isNotMfs.size() > 0) {
					resetParentState(isNotMfs);
				} else {
					//保存故障模式，并跳出死循环
					if(reportedMfs != null) {
						for(SchemaNode node : reportedMfs) {
							Util.addNotRepeatIntArray(node.getTestcase(), faultSchemas);
						}
					}
					break;
				}
			}
		}
	}
	
	@Test
	public void createSchemaTreeTest() {
		int[] ftc = {2, 2, 3, 1};
		SchemaNode head = createSchemaTree(ftc);
		Deque<SchemaNode> queue = new ArrayDeque<SchemaNode>();
		SchemaNode last = head, nlast = head;
		queue.offer(head);
		while(queue.size() > 0) {
			SchemaNode poll = queue.poll();
			poll.setInDeque(false); //初始化			
			System.out.print(Util.intArrayToStr(poll.getTestcase()));
			List<SchemaNode> pollChildren = poll.getChildren();
			for(SchemaNode node : pollChildren) {
				if(!node.isInDeque()) {
					queue.offer(node);
					node.setInDeque(true);
					nlast = node;
				}
			}
			if(poll == last) {
				System.out.print("\n");
				last = nlast;
			}
		}
	}
	
	/**
	 * 根据一个失效测试用例生成模式树
	 * @param ftc
	 * @return SchemaNode 树的头节点
	 */
	public SchemaNode createSchemaTree(int[] ftc) {
		if(ftc == null) return null;
		SchemaNode headNode = new SchemaNode(ftc);
		Deque<SchemaNode> stack1 = new ArrayDeque<SchemaNode>();
		Deque<SchemaNode> stack2 = new ArrayDeque<SchemaNode>();
		stack1.push(headNode);
		while(stack1.size() > 0) {
			SchemaNode popNode1 = stack1.pop();
			List<SchemaNode> subNodes = genSubNodes(popNode1);
			for(int i = 0; i < subNodes.size(); i++) {
				SchemaNode tmpNode = subNodes.get(i);
				
				//初始化stack2栈
//				while(stack2.size() > 0) {
//					stack2.pop().setInDeque(false); //初始化isInDeque
//				}
//				stack2.clear();
				stack2.push(headNode);
				
				SchemaNode existNode = null;	//保存已经存在的节点
				while(stack2.size() > 0) {
					SchemaNode popNode2 = stack2.pop();
					popNode2.setInDeque(false);		//初始化isInDeque
					if(Arrays.equals(tmpNode.getTestcase(), popNode2.getTestcase())) {
						existNode = popNode2;
						//break;				//不用break，目的是将isInDeque的true重置为false
					}
					//将popNode2的孩子节点压stack2栈
					for(SchemaNode node : popNode2.getChildren()) {
						if(!node.isInDeque()) {
							stack2.push(node);
							node.setInDeque(true);
						}
					}
				}
				if(existNode != null) {
					//该模式节点存在，那么设置existNode为popNode1的孩子节点，popNode1是existNode的父节点，但不压栈
					existNode.getParents().add(popNode1);
					popNode1.getChildren().add(existNode);
				} else {
					//该模式节点不存在，那么设置tmpNode为popNode1的孩子节点，popNode1是tmpNode的父节点，并压stack1栈
					tmpNode.getParents().add(popNode1);
					popNode1.getChildren().add(tmpNode);
					if(Util.schemaLen(tmpNode.getTestcase()) > 1) {
						stack1.push(tmpNode);
					}
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
		if(node == null) return null;
		List<SchemaNode> subNodes = new ArrayList<SchemaNode>();
		int[] testcase = node.getTestcase();
		for(int i = 0; i < testcase.length; i++) {
			int value = testcase[i];
			if(value != -1) {
				int[] subSchema = new int[testcase.length];
				System.arraycopy(testcase, 0, subSchema, 0, testcase.length);
				subSchema[i] = -1;
				subNodes.add(new SchemaNode(subSchema));
			}
		}
		return subNodes;
	}
	
	/**
	 * 生成附加测试用例
	 * @param valuesOfEachParam
	 * @param ftc
	 * @param schema
	 * @return int[]
	 */
	private int[] genExtraTc(int[] valuesOfEachParam, int[] ftc, int[] schema) {
		int[] extraTc = new int[ftc.length];
		System.arraycopy(ftc, 0, extraTc, 0, ftc.length);
		Random r = new Random();
		int extraValue = 0;
		for(int i = 0; i < schema.length; i++) {
			if(schema[i] == -1) {
				do {
					extraValue = r.nextInt(valuesOfEachParam[i]); 
				} while(extraValue == ftc[i]);
				extraTc[i] = extraValue;
			}
		}
		return extraTc;
	}
	
	/**
	 * 更新健康模式的子模式状态（健康模式的子模式都是健康模式）
	 * @param currentSchemaNode
	 */
	private void updateSubschemaState(SchemaNode currentSchemaNode) {
		Deque<SchemaNode> queue = new ArrayDeque<SchemaNode>();
		queue.offer(currentSchemaNode);
		while(queue.size() > 0) {
			SchemaNode poll = queue.poll();
			poll.setState(PASS);
			poll.setInDeque(false); 	//初始化			
			List<SchemaNode> pollChildren = poll.getChildren();
			for(SchemaNode node : pollChildren) {
				if(!node.isInDeque()) {
					queue.offer(node);
					node.setInDeque(true);
				}
			}
		}
	}
	
	/**
	 * 报告极小故障模式
	 * @param headSchemaNode
	 * @return List<SchemaNode>
	 */
	private List<SchemaNode> reportMfs(SchemaNode headSchemaNode) {
		List<SchemaNode> reportedMfs = new ArrayList<SchemaNode>();
		List<SchemaNode> nReportedMfs = new ArrayList<SchemaNode>();
		Deque<SchemaNode> queue = new ArrayDeque<SchemaNode>();
		SchemaNode last = headSchemaNode, nlast = headSchemaNode;
		queue.offer(headSchemaNode);
		while(queue.size() > 0) {
			SchemaNode poll = queue.poll();
			poll.setInDeque(false); //初始化			
			List<SchemaNode> pollChildren = poll.getChildren();
			//按行保存失效的节点
			if(poll.getState().equals(FAIL)) {
				nReportedMfs.add(poll);
			}
			for(SchemaNode node : pollChildren) {
				if(!node.isInDeque()) {
					queue.offer(node);
					node.setInDeque(true);
					nlast = node;
				}
			}
			if(poll == last) {
				//如果当前行有失效节点
				if(nReportedMfs.size() > 0) {
					reportedMfs.clear();
					reportedMfs.addAll(nReportedMfs);
					nReportedMfs.clear();
				} else {
					break;	//当前行没有失效节点，reportedMfs即为报告极小故障模式
				}
				last = nlast;
			}
		}
		return reportedMfs;
	}
	
	/**
	 * 重置父节点的状态为未知状态
	 * @param nodes
	 */
	private void resetParentState(List<SchemaNode> nodes) {
		if(nodes != null) {
			for(SchemaNode node : nodes) {
				Deque<SchemaNode> queue = new ArrayDeque<SchemaNode>();
				queue.offer(node);
				while(queue.size() > 0) {
					SchemaNode poll = queue.poll();
					poll.setState(UNKNOW);
					poll.setInDeque(false); 	//初始化			
					List<SchemaNode> pollParents = poll.getParents();
					for(SchemaNode parent : pollParents) {
						if(!parent.isInDeque()) {
							queue.offer(parent);
							parent.setInDeque(true);
						}
					}
				}
			}
		}
	}
}
