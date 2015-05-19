package cn.clickwise.dmpintegration;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.clickwise.ghh.lib.DecodeUid;
import cn.clickwise.ghh.lib.GetUrlContent;
import cn.clickwise.ghh.lib.StrPro;
import cn.clickwise.ghh.lib.dom.XmlTool;
import cn.clickwise.ghh.lib.hbase.HbaseUtil;

public class UidIntegration {
	private static Logger logger = LoggerFactory
			.getLogger(UidIntegration.class);
	// private static final String str = "src/conf/cookie_map.conf.xml";
	// UidIntegration.class.getResource("/").getPath()+java.io.File.separator+"conf"+java.io.File.separator+"cookie_map.conf.xml";
	private static final String str = "cookie_map.conf.xml";
	HbaseUtil hbaseUtil = new HbaseUtil();
	XmlTool ddTool = new XmlTool();
	private static HashMap<String, String> mapconf = new HashMap<String, String>();
	private static String radius_server = "";
	private static String radius_service = "";
	private static String userid_type = "";
	// 数据库中的列族名称
	private static final String[] cookieclass = { "cookieID", "radiusID",
			"baiduID", "taobaoID", "jdpin" };
	// 返回标示符的前缀
	private static final String[] cc = { "ck", "rd", "bd", "tb", "jd" };
	// 建立cookieclass和前缀的对应关系
	private static HashMap<String, String> ccpre = new HashMap<String, String>();
	
	private static final String tableName = "cookie_map";
	
	// 默认转换为CookieID类别的用户标示符
	private String[] cooks = { "uid", "uacd", "uaid" };
	private ArrayList<String> cooksarr = StrPro.strtoarr(cooks);
	private static final ArrayList<String> tbHosts = StrPro
			.strtoarr(new String[] { ".taobao.com", ".tmall.com", ".etao.com" });
	private static final ArrayList<String> jdHosts = StrPro
			.strtoarr(new String[] { ".jd.com", ".paipai.com" });
	private static final ArrayList<String> bdHosts = StrPro
			.strtoarr(new String[] { ".baidu.com" });

	/***
	 * 建表语句 若存在则建表
	 */
	public void createTable() {
		if (!hbaseUtil.isExistTable(tableName)) {
			boolean ret = hbaseUtil.createTable(tableName, cookieclass);
			if (!ret)
				return;
			else {
				logger.info("hbase表" + tableName + "创建成功！");
			}
		} else {
			logger.info("hbase表" + tableName + "已经创建！无需创建！");
		}
	}

	public HashMap<String, String> createCorres(String name[], String pre[]) {
		HashMap<String, String> tmphash = new HashMap<String, String>();
		int len = name.length;
		for (int i = 0; i < len; i++) {
			tmphash.put(name[i], pre[i]);
		}
		return tmphash;
	}

	UidIntegration() {
		// createTable();
		mapconf = ddTool.parserXml(str, "cookie_map");
		radius_server = mapconf.get("radius_server");
		radius_service = mapconf.get("radius_service");
		userid_type = mapconf.get("userid_type");
		logger.info("cookie类型为" + userid_type);
		ccpre = createCorres(cookieclass, cc);
	}

	/***
	 * 将cookie字符串按类别存储在hashmap里,会对符合条件的cookieid类型转换为CookieID，
	 * 并且会去除不符合类别的cookie类型
	 * 
	 * @param cookie
	 * @return
	 */
	public HashMap<String, String> getCookie(String cookie) {
		// 需要将符合cookieID类的所有前缀转换为cookieID
		HashMap<String, String> tmp = new HashMap<String, String>();
		// 需要将符合cookieID类的所有前缀转换为cookieID
		// String splitstr = "; ";
		String splitstr = ";";
		String[] result = cookie.split(splitstr);
		// System.out.println(result.length);
		// 分割cookie类型和值
		String splitstr1 = "=";
		for (int i = 0; i < result.length; i++) {
			if (result[i].indexOf("=") != -1) {
				String[] result1 = result[i].split(splitstr1, 2);
				// System.out.println(result1[0]);
				if (cooksarr.contains(result1[0]))
					tmp.put("cookieID", ccpre.get("cookieID") + "_"
							+ result1[1]);
				else if (result1[0].equals("pin"))
					tmp.put("jdpin", ccpre.get("jdpin") + "_" + result1[1]);
				else if (result1[0].equals("BAIDUID")) {
					String str = result1[1];
					if (str.indexOf(":FG") != -1) {
						str = str.substring(0, str.indexOf(":FG"));
					}
					tmp.put("baiduID", ccpre.get("baiduID") + "_" + str);
					// tmp.put("baiduID", ccpre.get("baiduID") + "_" +
					// result1[1]);
				}
				// private String []taobaoids={"tracknick","lgc","_nk_"};
				else if (result1[0].equals("tracknick")) {
					tmp.put("tracknick", ccpre.get("taobaoID") + "_"
							+ result1[1]);
				} else if (result1[0].equals("lgc")) {
					tmp.put("lgc", ccpre.get("taobaoID") + "_" + result1[1]);
				} else if (result1[0].equals("_nk_"))
					tmp.put("_nk_", ccpre.get("taobaoID") + "_" + result1[1]);
			}
		}
		String value = "";
		if (tmp.containsKey("tracknick")) {
			if (tmp.containsKey("lgc"))
				tmp.remove("lgc");
			if (tmp.containsKey("_nk_"))
				tmp.remove("_nk_");
			value = tmp.get("tracknick");
			tmp.remove("tracknick");
			tmp.put("taobaoID", value);
		} else if (tmp.containsKey("lgc")) {
			if (tmp.containsKey("_nk_"))
				tmp.remove("_nk_");
			value = tmp.get("lgc");
			tmp.remove("lgc");
			tmp.put("taobaoID", value);
		} else if (tmp.containsKey("_nk_")) {
			value = tmp.get("_nk_");
			tmp.remove("_nk_");
			tmp.put("taobaoID", value);
		}
		return tmp;
	}

