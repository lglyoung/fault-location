package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import faultlocation.IterAIFL;

public class IterAIFLTest {
	@Test
	public void IterAIFL() {
		IterAIFL ia = new IterAIFL();
		int[] valuesOfEachParam = {2, 2, 2, 2, 2};
		List<int[]> extraTcs = new ArrayList<int[]>();
		List<int[]> faultSchemas = new ArrayList<int[]>();
		List<int[]> ftcs = new ArrayList<int[]>();
		List<int[]> ptcs = new ArrayList<int[]>();
		ftcs.add(new int[] {1, 1, 1, 1, 0});
		ptcs.add(new int[] {1, 1, 0, 0, 0});
		ptcs.add(new int[] {1, 1, 1, 0, 0});
		ia.faultLocating(valuesOfEachParam, null, ftcs, ptcs, extraTcs, faultSchemas);
		for (int[] sche : faultSchemas) {
			System.out.println(Arrays.toString(sche));
		}
	}
}
