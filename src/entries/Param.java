package entries;

import base.IHandler;
import base.ILocateFault;
import common.CtToolNameEnum;
import common.DataHelper;
import common.LfNameEnum;

/**
 * 对相关参数封装起来
 * @author lglyoung
 *
 */
public class Param {
	private IHandler handler;				//该接口用来保存中间结果，或者处理中间结果
	private ILocateFault locateFault;		//故障定位方法
	private DataHelper dataHepler;			//数据助手
	private LfNameEnum lfName;					//故障定位方法的名称，枚举类型
	private CtToolNameEnum ctToolName;			//组合测试的名称，枚举类型
	private int lenOfCt;					//组合测试的维度
	
	public Param() {
		super();
	}

	/**
	 * 有参构造函数
	 * @param handler
	 * @param locateFault
	 * @param dataHepler
	 * @param flName
	 * @param ctToolName
	 * @param lenOfCt
	 */
	public Param(IHandler handler, ILocateFault locateFault, DataHelper dataHepler, LfNameEnum flName, CtToolNameEnum ctToolName, int lenOfCt) {
		super();
		this.handler = handler;
		this.locateFault = locateFault;
		this.dataHepler = dataHepler;
		this.lfName = flName;
		this.ctToolName = ctToolName;
		this.lenOfCt = lenOfCt;
	}
	
	/**
	 * 一次性设置所有属性值
	 * @param handler
	 * @param dataHepler
	 * @param flName
	 * @param ctToolName
	 * @param lenOfCt
	 * @param resultType
	 * @param resultHandler
	 */
	public void set(IHandler handler, ILocateFault locateFault, DataHelper dataHepler, LfNameEnum flName, CtToolNameEnum ctToolName, int lenOfCt) {
		this.handler = handler;
		this.locateFault = locateFault;
		this.dataHepler = dataHepler;
		this.lfName = flName;
		this.ctToolName = ctToolName;
		this.lenOfCt = lenOfCt;
	}

	public IHandler getHandler() {
		return handler;
	}

	public void setHandler(IHandler handler) {
		this.handler = handler;
	}

	public ILocateFault getLocateFault() {
		return locateFault;
	}

	public void setLocateFault(ILocateFault locateFault) {
		this.locateFault = locateFault;
	}

	public DataHelper getDataHepler() {
		return dataHepler;
	}

	public void setDataHepler(DataHelper dataHepler) {
		this.dataHepler = dataHepler;
	}


	public LfNameEnum getLfName() {
		return lfName;
	}

	public void setLfName(LfNameEnum lfName) {
		this.lfName = lfName;
	}

	public CtToolNameEnum getCtToolName() {
		return ctToolName;
	}

	public void setCtToolName(CtToolNameEnum ctToolName) {
		this.ctToolName = ctToolName;
	}

	public int getLenOfCt() {
		return lenOfCt;
	}

	public void setLenOfCt(int lenOfCt) {
		this.lenOfCt = lenOfCt;
	}

}
