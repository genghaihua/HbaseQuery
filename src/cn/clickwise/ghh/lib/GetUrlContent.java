package cn.clickwise.ghh.lib;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.clickwise.ghh.lib.dom.XmlTool;

public class GetUrlContent {
	private static Logger logger = LoggerFactory.getLogger(GetUrlContent.class);

	/***
	 * 查找某天的radiusid，返回多个
	 * @param GetRadiusIDServer
	 * @param ip
	 * @param time
	 * @return
	 */
	public static ArrayList<String> getRadiusIDS(String GetRadiusIDServer,
			String ip, String time) {
		String urlstr = GetRadiusIDServer + "ip=" + ip + "&time=" + time;
		logger.info(urlstr);
		String RadiusIDSstr = "";

		try {
			URL url = new URL(urlstr);
			Reader reader = new InputStreamReader(new BufferedInputStream(
					url.openStream()), "utf-8");
			int c;
			while ((c = reader.read()) != -1) {
				RadiusIDSstr += (char) c;
			}
			reader.close();
		} catch (Exception e) {
		}
		RadiusIDSstr = RadiusIDSstr.replaceAll("\n", "");
		logger.info(RadiusIDSstr);
		String[] rids = RadiusIDSstr.split(",");
		ArrayList<String> ridarr = new ArrayList<String>();
		for (int i = 0; i < rids.length; i++) {
			if (!ridarr.contains(rids[i])) {
				ridarr.add(rids[i]);
			}
		}
		return ridarr;
	}
	/***
	 * 查找某个时刻的radiusid，返回1个
	 * @param GetRadiusIDServer
	 * @param ip
	 * @param time
	 * @return
	 */
	public static String getRadiusID(String GetRadiusIDServer, String ip,
			String time) {

//		String tmpString="abcdefghijdjslfl/keslrfejkjk";
//		return tmpString;
		String urlstr = GetRadiusIDServer + "ip=" + ip + "&time=" + time;
		logger.info(urlstr);
		String RadiusIDSstr = "";

		try {
			URL url = new URL(urlstr);
			Reader reader = new InputStreamReader(new BufferedInputStream(
					url.openStream()), "utf-8");
			int c;
			while ((c = reader.read()) != -1) {
				RadiusIDSstr += (char) c;
			}
			reader.close();
		} catch (Exception e) {
		}
		RadiusIDSstr = RadiusIDSstr.replaceAll("\n", "");
		logger.info(RadiusIDSstr);
		return RadiusIDSstr;
	}

	public static void main(String[] args) {
		String str = "cookie_map.conf.xml";
		//String radius_service = "";
		XmlTool ddTool = new XmlTool();
		HashMap<String, String> mapconf = ddTool.parserXml(str, "cookie_map");
		String radius_service =mapconf.get("radius_service");
		String ip="115.237.191.156";
		String time="2015-04-05_12:00:00";
		System.out.println(getRadiusID(radius_service, ip, time));
	}
}
