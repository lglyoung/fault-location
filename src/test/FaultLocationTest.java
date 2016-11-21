package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import common.DataHelper;
import common.IStrategy;
import common.Util;
import faultlocation.IterAIFL;
import faultlocation.Ri;
import faultlocation.SchemaTreeStrategy;
import faultlocation.Simplification;
import faultlocation.Sri;

public class FaultLocationTest {
	private DataHelper dh;
	private int[] valuesOfEachParam;
	private List<int[]> affFtcs;
	private List<int[]> ftcs;
	private List<int[]> ptcs;
	private List<int[]> extraTcs;
	private List<int[]> faultSchemas;
	private String curBoolExp = "TCAS12LRF85";
	private IStrategy strategy;
	
	@Before
	public void before() throws IOException {
		String rootPath = "D:\\Files\\测试\\BoolExperiment\\";
		String tcasFailtestPath = rootPath + "TCAS_FAILTEST\\";
		String tcasMfsPath = rootPath + "TCAS_MFS\\";
		String ctsPath = rootPath + "CTS\\";
		dh = new DataHelper(tcasFailtestPath, tcasMfsPath);
		valuesOfEachParam = dh.getValuesOfEachParam(curBoolExp+".txt");
		affFtcs = dh.getAllFtcsOrMfs(curBoolExp+".txt", true);
		List<int[]> cts = Util.genCts(ctsPath+valuesOfEachParam.length+"_2_2.txt");
		ptcs = Util.arrDiffSet(cts, affFtcs);
		ftcs = Util.arrDiffSet(cts, ptcs);
		extraTcs = new ArrayList<int[]>();
		faultSchemas = new ArrayList<int[]>();
	}
	
	@Test
	public void simplificationTest() {
		strategy = new Simplification();
	}
	
	@Test
	public void riTest() {
		strategy = new Ri();
	}
	

	@Test
	public void sriTest() {
		strategy = new Sri();
	}
	
	@Test
	public void iterAIFLTest() {
		strategy = new IterAIFL();
	}
	
	@Test
	public void schemaTreeTest() {
		strategy = new SchemaTreeStrategy();
	}
	
	@After
	public void after() throws IOException {
		strategy.faultLocating(valuesOfEachParam, affFtcs, ftcs, ptcs, extraTcs, faultSchemas);
		
		//计算命中率
		System.out.println("命中率："+
				Util.hitRate(dh.getAllFtcsOrMfs(curBoolExp+"_MFS.txt", false), faultSchemas));
		
		//不命中率
		Util.removeParentSche(faultSchemas);
		System.out.println("错误率："+
				Util.notHitRate(dh.getAllFtcsOrMfs(curBoolExp+"_MFS.txt", false), faultSchemas));
		
	}
	
}
