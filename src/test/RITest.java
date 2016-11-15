package test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import deltadebugging.RI;

public class RITest {
	@Test
	public void riTest() {
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
		List<int[]> faultSchemas = new ArrayList<int[]>();
		RI.ri(valuesOfEachParam, ftcs, null, extraTcs, faultSchemas);
		for(int[] tmp : faultSchemas) {
			for(int value : tmp) {
				System.out.print(value+" ");
			}
			System.out.println();
		}

	}
}
