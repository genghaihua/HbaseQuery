package cn.clickwise.bigdata.tool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * File utility
 * 
 * @author alanshu
 *
 */
public class FileUtil {

	/**
	 * Write content into file
	 *  
	 * 
	 * @param filename	File name to write
	 * @param content	Content to write into file
	 * @param append	append mode if true
	 * @return true if everything is ok, false otherwise
	 */
	public boolean put_content(String filename, String content, boolean append) {
		FileWriter fw = null;
		try {
			File file = new File(filename);
			/*if(!append && file.isFile()&& file.exists()){
				file.delete();
			}*/
			
			fw = new FileWriter(file,append);
			fw.write(content);
		} catch (IOException e) {
			System.err.println("Write File exception:" + e.getLocalizedMessage());
			return false;
		} finally{
			if(fw != null)
				try {
					fw.close();
				} catch (IOException e) {
					
				}
		}
		
		return true;
	}

	/**
	 * Create a directory
	 * 
	 * @param dirname
	 */
	public boolean mkdir(String dirname) {
		File dir = new File(dirname);
		if(dir.exists()){
			if(!dir.isDirectory()){
				System.err.println("File with same name as the directory exists!:"+dirname);
				return false;
			}
			return true;
		}
		
		return dir.mkdirs();
	}

	/**
	 * Delete file or directory
	 * 
	 * @param filename	the name to delete
	 * 
	 * @return	true if success, false otherwise
	 */
	public boolean delete(String filename) {
		File file = new File(filename);
		if(file.exists())
			return file.delete();
		return false;
	}
	
	// 递归删除文件夹
	public boolean deleteFile(String filename) {
		File file = new File(filename);
		if (file.exists()) {// 判断文件是否存在
			if (file.isFile()) {// 判断是否是文件
				return file.delete();// 删除文件
			} else if (file.isDirectory()) {// 否则如果它是一个目录
				File[] files = file.listFiles();// 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) {// 遍历目录下所有的文件
					files[i].delete();// 把每个文件用这个方法进行迭代
				}
				return file.delete();// 删除文件夹
			}
		} 
		return false;
	}
	public ArrayList<String> getFiles(String filename){
		ArrayList<String> arr=new ArrayList<String>();
		File file = new File(filename);
		if (file.exists()) {// 判断文件是否存在
			if (file.isFile()) {// 判断是否是文件
				arr.add(file.getAbsolutePath());
				return arr;
			} else if (file.isDirectory()) {// 否则如果它是一个目录
				File[] files = file.listFiles();// 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) {// 遍历目录下所有的文件
					if(!files[i].getAbsolutePath().contains("crc")){
						arr.add(files[i].getAbsolutePath());// 把每个文件用这个方法进行迭代
					}
				}
				return arr;// 删除文件夹
			}
		}
		return null;
	}
	public static void main(String[] args) {
		FileUtil fu=new FileUtil();
		String fn="D:/my.txt";
		fu.put_content(fn, "yes"+"\n", true);
		fu.put_content(fn, "no"+"\n", true);
	}
}
