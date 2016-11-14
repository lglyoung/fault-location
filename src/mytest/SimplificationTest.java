package mytest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import common.Util;
import deltadebugging.Simplification;

public class SimplificationTest {
	@Test
	public void testList() {
		int[] nums = {1, 2, 3, 4};
		List<int[]> list = new ArrayList<int[]>();
		list.add(nums);
		System.out.println(list.get(0)[0]);
	}
	
	@Test
	public void genExtraTcTest() {
		int[] valuesOfEachParam = {4, 4, 4};
		int[] ftc = {3, 3, 3};
		List<Integer> changedParams = new ArrayList<Integer>();
		changedParams.add(0);
		changedParams.add(1);
		for(int tmp : Util.genExtraTc(valuesOfEachParam, ftc, changedParams)){
			System.out.print(tmp+" ");
		}
	}
	
	@Test
	public void isFailTcTest() {
		int[] extraTc = {1, 2, 3, 4};
		int[] ftc = {2, 1, 3, 4};
		List<int[]> ftcs = new ArrayList<int[]>();
		ftcs.add(extraTc);
		ftcs.add(ftc);
		System.out.println(Util.isFailTc(extraTc, ftcs, null));
	}
	
	@Test
	public void genFaultSchemaTest() {
		int[] ftc = {1, 0, 3, 4};
		List<Integer> unchangedParams = new ArrayList<Integer>();
		unchangedParams.add(2);
		unchangedParams.add(3);
		int[] fs = Util.genFaultSchema(ftc, unchangedParams);
		for(int tmp : fs) {
			System.out.print(tmp+" ");
		}
	}
	
	@Test
	public void paramGroupsTest() {
		List<Integer> unchangedParams = new ArrayList<Integer>();
		
		unchangedParams.add(0);
		unchangedParams.add(1);
		unchangedParams.add(2);
		unchangedParams.add(3);
		unchangedParams.add(4);
		unchangedParams.add(5);
		unchangedParams.add(6);
		
		System.out.println(Util.paramGroups(unchangedParams, 4));
	}
	
	@Test
	public void simplifyTest() {
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
		Simplification.simplify(valuesOfEachParam, ftcs, null, extraTcs, faultSchemas);
		for(int[] tmp : faultSchemas) {
			for(int value : tmp) {
				System.out.print(value+" ");
			}
			System.out.println();
		}
	}
}
