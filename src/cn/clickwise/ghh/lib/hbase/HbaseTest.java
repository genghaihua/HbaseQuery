package cn.clickwise.ghh.lib.hbase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbaseTest {
	private static Logger logger=LoggerFactory.getLogger(HbaseTest.class); 
	public static void main(String[] args) {
		HbaseUtil hbaseUtil=new HbaseUtil();
		String tableName="cookie_map";
		String []familycolumns={"cookieID","radiusID","baiduID","taobaoID","jdpin"};
		if(!hbaseUtil.isExistTable(tableName)){
			boolean ret=hbaseUtil.createTable(tableName, familycolumns);
			if(!ret)
				return;
		}
//		hbaseUtil.insertData(tableName, "ck_abcd123456","baiduID", "bd_123456");
//		hbaseUtil.insertData(tableName, "ck_abcd123456","taobaoID", "tb_123456");
//		hbaseUtil.insertData(tableName, "ck_abcd123456","cookieID", "ck_abcd123456");
//		hbaseUtil.queryTable(tableName, "ck_abcd123456");
		logger.info("创建表成功");
	}

}
