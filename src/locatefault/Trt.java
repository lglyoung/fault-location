package locatefault;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import base.ICreateScheTree;
import base.ILocateFault;
import base.ISelectUnknowNode;
import common.Util;
import entries.SchemaNode;

/**
 * 关系树模型
 * @author lglyoung
 *
 */
public class Trt implements ILocateFault {
	public static final String PASS = "pass";
	public static final String FAIL = "fail";
	public static final String UNKNOW = "unknow";
	public static final int STACK_INIT_SIZE = 1024;		//栈的初始大小，用空间换时间
	ISelectUnknowNode selectUnknowNode;					//选择策略
	ICreateScheTree createScheTree;						//创建树
	
	public Trt(ICreateScheTree createScheTree, ISelectUnknowNode selectUnknowNode) {
		this.selectUnknowNode = selectUnknowNode;
		this.createScheTree = createScheTree;
	}
	
	@Override
	public void locateFault(int[] valuesOfEachParam, List<int[]> allFtcs, List<int[]> ftcs, List<int[]> ptcs, List<int[]> extraTcs,
			List<int[]> faultSchemas) {
		for(int[] ftc : ftcs) {
			List<SchemaNode> mfss = null;

			//创建并初始化关系模式树
			SchemaNode head = createScheTree.create(ftc, valuesOfEachParam, allFtcs, ftcs, ptcs, extraTcs, faultSchemas);
			
			Util.dfsTrt(head);
			//挑选待测模式
			Deque<SchemaNode> stack = new ArrayDeque<SchemaNode>(STACK_INIT_SIZE);	//栈
			do {
				stack.clear();
				stack.push(head);
				int[] extraTc = null;
				SchemaNode selectedSchemaNode = null;
				for (List<SchemaNode> nodes = selectUnknowNode.selectUnknowNode(stack); 
						nodes != null; nodes = selectUnknowNode.selectUnknowNode(stack)) {
					int s = 0, m = 0, e = nodes.size() - 1;
					while (s <= e) {
						m = bsSelect(s, e);
						selectedSchemaNode = nodes.get(m);
						extraTc = genExtraTc(valuesOfEachParam, ftc, selectedSchemaNode.getSche());
						extraTcs.add(extraTc);
						if (Util.isFailTc(extraTc, allFtcs, null)) {
							selectedSchemaNode.setState(FAIL);
							s = m + 1;
						} else {
							selectedSchemaNode.setState(PASS);
							e = m - 1;
						}
						updateOthersState(selectedSchemaNode);
					}
				}
				
				//报告mfs
				mfss = reportMfs(head);
			
			//验证所有的mfs
			} while (!isCorrectMfss(mfss, valuesOfEachParam, allFtcs, ftc, extraTcs));
			
			//保存mfs
			for (SchemaNode tmpmfs : mfss) {
				faultSchemas.add(tmpmfs.getSche());
			}
			
		}
	}
	
	/**
	 * 二分搜索选择中间的元素，如果s > e，则返回-1，表示没有可选择的元素
	 * @param s
	 * @param e
	 * @return
	 */
	private int bsSelect(int s, int e) {
		if (s <= e) {
			return (s + e) / 2;
		}
		return -1;
	}

