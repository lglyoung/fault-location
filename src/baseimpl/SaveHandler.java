package baseimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import base.IHandler;
import common.ResultType;
import common.Util;
import entries.Param;

/**
 * 故障定位，并保存中间数据
 * @author lglyoung
 *
 */
public class SaveHandler implements IHandler {

	@Override
	public void handle(Param param, String fcasFailtestFileName) {
		int[] valuesOfEachParam;
		List<int[]> allFtcs;	//所有的失效测试用例
		List<int[]> cts;		//组合测试用例集
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
		} catch (IOException e) {
			System.out.println(this.getClass().getName()+"发生错误："+e.getMessage());
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
		param.getDataHepler().saveResult(param.getLfName(), param.getCtToolName(), param.getLenOfCt(), 
				ResultType.ExtraTc, booleanExprName, extraTcs);

		//保存测试用例数量
		param.getDataHepler().saveExtraTcSize(param.getLfName(), param.getCtToolName(), param.getLenOfCt(), 
				booleanExprName, extraTcs.size());
		
		//保存定位到的极小故障模式
		param.getDataHepler().saveResult(param.getLfName(), param.getCtToolName(), param.getLenOfCt(), 
				ResultType.FaultSche, booleanExprName, faultSchemas);
	}
}
