package cn.clickwise.ghh.lib.hbase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbaseDel {
	private static Logger logger=LoggerFactory.getLogger(HbaseDel.class); 
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length==0){
			logger.info("请输入删除的表名");
			return;
			}
		String tableName=args[0];
		HbaseUtil hbaseUtil=new HbaseUtil();
		if(hbaseUtil.isExistTable(tableName)){
			hbaseUtil.dropTable(tableName);
			logger.info("删除表成功");
		}
	}

}
