package cn.clickwise.ghh.lib;

import java.util.ArrayList;
import java.util.HashMap;

public class StrPro {

	public static ArrayList<String> strtoarr(String[] strArray) {
		ArrayList<String> arr = new ArrayList<String>();
		for (int i = 0; i < strArray.length; i++) {
			arr.add(strArray[i]);
		}
		return arr;
	}

	public static String getstr(String a,int num){
		String tmp="";
		for(int i=0;i<num;i++)
			tmp+=a;
		return tmp;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String []types={"a","b","c"};
		ArrayList<String> atr=strtoarr(types);
		for (int i = 0; i < atr.size(); i++) {
			System.out.println(atr.get(i));
		}
		if(atr.contains("a"))
			System.out.println("yes");
	}

}
