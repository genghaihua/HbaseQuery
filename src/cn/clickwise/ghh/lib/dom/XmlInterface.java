package cn.clickwise.ghh.lib.dom;

import java.util.HashMap;

public interface XmlInterface {
	
	public boolean createXml(String fileName,String parent,HashMap<String, String> child); 
	public HashMap<String, String> parserXml(String fileName,String parent); 
	
}
