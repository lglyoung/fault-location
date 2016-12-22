package common;

import java.util.List;

/**
 * 结果处理类
 * @author lglyoung
 *
 */
public class ResultHelper {
	private double hrSum;				//命中率总和
	private int numOfExtraTc;			//附加测试用例总和
	
	public synchronized void sum(List<int[]> extraTcs, List<int[]> faultSchemas, double hr) {
		hrSum += hr;
		numOfExtraTc += extraTcs.size();
	}
	
	public void show(int numOfBoolExp) {
		System.out.println("平均命中率：" + hrSum / numOfBoolExp);
		System.out.println("总的附加测试用例数：" + numOfExtraTc);
	}
}
