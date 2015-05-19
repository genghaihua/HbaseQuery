package cn.clickwise.dmpintegration;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.clickwise.ghh.lib.PropResolve;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class UidServerHost implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(UidServerHost.class);
	private static Properties properties=new Properties();
	UidIntegration uidIntegration = new UidIntegration();
	UidServerHost(String [] args){
		properties=PropResolve.getProperties(args);
	}
	public void run() {
		logger.info("start query server");
		try {
			HttpServer localHttpServer = HttpServer.create(
					new InetSocketAddress(Integer.parseInt(properties.getProperty("p"))), 0);
			logger.info("端口是："+properties.getProperty("p"));
			UidServerHandler queryHandler=new UidServerHandler();
			HttpContext context = localHttpServer.createContext("/uidhost",queryHandler);
			context.getFilters().add(new ParaFilter());
			localHttpServer.setExecutor(null);
			localHttpServer.start();
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	class UidServerHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			Map<String, Object> params = (Map<String, Object>) exchange
					.getAttribute("parameters");
			String ip="";
			String time="";
			String cookie="";
			String host="";
			for (String key : params.keySet()) {
				if (key.equals("ip")) {
					ip = params.get(key).toString();
				} else if (key.equals("time")) {
					time = params.get(key).toString();
				}
				else if (key.equals("cookie")) {
					cookie = params.get(key).toString();
				}
				else if (key.equals("host")) {
					host = params.get(key).toString();
				}
			}
			cookie=URLDecoder.decode(cookie,"unicode");
			cookie=URLDecoder.decode(cookie,"unicode");
			cookie=cookie.replaceAll("; ", ";");
			logger.info("ip: "+ip+" time: "+time+" cookie: "+cookie+"host: "+host);
			String restr=uidIntegration.cookieMapService(ip, time, cookie,host);
			logger.info("restr:"+restr);
			exchange.sendResponseHeaders(200,restr.getBytes().length);
			OutputStream out = exchange.getResponseBody(); // 获得输出流
			out.write(restr.getBytes());
			out.flush();
			exchange.close();
		}
		
	}

	
	public static void main(String[] args) {
		startServer(args);
	}

	public static void startServer(String[] args) {
		UidServerHost uidServer = new UidServerHost(args);
		Thread serverThread = new Thread(uidServer);
		serverThread.start();
	}
}
