package cn.clickwise.ghh.lib.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.rest.protobuf.generated.ColumnSchemaMessage.ColumnSchema;
import org.apache.hadoop.hbase.thrift2.generated.THBaseService.Processor.exists;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbaseUtil {
	private static Logger logger = LoggerFactory.getLogger(HbaseUtil.class);
	public static Configuration configuration;
	private static HTablePool pool;
	static {
		configuration = HBaseConfiguration.create();
		/************ sx *****************
		configuration.set("hbase.zookeeper.property.clientPort", "2181");
		configuration.set("hbase.zookeeper.quorum", "192.168.10.39");
		configuration.set("hbase.master", "192.168.10.39:60000");
		********************************/
		/************ zj *****************/
		configuration.set("hbase.zookeeper.property.clientPort", "2181");
		configuration.set("hbase.zookeeper.quorum", "192.168.10.130");
		configuration.set("hbase.master", "192.168.10.128:60010");
		/********************************/
		/************ local*****************
		configuration.addResource("/etc/hbase/conf/hbase-site.xml");
		configuration.set("hbase.zookeeper.property.clientPort", "2181");
		configuration.set("hbase.zookeeper.quorum", "192.168.110.80");
		configuration.set("hbase.master", "192.168.110.80:600000");
		********************************/
		pool = new HTablePool(configuration, 1000);
	}

	// private static String []familycolumns={"column1","column2","column3"};

	/**
	 * 判断hbase数据库中是否存在表
	 * 
	 * @param tableName
	 * @return
	 */
	public boolean isExistTable(String tableName) {
		try {
			HBaseAdmin hBaseAdmin = new HBaseAdmin(configuration);
			if (hBaseAdmin.tableExists(tableName))
				return true;
		} catch (MasterNotRunningException e) {
			e.printStackTrace();
		} catch (ZooKeeperConnectionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 若存在表先删除表
	 * 
	 * @param tableName
	 * @param familycolumns
	 */
	public boolean createTable(String tableName, String[] familycolumns) {
		//logger.info("start create table ......");
		try {
			HBaseAdmin hBaseAdmin = new HBaseAdmin(configuration);
			if (hBaseAdmin.tableExists(tableName)) {
				hBaseAdmin.disableTable(tableName);
				hBaseAdmin.deleteTable(tableName);
			//	logger.info(tableName + " is exist,detele....");
			}
			HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
			for (int i = 0; i < familycolumns.length; i++) {
				tableDescriptor.addFamily(new HColumnDescriptor(
						familycolumns[i]));
			}
			hBaseAdmin.createTable(tableDescriptor);
			return true;
		} catch (MasterNotRunningException e) {
			e.printStackTrace();
		} catch (ZooKeeperConnectionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 插入数据
	 * 
	 * @param tableName
	 * @param rowkeyString
	 * @param familycolumns
	 * @param data
	 */
	public void insertData(String tableName, String rowkeyString,
			String[] familycolumns, String[] data) {
		//logger.info("start insert data ......");
		if (familycolumns.length == data.length) {
			logger.info("插入数据非法");
			return;
		}
		// HTable table=(HTable) pool.getTable(tableName);
		HTableInterface table = pool.getTable(tableName);
		Put put = new Put(rowkeyString.getBytes());
		for (int i = 0; i < familycolumns.length; i++) {
			put.add(familycolumns[i].getBytes(), null, data[i].getBytes());
		}
		try {
			table.put(put);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	//	logger.info("end insert data ......");
	}

	public void insertData(String tableName, String rowkeyString,
			String familycolumn, String data) {
		//logger.info("start insert data ......");
		if (data.length() == 0)
			data = "NA";
		HTableInterface ht = pool.getTable(tableName);
		Put put = new Put(rowkeyString.getBytes());
		put.add(familycolumn.getBytes(), null, data.getBytes());
		try {
			ht.put(put);
			logger.info("插入数据,行健为"+rowkeyString+"  列族为"+familycolumn+"  值为"+data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//logger.info("end insert data ......");
	}

	/***
	 * 删除表
	 * 
	 * @param tableName
	 */
	public void dropTable(String tableName) {
		try {
			HBaseAdmin hBaseAdmin = new HBaseAdmin(configuration);
			hBaseAdmin.disableTable(tableName);
			hBaseAdmin.deleteTable(tableName);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/***
	 * 删除某表对应的某个行健数据
	 * 
	 * @param tableName
	 * @param rowkey
	 */
	public void deleteRow(String tableName, String rowkey) {
		try {
			HTable hTable = new HTable(configuration, tableName);
			List<Delete> list = new ArrayList<Delete>();
			Delete d1 = new Delete(rowkey.getBytes());
			list.add(d1);
			hTable.delete(list);
			logger.info("删除行成功!"+rowkey);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/***
	 * 查询表里的所有数据
	 * 
	 * @param tableName
	 */
	public void queryTable(String tableName) {
		// HTable table=(HTable) pool.getTable(tableName);
		HTableInterface table = pool.getTable(tableName);
		try {
			ResultScanner rs = table.getScanner(new Scan());
			for (Result r : rs) {
				logger.info("获得到rowkey:" + new String(r.getRow()));
				for (KeyValue kv : r.raw()) {
					logger.info("列：" + new String(kv.getFamily()) + "====值:"
							+ new String(kv.getValue()));
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/***
	 * 获取某个rowkey下 的数据
	 * 
	 * @param tableName
	 * @param rowkey
	 */
	public void queryTable(String tableName, String rowkey) {
		// HTable table=(HTable) pool.getTable(tableName);
		HTableInterface table = pool.getTable(tableName);
		try {
			Get get = new Get(rowkey.getBytes());
			Result result = table.get(get);
			logger.info("获得到rowkey:" + rowkey);
			for (KeyValue kv : result.raw()) {
				logger.info("列：" + new String(kv.getFamily()));
				logger.info("值:" + new String(kv.getValue()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/****
	 * 查询某个列族下的数据
	 * 
	 * @param tableName
	 * @param rowKey
	 * @param family
	 */
	public String queryTable(String tableName, String rowKey, String family) {
		// HTable table=(HTable) pool.getTable(tableName);
		HTableInterface table = pool.getTable(tableName);
		Get get = new Get(rowKey.getBytes());
		get.addFamily(Bytes.toBytes(family));
		try {
			Result result = table.get(get);
			if(result.isEmpty())
				return "NA";
			for (KeyValue kv : result.raw()) {
				//logger.info(new String(kv.getQualifier()));
				logger.info(new String(kv.getValue()));
				return new String(kv.getValue());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "NA";
	}

	public void queryTable(String tableName, String rowKey, String family,
			String column) {
		// HTable table=(HTable) pool.getTable(tableName);
		HTableInterface table = pool.getTable(tableName);
		Get get = new Get(rowKey.getBytes());
		get.addColumn(family.getBytes(), column.getBytes());
		try {
			Result result = table.get(get);
			for (KeyValue kv : result.raw()) {
				logger.info(new String(kv.getValue()));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/****
	 * 设置扫描的起始key
	 * 
	 * @param tableName
	 * @param startrow
	 * @param endrow
	 */
	public void scanTable(String tableName, String startrow, String endrow) {
		// HTable table=(HTable) pool.getTable(tableName);
		HTableInterface table = pool.getTable(tableName);
		Scan scan = new Scan();
		scan.setStartRow(startrow.getBytes());
		scan.setStopRow(endrow.getBytes());
		try {
			ResultScanner resultScanner = table.getScanner(scan);
			for (Result r : resultScanner) {
				logger.info(new String(r.getRow()));
				for (KeyValue kv : r.raw()) {
					logger.info(Bytes.toString(kv.getFamily()));
					logger.info(Bytes.toString(kv.getValue()));
				}
			}
			resultScanner.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/***
	 * 设置扫描的起始key,只返回对应列族的值
	 * 
	 * @param tableName
	 * @param startrow
	 * @param endrow
	 * @param family
	 */
	public void scanTable(String tableName, String startrow, String endrow,
			String family) {
		// HTable table=(HTable) pool.getTable(tableName);
		HTableInterface table = pool.getTable(tableName);
		Scan scan = new Scan();
		// 指定最多返回的Cell数目。用于防止一行中有过多的数据，导致OutofMemory错误。
		// scan.setBatch(1000);
		scan.addFamily(Bytes.toBytes(family));
		scan.setStartRow(startrow.getBytes());
		scan.setStopRow(endrow.getBytes());
		try {
			ResultScanner resultScanner = table.getScanner(scan);
			for (Result r : resultScanner) {
				logger.info(new String(r.getRow()));
				for (KeyValue kv : r.raw()) {
					logger.info(Bytes.toString(kv.getFamily()));
					logger.info(Bytes.toString(kv.getValue()));
				}
			}
			resultScanner.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		HbaseUtil hbaseUtil=new HbaseUtil();
		String tableName="cookie_map_ghh";
		String rowKey="ck_abcd123456";
		String family="baiduID";
		String resultString=hbaseUtil.queryTable(tableName, rowKey, family);
		System.out.println(resultString);
		family="taobaoID";
		resultString=hbaseUtil.queryTable(tableName, rowKey, family);
		System.out.println(resultString);
		family="cookieID";
		resultString=hbaseUtil.queryTable(tableName, rowKey, family);
		System.out.println(resultString);
		rowKey="ck_abcd1234567";
		resultString=hbaseUtil.queryTable(tableName, rowKey, family);
		System.out.println(resultString);
	}
}
