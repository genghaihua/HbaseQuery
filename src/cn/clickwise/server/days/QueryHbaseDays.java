package cn.clickwise.server.days;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;

import cn.clickwise.ghh.lib.*;

public class QueryHbaseDays {

	// 连接hadoop平台的配置
	public static Configuration configuration;
	public static HTablePool pool;
	private static HashMap<String, ArrayList<String>> tablecontent = new HashMap<String, ArrayList<String>>();
	public String default_day = "";

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

	public static HashMap<String, ArrayList<String>> inittab() {
		HashMap<String, ArrayList<String>> tmphash = new HashMap<String, ArrayList<String>>();
		// "user", "sip,area"
		String[] strs = { "sip", "area" };
		tmphash.put("user", StrPro.strtoarr(strs));
		// "user_host", "host,cnt,atime",增加了atime列
		strs = new String[] { "host", "cnt", "atime" };
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

	// 获取startkey和endkey
	public HashMap<String, String> getkeys(String uid, String stime,
			String etime, String type) {
		HashMap<String, String> sekeys = new HashMap<String, String>();
		String start = "";
		String end = "";
		if (type.equals("user_host")) {
			// uid ,atime , host ,cnt
			// str[0] + "_" + str[1] + makeMD5(str[2])
			start = uid + "_" + stime + StrPro.getstr("0", 32);
			end = uid + "_" + etime + StrPro.getstr("{", 32);

		}
		sekeys.put("start", start);
		sekeys.put("end", end);
		return sekeys;
	}

	public String get(String uid, String stime, String etime, String type) {
		if (!type.equals("user_host")) {
			return "只能查询user_host类别";
		}

		String resultstr = "";
		System.out.println("uid:" + uid + " time:" + stime + " type:" + type);
		HashMap<String, String> keys = getkeys(uid, stime, etime, type);
		String startkey = keys.get("start");
		System.out.println("startkey:" + startkey);
		String endkey = keys.get("end");
		System.err.println("endkey:" + endkey);
		// 获取查询表对应的所有列族
		ArrayList<String> cols = tablecontent.get(type);

		HashMap<String, String> kvs = new HashMap<String, String>();
		// List<String> rlist = new ArrayList<String>();
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
				for (int i = 0; i < cols.size() - 1; i++) {
					resultstr += (kvs.get(cols.get(i)) + "\t");
				}
				resultstr += (kvs.get(cols.get(cols.size() - 1)) + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("列：" + uid + "==开始时间:" + stime + "==结束时间:" + etime
				+ "==类型:" + type + "==结果:" + resultstr + "==");
		return resultstr;
	}
	public static void main(String[] args) {
		String time = "20150419";
		System.out.println(GetTime.changetime(time));
	}
}
