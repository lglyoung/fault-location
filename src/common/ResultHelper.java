package common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import entries.Param;

/**
 * 结果处理类
 * @author lglyoung
 *
 */
public class ResultHelper {
	private Map<String, Double> extraTcSizeMap = new TreeMap<String, Double>();			//保存平均附加测试用例数
	private Map<String, Double> recallMap = new TreeMap<String, Double>();				//召回率:命中的MFS/所有的MFS
	private Map<String, Double> precisionMap = new TreeMap<String, Double>();			//准确率:命中的MFS/获得的所有可疑MFS
	private Map<String, Double> fMeasureMap = new TreeMap<String, Double>();			//f值，综合评价指标: 2*recall*precision/(recall+precision)
	
	/**
	 * 计算每个变异体的指标
	 * @param param
	 * @param fcasFailtestFileName
	 * @throws IOException 
	 */
	public synchronized void putEachSUT(Param param, String fcasFailtestFileName) throws IOException {
		DataHelper dataHelper = param.getDataHepler();
		String booleanExprName = fcasFailtestFileName.substring(0, fcasFailtestFileName.lastIndexOf('.'));
		
		//1：处理附加测试用例数
		String key = genKey(param.getLfName(), param.getCtToolName(), param.getLenOfCt(), ResultType.ExtraTc);
		if (!extraTcSizeMap.containsKey(key)) {
			extraTcSizeMap.put(key, new Double("-1"));
			List<String> sizes = dataHelper.readExtraTcSize(param.getLfName(), 
					param.getCtToolName(), param.getLenOfCt());
			for (String str : sizes) {
				String[] tmps = str.split(":");
				extraTcSizeMap.put(key+":"+tmps[0], Double.parseDouble(tmps[1]));
			}
		}

		//2：处理mfs数据
		String mfsRelatedKey = genKey(param.getLfName(), param.getCtToolName(), param.getLenOfCt(), ResultType.FaultSche);
		mfsRelatedKey += ":"+booleanExprName;
		
		//读取定位到的MFS
		String path = dataHelper.getResultFilePath(param.getLfName(), param.getCtToolName(), 
				param.getLenOfCt(), ResultType.FaultSche, booleanExprName);
		List<String> locateMFS = DataHelper.readFileLineByLine(path, "UTF-8");
		List<int[]> locateMFS2 = Util.strScheSetToIntArrayList(new HashSet<String>(locateMFS));
		
		//读取所有的MFS
		List<int[]> allMfs = dataHelper.getAllFtcsOrMfs(dataHelper.getMfsFileName(fcasFailtestFileName), false);
		
		//命中的MFS的数量
		double hitNum = hitNum(allMfs, locateMFS2);
		
		//计算召回率
		double r = recall(hitNum, allMfs.size());
		recallMap.put(mfsRelatedKey, r);
		
		//计算准确率
		double p = percision(hitNum, locateMFS2.size());
		precisionMap.put(mfsRelatedKey, p);
		
		//计算f值
		fMeasureMap.put(mfsRelatedKey, fMeasure(r, p));
	}
	
	/**
	 * 获取制作盒图所需的数据，保存到实验根目录的BOXPLOT文件夹，文件名的格式{维度}-{指标类型}-{name}：2-extraTc-TCAS1.txt、2-extraTc-CDF.txt
	 * @param map
	 * @param dataHelper
	 * @param rge 如何分组：按照表达式分组或者按照变异类型分组或者全部
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void genBoxplotSourceData(Map<String, Double> map, IndicatorEnum indicator, DataHelper dataHelper, ResultGroupEnum rge) throws FileNotFoundException, IOException {
		Set<Entry<String, Double>> s = map.entrySet();
		Map<String, Map<String, List<Double>>> newMap = new HashMap<String, Map<String, List<Double>>>();
		for (Entry<String, Double> e : s) {
			if (e.getValue() != -1) {		//把-1这个值去掉
				//分割Key
				String key = e.getKey();
				String[] keyParts = key.split(":");
				
				//分组
				String groupName = null;
				if (rge == ResultGroupEnum.EXPR) {
					groupName = genBooleanExprName(keyParts[keyParts.length-1]);					
				} else if (rge == ResultGroupEnum.MUTA) {
					groupName = genMutationType(keyParts[keyParts.length-1]);
				} else if (rge == ResultGroupEnum.ALL) {
					groupName = rge.getName();
				} else {
					throw new RuntimeException(this.getClass().getName()+" genBoxplotSourceData error!");
				}
				genBoxplotSourceDataF1(newMap, e, keyParts, indicator, groupName);				
			}
		}
		
		//将数据写入到文件中
		genBoxplotSourceDataF2(newMap, indicator, dataHelper.getRootPath()+"/BOXPLOT/"+rge.getName()+"/"+indicator.getName());
	}
	
	/**
	 * genBoxplotSourceData的辅助函数:将每条数据保存到map中
	 * @param m
	 * @param e
	 * @param keyParts
	 * @param indicator
	 * @param name 20个表达式或者10个变异类型
	 */
	private void genBoxplotSourceDataF1(Map<String, Map<String, List<Double>>> m, Entry<String, Double> e, 
			String[] keyParts, IndicatorEnum indicator, String name) {
		String k1 = keyParts[0]+"-"+indicator.getName()+"-"+name;
		if (!m.containsKey(k1)) {
			m.put(k1, new TreeMap<String, List<Double>>());
		}
		if(!m.get(k1).containsKey(keyParts[1])) {
			m.get(k1).put(keyParts[1], new ArrayList<Double>());
		}
		m.get(k1).get(keyParts[1]).add(e.getValue());
	}
	
