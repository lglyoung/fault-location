package test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import common.Util;

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

		System.out.println(Util.isolate(ftc4, observedParams, valuesOfEachParam, ftcs, null, extraTcs));
	}
	
}
