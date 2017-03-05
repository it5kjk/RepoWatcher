package scripts;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author JNK
 * @since 1.0.1a
 */
public class ScriptProcessor {
	private ProcessBuilder pb;
	private Process process;
	public static String[] scriptSequence = {
		"gitinit.sh", 
		"gitc.sh"
	}; 
	
	public ScriptProcessor(Path dir, String script) {
		List<String> commands = new ArrayList<String>();
		
		try {
			commands.add("\"C:\\Program Files\\Git\\bin\\sh.exe\"");
			commands.add("--cd=" + "\""+ dir + "\"");
			commands.add("-c");
			commands.add("chmod +x " + script+ " && ./" + script);
				
	    	pb = new ProcessBuilder(commands);
	        pb.inheritIO();
	        process = pb.start();
	        process.waitFor();
	    } catch (InterruptedException | IOException e) {
			e.printStackTrace();
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

	public static void executeSequence(Path dir) {
		for (String script : scriptSequence) {
			new ScriptProcessor(dir, script);
		}
	}
	
	private static void provideScriptFiles(Path dir) {
		//TODO: copy *.sh files from a dedicated location to current dir
	}
}



