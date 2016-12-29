package common;

import java.io.IOException;
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
	private Map<String, Double> m = new HashMap<String, Double>();	//保存平均附加测试用例数
	private Map<String, Double> recallMap = new HashMap<String, Double>();				//召回率:命中的MFS/所有的MFS
	private Map<String, Double> precisionMap = new HashMap<String, Double>();			//准确率:命中的MFS/获得的所有可疑MFS
	private Map<String, Double> fMeasureMap = new HashMap<String, Double>();			//f值，综合评价指标: 2*recall*precision/(recall+precision)
	
	/**
	 * 计算
	 * @param param
	 * @param fcasFailtestFileName
	 * @throws IOException 
	 */
	public void putEachBooleanExpr(Param param, String fcasFailtestFileName) throws IOException {
		DataHelper dataHelper = param.getDataHepler();
		String booleanExprName = fcasFailtestFileName.substring(0, fcasFailtestFileName.lastIndexOf('.'));
		
		//1：处理附加测试用例数
		String key = genKey(param.getLfName(), param.getCtToolName(), param.getLenOfCt(), ResultType.ExtraTc);
		if (!m.containsKey(key)) {
			m.put(key, new Double("-1"));
			List<String> sizes = dataHelper.readExtraTcSize(param.getLfName(), 
					param.getCtToolName(), param.getLenOfCt());
			for (String str : sizes) {
				String[] tmps = str.split(":");
				m.put(key+":"+tmps[0], Double.parseDouble(tmps[1]));
			}
		}

		//2：处理mfs数据
		String mfsRelatedKey = genKey(param.getLfName(), param.getCtToolName(), param.getLenOfCt(), ResultType.FaultSche);
		mfsRelatedKey += ":"+booleanExprName;
		
		//读取定位到的MFS
		String path = dataHelper.getResultFilePath(param.getLfName(), param.getCtToolName(), 
				param.getLenOfCt(), ResultType.FaultSche, booleanExprName);
		List<String> locateMFS = dataHelper.readFileLineByLine(path, "UTF-8");
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
	 * 展示对象为每一个原始布尔表达式的实验结果
	 * @param map 
	 */
	public void showEachSourceExpr(Map<String, Double> map) {
		Map<String, Double> m1 = new TreeMap<String, Double>();	//保存每个原始布尔表达式的总的附加测试用例数
		Map<String, Double> m2 = new TreeMap<String, Double>();	//保存每个原始布尔表达式的变异体的个数
		
		Set<Entry<String, Double>> s = map.entrySet();
		for (Entry<String, Double> en : s) {
			if (en.getValue() != -1) {			//剔除掉extraTcSizeMap中值为-1的键值对
				String key = en.getKey();
				String[] tmp =  key.split(":");
				String sourceName = sourceBooleanExprName(tmp[tmp.length-1]);
				String key2 = tmp[0]+":"+tmp[1]+":"+tmp[2]+":"+tmp[3]+":"+sourceName;	
				m1.put(key2, m1.containsKey(key2) ? (m1.get(key2)+en.getValue()) : en.getValue());
				m2.put(key2, m2.containsKey(key2) ? (m2.get(key2)+1) : 1);
			}
		}
		
		//遍历
		Set<Entry<String, Double>> set = m1.entrySet();
		for (Entry<String, Double> en : set) {
			System.out.println(en.getKey()+":"+en.getValue()/m2.get(en.getKey()));
		}
	}
	
	/**
	 * 显示平均的实验结果
	 * @param map
	 * @param size
	 */
	public void showAvg(Map<String, Double> map, int size) {
		Map<String, Double> m1 = new TreeMap<String, Double>();
		Set<Entry<String, Double>> s = map.entrySet();
		for (Entry<String, Double> en : s) {
			if (en.getValue() != -1) {			//剔除掉extraTcSizeMap中值为-1的键值对
				String key = en.getKey();
				key = key.substring(0, key.lastIndexOf(':'));
				m1.put(key, m1.containsKey(key) ? en.getValue()+m1.get(key) : en.getValue());	//值累加
			}
		}
		
		//求平均，并显示
		Set<Entry<String, Double>> set = m1.entrySet();
		for (Entry<String, Double> en : set) {
			System.out.println(en.getKey()+":"+en.getValue()/size);
		}
	}
	
	
	/**
	 * 从变异体名中获取原始版本的布尔表达式名
	 * @param mutationName
	 * @return
	 */
	public String sourceBooleanExprName(String mutationName) {
		Pattern p = Pattern.compile("^[A-Z]+[0-9]{1,2}");
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
	public String genKey(LfName lfName, CtToolName ctToolName, int lenOfCt, ResultType resultType) {
		return lfName.getName()+":"+ctToolName.getName()+":"+lenOfCt+":"+resultType.getName();
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
		return hitNum / allNum;
	}
	
	/**
	 * 计算极小故障模式的准确率
	 * @param hitNum 命中的MFS的数量 
	 * @param allSuspNum 所有的定位到的可疑MFS的数量
	 * @return
	 */
	private double percision(double hitNum, double allSuspNum) {
		return hitNum == 0 ? 0 : hitNum / allSuspNum;
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
		return m;
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
