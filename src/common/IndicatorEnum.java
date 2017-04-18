package common;

/**
 * 指标类型
 * @author lglyoung
 *
 */
public enum IndicatorEnum {
	EXTRA_TC("extraTc"), RECALL("recall"), PERCISION("percision"), F_MEASURE("fMeasure");
	
	private String name;
	private IndicatorEnum(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
}
