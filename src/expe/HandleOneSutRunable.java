package expe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import common.ResultType;
import common.Util;
import entries.Param;

/**
 * 线程：处理一个SUT，如一个布尔表达式变异体
 * @author lglyoung
 *
 */
public class HandleOneSutRunable implements Runnable {
	private BlockingQueue<String> blockingQueue;		//阻塞队列，存放文件名集合
	private Param param;
	
	public HandleOneSutRunable(BlockingQueue<String> bq, Param param) {
		this.blockingQueue = bq;
		this.param = param;
	}
	
	@Override
	public void run() {
		int[] valuesOfEachParam;
		List<int[]> allFtcs;	//所有的失效测试用例
		List<int[]> cts;		//组合测试用例集
		String fcasFailtestFileName = blockingQueue.poll();
		for (; fcasFailtestFileName != null; fcasFailtestFileName = blockingQueue.poll()) {
			try {
				//读取相应的数据
				valuesOfEachParam = param.getDataHepler().getValuesOfEachParam(fcasFailtestFileName);
				allFtcs = param.getDataHepler().getAllFtcsOrMfs(fcasFailtestFileName, true);	
				cts = param.getDataHepler().genCts(param.getCtToolName(), valuesOfEachParam.length, 
						param.getLenOfCt());
				List<int[]> ptcs = Util.arrDiffSet(cts, allFtcs);
				List<int[]> ftcs = Util.arrDiffSet(cts, ptcs);
				List<int[]> extraTcs = new ArrayList<int[]>();
				List<int[]> faultSchemas = new ArrayList<int[]>();
				
				//故障定位
				param.getLocateFault().locateFault(valuesOfEachParam, allFtcs, ftcs, ptcs, extraTcs, faultSchemas);
				
				//处理实验结果
				saveData(param, fcasFailtestFileName, extraTcs, faultSchemas);
				
			} catch (Exception e) {
				System.out.println("HandleOneSutRunable线程发生错误:"+e.getMessage());
			}

		}
	}
	
	/**
	 * 保存实验数据
	 * @throws IOException 
	 */
	private void saveData(Param param, String fcasFailtestFileName, 
			List<int[]> extraTcs, List<int[]> faultSchemas) throws IOException {
		String booleanExprName = fcasFailtestFileName.substring(0, fcasFailtestFileName.lastIndexOf('.'));
		
		//保存附加测试用例
		param.getResultHelper().saveResult(param.getLfName(), param.getCtToolName(), param.getLenOfCt(), 
				ResultType.ExtraTc, booleanExprName, extraTcs);

		//保存测试用例数量
		param.getResultHelper().saveExtraTcSize(param.getLfName(), param.getCtToolName(), param.getLenOfCt(), 
				booleanExprName, extraTcs.size());
		
		//保存定位到的极小故障模式
		param.getResultHelper().saveResult(param.getLfName(), param.getCtToolName(), param.getLenOfCt(), 
				ResultType.FaultSche, booleanExprName, faultSchemas);
		
		/*String mfsFileName = fcasFailtestFileName.substring(0, fcasFailtestFileName.lastIndexOf("."))+"_MFS.txt";
		double hr = Util.hitRate(dh.getAllFtcsOrMfs(mfsFileName, false), faultSchemas);
		rh.sum(extraTcs, faultSchemas, hr);*/
	}
}
