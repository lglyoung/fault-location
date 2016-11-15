package test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import deltadebugging.SRI;

public class SRITest {
	@Test
	public void genCoverTablePtcsTest() {
		List<int[]> ftcs = new ArrayList<int[]>();
		List<int[]> coverTable = new ArrayList<int[]>();
		int[] ftc1 = {1, 0, 1, 0, 1, 0};
		int[] ftc2 = {1, 0, 1, 0, 1, 1};
		int[] ftc3 = {1, 0, 1, 1, 1, 0};
		int[] ftc4 = {1, 0, 1, 0, 1, 1};
		ftcs.add(ftc1);
		ftcs.add(ftc2);
		ftcs.add(ftc3);
		ftcs.add(ftc4);
		
		int[] tc = {0, 0, 0, 0, 0, 0};
		coverTable.add(tc);
		List<int[]> coverTablePtcs = SRI.genCoverTablePtcs(ftcs, coverTable);
		for(int[] tmp : coverTablePtcs) {
			for(int param : tmp) {
				System.out.print(param+" ");
			}
			System.out.println();
		}
	}
	
	@Test
	public void genObservedParamsTest() {
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
		
		List<Integer> relatedParams = new ArrayList<Integer>();
		relatedParams.add(0);
		relatedParams.add(1);
		relatedParams.add(2);
		relatedParams.add(3);
		relatedParams.add(4);
		System.out.println(SRI.genObservedParams(valuesOfEachParam, ftc1, relatedParams, new ArrayList<int[]>()));
	}
	
	@Test
	public void sriTest() {
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
		SRI.sri(valuesOfEachParam, ftcs, null, extraTcs, faultSchemas);
		for(int[] tmp : faultSchemas) {
			for(int value : tmp) {
				System.out.print(value+" ");
			}
			System.out.println();
		}
	}

}