	public HashMap<String, String> getCookieEncode(String cookie) {
		// 需要将符合cookieID类的所有前缀转换为cookieID
		HashMap<String, String> tmp = new HashMap<String, String>();
		// 需要将符合cookieID类的所有前缀转换为cookieID
		String splitstr = "; ";
		String[] result = cookie.split(splitstr);
		System.out.println(result.length);
		// 分割cookie类型和值
		String splitstr1 = "=";
		for (int i = 0; i < result.length; i++) {
			String[] result1 = result[i].split(splitstr1, 2);
			// System.out.println(result1[0]);
			if (cooksarr.contains(result1[0]))
				tmp.put("cookieID", ccpre.get("cookieID") + "_" + result1[1]);
			else if (result1[0].equals("pin"))
				tmp.put("jdpin",
						ccpre.get("jdpin") + "_"
								+ DecodeUid.decodeUserID(result1[1]));
			else if (result1[0].equals("BAIDUID"))
				tmp.put("baiduID", ccpre.get("baiduID") + "_" + result1[1]);
			// private String []taobaoids={"tracknick","lgc","_nk_"};
			else if (result1[0].equals("tracknick")) {
				tmp.put("tracknick",
						ccpre.get("taobaoID") + "_"
								+ DecodeUid.decodeUserID(result1[1]));
			} else if (result1[0].equals("lgc")) {
				tmp.put("lgc",
						ccpre.get("taobaoID") + "_"
								+ DecodeUid.decodeUserID(result1[1]));
			} else if (result1[0].equals("_nk_"))
				tmp.put("_nk_",
						ccpre.get("taobaoID") + "_"
								+ DecodeUid.decodeUserID(result1[1]));
		}
		String value = "";
		if (tmp.containsKey("tracknick")) {
			if (tmp.containsKey("lgc"))
				tmp.remove("lgc");
			if (tmp.containsKey("_nk_"))
				tmp.remove("_nk_");
			value = tmp.get("tracknick");
			tmp.remove("tracknick");
			tmp.put("taobaoID", value);
		} else if (tmp.containsKey("lgc")) {
			if (tmp.containsKey("_nk_"))
				tmp.remove("_nk_");
			value = tmp.get("lgc");
			tmp.remove("lgc");
			tmp.put("taobaoID", value);
		} else if (tmp.containsKey("_nk_")) {
			value = tmp.get("_nk_");
			tmp.remove("_nk_");
			tmp.put("taobaoID", value);
		}
		return tmp;
	}

	/***
	 * 忘hbase更新cookie
	 * 
	 * @param cookie
	 */
	public void updateUserID(HashMap<String, String> cv) {
		// 如果只有一个cookie，无需更新数据库
		// if(cv.keySet().size()<=1)
		// return ;
		ArrayList<Integer> hashindexs = new ArrayList<Integer>();
		int len = cookieclass.length;
		// 找到存在的cookie类型索引

		for (int i = 0; i < len; i++) {
			if (cv.containsKey(cookieclass[i])) {
				hashindexs.add(i);
			}
		}
		for (int i = 0; i < hashindexs.size(); i++) {
			for (int j = 0; j < hashindexs.size(); j++) {
				if (i != j) {
					hbaseUtil.insertData(tableName,
							cv.get(cookieclass[hashindexs.get(i)]),
							cookieclass[hashindexs.get(j)],
							cv.get(cookieclass[hashindexs.get(j)]));
				}
			}
		}
	}

