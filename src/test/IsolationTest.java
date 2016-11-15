package test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import deltadebugging.Isolation;
import deltadebugging.RI;

public class IsolationTest {
	@Test
	public void isolateTest() {
		int[] valuesOfEachParam = {2, 2, 2, 2, 2};
		int[] ftc1 = {0, 0, 0, 1, 0};
		int[] ftc2 = {0, 1, 0, 1, 0};
		int[] ftc3 = {1, 0, 0, 0, 0};
		int[] ftc4 = {1, 0, 1, 0, 0};
		
		List<int[]> ftcs = new ArrayList<int[]>();
		ftcs.add(ftc1);
		ftcs.add(ftc2);
		ftcs.add(ftc3);
		ftcs.add(ftc4);
		List<int[]> extraTcs = new ArrayList<int[]>();
		
		List<Integer> observedParams = new ArrayList<Integer>();
		for(int i = 0; i < valuesOfEachParam.length; i++) {
			observedParams.add(i);
		}

		System.out.println(Isolation.isolate(ftc4, observedParams, valuesOfEachParam, ftcs, null, extraTcs));
	}
	
	@Test
	public void genChangedParamsTest() {
		int[] ftc = {1, 0, 1, 0};
		List<Integer> relatedParams = new ArrayList<Integer>();
		relatedParams.add(0);
		relatedParams.add(1);
		relatedParams.add(2);
		relatedParams.add(3);
		System.out.println(RI.genChangedParams(ftc, relatedParams));
	}
}
