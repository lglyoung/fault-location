package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import base.ILocateFault;
import baseimpl.BFSSelectUnknowNode;
import baseimpl.BSLocateFixedParam;
import baseimpl.CompleteScheTree;
import baseimpl.CuttedByFicScheTree;
import baseimpl.GreedSelectUnknowNode;
import baseimpl.LocateFixedParam;
import baseimpl.Ri;
import baseimpl.Simplification;
import baseimpl.Sri;
import common.Configure;
import common.CtToolNameEnum;
import common.DataHelper;
import common.Util;
import expe.LocateFaultFactory;
import locatefault.BooleanExpressLocateFault;
import locatefault.DeltaDebug;
import locatefault.DeltaDebugMul;
import locatefault.Fic;
import locatefault.IterAIFL;
import locatefault.Trt;

public class FaultLocationTest {
	private DataHelper dh;
	private int[] valuesOfEachParam;
	private List<int[]> affFtcs;
	private List<int[]> ftcs;
	private List<int[]> ptcs;
	private List<int[]> extraTcs;
	private List<int[]> faultSchemas;
	private String curBoolExp = "TCAS20CCF267";//"TCAS12LRF85";TCAS20ASF2
	private ILocateFault faultLocate;
	
	@Before
	public void before() throws IOException {
		String rootPath = "D:/Files/测试/BoolExperiment/";
		dh = new DataHelper(rootPath);
		valuesOfEachParam = dh.getValuesOfEachParam(curBoolExp+".txt");
		affFtcs = dh.getAllFtcsOrMfs(curBoolExp+".txt", true);
		List<int[]> cts = dh.genCts(CtToolNameEnum.TCONFIG, valuesOfEachParam.length, 4);
		ptcs = Util.arrDiffSet(cts, affFtcs);
		ftcs = Util.arrDiffSet(cts, ptcs);
		extraTcs = new ArrayList<int[]>();
		faultSchemas = new ArrayList<int[]>();
		
		System.out.println("失效测试用例集的个数："+ftcs.size());
	}
	
	@Test
	public void simplificationTest() {
		faultLocate = new DeltaDebugMul(new Simplification());
	}
	
	@Test
	public void riTest() {
		faultLocate = new DeltaDebug(new Ri());
	}
	

	@Test
	public void sriTest() {
		faultLocate = new DeltaDebug(new Sri());
	}
	
	@Test
	public void iterAIFLTest() {
		faultLocate = new IterAIFL();
	}
	
	@Test
	public void schemaTreeTest() {
		faultLocate = new Trt(new CompleteScheTree(), new BFSSelectUnknowNode());
	}
	
	@Test
	public void cuttedSchemaTreeTest() {
		faultLocate = new Trt(new CuttedByFicScheTree(new BSLocateFixedParam()), new GreedSelectUnknowNode());
	}
	
	@Test
	public void finovlpTest() throws IOException {
		faultLocate = new Fic(new LocateFixedParam());
	}
	
	@Test
	public void factoryTest() {
		faultLocate = LocateFaultFactory.getProxyInstance(Configure.SRI_MUL);
	}
	
	@Test
	public void booleanExpressLocateFaultTest() {
		faultLocate = new BooleanExpressLocateFault();
	}
	
	@After
	public void after() throws IOException {
		faultLocate.locateFault(valuesOfEachParam, affFtcs, ftcs, ptcs, extraTcs, faultSchemas);
		
		//移除重复的附加测试用例
		Util.delRepeat(extraTcs);
		Util.delRepeat(faultSchemas);
		
		//附加测试用例数
		System.out.println("附加测试用例数：" + extraTcs.size());	
		System.out.println("故障模式数：" + faultSchemas.size());
		
		System.out.println("附加测试用例：");
		for (int[] t : extraTcs) {
			System.out.println(Util.intArrayToStr(t));
		}
		
		System.out.println("故障模式：");
		for (int[] t : faultSchemas) {
			System.out.println(Util.intArrayToStr(t));
		}
	}
	
}
