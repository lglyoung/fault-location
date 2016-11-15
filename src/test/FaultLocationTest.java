package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import common.DataHelper;
import common.Util;
import faultlocation.Context;
import faultlocation.IterAIFL;
import faultlocation.SchemaTreeStrategy;

public class FaultLocationTest {
	private DataHelper dh;
	private int[] valuesOfEachParam;
	private List<int[]> affFtcs;
	private List<int[]> ftcs;
	private List<int[]> ptcs;
	private List<int[]> extraTcs;
	private List<int[]> faultSchemas;
	private Context ctx;
	
	@Before
	public void before() throws IOException {
		List<int[]> cts = new ArrayList<int[]>();	//组合测试工具生成的测试用例集
		cts.add(new int[] {0,0,0,1,0});
		cts.add(new int[] {1,0,1,1,0});
		cts.add(new int[] {1,1,0,1,0});
		cts.add(new int[] {0,1,1,0,1});
		cts.add(new int[] {0,1,1,1,1});
		cts.add(new int[] {1,0,0,0,1});
		
		String tcasFailtestPath = "D:\\Files\\测试\\BoolExperiment\\TCAS_FAILTEST\\";
		String tcasMfsPath = "D:\\Files\\测试\\BoolExperiment\\TCAS_MFS\\";
		dh = new DataHelper(tcasFailtestPath, tcasMfsPath);
		valuesOfEachParam = dh.getValuesOfEachParam("TCAS1ASF1.txt");
		affFtcs = dh.getAllFtcsOrMfs("TCAS1ASF1.txt", true);
		ptcs = Util.arrDiffSet(cts, affFtcs);
		ftcs = Util.arrDiffSet(cts, ptcs);
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
		ctx.faultLocating(valuesOfEachParam, affFtcs, ftcs, ptcs, extraTcs, faultSchemas);

		//打印故障模式
		for(int[] tmp : faultSchemas) {
			System.out.println(Util.intArrayToStr(tmp));
		}
	}
	
}
