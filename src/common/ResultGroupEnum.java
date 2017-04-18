package common;

/**
 * 结果分组名枚举
 * @author lglyoung
 *
 */
public enum ResultGroupEnum {
	EXPR("expr"), MUTA("muta"), ALL("all");
	
	private String name;		
	
	private ResultGroupEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
