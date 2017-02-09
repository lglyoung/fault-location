package common;

/**
 * 故障定位方法名称
 * @author lglyoung
 *
 */
public enum LfNameEnum {
	//ITERAIFL算法
	ITERAIFL(Configure.ITERAIFL),
	
	//FIC算法
	FIC(Configure.FIC), FIC_BS(Configure.FIC_BS), 
	FINOVLP(Configure.FINOVLP), FINOVLP_BS(Configure.FINOVLP_BS),
	
	//差异定位算法
	SIMPLIFICATION(Configure.SIMPLIFICATION), SIMPLIFICATION_MUL(Configure.SIMPLIFICATION_MUL),
	RI(Configure.RI), RI_MUL(Configure.RI_MUL), SRI(Configure.SRI), SRI_MUL(Configure.SRI_MUL),
	
	//关系树模型
	COMPLETE_DFSTRT(Configure.COMPLETE_DFSTRT), COMPLETE_BFSTRT(Configure.COMPLETE_BFSTRT), 
	COMPLETE_LPTRT(Configure.COMPLETE_LPTRT), //COMPLETE_GREEDTRT(Configure.COMPLETE_GREEDTRT),
	
	//布尔表达式故障定位方法
	BELF(Configure.BELF);
	
	private String name;
	
	private LfNameEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static LfNameEnum getLfName(String name) {
		if (name.toUpperCase().equals(Configure.ITERAIFL.toUpperCase())) {
			return LfNameEnum.ITERAIFL;
		} else if (name.toUpperCase().equals(Configure.FIC.toUpperCase())) {
			return LfNameEnum.FIC;
		} else if (name.toUpperCase().equals(Configure.FIC_BS.toUpperCase())) {
			return LfNameEnum.FIC_BS;
		} else if (name.toUpperCase().equals(Configure.FINOVLP.toUpperCase())) {
			return LfNameEnum.FINOVLP;
		} else if (name.toUpperCase().equals(Configure.FINOVLP_BS.toUpperCase())) {
			return LfNameEnum.FINOVLP_BS;
		} else if (name.toUpperCase().equals(Configure.SIMPLIFICATION.toUpperCase())) {
			return LfNameEnum.SIMPLIFICATION;
		} else if (name.toUpperCase().equals(Configure.RI.toUpperCase())) {
			return LfNameEnum.RI;
		} else if (name.toUpperCase().equals(Configure.SRI.toUpperCase())) {
			return LfNameEnum.SRI;
		} else if (name.toUpperCase().equals(Configure.SIMPLIFICATION_MUL.toUpperCase())) {
			return LfNameEnum.SIMPLIFICATION_MUL;
		} else if (name.toUpperCase().equals(Configure.RI_MUL.toUpperCase())) {
			return LfNameEnum.RI_MUL;
		} else if (name.toUpperCase().equals(Configure.SRI_MUL.toUpperCase())) {
			return LfNameEnum.SRI_MUL;
		} else if (name.toUpperCase().equals(Configure.COMPLETE_DFSTRT.toUpperCase())) {
			return LfNameEnum.COMPLETE_DFSTRT;
		} else if (name.toUpperCase().equals(Configure.COMPLETE_BFSTRT.toUpperCase())) {
			return LfNameEnum.COMPLETE_BFSTRT;
		} else if (name.toUpperCase().equals(Configure.COMPLETE_LPTRT.toUpperCase())) {
			return LfNameEnum.COMPLETE_LPTRT;
		/*} else if (name.toUpperCase().equals(Configure.COMPLETE_GREEDTRT.toUpperCase())) {
			return LfName.COMPLETE_GREEDTRT;*/
		} else if (name.toUpperCase().equals(Configure.BELF.toUpperCase())) {
			return LfNameEnum.BELF;
		}
		return null;
	}
	
}
