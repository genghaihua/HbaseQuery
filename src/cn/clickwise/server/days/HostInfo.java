package cn.clickwise.server.days;

public class HostInfo implements Comparable<HostInfo> {

	String host;
	int ips;
	String time;
	public HostInfo(String host,int ips,String time) {
		this.host=host;
		this.ips=ips;
		this.time=time;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getIps() {
		return ips;
	}
	public void setIps(int ips) {
		this.ips = ips;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return host+"\t"+ips+"\t"+time;
	}
	@Override
	public int compareTo(HostInfo o) {
		// TODO Auto-generated method stub
		// 按name排序
        if (this.time.compareTo(o.getTime()) > 0) {
            return 1;
        }
        if (this.time.compareTo(o.getTime()) < 0) {
            return -1;
        }
        if(this.getIps()-o.getIps()>0)
        	return 1;
        if(this.getIps()-o.getIps()<0)
        	return -1;
        
        return 0;
	}
	
}
