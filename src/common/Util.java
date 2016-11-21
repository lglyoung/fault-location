package common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Util {
	/**
	 * 对参数进行分组
	 * @param params 被分组的参数
	 * @param g 划分粒度
	 * @return 参数组，如{{1, 2}, {3, 4}}
	 */
	public static List<List<Integer>> paramGroups(List<Integer> params, int g) {
		if(params.size() < g) {
			return null;				//如果params的长度小于g，则返回null
		}
		
		List<List<Integer>> groups = new ArrayList<List<Integer>>();
		
		int[] subLens = new int[g];
		int quotient = params.size() / g;
		int mod = params.size() % g;
		
		//如果有余数
		if(mod > 0) {
			subLens[subLens.length-1] = quotient;
			for(int i = 0; i < subLens.length; i++) {
				if(i < subLens.length-1) {
					subLens[i] = quotient;
					if(mod > 0) {
						subLens[i] += 1;
						mod--;
					}
				}
			}
		} else {	//如果没有余数
			for(int i = 0; i < subLens.length; i++) {
				subLens[i] = quotient;
			}
		}
		
		//根据subLens来分割
		int index = -1;
		for(int i = 0; i < g; i++) {
			List<Integer> tmp = new ArrayList<Integer>();
			for(int j = 0; j < subLens[i]; j++) {
				index++;
				tmp.add(params.get(index));
			}
			groups.add(tmp);
		}
		
		return groups;
	}
	
	/**
	 * 生成附加测试用例
	 * @param valuesOfEachParam 如{3, 4, 5}表示第0个参数可以取3个值，第1个参数可以取4个值，第2个参数可以取5个值
	 * @param ftc 被参考的，用来生成附加测试用例的失效测试用例
	 * @param changedParams 被修改的参数。如{3， 4}表示第3、4个参数所对应的值需要被修改。
	 * @return 附加测试用例
	 */
	public static int[] genExtraTc(int[] valuesOfEachParam, int[] ftc, List<Integer> changedParams) {
		int[] extraTc = new int[ftc.length];
		System.arraycopy(ftc, 0, extraTc, 0, ftc.length);
		Random random = new Random();
		for(Integer changedParam : changedParams) {
			int newValue = 0;
			do{
				newValue = random.nextInt(valuesOfEachParam[changedParam]);
			}
			while(newValue == extraTc[changedParam]);
			extraTc[changedParam] = newValue;
		}
		return extraTc;
	}
	
	/**
	 * 根据"失效测试用例集"或者"通过测试用例集"来判断附加测试用例是否是失效测试用例
	 * @param extraTc 附加测试用例
	 * @param ftcs 失效测试用例集
	 * @param ptcs 通过测试用例集
	 * @return 如果返回true，则tc是失效测试用例； 否则tc不是失效测试用例 
	 */
	public static boolean isFailTc(int[] extraTc, List<int[]> ftcs, List<int[]> ptcs) {
		if(ftcs != null) {
			for(int[] ftc : ftcs) {
				if(Arrays.equals(extraTc, ftc)) {
					return true;
				}
			}
			return false;
		} else if(ptcs != null) {
			for(int[] ftc : ptcs) {
				if(Arrays.equals(extraTc, ftc)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * 如果extraTc不存在extraTcs中，则将extraTc保存到extraTcs
	 * @param intArray
	 * @param intArrayList
	 */
	public static void addNotRepeatIntArray(int[] intArray, List<int[]> intArrayList) {
		for(int[] tmp : intArrayList) {
			if(Arrays.equals(intArray, tmp)) return; 
		}
		intArrayList.add(intArray);
	}
	
	
	/**
	 * 根据ftc和unchangedParams来生成形如[1, 2, 1, -1]这样的故障模式，-1就类似于"-"。
	 * @param ftc 失效测试用例
	 * @param unchangedParams 最终未被修改的参数。根据这个数据，将其他参数置为-1
	 * @return int[]
	 */
	public static int[] genFaultSchema(int[] ftc, List<Integer> unchangedParams) {
		int[] fs = new int[ftc.length];
		
		//初始化
		for(int i = 0; i < fs.length; i++) {
			fs[i] = -1;
		}
		
		//根据ftc，将固定参数所对应的值赋给fs
		for(Integer unchangedParam : unchangedParams) {
			fs[unchangedParam] = ftc[unchangedParam];
		}
		return fs;
	}
	
	/**
	 * 返回与故障无关的参数
	 * @param ftc 失效测试用例
	 * @param relatedParams 与故障相关的参数
	 * @return List<Integer> 
	 */
	public static List<Integer> genChangedParams(int[] ftc, List<Integer> relatedParams) {
		Set<Integer> allParams = new HashSet<Integer>();
		for(int i = 0; i < ftc.length; i++) {
			allParams.add(i);
		}
		allParams.removeAll(relatedParams);
		return new ArrayList<Integer>(allParams);
	}

	
	/**
	 * 将int[]转成字符串
	 * @param nums
	 * @return String 如 "1,-1,1,-1"
	 */
	public static String intArrayToStr(int[] nums) {
		StringBuilder sb = new StringBuilder();
		for (int num : nums) {
			sb.append(num+",");
		}
		sb.delete(sb.length()-1, sb.length());
		return sb.toString();
	}
	
	/**
	 * 将字符串转成int[]
	 * @param str 如 "1,-1,1,-1"
	 * @return
	 */
	public static int[] strToIntArray(String str) {
		String[] strs = str.split(",");
		int[] arr = new int[strs.length];
		for (int i = 0; i < strs.length; i++) {
			arr[i] = Integer.parseInt(strs[i]);								
		}
		return arr;
	}
	
	/**
	 * 计算模式长度
	 * @param testcase
	 * @return
	 */
	public static int schemaLen(int[] testcase) {
		int len = 0;
		for(int i = 0; i < testcase.length; i++) {
			if(testcase[i] != -1) {
				len++;
			}
		}
		return len;
	}
	
	/**
	 * 生成所有的测试用例
	 * @param
	 *  paramValues: 可以看成是一个矩阵（每一行的元素个数可能不一样），i行表示第i个参数的所有可能取值，如：{{1, 2}, {1, 3}, {0}}
	 * @return
	 *  返回一个所有的测试用例。用int[]来存储一个测试用例
	 */
	public static List<int[]> genTcs(int[][] paramValues) {
		List<int[]> tcs = new ArrayList<int[]>();
		int m = 1, n = paramValues.length;
		for (int i = 0; i < n; i++) {
			m = m * paramValues[i].length;		//求测试用例的总的数量
		}
		
		//创建二维数组，用来保存所有的测试用例
		for (int i = 0; i < m; i++) {
			int[] tmptc = new int[n];
			tcs.add(tmptc);
		}
		
		//生成所有的测试用例
		int tmp = m, repeat = 1;
		for(int i = 0; i < n; i++) {
			tmp = tmp/paramValues[i].length;
			int index = 0;
			
			for(int r = 0 ; r < repeat; r++) {
				for(int j = 0; j < paramValues[i].length; j++) {
					for(int k = 0; k < tmp; k++) {
						tcs.get(index++)[i] = paramValues[i][j];
					}
				}
			}
			
			repeat = repeat*paramValues[i].length;
		}
		return tcs;
	}
	
	/**
	 * 数组的差集运算
	 * @param all 被减集合
	 * @param aset 减集
	 * @return 返回的是差集
	 */
	public static List<int[]> arrDiffSet(List<int[]> all, List<int[]> aset) {
		List<int[]> diffSet = new ArrayList<int[]>();
		boolean isIn = false;
		for (int[] allEle : all) {
			isIn = false;
			for (int[] asetEle : aset) {
				if (Arrays.equals(allEle, asetEle)) {
					isIn = true;
					break;
				}
			}
			if (!isIn) diffSet.add(allEle);
		}	
		return diffSet;
	}
	
	/**
	 * 根据一个测试用例，生成该测试用例的所有子模式
	 */
	public static List<int[]> genAllSubSchemas(int[] tc) {
		Set<String> allSubSchemasStr = new HashSet<String>();

		Deque<int[]> stack = new ArrayDeque<int[]>(1024);
		stack.push(tc);
		int[] pop = null;
		int schemaLen = 0;
		while (stack.size() > 0) {
			pop = stack.pop();
			
			//生成pop这个模式的下一级子模式
			schemaLen = Util.schemaLen(pop);	
			if (schemaLen > 1 && !allSubSchemasStr.contains(intArrayToStr(pop))) {
				for (int i = 0; i < pop.length; i++) {
					if (pop[i] != -1) {
						
						//赋值测试用例
						int[] tmpArr = Arrays.copyOf(pop, pop.length);
						
						//对应为置为-1
						tmpArr[i] = -1;
						
						//保存到栈中
						stack.push(tmpArr);
					}
				}
			} 
			allSubSchemasStr.add(intArrayToStr(pop));
		}
		return 	strScheSetToIntArrayList(allSubSchemasStr);
	}
	
	/**
	 * 将字符串形式的模式集合转成int[]类型的list
	 * @param strScheSet {"1,-1,1", "-1,1,1"}
	 * @return List<int[]> {[1, -1, 1],[-1, 1, 1]}
	 */
	public static List<int[]> strScheSetToIntArrayList(Set<String> strScheSet) {
		List<int[]> intArrList = new ArrayList<int[]>();
		for (String tmpStr : strScheSet) {
			intArrList.add(strToIntArray(tmpStr));
		}
		return intArrList;
	}
	
	/**
	 * List<int[]>转Set<String>
	 * @param intArrList
	 * @return
	 */
	public static Set<String> intArrayListToStrScheSet(List<int[]> intArrList) {
		Set<String> set = new HashSet<String>();
		for (int[] tmparr : intArrList) {
			set.add(intArrayToStr(tmparr));
		}
		return set;
	}
	
	/**
	 * 获取测试用例集的模式集
	 */
	public static List<int[]> genScheSet(List<int[]> tcs) {
		List<int[]> scheSet = new ArrayList<int[]>();
		for (int[] tc : tcs) {
			scheSet.addAll(genAllSubSchemas(tc));
		}
		
		//去重
		delRepeat(scheSet);
		return scheSet;
	}
	
	/**
	 * 计算tc与ftc之间的相似度
	 * @param ftc
	 * @param tc
	 * @return int
	 */
	public static int degreeOfSimilary(int[] ftc, int[] tc) {
		int n = 0;
		for(int i = 0; i < ftc.length; i++) {
			if(ftc[i] == tc[i]) {
				n++;
			}
		}
		return n;
	}
	
	/**
	 * 拿一个ftc与组合测试用例的通过测试用例集进行比较，找出长度最长（相似度最大）的模式集（一定是健康模式）
	 * @param ftc 一个失效测试用例
	 * @param ptcs 组合测试用例的通过测试用例集
	 * @return 长度最长的模式集
	 */
	public static List<int[]> mostLongSches(int[] ftc, List<int[]> ptcs) {
		List<int[]> mostLongTcs = new ArrayList<int[]>();
		List<int[]> mostLongSches = new ArrayList<int[]>();
		int maxLen = 1, tmpLen = 0;
		for (int[] ptc : ptcs) {
			tmpLen = Util.degreeOfSimilary(ftc, ptc);
			if (tmpLen == maxLen) {
				mostLongTcs.add(ptc);			
			} else if (tmpLen > maxLen) {
				maxLen = tmpLen;
				mostLongTcs.clear();
				mostLongTcs.add(ptc);
			}
		}
		
		//生成对应的模式
		for (int[] tmptc : mostLongTcs) {
			mostLongSches.add(genMostLongSche(ftc, tmptc));
		}
		
		return mostLongSches;
	}
	
	/**
	 * 根据失效测试用例和通过测试用例生成一个最长公共模式
	 */
	public static int[] genMostLongSche(int[] ftc, int[] ptc) {
		int[] sche = Arrays.copyOf(ftc, ftc.length);
		for (int i = 0; i < ftc.length; i++) {
			if (ftc[i] != ptc[i]) sche[i] = -1;
		}
		return sche;
	}
	
	/**
	 * 生成上一级的父模式，如果是根模式，那么返回它本身
	 * @param valuesOfEachParam
	 * @param shce 一个模式
	 */
	public static List<int[]> genParentSche(int[] valuesOfEachParam, int[] sche) {
		List<int[]> parentSches = new ArrayList<int[]>();
		for (int i = 0; i < sche.length; i++) {
			if (sche[i] == -1) {
				for (int j = 0; j < valuesOfEachParam[i]; j++) {
					int[] psche = Arrays.copyOf(sche, sche.length);
					psche[i] = j;
					parentSches.add(psche);
				}
			}
		}
		if (parentSches.size() == 0) parentSches.add(sche);	//如果sche是一个具体的测试用例，那么返回这条测试用例
		return parentSches;
	}
	
	/**
	 * 根据ptcs(通过测试用例集)，过滤可疑的故障模式集
	 */
	public static void filterSuspSches(List<int[]> suspSches, List<int[]> ptcs) {
		List<int[]> tmp = new ArrayList<int[]>();	//临时保存将被删除的模式
		boolean isDelete = false, isMatch = true;
		for (int[] tmpSche : suspSches) {
			isDelete = false;
			for (int[] tmpPtc : ptcs) {
				isMatch = true;
				for (int i = 0; i < tmpSche.length; i++) {
					if (tmpSche[i] != -1 && tmpSche[i] != tmpPtc[i]) isMatch = false;
				}
				if (isMatch) isDelete = true;
			}
			if (isDelete) tmp.add(tmpSche);
		}
		
		//删除
		suspSches.removeAll(tmp);
	}
	
	/**
	 * 去掉一个模式集中某个模式的父模式: 判断当前模式有没有子模式，如果有，那么就把这个模式去掉
	 */
	public static void removeParentSche(List<int[]> schemas) {
		List<int[]> parentSche = new ArrayList<int[]>();
		for (int i = 0; i < schemas.size(); i++) {
			for (int j = i+1; j < schemas.size(); j++) {
				if (aHasSubScheB(schemas.get(i), schemas.get(j))) {
					parentSche.add(schemas.get(i));
				}
			}
		}
		
		schemas.removeAll(parentSche);
	}
	
	/**
	 * 判断a是否含有b这个子模式
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean aHasSubScheB(int[] a, int[] b) {
		for (int i = 0; i < a.length; i++) {
			if (a[i] != -1) {
				if (b[i] != -1 && b[i] != a[i]) return false;
			} else {
				if (b[i] != -1) return false;
			}
		}
		return a.length > 0 ? true : false;
	}

	/**
	 * 根据失效测试用例集生成附加测试用例集，生成规则：迭代的将某一位上的参数值换成另外一个值，从而生成多个附加测试用例
	 * @param ftcs 失效测试用例集
	 * @param valuesOfEachParam 这是一个数组，数组的每个元素表示对应的参数的值的个数，且这个值默认是从0开始。
	 * @return 附加测试用例集
	 * @author lglyoung 2016.11.07
	 */
	public static List<int[]> genAT(List<int[]> ftcs, int[] valuesOfEachParam) {
		List<int[]> at = new ArrayList<int[]>();
		int tcLen = valuesOfEachParam.length; 	//测试用例的长度
		int curParamLen = 1;					//当前参数的可取值的长度
		for (int i = 0; i < ftcs.size(); i++) {
			for (int j = 0; j < tcLen; j++) {
				curParamLen = valuesOfEachParam[j];
				
				//复制原失效测试用例，并根据该测试用例生成附加测试用例
				int[] oneat = Arrays.copyOf(ftcs.get(i), tcLen);
								
				//当前参数的可取值的个数大于1时，才生成附加测试用例
				if (curParamLen > 1) {
					
					//根据当前参数值和当前参数的参数值数组，生成一个附加测试用例
					for (int k = 0; k < curParamLen; k++) {
						if (k != oneat[j]) {
							oneat[j] = k;
							break;
						}
					}
					
					//保存附加测试用例
					at.add(oneat);					
				}
			}
		}
		return at;
	}
	
	/**
	 * 将一个txt文件的内容转成int[]类型的数组，即组合测试 用例集
	 * @return
	 * @throws IOException 
	 */
	public static List<int[]> genCts(String path) throws IOException {
		List<int[]> cts = new ArrayList<int[]>();
		
		//根据文件名获取测试用例的长度
		File f = new File(path);
		int numsOfParam = Integer.parseInt(f.getName().split("_")[0]);
		
		FileInputStream fi = new FileInputStream(path);
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
	 * 将List<int[]>重复的元素进行过滤
	 * @param intArrList
	 */
	public static void delRepeat(List<int[]> intArrList) {
		Set<String> set = intArrayListToStrScheSet(intArrList);
		intArrList.clear();
		intArrList.addAll(strScheSetToIntArrayList(set));
	}
	
	/**
	 * 计算极小故障模式的命中率
	 * @param allMfs
	 * @param hitMfs
	 * @return
	 */
	public static double hitRate(List<int[]> allMfs, List<int[]> hitMfs) {
		double count = 0;
		for (int[] tmpmfs : allMfs) {
			for (int[] tmphit : hitMfs) {
				if (Arrays.equals(tmpmfs, tmphit)) {
					count++;
					break;
				}
			}
		}
		return count / allMfs.size();
	}
	
	/**
	 * 计算极小故障模式的非命中率
	 * @param allMfs
	 * @param hitMfs
	 * @return
	 */
	public static double notHitRate(List<int[]> allMfs, List<int[]> hitMfs) {
		int count = 0;
		boolean isIn = false;
		for (int[] tmpmfs : hitMfs) {
			isIn = false;
			for (int[] tmphit : allMfs) {
				if (Arrays.equals(tmpmfs, tmphit)) {
					isIn = true;
					break;
				}
			}
			if (!isIn) count++;
		}
		return count / allMfs.size();
	}
	
	/**
	 * all集合不包含sub的任一元素时，返回true
	 * @param all
	 * @param sub
	 * @return
	 */
	public static boolean notContainsAnyOneOf(Collection<?> all, Collection<?> sub) {
		Iterator<?> i = sub.iterator();
		while (i.hasNext()) {
			if (all.contains(i.next())) return false;
		}
		return true;
	}
}



