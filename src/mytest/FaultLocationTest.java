package mytest;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import common.Util;
import faultlocation.Context;
import faultlocation.IterAIFL;
import faultlocation.SchemaTreeStrategy;

public class FaultLocationTest {
	private int[] valuesOfEachParam;
	private List<int[]> ftcs;
	private List<int[]> extraTcs;
	private List<int[]> faultSchemas;
	private Context ctx;
	
	@Before
	public void before() {
		valuesOfEachParam = new int[] {2, 2, 2, 2, 2};
		int[] ftc1 = {0, 0, 0, 1, 0};
		int[] ftc2 = {0, 1, 0, 1, 0};
		int[] ftc3 = {1, 0, 0, 0, 0};
		int[] ftc4 = {1, 0, 1, 0, 0};
		ftcs = new ArrayList<int[]>();
		ftcs.add(ftc1);
		ftcs.add(ftc2);
		ftcs.add(ftc3);
		ftcs.add(ftc4);
		extraTcs = new ArrayList<int[]>();
		faultSchemas = new ArrayList<int[]>();
	}
	
	@Test
	public void SchemaTreeStrategyTest() {
		ctx = new Context(new SchemaTreeStrategy());
	}
	
	@Test
	public void IterAIFLTest() {
		ctx = new Context(new IterAIFL());
	}
	
	@After
	public void after() {
		ctx.faultLocating(valuesOfEachParam, null, ftcs, null, extraTcs, faultSchemas);

		//打印故障模式
		for(int[] tmp : faultSchemas) {
			System.out.println(Util.intArrayToStr(tmp));
		}
	}
	
}
