package cn.clickwise.dmpintegration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.clickwise.ghh.lib.hbase.HbaseUtil;

public class CreateTable {

	private static Logger logger=LoggerFactory.getLogger(CreateTable.class); 
	public static void main(String[] args) {
		HbaseUtil hbaseUtil=new HbaseUtil();
		String tableName="cookie_map";
		String []familycolumns={"cookieID","radiusID","baiduID","taobaoID","jdpin"};
		if(!hbaseUtil.isExistTable(tableName)){
			boolean ret=hbaseUtil.createTable(tableName, familycolumns);
			if(!ret)
				return;
			else{
				logger.info("hbase表"+tableName+"创建成功！");
			}
		}
		else {
			logger.info("hbase表"+tableName+"已经创建！无需创建！");
		}
	}

}
