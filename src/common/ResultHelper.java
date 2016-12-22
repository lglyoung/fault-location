package common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 实验数据处理类
 * @author lglyoung
 *
 */
public class ResultHelper {
	private double hrSum;		//命中率总和
	private int numOfExtraTc;	//附加测试用例总和
	private DataHelper dataHepler;
	
	public ResultHelper(DataHelper dh) {
		this.dataHepler = dh;
	}
	
	public synchronized void sum(List<int[]> extraTcs, List<int[]> faultSchemas, double hr) {
		hrSum += hr;
		numOfExtraTc += extraTcs.size();
	}
	
	public void show(int numOfBoolExp) {
		System.out.println("平均命中率：" + hrSum / numOfBoolExp);
		System.out.println("总的附加测试用例数：" + numOfExtraTc);
	}
	
	/**
	 * 保存实验得到的附加测试用例集和故障模式集
	 * @param lfName 故障定位方法的名称
	 * @param ctToolName 生成组合测试用例的工具名称
	 * @param lenOfCt 组合测试的维度
	 * @param booleanExprName 布尔表达式的名称
	 * @param ExtraTcsOrFss 附加测试用例集或故障模式集
	 * @param ResultType 结果的类型，附加测试用例还是故障模式
	 * @throws IOException 
	 */
	public void saveResult(LfName lfName, CtToolName ctToolName, int lenOfCt, ResultType resultType,
			String booleanExprName, List<int[]> ExtraTcsOrFss) throws IOException {
		//保存结果的路径
		String path = getResultFilePath(lfName, ctToolName, 
				lenOfCt, resultType, booleanExprName);
		path = path.substring(0, path.lastIndexOf('/')+1);
		
		//创建目录
		File pathFileObj = new File(path);
		if (!pathFileObj.exists()) pathFileObj.mkdirs();
		
		//创建BufferedWriter
		String resultFileName = path+booleanExprName+".txt";
		File resultFile = new File(resultFileName);
		if (!resultFile.exists()) {
			resultFile.createNewFile();
		}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(resultFile), "UTF-8"));
		
		//遍历List<int[]>，把数据写入到文件中
		for (int[] tmpItem : ExtraTcsOrFss) {
			bw.write(Util.intArrayToStr(tmpItem));
			bw.newLine();
		}
		
		//关闭BufferedWriter
		bw.close();
	}
	
	/**
	 * 获取保存结果的文件的绝对路径
	 * @param flName
	 * @param ctToolName
	 * @param lenOfCt
	 * @param resultType
	 * @param name 没有后缀的文件名，可以是布尔表达式名
	 * @return
	 */
	private String getResultFilePath(LfName flName, CtToolName ctToolName, int lenOfCt,
			ResultType resultType, String name) {
		String path = dataHepler.getResultPath()+flName.getName()+"/"+ctToolName.getName()+"/"
				+lenOfCt+"_ct/"+resultType.getName()+"/"+name+".txt";
		return path;
	}
	
	/**
	 * 一行一行读取文本文件
	 * @param path
	 * @param charset 字符编码
	 * @return
	 * @throws IOException 
	 */
	public List<String> readFileLineByLine(String path, String charset) throws IOException {
		FileInputStream in = new FileInputStream(new File(path));
		BufferedReader br = new BufferedReader(new InputStreamReader(in, charset));
		List<String> strs = new ArrayList<String>();
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			strs.add(line);
		}
		br.close();
		return strs;
	}
	
	/**
	 * 保存附加测试用例的数量
	 * @param booleanExprName
	 * @param size
	 * @throws IOException 
	 */
	public synchronized void saveExtraTcSize(LfName flName, CtToolName ctToolName, int lenOfCt, 
			String booleanExprName, int size) throws IOException {
		String absolutePath = getResultFilePath(flName, ctToolName, lenOfCt, ResultType.ExtraTc, "size");
		
		//创建BufferedWriter
		File f = new File(absolutePath);
		if (!f.exists()) {
			f.createNewFile();
		}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(f, true), "UTF-8"));		//追加
		bw.write(booleanExprName+":"+size);
		bw.newLine();
		
		//关闭BufferedWriter
		bw.close();
	}
	
}
