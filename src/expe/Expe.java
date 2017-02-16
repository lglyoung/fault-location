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
import common.CtToolNameEnum;
import common.DataHelper;
import common.IndicatorEnum;
import common.LfNameEnum;
import common.ResultGroupEnum;
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
			handler = new ResultHandler();		
		} else {
			System.err.println("请输入正确的handler名！savehandler or resulthandler");
			return;
		}
		
		DataHelper dataHelper = new DataHelper("D:\\Files\\测试\\故障定位实验\\最终实验结果\\BoolExperimentAndFaultLocation");	//System.getProperty("user.dir")得到的是执行java -jar命令所在的路径;"D:/Files/测试/BoolExperiment/"
		
		System.out.println("starting...");
		
		//关键调用
		LfNameEnum[] lfNames = LfNameEnum.values();
		int[] lenOfCts = {2, 3, 4};
		Param param = new Param();
		
		//遍历
		for (int i = 0; i < lfNames.length; i++) {
			LfNameEnum lfName = lfNames[i];
			System.out.println(lfName.getName());
			for (int lenOfCt : lenOfCts) {
				param.set(handler, LocateFaultFactory.getProxyInstance(lfName.getName()), 
						dataHelper, lfName, CtToolNameEnum.TCONFIG, lenOfCt);
				doExpe(param);
			}
		}
		
		//打印结果
		if (handler instanceof ResultHandler) {
			ResultHelper resultHelper = ((ResultHandler) handler).getResultHelper();
			resultHelper.genBoxplotSourceData(resultHelper.getPercentageExtraTcSizeMap(param, resultHelper.getExtraTcSizeMap()), 
					IndicatorEnum.EXTRA_TC, dataHelper, ResultGroupEnum.ALL);
			resultHelper.genBoxplotSourceData(resultHelper.getPercentageExtraTcSizeMap(param, resultHelper.getExtraTcSizeMap()), 
					IndicatorEnum.EXTRA_TC, dataHelper, ResultGroupEnum.EXPR);
			resultHelper.genBoxplotSourceData(resultHelper.getPercentageExtraTcSizeMap(param, resultHelper.getExtraTcSizeMap()), 
					IndicatorEnum.EXTRA_TC, dataHelper, ResultGroupEnum.MUTA);
			
//			resultHelper.genBoxplotSourceData(resultHelper.getRecallMap(), IndicatorEnum.RECALL, dataHelper, ResultGroupEnum.ALL);
//			resultHelper.genBoxplotSourceData(resultHelper.getPrecisionMap(), IndicatorEnum.PERCISION, dataHelper, ResultGroupEnum.ALL);
//			resultHelper.genBoxplotSourceData(resultHelper.getfMeasureMap(), IndicatorEnum.F_MEASURE, dataHelper, ResultGroupEnum.ALL);
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
		} else {
			System.out.println("ExecutorService: done");
		}
	}
	
}