	/**
	 * genBoxplotSourceData的辅助函数: 将map的内容保存到文件中
	 * @param m
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private void genBoxplotSourceDataF2(Map<String, Map<String, List<Double>>> m, IndicatorEnum indicator, String boxplotPath) throws FileNotFoundException, IOException {
		Set<Entry<String, Map<String, List<Double>>>> s = m.entrySet();
		for (Entry<String, Map<String, List<Double>>> e : s) {
			//将value格式化成字符串数组
			List<String> strs = new ArrayList<String>();
			int colNums = 0;		//列数
			
			//将故障方法名作为第一行
			Set<String> flNames = e.getValue().keySet();
			colNums = flNames.size();
			String headerFormat = "";
			for (int i = 0; i < colNums; i++) {
				headerFormat += "%15s";
			}
			strs.add(String.format(headerFormat, flNames.toArray()));
			
			//接下来是数据
			String dataFormate = "";
			int rowNums = e.getValue().get(flNames.toArray()[0]).size();	//获取行数	
			Double[][] data = new Double[rowNums][colNums];					//矩阵存储
			for (int i = 0; i < colNums; i++) {
				dataFormate += "%-15.2f";
			}
			
			//把数据存到矩阵中
			Set<Entry<String, List<Double>>> valueSet = e.getValue().entrySet();
			int curCol = 0;
			for (Entry<String, List<Double>> e2 : valueSet) {
				List<Double> tmpList = e2.getValue();
				for (int j = 0; j < tmpList.size(); j++) {
					data[j][curCol] = indicator == IndicatorEnum.EXTRA_TC ? tmpList.get(j) : tmpList.get(j)*100;	//百分制处理		
				}
				curCol++;
			}
			
			//将矩阵转换成字符串数组
			for (int i = 0; i < rowNums; i++) {
				String oneRow = String.format(dataFormate, (Object[])data[i]);
				strs.add(oneRow);
			}
			
			//写入文件
			DataHelper.writeStrListLineByLine(boxplotPath+"/"+e.getKey()+".txt", strs, "utf-8", false);
		}
	}
	
	/**
	 * 从变异体名中获取原始版本的布尔表达式名
	 * @param mutationName 具体的变异体的名称
	 * @return
	 */
	public String genBooleanExprName(String mutationName) {
		Pattern p = Pattern.compile("^[A-Z]+[0-9]{1,2}");
		Matcher m = p.matcher(mutationName);
		if (m.find()) {
			return m.group();
		}
		return null;
	}
	
	/**
	 * 从变异体名中获取变异类型
	 * @param mutationName
	 * @return
	 */
	public String genMutationType(String mutationName) {
		Pattern p = Pattern.compile("(?<=[0-9]+)[A-Z]+(?=[0-9]+)");
		Matcher m = p.matcher(mutationName);
		if (m.find()) {
			return m.group();
		}
		return null;
	}
	
	/**
	 * 生成哈希表的键值
	 * @param lfName
	 * @param ctToolName
	 * @param lenOfCt
	 * @param resultType
	 * @return
	 */
	public String genKey(LfNameEnum lfName, CtToolNameEnum ctToolName, int lenOfCt, ResultType resultType) {
		return lenOfCt+":"+lfName.getName()+":"+ctToolName.getName()+":"+resultType.getName();
	}
	
	/**
	 * 计算命中的MFS的数量
	 * @param allMfs
	 * @param hitMfs
	 * @return
	 */
	private double hitNum(List<int[]> allMfs, List<int[]> hitMfs) {
		double hitNum = 0;		//命中的个数
		for (int[] tmpmfs : hitMfs) {
			for (int[] tmphit : allMfs) {
				if (Arrays.equals(tmpmfs, tmphit)) {
					hitNum++;
					break;
				}
			}
		}
		return hitNum;
	}
	
	/**
	 * 计算极小故障模式的召回率
	 * @param hitNum 命中的MFS的数量
	 * @param allNum 所有的MFS的数量
	 * @return
	 */
	private double recall(double hitNum, double allNum) {
		return allNum == 0 ? 0 : hitNum / allNum;
	}
	
	/**
	 * 计算极小故障模式的准确率
	 * @param hitNum 命中的MFS的数量 
	 * @param allSuspNum 所有的定位到的可疑MFS的数量
	 * @return
	 */
	private double percision(double hitNum, double allSuspNum) {
		return allSuspNum == 0 ? 0 : hitNum / allSuspNum;
	}
	
	/**
	 * 计算f值
	 * @param r 召回率
	 * @param p 准确率
	 * @return 
	 */
	private double fMeasure(double r, double p) {
		return r == 0 || p == 0 ? 0 : 2*r*p/(r+p);
	}

	public Map<String, Double> getExtraTcSizeMap() {
		return extraTcSizeMap;
	}

	public Map<String, Double> getRecallMap() {
		return recallMap;
	}

	public Map<String, Double> getPrecisionMap() {
		return precisionMap;
	}

	public Map<String, Double> getfMeasureMap() {
		return fMeasureMap;
	}
	
}
