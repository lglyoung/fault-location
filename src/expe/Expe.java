package expe;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import base.IHandler;
import baseimpl.ResultHandler;
import baseimpl.SaveHandler;
import common.CtToolName;
import common.DataHelper;
import common.LfName;
import common.ResultHelper;
import entries.Param;

public class Expe {
	/**
	 * 程序入口
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		//根据第一个参数创建handler
		IHandler handler = null; 
		if (args.length == 0) {
			System.err.println("请输入handler！savehandler or resulthandler");
			return;
		}
		if (args[0].equals("savehandler")) {
			handler = new SaveHandler();		//保存或读取中间数据的handler
		} else if (args[0].equals("resulthandler")) {
			handler = new ResultHandler();		//保存或读取中间数据的handler
		} else {
			System.err.println("请输入正确的handler名！savehandler or resulthandler");
			return;
		}
		
		DataHelper dataHelper = new DataHelper(System.getProperty("user.dir"));	//System.getProperty("user.dir")得到的是执行java -jar命令所在的路径;"D:/Files/测试/BoolExperiment/"
		
		System.out.println("starting...");
		
		//关键调用
		LfName[] lfNames = LfName.values();
		int[] lenOfCts = {2, 3, 4};
		Param param = new Param();
		
		//遍历
		for (int i = 0; i < lfNames.length; i++) {
			LfName lfName = lfNames[i];
			System.out.println(lfName.getName());
			for (int lenOfCt : lenOfCts) {
				param.set(handler, LocateFaultFactory.getProxyInstance(lfName.getName()), 
						dataHelper, lfName, CtToolName.TCONFIG, lenOfCt);
				doExpe(param);
			}
		}
		
		//打印结果
		if (handler instanceof ResultHandler) {
			ResultHelper resultHelper = ((ResultHandler) handler).getResultHelper();
			
			//附加测试用例
			ResultHelper.formateShowResult(resultHelper.showEachSourceExpr(resultHelper.getExtraTcSizeMap()), false);
			ResultHelper.formateShowResult(
					resultHelper.showAvg(resultHelper.getExtraTcSizeMap(), dataHelper.getTcasFailtestFileNames().size())
					, false);
					
			//recall
			ResultHelper.formateShowResult(resultHelper.showEachSourceExpr(resultHelper.getRecallMap()), true);
			ResultHelper.formateShowResult(
					resultHelper.showAvg(resultHelper.getRecallMap(), dataHelper.getTcasFailtestFileNames().size())
					, true);

			
			//percision
			ResultHelper.formateShowResult(resultHelper.showEachSourceExpr(resultHelper.getPrecisionMap()), true);
			ResultHelper.formateShowResult(
					resultHelper.showAvg(resultHelper.getPrecisionMap(), dataHelper.getTcasFailtestFileNames().size())
					, true);
			
			//f
			ResultHelper.formateShowResult(resultHelper.showEachSourceExpr(resultHelper.getfMeasureMap()), true);
			ResultHelper.formateShowResult(
					resultHelper.showAvg(resultHelper.getfMeasureMap(), dataHelper.getTcasFailtestFileNames().size())
					, true);
		}
	}
	
	/**
	 * 做实验(多线程版)
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static void doExpe(Param param) throws IOException, InterruptedException {
		int numOfProcessor = Runtime.getRuntime().availableProcessors();
		//阻塞队列，存放布尔表达式文件名
		BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<String>(param.getDataHepler().getTcasFailtestFileNames());
		Runnable[] runners = new Runnable[numOfProcessor+1];
		ExecutorService es = Executors.newFixedThreadPool(numOfProcessor+1);
		
		//创建多个消费线程来处理实验
		for (int i = 0; i < numOfProcessor+1; i++) {
			runners[i] = new Thread(new HandleOneSutRunable(blockingQueue, param));
			es.submit(runners[i]);
		}
		es.shutdown();
		if(!es.awaitTermination(30, TimeUnit.DAYS)) {
			System.out.println("ExecutorService: timeout elapsed before termination");
		}
	}
	
}
