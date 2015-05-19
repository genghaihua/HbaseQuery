package cn.clickwise.ghh.lib;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GetTime {
	public static String delother(String str,String delchar){
		String res=str.replaceAll(delchar, "");
		return res;
	}
	public  static String getDayBefore(String specifiedDay){  
        Calendar c = Calendar.getInstance();  
        Date date = null;  
        try{  
            date = new SimpleDateFormat("yyyy-MM-dd").parse(specifiedDay);  
        }catch (Exception e) {  
            e.printStackTrace();  
        }  
        c.setTime(date);  
        int day = c.get(Calendar.DATE);  
        c.set(Calendar.DATE, day-1);  
        String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());  
        return dayBefore;  
    }  
	public  static String getDayAfter(String specifiedDay){  
        Calendar c = Calendar.getInstance();  
        Date date = null;  
        try{  
            date = new SimpleDateFormat("yyyyMMdd").parse(specifiedDay);  
        }catch (Exception e) {  
            e.printStackTrace();  
        }  
        c.setTime(date);  
        int day = c.get(Calendar.DATE);  
        c.set(Calendar.DATE, day+1);  
        String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());  
        return dayBefore;  
    } 
	public static String changetime(String str){
		Date date = null;    
		DateFormat format1 = new SimpleDateFormat("yyyyMMdd"); 
		try {
			date = format1.parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(date);
		return dateString.toString();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//        String string="20150419";
//		System.out.println(getDayAfter(string));
		String timesString="2015-04-12";
		System.out.println(delother(timesString, "-"));
	}

}
