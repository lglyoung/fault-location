package expe;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import base.ILocateFault;
import common.DataHelper;
import common.ResultHandler;
import common.Util;

public class Expe {
	private String resultPath;		//保存实验结果的路径
	private String ctsPath;			//组合测试用例的路径
	private DataHelper dh;			//处理实验数据的助手类
	private int[] valuesOfEachParam;
	private List<int[]> allFtcs;
	private List<int[]> ftcs;
	private List<int[]> ptcs;
	private List<int[]> extraTcs;
	private List<int[]> faultSchemas;
	private ResultHandler rh = new ResultHandler();	//处理实验结果

	
	/**
	 * args[0]：实验数据的根目录
	 * args[1]: 故障定位方法的名称，不区分大小写
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		String rootPath = args[0];			//"D:/Files/测试/BoolExperiment/"
		String tcasFailtestPath = rootPath + "/TCAS_FAILTEST/";
		String tcasMfsPath = rootPath + "/TCAS_MFS/";
		String ctsPath = rootPath + "/CTS/";
		Expe expe = new Expe(tcasFailtestPath, tcasMfsPath, ctsPath);
		expe.resultPath = rootPath + "/FL_RESULT/";
		System.out.println("starting...");
		long start = System.currentTimeMillis();
		
		//关键调用
		expe.doExpe2(LocateFaultFactory.getProxyInstance(args[1]), args[1], "Tconfig", 4);
		System.out.println("end!"+(System.currentTimeMillis()-start));
	}

	/**
	 * 构造器
	 * @param tcasFailtestPath
	 * @param tcasMfsPath
	 * @param ctsPath
	 * @throws IOException
	 */
	public Expe(String tcasFailtestPath, String tcasMfsPath, String ctsPath) throws IOException {
		this.ctsPath = ctsPath;
		dh = new DataHelper(tcasFailtestPath, tcasMfsPath, ctsPath);
	}
	
	/**
	 * 做实验(单线程版)
	 * @throws IOException 
	 */
	public void doExpe(ILocateFault locateFault, String resFileName, String ctToolName) throws IOException {
		//获取所有的保存失效测试用例集的文件名
		List<String> fcasFailtestFileNames = dh.getTcasFailtestFileNames();

		//保存结果的文件
		resFileName = resFileName.toUpperCase()+".txt";
		File resFile = new File(resultPath+resFileName);
		if (!resFile.exists()) resFile.createNewFile();
		OutputStream os = new FileOutputStream(resFile);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));
		
		double hr = 0, sum = 0, extraTcSum = 0;
		for (String tmpStr : fcasFailtestFileNames) {
			//tmpStr = "TCAS12LRF85.txt";
			valuesOfEachParam = dh.getValuesOfEachParam(tmpStr);
			allFtcs = dh.getAllFtcsOrMfs(tmpStr, true);
			List<int[]> cts = Util.genCts(ctsPath+"/"+ctToolName+"/"+valuesOfEachParam.length+"_2_4.txt");
			ptcs = Util.arrDiffSet(cts, allFtcs);
			ftcs = Util.arrDiffSet(cts, ptcs);
			extraTcs = new ArrayList<int[]>();
			faultSchemas = new ArrayList<int[]>();
			
			//故障定位
			locateFault.locateFault(valuesOfEachParam, allFtcs, ftcs, ptcs, extraTcs, faultSchemas);
			
			//处理实验数据
			String mfsFileName = tmpStr.substring(0, tmpStr.lastIndexOf("."))+"_MFS.txt";
			hr = Util.hitRate(dh.getAllFtcsOrMfs(mfsFileName, false), faultSchemas);
			if (hr != 0.0) {
				sum += hr;
			}
			
			extraTcSum += extraTcs.size();	
			
			System.out.println(tmpStr);
		}
		System.out.println("平均命中率：" + sum / fcasFailtestFileNames.size());
		System.out.println("总的附加测试用例数：" + extraTcSum);
		bw.write(extraTcSum+"");
		
		bw.close();
	}
	
	/**
	 * 做实验(多线程版)
	 * @param locateFault	故障定位方法
	 * @param resFileName	保存结果的文件名	
	 * @param lenOfCt		组合测试用例的维度
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public void doExpe2(ILocateFault locateFault, String resFileName, String ctToolName, int lenOfCt) throws IOException, InterruptedException {
		int numOfProcessor = Runtime.getRuntime().availableProcessors();
		//阻塞队列，存放布尔表达式文件名
		BlockingQueue<String> bq = new LinkedBlockingQueue<String>(dh.getTcasFailtestFileNames());
		Runnable[] runners = new Runnable[numOfProcessor+1];
		ExecutorService es = Executors.newFixedThreadPool(numOfProcessor+1);
		
		//创建多个消费线程来处理实验
		for (int i = 0; i < numOfProcessor+1; i++) {
			
			runners[i] = new Thread(new HandleOneSutRunable(locateFault, dh, bq, ctToolName, lenOfCt, rh));
			es.submit(runners[i]);
		}
		es.shutdown();
		es.awaitTermination(365, TimeUnit.DAYS);
		rh.show(dh.getTcasFailtestFileNames().size());
	}
	
}
