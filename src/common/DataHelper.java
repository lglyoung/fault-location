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
 * 处理布尔表达式实验结果工具类
 * @author lglyoung
 *
 */
public class DataHelper {
	private String rootPath;			//实验的根目录，如"D:/Files/测试/BoolExperiment/"
	private String tcasFailtestPath;	//保存布尔表达式的失效测试用例集的文件夹
	private String tcasMfsPath;			//保存布尔表达式的MFS的文件夹
	private String ctsPath;				//组合测试用例的文件集的路径
	private String resultPath;			//保存实验结果的路径
	private List<String> tcasFailtestFileNames = new ArrayList<String>();	//tcasFailtestPath文件夹下的所有文件名
	private List<String> allBooleanExpr = new ArrayList<String>();			//所有的布尔表达式 
	
	/**
	 * 构造器，读取文件，获取失效测试用例集的所有文件名
	 * @throws IOException
	 */
	public DataHelper(String rootPath) throws IOException {
		this.rootPath = rootPath;
		tcasFailtestPath = rootPath + "/TCAS_FAILTEST/";
		tcasMfsPath = rootPath + "/TCAS_MFS/";
		ctsPath = rootPath + "/CTS/";
		resultPath = rootPath + "/FL_RESULT/";

		FileInputStream fi = new FileInputStream(tcasFailtestPath+"AllListFile.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fi, "utf-8"));
		String line = br.readLine();
		while (line != null) {
			tcasFailtestFileNames.add(line);
			allBooleanExpr.add(line.split(".txt")[0]);
			line = br.readLine();
		}
		br.close();
	}

	/**
	 * 获取失效测试用例集的文件名
	 * @return
	 */
	public List<String> getTcasFailtestFileNames() {
		return tcasFailtestFileNames;
	}
	
	/**
	 * 获取保存布尔表达式的MFS的文件夹
	 * @return
	 */
	public String getTcasMfsPath() {
		return tcasMfsPath;
	}
	
	public String getCtsPath() {
		return ctsPath;
	}
	
	public String getRootPath() {
		return rootPath;
	}

	public String getResultPath() {
		return resultPath;
	}

	/**
	 * 获取所有失效测试用例集或者MFS
	 * @param tcasFailtestOfMfsFileName 如果是mfs文件名，那么需要根据这个文件名获取对应的ftc文件名，从而获取valuesOfEachParam
	 * @param isGetAllFtcs 如果为true，则读取ftcs，否则读取mfs
	 * @return
	 * @throws IOException 
	 */
	public List<int[]> getAllFtcsOrMfs(String tcasFailtestOfMfsFileName, boolean isGetAllFtcs) throws IOException {
		List<int[]> allFtcsOfMfs = new ArrayList<int[]>();
		int[] valuesOfEachParam = getValuesOfEachParam(isGetAllFtcs ? tcasFailtestOfMfsFileName : tcasFailtestOfMfsFileName.split("_")[0]+".txt");	
		int itemsOfLine = 1;		//考虑一行有多个item的情况
		
		File f = new File((isGetAllFtcs ? tcasFailtestPath : tcasMfsPath)+tcasFailtestOfMfsFileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf-8"));
		String line = br.readLine();
		String oneItem = null;
		while (line != null) {
			if (!line.isEmpty()) {	//考虑了空行这种情况
				itemsOfLine = line.length() / valuesOfEachParam.length;
				for (int i = 0; i < itemsOfLine; i++) {
					oneItem = line.substring(i*valuesOfEachParam.length, (i+1)*valuesOfEachParam.length);
					
					int[] oneItemArr = new int[valuesOfEachParam.length];
					//读取的是失效测试用例
					if (isGetAllFtcs) {
						for (int j = 0; j < oneItem.length(); j++) {
							oneItemArr[j] = Integer.parseInt(oneItem.substring(j, j+1));
						}
					} else {
						for (int j = 0; j < oneItem.length(); j++) {
							if (!oneItem.substring(j, j+1).equals("-")) {
								oneItemArr[j] = Integer.parseInt(oneItem.substring(j, j+1));
							} else {
								oneItemArr[j] = -1;	//将"-"字符转成-1
							}
						}
					}
					allFtcsOfMfs.add(oneItemArr);
				}
			}
			line = br.readLine();
		}
		br.close();
		
		return allFtcsOfMfs;
	}
	
	/**
	 * 根据测试用例的长度来获取参数的个数（每隔参数的值的个数默认为2。因为做的实验是布尔表达式）
	 * @param tcasFailtestFileName
	 * @return 返回null，表示没有失效测试用例
	 * @throws IOException 
	 */
	public int[] getValuesOfEachParam(String tcasFailtestFileName) throws IOException {
		File f = new File(this.tcasFailtestPath+tcasFailtestFileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf-8"));
		String line = br.readLine();
		while (line != null) {
			if (!line.isEmpty()) {
				int[] valuesOfEachParam = new int[line.length()];
				for (int i = 0; i < line.length(); i++) {
					valuesOfEachParam[i] = 2;			//因为是布尔表达式，所以每个参数的值都是两个
				}
				br.close();
				return valuesOfEachParam;
			}
			line = br.readLine();
		}
		br.close();
		return null;
	}

	/**
	 * 获取组合测试用例集
	 * @param lfName 
	 * @param params 布尔表达式参数的个数
	 * @param lenOfCt 
	 * @return
	 * @throws IOException
	 */
	public List<int[]> genCts(CtToolName ctToolName, int params, int lenOfCt) throws IOException {
		//组合测试用例集路径
		String ctpath = ctsPath+"/"+ctToolName.getName()+
				"/"+params+"_2_"+lenOfCt+".txt";
		List<int[]> cts = new ArrayList<int[]>();
		
		//根据文件名获取测试用例的长度
		File f = new File(ctpath);
		int numsOfParam = Integer.parseInt(f.getName().split("_")[0]);
		
		FileInputStream fi = new FileInputStream(ctpath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fi, "utf-8"));
		String line = br.readLine();
		while (line != null) {
			String tmpStr = line.split("\\|")[1].replaceAll(" ", "");
			int[] tmpCt = new int[numsOfParam];
			for (int i = 0; i < numsOfParam; i++) {
				if (tmpStr.charAt(i) == '-') {			//用0表示'-'
					tmpCt[i] = 0;
				} else {
					tmpCt[i] = Integer.parseInt(tmpStr.substring(i, i+1))-1;
				}
			}
			cts.add(tmpCt);
			line = br.readLine();
		}
		br.close();
		return cts;
	}
	
	/**
	 * 根据失效测试用例集的文件名获取MFS的文件名
	 * @param fcasFailtestFileName 保存失效测试用例集的文件名
	 * @return
	 */
	public String getMfsFileName(String fcasFailtestFileName) {
		 return fcasFailtestFileName.substring(0, fcasFailtestFileName.lastIndexOf("."))+"_MFS.txt";
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
	public String getResultFilePath(LfName flName, CtToolName ctToolName, int lenOfCt,
			ResultType resultType, String name) {
		String path = resultPath+flName.getName()+"/"+ctToolName.getName()+"/"
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
	
	/**
	 * 读取附加测试用例的数量
	 * @param flName
	 * @param ctToolName
	 * @param lenOfCt
	 * @return
	 * @throws IOException
	 */
	public List<String> readExtraTcSize(LfName flName, CtToolName ctToolName, int lenOfCt) throws IOException {
		String absolutePath = getResultFilePath(flName, ctToolName, lenOfCt, ResultType.ExtraTc, "size");
		return readFileLineByLine(absolutePath, "utf-8");
	}
	
}
