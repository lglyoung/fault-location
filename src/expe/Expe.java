package expe;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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
		DataHelper dataHelper = new DataHelper(System.getProperty("user.dir"));	//System.getProperty("user.dir")得到的是执行java -jar命令所在的路径
		ResultHelper resultHelper = new ResultHelper(dataHelper);
		
		System.out.println("starting...");
		
		//关键调用
		LfName[] lfNames = LfName.values();
		int[] lenOfCts = {4};
		Param param = new Param();
		for (LfName lfName : lfNames) {
			System.out.println(lfName.getName());
			for (int lenOfCt : lenOfCts) {
				param.set(LocateFaultFactory.getProxyInstance(lfName.getName()), 
						dataHelper, lfName, CtToolName.TCONFIG, lenOfCt, resultHelper);
				doExpe(param);
			}
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
		es.awaitTermination(30, TimeUnit.DAYS);
	}
	
}
