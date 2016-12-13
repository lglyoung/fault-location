package common;

public enum ResultType {
	ExtraTc("ExtraTc"), FaultSche("FaultSche");
	
	private String name;		//类型名字
	
	private ResultType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
}
