package cn.clickwise.bigdata.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.clickwise.bigdata.tool.FileUtil;
import cn.clickwise.bigdata.tool.SystemRun;

/**
 * Utility for Infobright Database
 * 
 * @author alanshu
 * 
 */
public class InfoDBUtil {
	private String db_name;
	FileUtil futil = new FileUtil();
	protected final Logger LOG = LoggerFactory.getLogger(InfoDBUtil.class);
	
	public InfoDBUtil(String dbname) {
		this.db_name = dbname;
	}

	/**
	 * Load local file into Infobright Table
	 * 
	 * @param fn
	 *            local file name
	 * @param tb_name
	 *            Infobright table name
	 * @param create_tb
	 *            The statement to create the table if not exists
	 * 
	 * @return true if ok, false if failed
	 */
	public boolean load_data_into_tb(String fn, String tb_name,
			String create_desc) {
		LOG.info("Load data into infobright:filename="+fn + ",tb_name="+tb_name);
		
		String create_stmt = "CREATE TABLE IF NOT EXISTS "
				+ tb_name
				+ " ("
				+ create_desc
				+ ") ENGINE=BRIGHTHOUSE DEFAULT CHARSET=latin1 COLLATE=latin1_bin;";

		// Prepare rnd_dir
		String base_dir = "/tmp/rnd";
		File rnd_dir = new File(base_dir);
		if (!rnd_dir.exists())
			rnd_dir.mkdir();

		// Prepare import file
		String import_file = base_dir + "/import_rand_" + tb_name
				+ System.currentTimeMillis() + ".txt";

		StringBuilder sb = new StringBuilder();
		// Create database
		sb.append("create database if not exists " + db_name + ";\n");
		sb.append("use " + db_name + ";\n");
		sb.append("set autocommit=1;\n");
		sb.append("set @bh_dataformat ='txt_variable';\n");
		sb.append("set @BH_ABORT_ON_THRESHOLD = 0.5;\n");
		// Create table
		sb.append(create_stmt + ";\n");

		// Set reject file
		//String reject_file = base_dir + "/reject_" + tb_name + "_"
		//		+ Math.random();
		String reject_file = "/tmp/reject_" + tb_name + "_"
				+ Math.random();
		sb.append("set @BH_REJECT_FILE_PATH = '" + reject_file + "';\n");

		// Load statement
		String filename = base_dir + "/load_file_" + tb_name + "_"
				+ Math.random();

		// Filter content
		if (filter_content(fn, filename) == false) {
			System.err
					.println("Can not filter file content before loading into infobright:src="+fn+",dst="+filename);
			return false;
		}
		;

		sb.append("LOAD DATA INFILE '"
				+ filename
				+ "' into table "
				+ tb_name
				+ " FIELDS TERMINATED BY '\t' "
				+ "ENCLOSED BY '\"' ESCAPED BY '\\\\' LINES TERMINATED BY '\n';");

		// Write content into file
		futil.put_content(import_file,sb.toString(),false);
		
		// Execute Shell to load file
		String cmd = "mysql-ib -uroot < " + import_file ;
		//String cmd = "sh -c \'cat import_file | mysql-ib -uroot \'";
		LOG.info("Load data with command:"+cmd);
		String shfile = "/tmp/load_infodb_"+tb_name + "_"+Math.random()+".sh";
		futil.put_content(shfile, cmd, false);
		SystemRun sr = new SystemRun();
		boolean ret = sr.run("sh " + shfile);
		
		//clean up
		if(ret == true)
			futil.delete(import_file);
		futil.delete(reject_file);
		futil.delete(shfile);
		
		return ret;
	}

	/**
	 * Filter file content in order to load into fragile Infobright table
	 * 
	 * @param infile
	 * @param outfile
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException
	 */
	private boolean filter_content(String infile, String outfile){
		BufferedReader br = null;
		BufferedWriter bw = null;
		
		try {
			// Prepare input file
			File inFile = new File(infile);
			br = new BufferedReader(new FileReader(inFile));
			
			// Prepare output file
			File outFile = new File(outfile);
			if (outFile.exists())
				outFile.delete();

			bw = new BufferedWriter(new FileWriter(outFile));
			
			//Filter content
			String line;
			while((line = br.readLine())!= null){
				StringBuilder sb = new StringBuilder();
				
				//sed -e 's/\x1/\t/g' -e 's/\"/ /g' -e 's/-/ /g'
				for(int i = 0; i < line.length();i++){
					line = line.trim();
					char c = line.charAt(i);
					if(c == '-'){
						sb.append(' ');
					}else if(c == '"'){
						sb.append(' ');
					}else if(c == '\01'){
						sb.append('\t');
					}else{
						sb.append(c);
					}
				}
				//sb.append('\n');
				bw.write(sb.toString());
				bw.newLine();
			}
		} catch (FileNotFoundException e) {
			System.err.println("Input File does not exists:" + infile);
			return false;
		} catch (IOException e) {
			System.err.println("File IO Exception:" + e.getLocalizedMessage());
			return false;
		} finally{
			try{
				if(br != null)
					br.close();
				if(bw != null)
					bw.close();
			}catch(Exception e){}
		}

		return true;
	}
}
