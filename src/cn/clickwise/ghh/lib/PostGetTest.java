package cn.clickwise.ghh.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;



public class PostGetTest {

	public static String sendPostRequest1(String urlstr, String param)
			throws Exception {
//ArrayList<String> response=new ArrayList<String>();
       StringBuffer  response=new StringBuffer();
		try {
			URL url = new URL(urlstr);
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);
			//urlConn.setRequestProperty("Content-type","application/x-java-serialized-object");
			urlConn.setRequestProperty("Content-type","text/plain");

			// 设定请求的方法为"POST"，默认是GET
			urlConn.setRequestMethod("POST");
			urlConn.setConnectTimeout(1000000);
			urlConn.connect();
			
			OutputStream outStrm = urlConn.getOutputStream();
			// 现在通过输出流对象构建对象输出流对象，以实现输出可序列化的对象。
			//ObjectOutputStream oos = new ObjectOutputStream(outStrm);
            OutputStreamWriter osw=new OutputStreamWriter(outStrm);
			PrintWriter pw=new PrintWriter(osw);
            pw.println(param);
			// 向对象输出流写出数据，这些数据将存到内存缓冲区中
//			for(int j=0;j<texts.length;j++)
//			{
//					pw.println(texts[j]);
//		
//			}

			// 刷新对象输出流，将任何字节都写入潜在的流中（些处为ObjectOutputStream）
			pw.flush();		
	        BufferedReader reader = new BufferedReader(new InputStreamReader(
	        		urlConn.getInputStream()));
	        
	        String line;
	        while ((line = reader.readLine()) != null) {
	        	//System.out.println("res line:"+line);
	        	//response.add(URLDecoder.decode(line));
	        	response.append(line);
	        }	        
	        
	        
	        reader.close();
	        urlConn.disconnect();
	        
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return URLDecoder.decode(response.toString());
	}
	public static String sendPostRequest(String urlstr, String param)
			throws Exception {
//ArrayList<String> response=new ArrayList<String>();
       StringBuffer  response=new StringBuffer();
		try {
			URL url = new URL(urlstr);
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);
			// 设定请求的方法为"POST"，默认是GET
			urlConn.setRequestMethod("POST");
			urlConn.setConnectTimeout(1000000);
			urlConn.setRequestProperty("Content-type","text/plain");
			urlConn.setRequestProperty("Content-Length",String.valueOf(param.getBytes().length));
			//urlConn.connect();
			
			
			urlConn.setReadTimeout(1000000);
			
			OutputStream outStrm = urlConn.getOutputStream();
			outStrm.write(param.getBytes());
			outStrm.flush();
			outStrm.close();

	        BufferedReader reader = new BufferedReader(new InputStreamReader(
	        		urlConn.getInputStream()));
	        String line;
	        while ((line = reader.readLine()) != null) {
	        	//System.out.println("res line:"+line);
	        	//response.add(URLDecoder.decode(line));
	        	response.append(line);
	        }	        
	        reader.close();
	        urlConn.disconnect();
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return response.toString();
	}

	public static void main(String[] args) {

	}

}
