package cn.clickwise.bigdata.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * A Wrapper for running system command From java
 * 
 * @author alanshu
 *
 */

/**
 * An Stream wrapper for STDOUT and STDERR
 * 
 * @author alanshu
 *
 */
class StreamGobbler extends Thread {
    InputStream is;
    String      type;
    OutputStream os;
    StreamGobbler(InputStream is, String type) {
        this(is, type, null);
    }
    StreamGobbler(InputStream is, String type, OutputStream redirect) {
        this.is = is;
        this.type = type;
        this.os = redirect;
    }
    public void run() {
        try {
            PrintWriter pw = null;
            if (os != null)
                pw = new PrintWriter(os);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (pw != null)
                    pw.println(line);
                System.out.println("["+type+"]> " + line);
            }
            if (pw != null)
                pw.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}

/**
 * System Run Wrapper
 * 
 * @author alanshu
 *
 */
public class SystemRun {
	/**
	 * Run the command
	 * 
	 * @NOTE: Shell pipe will not work, e.g 'ls |awk' 
	 * @param cmd	the command to run
	 * @return	true if everything is ok and return 0, false otherwise
	 */
	public boolean run(String cmd){
		try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(cmd);
            // any error message?
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "sys.err");
            // any output?
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "sys.out");
            // kick them off
            errorGobbler.start();
            outputGobbler.start();
            // any error???
            int exitVal = proc.waitFor();
            System.err.println("ExitValue: " + exitVal);
            if(exitVal != 0)
            	return false;
        } catch (Throwable t) {
            System.err.println("Run error:" + t.getLocalizedMessage());
            return false;
        }
		return true;
	}
	/**
	 * Run command array
	 * 
	 * The command will be the array of string. The first element should be the command file
	 * others are parameters
	 * 
	 * @param cmdarray
	 * 
	 * @return true if everything is ok and return 0, false otherwise
	 */
	public boolean run(String[] cmdarray){
		try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(cmdarray);
            // any error message?
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "sys.err");
            // any output?
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "sys.out");
            // kick them off
            errorGobbler.start();
            outputGobbler.start();
            // any error???
            int exitVal = proc.waitFor();
            System.err.println("ExitValue: " + exitVal);
            if(exitVal != 0)
            	return false;
        } catch (Throwable t) {
            System.err.println("Run error:" + t.getLocalizedMessage());
            return false;
        }
		return true;
	}
	
	/**
	 * This method will be could use "|" in command
	 * @param cmd
	 * @return
	 */
	public boolean start(String cmd){		
		try {
			List<String> cmds=new ArrayList<String>();
			cmds.add("bash");
			cmds.add("-c");
			cmds.add(cmd);
			System.out.println("执行 cmd"+cmds.toString());
			ProcessBuilder pb=new ProcessBuilder(cmds);
			
            Process proc = pb.start();
            // any error message?
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "sys.err");
            // any output?
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "sys.out");
            // kick them off
            errorGobbler.start();
            outputGobbler.start();
            // any error???
            int exitVal = proc.waitFor();
            System.err.println("ExitValue: " + exitVal);
            if(exitVal != 0)
            	return false;
        } catch (Throwable t) {
            System.err.println("Run error:" + t.getLocalizedMessage());
            return false;
        }
		
		return true;
		
	}
}


