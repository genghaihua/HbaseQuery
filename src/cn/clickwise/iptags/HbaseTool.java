package cn.clickwise.iptags;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.clickwise.ghh.lib.*;

public class HbaseTool {

	// 连接hadoop平台的配置
	private static Configuration configuration;
	private static HTablePool pool;
	private static final String GetRadiusIDServer = "http://192.168.10.130:9999/ipq?";
	private static HashMap<String, String> hostcates = new HashMap<String, String>();
	private static HashMap<String, ArrayList<String>> tablecontent = new HashMap<String, ArrayList<String>>();
	private static Logger logger = LoggerFactory.getLogger(HbaseTool.class);
	static {
		/************
		 * hn *****************
		 * configuration.set("hbase.zookeeper.property.clientPort", "2181");
		 * configuration.set("hbase.zookeeper.quorum", "192.168.10.103");
		 * configuration.set("hbase.master", "192.168.10.103:60000");
		 ********************************/

		/************
		 * local*******************
		 * configuration.set("hbase.zookeeper.property.clientPort", "2181");
		 * configuration.set("hbase.zookeeper.quorum", "192.168.110.80");
		 * configuration.set("hbase.master", "192.168.110.80:60000");
		 ************************************/
		/************ zj *****************/
		/***
		 * configuration.set("hbase.zookeeper.property.clientPort", "2181");
		 * configuration.set("hbase.zookeeper.quorum", "192.168.10.130");
		 * configuration.set("hbase.master", "192.168.10.128:60010");
		 ****/
		/********************************/
		configuration = HBaseConfiguration.create();
		configuration.set("hbase.zookeeper.property.clientPort", "2181");
		configuration.set("hbase.zookeeper.quorum", "192.168.10.130");
		configuration.set("hbase.master", "192.168.10.128:60010");
		/************ shanxi *****************/
		/***
		 * configuration.set("hbase.zookeeper.property.clientPort", "2181");
		 * configuration.set("hbase.zookeeper.quorum", "192.168.10.39");
		 * configuration.set("hbase.master", "192.168.10.39:60000");
		 ****/
		pool = new HTablePool(configuration, 100);
		tablecontent = inittab();
	}
	static {
		try {
			hostcates = HostClass.getHostCate("hostcates.txt");
		} catch (Exception e) {
			logger.info("host分类文件不存在,请检查是否存在文件：hostcates.txt");
			e.printStackTrace();
		}
	}

	/***
	 * 将每一个表对应的列族保存在hashmap中
	 * @return HashMap<String, ArrayList<String>>
	 */
	public static HashMap<String, ArrayList<String>> inittab() {
		HashMap<String, ArrayList<String>> tmphash = new HashMap<String, ArrayList<String>>();
		// "user", "sip,area"
		String[] strs = { "sip", "area" };
		tmphash.put("user", StrPro.strtoarr(strs));
		// "user_host", "host,cnt"
		strs = new String[] { "host", "cnt" };
		tmphash.put("user_host", StrPro.strtoarr(strs));
		// "user_cate", "cate , cnt, tags"
		strs = new String[] { "cate", "cnt", "tags" };
		tmphash.put("user_cate", StrPro.strtoarr(strs));
		// "user_se_links","atime, se_type, keywords ,links"
		strs = new String[] { "atime", "se_type", "keywords", "links" };
		tmphash.put("user_se_links", StrPro.strtoarr(strs));
		// "user_se_keywords", "atime , se_type , keywords"
		strs = new String[] { "atime", "se_type", "keywords" };
		tmphash.put("user_se_keywords", StrPro.strtoarr(strs));
		// "user_ec_product","atime , ds , title , cate ,tags ,url"
		strs = new String[] { "atime", "ds", "title", "cate", "tags", "url" };
		tmphash.put("user_ec_product", StrPro.strtoarr(strs));
		// "user_ec_search","atime , ds ,keywords ,cate , tags"
		strs = new String[] { "atime", "ds", "keywords", "cate", "tags" };
		tmphash.put("user_ec_search", StrPro.strtoarr(strs));
		return tmphash;
	}

	/***
	 * 获取startkey和endkey
	 * @param uid
	 * @param time
	 * @param type
	 * @return HashMap<String, String>
	 */
	public  static HashMap<String, String> getkeys(String uid, String time, String type) {
		HashMap<String, String> sekeys = new HashMap<String, String>();
		String start = "";
		String end = "";
		if (type.equals("user")) {
			// "uid ,atime , sip ,area"
			start = uid + "_" + time + "0";
			end = uid + "_" + time + "1";

		} else if (type.equals("user_host")) {
			// uid ,atime , host ,cnt
			// str[0] + "_" + str[1] + makeMD5(str[2])
			start = uid + "_" + time + StrPro.getstr("0", 32);
			end = uid + "_" + time + StrPro.getstr("{", 32);

		} else if (type.equals("user_cate")) {
			// uid , cate , cnt , tags
			// line = str[0] + "_" + day + makeMD5(str[1])
			start = uid + "_" + time + StrPro.getstr("0", 32);
			end = uid + "_" + time + StrPro.getstr("{", 32);

		} else if (type.equals("user_se_links")) {
			// uid , atime , se_type , keywords ,links
			// 2015-04-19 10:55:20
			// line = str[0] + "_" + str[1]
			start = uid + "_" + GetTime.changetime(time) + "00:00:00";

			end = uid + "_" + GetTime.getDayAfter(time) + "00:00:00";

		} else if (type.equals("user_se_keywords")) {
			// uid , atime , se_type , keywords
			// line = str[0] + "_" + str[1]
			start = uid + "_" + GetTime.changetime(time) + "00:00:00";
			end = uid + "_" + GetTime.getDayAfter(time) + "00:00:00";

		} else if (type.equals("user_ec_product")) {
			// uid ,atime , ds , title , cate , tags ,url
			// str[0] + "_" + str[1]
			start = uid + "_" + GetTime.changetime(time) + "00:00:00";
			end = uid + "_" + GetTime.getDayAfter(time) + "00:00:00";

		} else if (type.equals("user_ec_search")) {
			// uid ,atime , ds , keywords , cate , tags
			start = uid + "_" + GetTime.changetime(time) + "00:00:00";
			end = uid + "_" + GetTime.getDayAfter(time) + "00:00:00";
		}
		sekeys.put("start", start);
		sekeys.put("end", end);
		return sekeys;
	}

