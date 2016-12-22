package expe;

import java.util.concurrent.BlockingQueue;

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
		String fcasFailtestFileName = blockingQueue.poll();
		for (; fcasFailtestFileName != null; fcasFailtestFileName = blockingQueue.poll()) {
			param.getHandler().handle(param, fcasFailtestFileName);
		}
	}
	

}
