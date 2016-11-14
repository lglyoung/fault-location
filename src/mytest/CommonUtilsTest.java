package mytest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import common.DataHelper;
import common.Util;

public class CommonUtilsTest {
	@Test
	public void utilGenTcsTest() {
		int[][] paramValues = {{0, 1}, {0}};
		List<int[]> tcs = Util.genTcs(paramValues);
		for (int[] tc : tcs) {
			System.out.println(Arrays.toString(tc));
		}
	}
	
	@Test
	public void utilDivideTcsTest() {
		int[][] paramValues = {{0, 1}, {0, 1},{0, 1}, {0, 1},{0, 1}, {0, 1},{0, 1}, {0, 1},{0, 1}, {0, 1},{0, 1}, {0, 1},{0, 1}, {0, 1}};
		List<int[]> tcs = Util.genTcs(paramValues);
		List<int[]> ftcs = new ArrayList<int[]>();
		int[] tc = {0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0};
		ftcs.add(tc);
		List<int[]> ptcs = Util.arrDiffSet(tcs, ftcs);
		for (int[] tctmp : ptcs) {
			System.out.println(Arrays.toString(tctmp));
		}
	}
	
	@Test
	public void utilGenAllSubSchemasTest() {
		List<int[]> allSubSchemas = Util.genAllSubSchemas(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		System.out.println(allSubSchemas.size());
	}
	
	@Test
	public void mostLongSchesTest() {
		int[] ftc = {1, 1, 1, 1};
		List<int[]> ptcs = new ArrayList<int[]>();
		ptcs.add(new int[] {1, 1, 0, 0});
		ptcs.add(new int[] {1, 1, 0, 1});
		ptcs.add(new int[] {1, 1, 1, 1});
		List<int[]> sches = Util.mostLongSches(ftc, ptcs);
		for (int[] tmpSche : sches) {
			System.out.println(Arrays.toString(tmpSche));
		}
	}
	
	@Test
	public void genParentScheTest() {
		int[] valuesOfEachParam = {2, 2, 2, 2};
		int[] sche = {1, 1, 1, 1};
		List<int[]> parentSches = Util.genParentSche(valuesOfEachParam, sche);
		for (int[] tmpsche : parentSches) {
			System.out.println(Arrays.toString(tmpsche));
		}
	}
	
	@Test
	public void filterSuspSchesTest() {
		List<int[]> suspSches = new ArrayList<int[]>();
		suspSches.add(new int[] {1, 1, 1, -1});
		suspSches.add(new int[] {1, 0, -1, 1});
		List<int[]> ptcs = new ArrayList<int[]>();
		ptcs.add(new int[] {1, 1, 1, 1});
		Util.filterSuspSches(suspSches, ptcs);
		for (int[] tmpsche : suspSches) {
			System.out.println(Arrays.toString(tmpsche));
		}
	}
	
	@Test
	public void strToInt() {
		System.out.println(Integer.parseInt("-1"));
	}
	
	@Test
	public void fileTest() throws IOException {
		Set<String> fileName = new TreeSet<String>();
		File f = new File("D:\\Files\\测试\\BoolExperiment\\TCAS_FAILTEST\\AllListFile.txt");
		File f2 = new File("D:\\Files\\测试\\BoolExperiment\\TCAS_FAILTEST\\AllListFile2.txt");
		BufferedWriter bw = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf-8"));
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f2), "utf-8"));
			String line = br.readLine();
			while (line != null) {
				fileName.add(line);
				line = br.readLine();
			}
			br.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		}
		for (String tmpStr : fileName) {
			bw.write(tmpStr+"\n");
		}
	}
	
	@Test
	public void dataHelperTest() throws IOException {
		String tcasFailtestPath = "D:\\Files\\测试\\BoolExperiment\\TCAS_FAILTEST\\";
		String tcasMfsPath = "D:\\Files\\测试\\BoolExperiment\\TCAS_MFS\\";
		DataHelper dh = new DataHelper(tcasFailtestPath, tcasMfsPath);
		List<int[]> ftcs = dh.getAllFtcsOrMfs("TCAS1ASF1_MFS.txt", false);
		for (int[] tmpftc : ftcs) {
			System.out.println(Arrays.toString(tmpftc));
		}
	}
}
