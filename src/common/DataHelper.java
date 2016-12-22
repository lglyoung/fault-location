package common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
}
