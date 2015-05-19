package cn.clickwise.ghh.lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HostClass {
	private static Logger logger = LoggerFactory.getLogger(HostClass.class);

	//获取host对应的分类属性
	public static HashMap<String, String> getHostCate(String fileName)
			throws IOException {
		HashMap<String, String> hostcates = new HashMap<String, String>();
		try {
			File file = new File(fileName);
			StringBuffer mm = new StringBuffer();
			BufferedReader br;
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] hc = line.split("\t");
				if (hc.length == 2) {
					hostcates.put(hc[0], hc[1]);
				}
			}
			br.close();
			logger.info("已载入host分类文件");
			return hostcates;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
