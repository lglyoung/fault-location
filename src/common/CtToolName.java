package common;

public enum CtToolName {
	TCONFIG("TCONFIG"),PICT("PICT"),TVG("TVG");
	
	private String name;
	
	private CtToolName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
