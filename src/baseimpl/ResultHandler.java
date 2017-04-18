package baseimpl;

import java.io.IOException;

import base.IHandler;
import common.ResultHelper;
import entries.Param;

/**
 * 结果处理类：从硬盘中读取中间数据计算
 * @author lglyoung
 *
 */
public class ResultHandler implements IHandler {
	private ResultHelper resultHelper = new ResultHelper();
	
	@Override
	public void handle(Param param, String fcasFailtestFileName){
		try {
			//计算结果
			resultHelper.putEachSUT(param, fcasFailtestFileName);
			
		} catch (IOException e) {
			System.out.println(this.getClass().getName()+"发生错误："+e.getMessage());
		}
		
	}

	public ResultHelper getResultHelper() {
		return resultHelper;
	}
	
}
