package expe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import base.ILocateFault;
import common.DataHelper;
import common.ResultHandler;
import common.Util;

/**
 * 线程：处理一个SUT，如一个布尔表达式变异体
 * @author lglyoung
 *
 */
public class HandleOneSutRunable implements Runnable {
	private DataHelper dh;
	private String ctToolName;				//组合测试工具名
	private int lenOfCt;					//多少维的组合测试
	private ILocateFault locateFault;		//故障定位方法
	private BlockingQueue<String> bq;		//阻塞队列，存放文件名集合
	private ResultHandler rh;
	
	public HandleOneSutRunable(ILocateFault locateFault, DataHelper dh, 
			BlockingQueue<String> bq, String ctToolName, int lenOfCt, ResultHandler rh) {
		this.locateFault = locateFault;
		this.dh = dh;
		this.bq = bq;
		this.ctToolName = ctToolName;
		this.lenOfCt = lenOfCt;
		this.rh = rh;
	}
	
	@Override
	public void run() {
		int[] valuesOfEachParam;
		List<int[]> allFtcs;	//所有的失效测试用例
		List<int[]> cts;		//组合测试用例集
		String fcasFailtestFileName = bq.poll();
		for (; fcasFailtestFileName != null; fcasFailtestFileName = bq.poll()) {
			try {
				valuesOfEachParam = dh.getValuesOfEachParam(fcasFailtestFileName);
				allFtcs = dh.getAllFtcsOrMfs(fcasFailtestFileName, true);
				//组合测试用例集路径
				String ctpath = dh.getCtsPath()+"/"+ctToolName+"/"+valuesOfEachParam.length+"_2_"+lenOfCt+".txt";
				cts = Util.genCts(ctpath);
			
				List<int[]> ptcs = Util.arrDiffSet(cts, allFtcs);
				List<int[]> ftcs = Util.arrDiffSet(cts, ptcs);
				List<int[]> extraTcs = new ArrayList<int[]>();
				List<int[]> faultSchemas = new ArrayList<int[]>();
				
				//故障定位
				locateFault.locateFault(valuesOfEachParam, allFtcs, ftcs, ptcs, extraTcs, faultSchemas);
				
				System.out.println(Thread.currentThread().getName()+":"+fcasFailtestFileName);
				
				String mfsFileName = fcasFailtestFileName.substring(0, fcasFailtestFileName.lastIndexOf("."))+"_MFS.txt";
				double hr = Util.hitRate(dh.getAllFtcsOrMfs(mfsFileName, false), faultSchemas);
				rh.sum(extraTcs, faultSchemas, hr);
			} catch (Exception e) {
				System.out.println("ERROR:"+fcasFailtestFileName+e.getMessage());
				return ;
			}

		}
	}
}
