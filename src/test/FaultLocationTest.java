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
import common.CtToolName;
import common.DataHelper;
import common.LfName;
import common.ResultHandler;
import common.ResultType;
import common.Util;
import expe.Expe;
import expe.LocateFaultFactory;
import locatefault.BooleanExpressLocateFault;
import locatefault.DeltaDebug;
import locatefault.DeltaDebugMul;
import locatefault.Fic;
import locatefault.IterAIFL;
import locatefault.Trt;

public class FaultLocationTest {
	private DataHelper dh;
	private ResultHandler rh;
	private int[] valuesOfEachParam;
	private List<int[]> affFtcs;
	private List<int[]> ftcs;
	private List<int[]> ptcs;
	private List<int[]> extraTcs;
	private List<int[]> faultSchemas;
	private String curBoolExp = "TCAS12LRF85";//"TCAS12LRF85";TCAS20ASF2
	private ILocateFault faultLocate;
	
	@Before
	public void before() throws IOException {
		String rootPath = "D:/Files/测试/BoolExperiment/";
		String tcasFailtestPath = rootPath + "TCAS_FAILTEST/";
		String tcasMfsPath = rootPath + "TCAS_MFS/";
		String ctsPath = rootPath + "CTS/Tconfig/";
		dh = new DataHelper(rootPath, tcasFailtestPath, tcasMfsPath, ctsPath);
		rh = new ResultHandler(dh);
		valuesOfEachParam = dh.getValuesOfEachParam(curBoolExp+".txt");
		affFtcs = dh.getAllFtcsOrMfs(curBoolExp+".txt", true);
		List<int[]> cts = Util.genCts(ctsPath+valuesOfEachParam.length+"_2_4.txt");
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
		Expe.lenOfCt = 5;
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
		
		//计算命中率
		System.out.println("命中率："+
				Util.hitRate(dh.getAllFtcsOrMfs(curBoolExp+"_MFS.txt", false), faultSchemas));
		
		//不命中率
		//Util.removeParentSche(faultSchemas);
		System.out.println("错误率："+
				Util.notHitRate(dh.getAllFtcsOrMfs(curBoolExp+"_MFS.txt", false), faultSchemas));		
		
		//附加测试用例数
		System.out.println("附加测试用例数：" + extraTcs.size());	
		
	}
	
}
