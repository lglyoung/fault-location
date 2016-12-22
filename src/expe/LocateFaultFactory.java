package expe;

import base.ILocateFault;
import baseimpl.BFSSelectUnknowNode;
import baseimpl.BSLocateFixedParam;
import baseimpl.CompleteScheTree;
import baseimpl.DFSSelectUnknowNode;
import baseimpl.LPSelectUnknowNode;
import baseimpl.LocateFixedParam;
import baseimpl.Ri;
import baseimpl.Simplification;
import baseimpl.Sri;
import common.LfName;
import locatefault.BooleanExpressLocateFault;
import locatefault.DeltaDebug;
import locatefault.DeltaDebugMul;
import locatefault.Fic;
import locatefault.Finovlp;
import locatefault.IterAIFL;
import locatefault.Trt;

/**
 * 故障定位方法的工厂类
 * @author lglyoung
 *
 */
public class LocateFaultFactory {
	/**
	 * 获取ILocateFault代理类实例
	 * @param name 故障定位方法的字符串名称
	 * @return
	 */
	public static ILocateFault getProxyInstance(String name) {
		ILocateFault lf = null;
		LfName lfNameEnum = LfName.getLfName(name);
		if (lfNameEnum == null) {
			throw new RuntimeException("ERROR: 没有"+name+"故障定位方法，可能是字符串写错了");
		}
		
		//创建故障定位方法对象
		switch (lfNameEnum) {
			//IterAIFL算法
		case ITERAIFL:
			lf = new IterAIFL();
			break;
			
			//FIC算法
		case FIC:
			lf = new Fic(new LocateFixedParam());
			break;
		case FIC_BS:
			lf = new Fic(new BSLocateFixedParam());
			break;
		case FINOVLP:
			lf = new Finovlp(new LocateFixedParam());
			break;
		case FINOVLP_BS:
			lf = new Finovlp(new BSLocateFixedParam());
			break;
		
			//差异定位算法
		case SIMPLIFICATION:
			lf = new DeltaDebug(new Simplification());
			break;	
		case SIMPLIFICATION_MUL:
			lf = new DeltaDebugMul(new Simplification());
			break;
		case RI:
			lf = new DeltaDebug(new Ri());
			break;	
		case RI_MUL:
			lf = new DeltaDebugMul(new Ri());
			break;
		case SRI:
			lf = new DeltaDebug(new Sri());
			break;	
		case SRI_MUL:
			lf = new DeltaDebugMul(new Sri());
			break;
			
			//关系树模型
		case COMPLETE_DFSTRT:
			lf = new Trt(new CompleteScheTree(), new DFSSelectUnknowNode());
			break;	
		case COMPLETE_BFSTRT:
			lf = new Trt(new CompleteScheTree(), new BFSSelectUnknowNode());
			break;	
		case COMPLETE_LPTRT:
			lf = new Trt(new CompleteScheTree(), new LPSelectUnknowNode());
			break;	
		/*case COMPLETE_GREEDTRT:
			lf = new Trt(new CompleteScheTree(), new GreedSelectUnknowNode());
			break;	*/
		case BELF:
			lf = new BooleanExpressLocateFault();
			break;
		}
		return new LocateFaultProxy(lf);
	}
}
