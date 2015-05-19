package cn.clickwise.server.days;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HostInfoFun {

	public  static String getHost(String hostinfos) {
		String result = "";
		List<HostInfo> a1 = new ArrayList<HostInfo>();
		String[] hid = hostinfos.split("\n");
		for (String hs : hid) {
			String[] h = hs.split("\t");
			a1.add(new HostInfo(h[0], Integer.parseInt(h[1]), h[2]));
		}
		Collections.sort(a1);
		Collections.reverse(a1);
		int size=a1.size();  
		for(int i=0;i<size-1;i++){  
			result+=a1.get(i)+"\n";
        } 
		result+=a1.get(size-1);
		a1.clear();
		return result;
	}
	
	public  static void test() {
		String hostinfos = "";
		
		String str = "show.re.taobao.com" + "\t" + 1 + "\t" + "20150412";
		hostinfos += str + "\n";
		str = "g.fastapi.net" + "\t" + 9 + "\t" + "20150416";
		hostinfos += str + "\n";
		str = "api.tv.sohu.com" + "\t" + 4 + "\t" + "20150414";
		hostinfos += str + "\n";
		str = "g.fastapi.net" + "\t" + 1 + "\t" + "20150416";
		hostinfos += str + "\n";
		str = "www.iqiyi.com" + "\t" + 1 + "\t" + "20150412";
		hostinfos += str + "\n";
		str = "sax.sina.com.cn" + "\t" + 6 + "\t" + "20150412";
		hostinfos += str + "\n";
		str = "finance.sina.com.cn" + "\t" + 3 + "\t" + "20150412";
		hostinfos += str + "\n";
		str = "api.tv.sohu.com" + "\t" + 4 + "\t" + "20150412";
		hostinfos += str + "\n";
		str = "g.fastapi.net" + "\t" + 1 + "\t" + "20150412";
		hostinfos += str;
		//getHost(hostinfos);
		System.out.println(getHost(hostinfos));
	}
	public static void main(String[] args) {
		test();

	}
}