	public String cookieMapService(String ip, String time, String cookie) {
		if (cookie.equals("(null)") || cookie.length() == 0)
			return "NA";
		if (radius_server.equals("true")) {
			String radiusidvalue = ccpre.get("radiusID") + "_"
					+ GetUrlContent.getRadiusID(radius_service, ip, time);
			HashMap<String, String> cookie1 = getCookie(cookie);
			cookie1.put("radiusID", radiusidvalue);
			shucu(cookie1);
			updateUserID(cookie1);
			return radiusidvalue;
		} else {
			HashMap<String, String> cv = getCookie(cookie);
			String res = "";
			if (cv.containsKey(userid_type)) {
				res = cv.get(userid_type);
			} else {
				// cv.put(userid_type, "NA");
				res = "NA";
			}
			shucu(cv);
			updateUserID(cv);
			return res;
		}
	}

	public String cookieMapService(String ip, String time, String cookie,
			String host) {
		if (cookie.equals("(null)") || cookie.trim().length() == 0)
			return "NA";
		if (radius_server.equals("true")) {
			String radiusidvalue = ccpre.get("radiusID") + "_"
					+ GetUrlContent.getRadiusID(radius_service, ip, time);
			HashMap<String, String> cookie1 = getCookie(cookie);
			if (!radiusidvalue.equals("NA")) {
				cookie1.put("radiusID", radiusidvalue);
			}
			shucu(cookie1);
			updateUserID(cookie1);
			return radiusidvalue;
		} else {
			HashMap<String, String> cv = getCookie(cookie);
			String res = "";
			ArrayList<String> regearr = new ArrayList<String>();
			if (userid_type.equals("taobaoID"))
				regearr = tbHosts;
			else if (userid_type.equals("jdpin"))
				regearr = jdHosts;
			else if (userid_type.equals("baiduID"))
				regearr = bdHosts;
			else
				regearr = null;
			boolean isok = false;
			if (regearr != null) {
				for (String str : regearr) {
					if (host.indexOf(str) != -1)
						isok = true;
				}
				// 如果传入的host是定义的类型并且含有该类型的cookie，则返回该cookie，否则删除该类型cookie值，并更新数据库
				if (isok) {
					res = cv.get(userid_type);
				} else {
					cv.remove(userid_type);
					res = "NA";
				}
			} else {
				if (cv.containsKey(userid_type))
					res = cv.get(userid_type);
				else
					res = "NA";
			}
			shucu(cv);
			updateUserID(cv);
			return res;
		}

	}

	public void shucu(HashMap<String, String> hMap) {
		for (String k : hMap.keySet()) {
			logger.info(k + ":" + hMap.get(k));
			// System.out.println(k+":"+hMap.get(k));
		}
	}

	public static void main(String[] args) {
		test1();

	}

	public static void test1() {
		UidIntegration uidIntegration = new UidIntegration();
		String cooString = "pin=V2_ZwVHVUNUS0ByDERRfkkMUG9TEV8WVRMXfQpGBHxMCQIIABNdRlZHFXQIRVR/HVpqZwARQkFVQQpzDFlUcgxJ; __jda=122270672.544057603.1431231140.1431231141.1431258056.2; uid=V2_ZzNtbRYHQhNwX0VWehhfDWIFQV8RBUEUdQxHXHxOWABvBUcKclRCFXEUR1BnGV0UZwsZWEdcQRdFCHZXfBpaAmEBFl5yFR1DK0wCCyNHA2tiBUJVQV9GFCIAElx%2bEQwMbgsTXEJWRx1yWBMALhAJAFczEl5yV0IlcQ1DVX8fWwRlMw%3d%3d; mt_subsite=||1111%2C1431258273; __jdv=122270672|sogou-union|t_262767352_sogouunion|cpc|47a92940f9e949a8890010596aded8d4_0_ea074f23002947b3bc3015096f5497df; __jdu=544057603; ipLoc-djd=1-72-4137-0; areaId=1; ipLocation=%u5317%u4EAC; user-key=22aa6375-8ff4-4fdd-a31c-8e4e98b839c2; cn=0";
		HashMap<String, String> hMap = uidIntegration.getCookie(cooString);
		for (String k : hMap.keySet()) {
			System.out.println(k + ":" + hMap.get(k));
		}
		System.out.println(uidIntegration.cookieMapService("12.34.5.6",
				"20150506", cooString, "123.jd.com"));

	}

}