	/*****
	 * 获取对应查询结果,针对user_host表
	 * @param uid
	 * @param time
	 * @param type
	 * @return ArrayList<String>   cate+"\t"+host+"\t"+cnt
	 */
	public static ArrayList<String> getHostCatesCnt(String uid, String time, String type) {

		
		ArrayList<String> result = new ArrayList<String>();
		logger.info("uid:" + uid + " time:" + time + " type:" + type);
		HashMap<String, String> keys = getkeys(uid, time, type);
		String startkey = keys.get("start");
		logger.info("startkey:" + startkey);
		String endkey = keys.get("end");
		logger.info("endkey:" + endkey);
		// 获取查询表对应的所有列族
		ArrayList<String> cols = tablecontent.get(type);

		HashMap<String, String> kvs = new HashMap<String, String>();
		try {
			Scan s = new Scan(startkey.getBytes(), endkey.getBytes());

			ResultScanner rs = pool.getTable(type).getScanner(s);

			String cf = "";
			String va = "";

			for (Result r : rs) {
				System.out.println("获得到rowkey:" + new String(r.getRow()));
				for (KeyValue keyValue : r.raw()) {
					cf = new String(keyValue.getFamily());
					va = new String(keyValue.getValue());
					kvs.put(cf, va);
				}
				String host=kvs.get(cols.get(0));
				String cnt=kvs.get(cols.get(1));
				if(hostcates.containsKey(host)){
					result.add(hostcates.get(host)+"\t"+host+"\t"+cnt);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/***
	 * 获取一个IP对应的radiusid
	 * 
	 * @param ip
	 *            day
	 */
	public  ArrayList<String> getRadiusIDS(String ip, String time) {
		ArrayList<String> tmp=new ArrayList<String>();
		tmp.add("0000084bd0645e46cdb2fc7219599f12");
		tmp.add("0000084bd0645e46cdb2fc7219599f12");
		return tmp;
		
		
		/*
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
		String[] rids = RadiusIDSstr.split(",");
		ArrayList<String> ridarr = new ArrayList<String>();
		for (int i = 0; i < rids.length; i++) {
			if (!ridarr.contains(rids[i])) {
				ridarr.add(rids[i]);
			}
		}
		return ridarr;
		*/
	}

	/***
	 * 对iplist进行处理，放到arraylist中
	 * 
	 * @param iplist
	 * @return
	 */
	public ArrayList<String> getIpLists(String iplist) {
		ArrayList<String> arr = new ArrayList<String>();
		String[] ips = iplist.split("\n");
		for (String ip : ips) {
			ip = ip.replace("\r", "");
			arr.add(ip);
		}
		return arr;
	}


	public String getResult(String iplist,String time){
		ArrayList<String> res=new ArrayList<String>();
		ArrayList<String> ips=getIpLists(iplist);
		for (String ip: ips) {
			logger.info(ip);
			ArrayList<String> radiusIds=getRadiusIDS(ip, time);
			for (String radiusid : radiusIds) {
				logger.info(radiusid);
				String area=getRecordByRowKey("user", radiusid+"_"+time, "area");
				logger.info(area);
				//获取cate,host,cnt
				ArrayList<String> hccs=getHostCatesCnt(radiusid, time, "user_host");
				for (String hcc : hccs) {
					logger.info(ip+"\t"+radiusid+"\t"+area+"\t"+hcc);
					res.add(ip+"\t"+radiusid+"\t"+area+"\t"+hcc);
				}
			}
		}
		String resstr="";
		for(int i=0;i<res.size()-1;i++){
			resstr+=res.get(i)+"\n";
		}
		resstr+=res.get(res.size()-1);
		return resstr;
	}

	/***
	 * 根据rowkey查询一行数据
	 * 
	 * @param tablename
	 *            rowkey familyname
	 */
	public  String getRecordByRowKey(String tablename, String rowkey,
			String familyname) {
		HTable table = (HTable) pool.getTable(tablename);
		String fa = "";
		String va = "";
		try {
			Get get = new Get(rowkey.getBytes());
			get.addFamily(familyname.getBytes());
			Result r = table.get(get);
			for (KeyValue kv : r.raw()) {
				fa = new String(kv.getFamily());
				va = new String(kv.getValue());
				// if(fa.equals(familyname))
				// return va;
				return va;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void main(String[] args) {
		String time = "20150419";
		System.out.println(GetTime.changetime(time));
	}
}