	/**
	 * 验证mfs
	 * @param mfss
	 * @return
	 */
	private boolean isCorrectMfss(List<SchemaNode> mfss, int[] valuesOfEachParam, 
			List<int[]> allFtcs, int[] ftc, List<int[]> extraTcs) {
		boolean isAllMfss = true;
		int[] extraTc = null;
		for (SchemaNode tmpMfs : mfss) {
			extraTc = genExtraTc(valuesOfEachParam, ftc, tmpMfs.getSche());
			extraTcs.add(extraTc);
			if (!Util.isFailTc(extraTc, allFtcs, null)) {
				tmpMfs.setState(PASS);
				resetAllParentsState(tmpMfs);
				isAllMfss = false;
			}
		}
		return isAllMfss;
	}


	
	/**
	 * 更新当前模式的所有父模式或者所有子模式
	 * @param curScheNode
	 */
	public static void updateOthersState(SchemaNode curScheNode) {
		String curState = curScheNode.getState();	//获取当前模式的状态
		Deque<SchemaNode> stack = new ArrayDeque<SchemaNode>(STACK_INIT_SIZE);	//栈
		stack.push(curScheNode);
		SchemaNode popNode = null;			//指向弹出的节点
		List<SchemaNode> tmpNodes = null;	//指向父模式列表或者子模式列表
		Map<SchemaNode, SchemaNode> map = new HashMap<SchemaNode, SchemaNode>();	//保存已经生成的节点，目的是去重，用Map而不用Set的目的是方便取到已经存在的节点	

		while (!stack.isEmpty()) {
			popNode = stack.pop();
			map.put(popNode, popNode);
			
			//获取父模式列表或者子模式列表
			if (curState.equals(PASS)) {
				tmpNodes = popNode.getDirectChildren();
			} else if (curState.equals(FAIL)) {
				tmpNodes = popNode.getDirectParents();
			}	
			
			//更新状态并压栈
			for (SchemaNode sn : tmpNodes) {
				if (!map.containsKey(sn)) {
					sn.setState(curState);
					stack.push(sn);
				}
			}
		}
	}
	
	
	/**
	 * 报告极小故障模式
	 * @param headNode
	 * @return List<SchemaNode>
	 */
	private List<SchemaNode> reportMfs(SchemaNode headNode) {
		List<SchemaNode> mfs = new ArrayList<SchemaNode>();
		Deque<SchemaNode> stack = new ArrayDeque<SchemaNode>(STACK_INIT_SIZE);	//栈
		stack.push(headNode);
		SchemaNode popNode = null;			//指向弹出的节点
		List<SchemaNode> tmpNodes = null;
		Map<SchemaNode, SchemaNode> map = new HashMap<SchemaNode, SchemaNode>();	//保存已经生成的节点，目的是去重，用Map而不用Set的目的是方便取到已经存在的节点	

		while (!stack.isEmpty()) {
			//弹栈
			popNode = stack.pop();
			map.put(popNode, popNode);
			
			//保存mfs故障模式
			if (isMfS(popNode)) mfs.add(popNode);
			
			//将直接子节点压栈
			tmpNodes = popNode.getDirectChildren();
			for (SchemaNode tmpNode : tmpNodes) {
				if (!map.containsKey(tmpNode)) {
					stack.push(tmpNode);					
				}
			}
		}

		return mfs;
	}
	
	/**
	 * 判断节点是否是MFS
	 * 思路：当前节点为故障模式且所有的直接子节点都是健康模式
	 * @param popNode
	 * @return
	 */
	private boolean isMfS(SchemaNode popNode) {
		boolean isMfs = false;
		if (popNode.getState().equals(FAIL)) {
			List<SchemaNode> tmpNodes = popNode.getDirectChildren();
			boolean isAllPass = true;
			for (SchemaNode tmNode : tmpNodes) {
				if (tmNode.getState().equals(FAIL)) {
					isAllPass = false;
					break;
				}
			}
			if (isAllPass) {
				isMfs = true;
			}
		}
		return isMfs;
	}

	/**
	 * 重置所有父节点的状态为未知状态
	 * @param curNode
	 */
	private void resetAllParentsState(SchemaNode curScheNode) {
		Deque<SchemaNode> stack = new ArrayDeque<SchemaNode>(STACK_INIT_SIZE);	//栈
		stack.push(curScheNode);
		SchemaNode popNode = null;			//指向弹出的节点
		List<SchemaNode> tmpNodes = null;	//指向父模式列表
		Map<SchemaNode, SchemaNode> map = new HashMap<SchemaNode, SchemaNode>();	//保存已经生成的节点，目的是去重，用Map而不用Set的目的是方便取到已经存在的节点	

		while (!stack.isEmpty()) {
			popNode = stack.pop();
			map.put(popNode, popNode);
			
			//获取当前弹出节点的所有直接父节点
			tmpNodes = popNode.getDirectParents();
			
			//更新状态并压栈
			for (SchemaNode sn : tmpNodes) {
				if (!map.containsKey(sn)) {
					sn.setState(UNKNOW);
					stack.push(sn);
				}
			}
		}
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
				} while(extraValue == ftc[i] && valuesOfEachParam[i] > 1);
				extraTc[i] = extraValue;
			}
		}
		return extraTc;
	}
}
