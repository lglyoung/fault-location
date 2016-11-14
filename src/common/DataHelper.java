package common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DataHelper {
	private String tcasFailtestPath;	//保存布尔表达式的失效测试用例集的文件夹
	private String tcasMfsPath;			//保存布尔表达式的MFS的文件夹
	private List<String> tcasFailtestFileNames = new ArrayList<String>();	//tcasFailtestPath文件夹下的所有文件名
	
	/**
	 * 构造器，读取文件，获取失效测试用例集的所有文件名
	 * @param tcasFailtestPath
	 * @param tcasMfsPath
	 * @throws IOException
	 */
	public DataHelper(String tcasFailtestPath, String tcasMfsPath) throws IOException {
		this.tcasFailtestPath = tcasFailtestPath;
		this.tcasMfsPath = tcasMfsPath;
		FileInputStream fi = new FileInputStream(tcasFailtestPath+"AllListFile.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fi, "utf-8"));
		String line = br.readLine();
		while (line != null) {
			tcasFailtestFileNames.add(line);
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
	 * 获取所有失效测试用例集
	 * @param tcasFailtestFileName
	 * @return
	 * @throws IOException 
	 */
	public List<int[]> getAllFtcs(String tcasFailtestFileName) throws IOException {
		File f = new File(this.tcasFailtestPath+tcasFailtestFileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf-8"));
		String line = br.readLine();
		while (line != null) {
			if (!line.isEmpty()) {	//考虑了空行这种情况
				
			}
		}
		br.close();
		return null;
	}
	
	/**
	 * 获取所有的MFS
	 * @param tcasFailtestFileName
	 * @return
	 */
	public List<int[]> getMfs(String tcasFailtestFileNam) {
		return null;
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
	
}
