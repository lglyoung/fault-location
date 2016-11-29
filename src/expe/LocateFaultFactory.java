package expe;

import base.ILocateFault;
import baseimpl.BFSSelectUnknowNode;
import baseimpl.BSLocateFixedParam;
import baseimpl.CompleteScheTree;
import baseimpl.DFSSelectUnknowNode;
import baseimpl.GreedSelectUnknowNode;
import baseimpl.LPSelectUnknowNode;
import baseimpl.LocateFixedParam;
import baseimpl.Ri;
import baseimpl.Simplification;
import baseimpl.Sri;
import common.Configure;
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
	 * @param lfName
	 * @return
	 */
	public static ILocateFault getProxyInstance(String lfName) {
		ILocateFault lf = null;
		if (lfName.toUpperCase().equals(Configure.ITERAIFL)) {
			lf = new IterAIFL();
		} else if (lfName.toUpperCase().equals(Configure.FIC)) {
			lf = new Fic(new LocateFixedParam());
		} else if (lfName.toUpperCase().equals(Configure.FIC_BS)) {
			lf = new Fic(new BSLocateFixedParam());
		} else if (lfName.toUpperCase().equals(Configure.FINOVLP)) {
			lf = new Finovlp(new LocateFixedParam());
		} else if (lfName.toUpperCase().equals(Configure.FINOVLP_BS)) {
			lf = new Finovlp(new BSLocateFixedParam());
		} else if (lfName.toUpperCase().equals(Configure.SIMPLIFICATION)) {
			lf = new DeltaDebug(new Simplification());
		} else if (lfName.toUpperCase().equals(Configure.RI)) {
			lf = new DeltaDebug(new Ri());
		} else if (lfName.toUpperCase().equals(Configure.SRI)) {
			lf = new DeltaDebug(new Sri());
		} else if (lfName.toUpperCase().equals(Configure.SIMPLIFICATION_MUL)) {
			lf = new DeltaDebugMul(new Simplification());
		} else if (lfName.toUpperCase().equals(Configure.RI_MUL)) {
			lf = new DeltaDebugMul(new Ri());
		} else if (lfName.toUpperCase().equals(Configure.SRI_MUL)) {
			lf = new DeltaDebugMul(new Sri());
		} else if (lfName.toUpperCase().equals(Configure.COMPLETE_DFSTRT)) {
			lf = new Trt(new CompleteScheTree(), new DFSSelectUnknowNode());
		}  else if (lfName.toUpperCase().equals(Configure.COMPLETE_BFSTRT)) {
			lf = new Trt(new CompleteScheTree(), new BFSSelectUnknowNode());
		}  else if (lfName.toUpperCase().equals(Configure.COMPLETE_LPSTRT)) {
			lf = new Trt(new CompleteScheTree(), new LPSelectUnknowNode());
		}  else if (lfName.toUpperCase().equals(Configure.COMPLETE_GREEDSTRT)) {
			lf = new Trt(new CompleteScheTree(), new GreedSelectUnknowNode());
		}
		return new LocateFaultProxy(lf);
	}
}
