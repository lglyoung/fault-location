package experiment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import common.DataHelper;
import common.Util;
import faultlocation.Context;
import faultlocation.IStrategy;
import faultlocation.IterAIFL;

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
	private Context ctx;
	
	public static void main(String[] args) throws IOException {
		String rootPath = "D:\\Files\\测试\\BoolExperiment\\";
		String tcasFailtestPath = rootPath + "TCAS_FAILTEST\\";
		String tcasMfsPath = rootPath + "TCAS_MFS\\";
		String ctsPath = rootPath + "CTS\\";
		Expe expe = new Expe(tcasFailtestPath, tcasMfsPath, ctsPath);
		expe.resultPath = rootPath + "FL_RESULT\\";
		expe.doExpe(new IterAIFL());
		System.out.println("end!");
	}

	
	public Expe(String tcasFailtestPath, String tcasMfsPath, String ctsPath) throws IOException {
		this.ctsPath = ctsPath;
		dh = new DataHelper(tcasFailtestPath, tcasMfsPath);
	}
	
	/**
	 * 做实验
	 * @throws IOException 
	 */
	public void doExpe(IStrategy strategy) throws IOException {
		ctx = new Context(strategy);
		
		//获取所有的保存失效测试用例集的文件名
		List<String> fcasFailtestFileNames = dh.getTcasFailtestFileNames();

		//保存结果的文件
		String resFileName = strategy.getClass().getSimpleName()+".txt";
		File resFile = new File(resultPath+resFileName);
		if (!resFile.exists()) resFile.createNewFile();
		OutputStream os = new FileOutputStream(resFile);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));
		
		for (String tmpStr : fcasFailtestFileNames) {
			//tmpStr = "TCAS12LRF85.txt";
			valuesOfEachParam = dh.getValuesOfEachParam(tmpStr);
			allFtcs = dh.getAllFtcsOrMfs(tmpStr, true);
			List<int[]> cts = Util.genCts(ctsPath+valuesOfEachParam.length+"_2_4.txt");
			ptcs = Util.arrDiffSet(cts, allFtcs);
			ftcs = Util.arrDiffSet(cts, ptcs);
			extraTcs = new ArrayList<int[]>();
			faultSchemas = new ArrayList<int[]>();
			
			//故障定位
			ctx.faultLocating(valuesOfEachParam, allFtcs, ftcs, ptcs, extraTcs, faultSchemas);
			
			//处理实验数据
			
			String mfsFileName = tmpStr.substring(0, tmpStr.lastIndexOf("."))+"_MFS.txt";
			String res = tmpStr+"命中率："+
					Util.hitRate(dh.getAllFtcsOrMfs(mfsFileName, false), faultSchemas);
			System.out.println(res);
			bw.write(res);
		}
		bw.close();
	}
}