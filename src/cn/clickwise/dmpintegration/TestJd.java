package cn.clickwise.dmpintegration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.clickwise.ghh.lib.PostGetTest;

public class TestJd {

	private static Logger logger = LoggerFactory.getLogger(TestJd.class);

	public static void readFileByLines(String fileName) throws Exception {
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				// tempString=tempString.trim();
				//ip+ time+host+cookie
				String[] jddata = tempString.split("\001");
				//System.out.println(jddata.length);
				if (jddata.length == 4 && !jddata[3].equals("(null)")
						&& jddata[3].replaceAll(" ", "").length() > 0) {
//					System.out.println("编码前字符串为" + tempString);
					String tmp1 = "";
					tmp1 = URLEncoder.encode(jddata[3], "unicode");
					tmp1 = URLEncoder.encode(tmp1, "unicode");
					tmp1 = URLEncoder.encode(tmp1, "utf-8");
					String timestr=jddata[1].replaceAll(" ", "_");
					// System.out.println("编码后字符串为"+tempString);
//					String sr = PostGetTest.sendPostRequest(
//							"http://192.168.10.139:9998/uidhost",
//							"ip=10.180.23.196&time=2015-05-09_01:47:27&host=" + jddata[0]
//									+ "&cookie=" + tmp1);
					String sr = PostGetTest.sendPostRequest(
							"http://192.168.10.139:9998/uidhost",
							"ip="+jddata[0]+"&time="+timestr+"&host=" + jddata[2]
									+ "&cookie=" + tmp1);
					if (!sr.equals("NA")) {
						System.out.println("编码前字符串为" + tempString);
						System.out.println("结果为：" + sr);
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			// e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		String file = args[0];
		logger.info("解析的文件为" + file);
		readFileByLines(file);
	}

}
