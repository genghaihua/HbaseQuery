package cn.clickwise.ghh.lib;

import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.tools.JavaCompiler;

public class DecodeUid {

	public static void main(String[] args) throws Exception {
		String test ="uacd=V2_YVQRWURUQEYgDREHfh1ZUG8FFAoQVBcddwtAVX0fCQUIABNdRlZBFHALR1FyHF5qZwAXQkJXRAp2DUZLfB5J; _jrda=1; pin=260826413_m; TrackID=1-O5KOQ4hckNaS8uTjBFtBe0Ie1csHKjVT_WbicXy8mKznON6J7zifs30sGeG3kva; pinId=g52pHvpf10zq_D3Ya9Y9Cw; pin=260826413_m; unick=260826413_m; _tp=Z7zShA%2B9gslxuIUzWk%2BWbA%3D%3D;  ipLocation=%u5317";
		String unicode1 = URLEncoder.encode(test,"unicode");
		System.out.println(unicode1);
		String unicode2 = URLEncoder.encode(unicode1,"unicode");
		System.out.println(unicode2);
		String unicode3 = URLEncoder.encode(unicode2,"utf-8");
		System.out.println(unicode3);
		System.out.println("------------------------------------------");
		
		String string1 = URLDecoder.decode(unicode2,"utf-8");
		System.out.println(string1);
		System.out.println("------------------------------------------");
		
		
		
		System.out.println(URLDecoder.decode(URLEncoder.encode(unicode2,"utf-8"),"utf-8"));
		
		System.out.println("------------------------------------------");
		
		String string2=URLEncoder.encode(string1,"utf-8");
		String string3=URLDecoder.decode(string2,"utf-8");
//		String string4=URLDecoder.decode(string3,"utf-8");
		System.out.println(string3);
	}

	public static void test() {
		String test = "最代码网站地址:www.zuidaima.com";
		String unicode = string2Unicode(test);
		String string = unicode2String(unicode);
		System.out.println(unicode);
		System.out.println(string);
	}

	// 测试用户id解码
	public static void testUidDecode() {
		// String str = "gvz%5Cu65D7%5Cu8230%5Cu5E97";
		String str = "gvz%E6%97%97%E8%88%B0%E5%BA%97%26center";
		// String str1 = URLDecoder.decode(str);
		System.out.println(decodeUserID(str));
	}

	/**
	 * 
	 * 字符串转换unicode
	 */

	public static String string2Unicode(String string) {
		StringBuffer unicode = new StringBuffer();
		for (int i = 0; i < string.length(); i++) {
			// 取出每一个字符
			char c = string.charAt(i);
			// 转换为unicode
			unicode.append("\\u" + Integer.toHexString(c));
		}
		return unicode.toString();

	}

	public static String unicode2String(String unicode) {
		StringBuffer string = new StringBuffer();
		String[] hex = unicode.split("\\\\u");
		for (int i = 1; i < hex.length; i++) {
			// 转换出每一个代码点
			int data = Integer.parseInt(hex[i], 16);
			// 追加成string
			string.append((char) data);
		}
		return string.toString();
	}

	public static String decodeUserID(String str) {
		String str1 = URLDecoder.decode(str);
		return decodeUnicode(str1);
	}

	public static String decodeUnicode(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException(
									"Malformed   \\uxxxx   encoding.");
						}
					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}
}
