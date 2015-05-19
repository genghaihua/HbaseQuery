package cn.clickwise.ghh.lib;

import java.math.BigInteger;
import java.security.MessageDigest;

public class Encryption {

	public static String makeMD5(String paramString) {
		try {
			MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
			localMessageDigest.update(paramString.getBytes());
			String str = new BigInteger(1, localMessageDigest.digest())
					.toString(16);
			return str;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return paramString;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
