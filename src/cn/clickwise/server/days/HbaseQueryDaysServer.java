package cn.clickwise.server.days;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.clickwise.bigdata.tool.FileUtil;
import cn.clickwise.bigdata.tool.InfoDBUtil;
import cn.clickwise.bigdata.tool.SystemRun;
import cn.clickwise.lib.string.SSO;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class HbaseQueryDaysServer implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(HbaseQueryDaysServer.class);
	FileUtil fUtil = new FileUtil();
	SystemRun sr = new SystemRun();
	QueryHbaseDays qhbase=new QueryHbaseDays();

	public void run() {
		logger.info("start query server");
		try {
			HttpServer localHttpServer = HttpServer.create(
					new InetSocketAddress(Integer.parseInt("9987")), 0);
			QueryHandler queryHandler=new QueryHandler();
			localHttpServer.createContext("/querydays",queryHandler);
			localHttpServer.setExecutor(null);
			localHttpServer.start();
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	class QueryHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			
			String uri = exchange.getRequestURI().toString();
			System.out.println("uri:"+uri);
			logger.info("uri:"+uri);
			uri = uri.replaceFirst("\\/querydays\\?", "");
			//uri:/ipq?ip=115.237.191.156&time=2015-04-05_12:00:00
			HashMap<String,String> phash=convertParams(uri);
			String uid="";
			String stime="";
			String etime="";
			String type="";
			uid=phash.get("uid");
			stime=phash.get("stime");
			etime=phash.get("etime");
			type=phash.get("type");
			String restr=qhbase.get(uid, stime,etime,type);
			
			restr=HostInfoFun.getHost(restr);
			System.err.println("restr:"+restr);
			
			exchange.sendResponseHeaders(200,
					restr.getBytes().length);
			OutputStream out = exchange.getResponseBody(); // 获得输出流
			out.write(restr.getBytes());
			out.flush();
			exchange.close();
		}
		
	}

	public HashMap<String,String> convertParams(String param_str)
	{
		String[] fields=param_str.split("&");
		if(fields==null||fields.length<1)
		{
			return null;
		}
		
		HashMap<String,String> phash=new HashMap<String,String>();
		String key="";
		String value="";
		
		for(int i=0;i<fields.length;i++)
		{
		  key=SSO.beforeStr(fields[i], "=");
		  value=SSO.afterStr(fields[i], "=");
		  if(SSO.tioe(key)||SSO.tioe(value))
		  {
			  continue;
		  }
		  
		  phash.put(key, value);
		}
		
		return phash;
	}
	public static void main(String[] args) {
		startServer();
	}

	

	public static void startServer() {
		HbaseQueryDaysServer cs = new HbaseQueryDaysServer();
		Thread serverThread = new Thread(cs);
		serverThread.start();
	}

}
