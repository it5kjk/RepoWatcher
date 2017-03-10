package scripts;

import java.io.*;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author JNK
 * @since 1.0.1a
 */
public class ScriptProcessor {
	private static final Path SCRIPT_LOCATION = Paths.get("C:\\scripts\\RW");
	private ProcessBuilder pb;
	private Process process;
	public static ArrayList<String>scriptSequence = new ArrayList<String>(
														Arrays.asList(
															"gitinit.sh", 
															"gitc.sh")
	); 
	
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
		if (scriptSequence != null) {
			for (String script : scriptSequence) {
				new ScriptProcessor(dir, script);
			}
		} else {
			collectScriptFiles(SCRIPT_LOCATION);
			executeSequence(dir);
		}
	}
	
	/*
	 * Copy shell script files from a given location to the current directory
	 */
	private static void collectScriptFiles(Path location) {
		//TODO: test this
		File[] files = location.toFile().listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".sh");
			}
		});
		for (File file : files) {
			try {
				Files.copy(
						file.toPath(), 
						Paths.get("./"),
						StandardCopyOption.COPY_ATTRIBUTES);
				scriptSequence.add(file.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}



