package cn.clickwise.ghh.lib;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class GetProperties {
	public  Properties getPro(){
		Properties pro=new Properties();
		InputStream is=GetProperties.class.getClassLoader().getResourceAsStream("conf/aaa.xml");
		try {
			pro.load(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		for (Object obj : pro.keySet()) {
//			System.out.println(((String) obj)+":"+pro.getProperty((String) obj));
//		}
		//System.out.println(pro.getProperty("radius_server"));
		return pro;
	}
	public static void main(String[] args) {
		GetProperties g=new GetProperties();
		g.getPro();
	}

}
