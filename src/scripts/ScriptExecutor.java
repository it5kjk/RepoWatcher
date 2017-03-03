package scripts;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author JNK
 *
 */
public class ScriptExecutor {
	ProcessBuilder pb;
	Process process;
	
	public ScriptExecutor(Path dir) {

		List<String> command = new ArrayList<String>();
		
		try {
			command.add("\"C:\\Program Files\\Git\\bin\\sh.exe\"");
			command.add("--cd=" + "\""+ dir + "\"");
			command.add("-c");
			//TODO: fix second script not running
//			command.add("chmod +x gitinit.sh && ./gitinit.sh");
			//TODO adjust second script to windows
			command.add("chmod +x gitc.sh && ./gitc.sh");
				
	    	pb = new ProcessBuilder(command);
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



