package common;

/**
 * 组合测试用例生成工具
 * @author lglyoung
 *
 */
public enum CtToolNameEnum {
	TCONFIG("TCONFIG");
	
	private String name;
	
	private CtToolNameEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
