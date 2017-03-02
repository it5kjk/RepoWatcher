package scripts;

import java.io.*;
import java.nio.file.Path;

/**
 * 
 * @author JNK
 *
 */
public class ScriptExecutor {
	ProcessBuilder pb;
	Process process;
	
	public ScriptExecutor(Path dir) {
		//TODO: implement constructor
		/*
		 * pass directory where script is run
		 * run bash with shell script
		 */
		
		try {
	    	pb = new ProcessBuilder(
    			"cmd",
    			//open command line to run bash to run script
    			"\"C:\\Git\\bin\\sh.exe\" --login -i --cd=\""+dir+"\" -- hw.sh"
			);
	        pb.inheritIO();
	        process = pb.start();
	        process.waitFor();
	    } catch (InterruptedException | IOException e) {
			e.printStackTrace();
		} finally {
	    }
	}
	
	
	// write a temporary script file to run
	public File createTempScript() throws IOException {
	    File tempScript = File.createTempFile("script", null);

	    Writer streamWriter = new OutputStreamWriter(new FileOutputStream(
	            tempScript));
	    PrintWriter printWriter = new PrintWriter(streamWriter);

	    printWriter.println("#!/bin/bash");
	    printWriter.println("cd bin");
	    printWriter.println("ls");
	    
	    printWriter.close();

	    return tempScript;
	}

	public void executeCommands() throws IOException {

	    File tempScript = createTempScript();
	
	    try {
	    	pb = new ProcessBuilder("bash", tempScript.toString());
	        pb.inheritIO();
	        process = pb.start();
	        process.waitFor();
	    } catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
	        tempScript.delete();
	    }
	}

}



