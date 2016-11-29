package baseimpl;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import base.ICreateScheTree;
import base.ILocateFixedParam;
import common.Util;
import entries.SchemaNode;
import locatefault.Trt;

/**
 * 根据ptcs、Fic定位的故障模式来创建修建后的关系树
 * @author lglyoung
 *
 */
public class CuttedByFicScheTree implements ICreateScheTree {
	private ILocateFixedParam lfp;
	
	public CuttedByFicScheTree(ILocateFixedParam lfp) {
		this.lfp = lfp;
	}
	
	@Override 
	public SchemaNode create(int[] ftc, int[] valuesOfEachParam, List<int[]> allFtcs, List<int[]> ftcs, List<int[]> ptcs, List<int[]> extraTcs,
			List<int[]> faultSchemas) {
		List<Integer> fixedParams = Util.fic(ftc, valuesOfEachParam, valuesOfEachParam.length, new ArrayList<Integer>(), allFtcs, extraTcs, lfp);
		int[] sche = Util.genFaultSchema(ftc, fixedParams);
		
		List<int[]> mfss = new ArrayList<int[]>();
		mfss.add(sche);	
		return createCuttedScheTree(ftc, ptcs, mfss);
	}
	
	/**
	 * 根据通过测试用例集和故障模式集创建不完全的模式树
	 * @param ftc
	 * @param ptcs
	 * @param mfss
	 * @return
	 */
	private SchemaNode createCuttedScheTree(int[] ftc, List<int[]> ptcs, List<int[]> mfss) {
		SchemaNode headNode = new SchemaNode(ftc);				//创建树的根节点
		headNode.setState(Trt.FAIL);								
		Deque<SchemaNode> stack = new ArrayDeque<SchemaNode>(Trt.STACK_INIT_SIZE);	//栈
		Map<SchemaNode, SchemaNode> map = new HashMap<SchemaNode, SchemaNode>();	//保存已经生成的节点，目的是去重，用Map而不用Set的目的是方便取到已经存在的节点	
		stack.push(headNode);
		map.put(headNode, headNode);								//保存已经生成的节点
		while(!stack.isEmpty()) {
			SchemaNode popNode = stack.pop();
			//如果当前弹出模式长度不为1，则生成子节点
			if (Util.schemaLen(popNode.getSche()) > 1) {
				List<SchemaNode> subNodes = CompleteScheTree.genSubNodes(popNode);	//获取当前弹出节点的直接子模式
				
				//将生成的节点的直接子节点压栈，如果已经生成，则不压栈
				for(int i = 0; i < subNodes.size(); i++) {
					SchemaNode tmpNode = subNodes.get(i);
					if (!map.containsKey(tmpNode) && !isPassNode(tmpNode, ptcs, mfss)) { 	//未生成该子节点，且不是健康模式
						stack.push(tmpNode);
						map.put(tmpNode, tmpNode);
					} else if (map.containsKey(tmpNode)) {						//已生成该子节点
						//将tmpNode指向之前已经创建的节点对象
						tmpNode = map.get(tmpNode);
					}
					
					//如果当前节点被加入到树中
					if (map.containsKey(tmpNode)) {
						//更新当前节点的子节点列表
						popNode.addDirectChild(tmpNode);
						
						//更新当前子节点的父节点列表
						tmpNode.addDirectParent(popNode);
					}
				}
			}
		}
		return headNode;
	}

	/**
	 * 根据通过测试用例集和MFS集来判断当前节点是不是健康模式节点
	 * @param tmpNode
	 * @param ptcs
	 * @param mfss
	 * @return
	 */
	private boolean isPassNode(SchemaNode tmpNode, List<int[]> ptcs, List<int[]> mfss) {
		boolean isPassNode = false;
		
		//用通过测试用例集来判断
		for (int[] tmpTc : ptcs) {
			if (CompleteScheTree.isMatchSche(tmpTc, tmpNode)) {
				isPassNode = true;
				break;
			}
		}
		
		//用极小故障模式集来判断：如果当前模式节点的父模式出现在mfss中，则当前节点为健康模式节点
		if (!isPassNode) {
			List<SchemaNode> parentsNodes = tmpNode.getDirectParents();
			for (SchemaNode parent : parentsNodes) {
				for (int[] mfs : mfss) {
					if (Arrays.equals(mfs, parent.getSche())) {
						isPassNode = true;
						
						//更新极小故障模式的父模式为故障模式
						parent.setState(Trt.FAIL);
						Trt.updateOthersState(parent);
					}
				}
			}
		}
		return isPassNode;
	}
	
}
