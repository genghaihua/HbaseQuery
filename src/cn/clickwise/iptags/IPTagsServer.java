package cn.clickwise.iptags;


import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.clickwise.ghh.lib.GetTime;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class IPTagsServer implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(IPTagsServer.class);
	HbaseTool hbaseTool=new HbaseTool();
	IPTagsServer() {
		logger.info("服务已启动");
	}

	public void run() {
		try {
			HttpServer localHttpServer = HttpServer.create(
					new InetSocketAddress(Integer.parseInt("9984")), 0);
			HttpContext context = localHttpServer.createContext("/iptags",
					new IPTagHttpHandler());
			context.getFilters().add(new ParameterFilter());
			localHttpServer.setExecutor(null);
			localHttpServer.start();
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	class IPTagHttpHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange httpExchange) throws IOException {
			Map<String, Object> params = (Map<String, Object>) httpExchange
					.getAttribute("parameters");
			String iplist = "";
			String time = "";
			for (String key : params.keySet()) {
				if (key.equals("ip")) {
					iplist = params.get(key).toString();
				} else if (key.equals("time")) {
					time = params.get(key).toString();
				}
			}
			time=GetTime.delother(time, "-");
			logger.info("iplist is: " + iplist);
			logger.info("time is: " + time);

			String responseMsg = hbaseTool.getResult(iplist, time);
			httpExchange
					.sendResponseHeaders(200, responseMsg.getBytes().length);
			OutputStream out = httpExchange.getResponseBody(); // 获得输出流
			out.write(responseMsg.getBytes());
			out.flush();
			httpExchange.close();

		}
	}

	public static void main(String[] args) {
		startServer();
	}

	public static void startServer() {
		IPTagsServer cs = new IPTagsServer();
		Thread serverThread = new Thread(cs);
		serverThread.start();
	}

}
