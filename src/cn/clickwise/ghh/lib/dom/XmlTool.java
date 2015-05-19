package cn.clickwise.ghh.lib.dom;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class XmlTool implements XmlInterface {
	private static Logger logger = LoggerFactory.getLogger(XmlTool.class);
	private Document document;

	public XmlTool(){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			this.document = builder.newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	@Override
	public HashMap<String, String> parserXml(String fileName, String parent) {
		HashMap<String, String> res = new HashMap<String, String>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			document = db.parse(fileName);
			NodeList peizhi = document.getChildNodes();
			for (int i = 0; i < peizhi.getLength(); i++) {
				Node nd = peizhi.item(i);
				NodeList ndInfo = nd.getChildNodes();
				for (int j = 0; j < ndInfo.getLength(); j++) {
					if (ndInfo.item(j).getNodeName().equals(parent)) {
						Node re = ndInfo.item(j);
						NodeList reInfo = re.getChildNodes();
						for (int k = 0; k < reInfo.getLength(); k++) {
							Node fnode=reInfo.item(k);
							if(fnode instanceof Element)
							res.put(fnode.getNodeName(), fnode.getTextContent());
						}
					}

				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
	@Override
	public boolean createXml(String fileName, String parent,
			HashMap<String, String> childs) {
		Element root = this.document.createElement("xml");
		this.document.appendChild(root);
		Element parentElement = this.document.createElement(parent);
		for (String k : childs.keySet()) {
			Element e = document.createElement(k);
			e.appendChild(document.createTextNode(childs.get(k)));
			parentElement.appendChild(e);
		}
		// document.appendChild(parentElement);
		root.appendChild(parentElement);
		TransformerFactory tf = TransformerFactory.newInstance();
		try {
			Transformer transformer = tf.newTransformer();
			DOMSource source = new DOMSource(document);
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));
			StreamResult result = new StreamResult(pw);
			transformer.transform(source, result);
			logger.info("生成XML文件成功!");
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) {
		test2();
	}

	public static void test2(){
		XmlTool ddTool = new XmlTool();
		String str1=XmlTool.class.getResource("/").getPath()+java.io.File.separator+"conf"+java.io.File.separator+"cookie_map.conf.xml";
		String str = "src/conf/aaa.xml";
		HashMap<String, String> childs = ddTool.parserXml(str1, "cookie_map");
		for (String k : childs.keySet()) {
			System.out.println(k + ":  " + childs.get(k));
		}
	}
	public static void test1() {
		XmlTool ddTool = new XmlTool();
		String str = "src/conf/aaa.xml";
		HashMap<String, String> childs = ddTool.parserXml(str, "cookie_map");
		for (String k : childs.keySet()) {
			System.out.println(k + ":  " + childs.get(k));
		}
	}

	public static void test() {
		XmlTool ddTool = new XmlTool();
		String str = "src/conf/aaa.xml";
		HashMap<String, String> h = new HashMap<String, String>();
		h.put("radius_server", "true");
		h.put("radius_service", "http://IP:port");
		h.put("userid_type", "uacd");
		ddTool.createXml(str, "cookie_map", h);
	}
}
