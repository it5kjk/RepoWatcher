package scripts;

import java.io.*;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sun.net.www.content.text.plain;

/**
 * 
 * @author JNK
 * @since 1.0.1a
 */
public class ScriptProcessor {
	private static final Path scriptPath = Paths.get("C:\\Temp\\scripts");
	private ProcessBuilder pb;
	private Process process;
	public static ArrayList<String>scriptSequence 
				= new ArrayList<String>(Arrays.asList(
										"gitinit.sh", 
										"gitc.sh"
										)
	); 
	
	public ScriptProcessor(Path dir, String script) {
		List<String> commands = new ArrayList<String>();
		
		try {
//			 commands.add("cmd");
			if (new File("C:\\Program Files\\Git\\bin\\sh.exe\\").exists()) {
				commands.add("\"C:\\Program Files\\Git\\bin\\sh.exe\"");
			} else if (new File("D:\\Programme\\Git\\bin\\sh.exe\\").exists()) {
				commands.add("\"D:\\Programme\\Git\\bin\\sh.exe\"");
			}
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
		if (scriptSequence != null && !scriptSequence.isEmpty()) {
			collectScriptFiles(scriptPath, dir); //test
			for (String script : scriptSequence) {
				new ScriptProcessor(dir, script);
			}
			deleteScriptFiles(dir);
		} else {
			collectScriptFiles(scriptPath, dir);
			executeSequence(dir);
		}
	}
	
	/*
	 * Copy shell script files to target location
	 */
	private static void collectScriptFiles(Path location, Path target) {
		File[] files = location.toFile().listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".sh");
			}
		});
		for (File file : files) {
			try {
				String dest = target.toString() + "\\";
				Files.copy(
						file.toPath(), 
						(new File(dest + file.getName())).toPath(),
						StandardCopyOption.REPLACE_EXISTING);
//				scriptSequence.add(file.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// clean up directory
	private static void deleteScriptFiles(Path dir) {
		// TODO implement function
		for (String script : scriptSequence) {
			File scriptFile = new File(dir.toString() + "\\" + script);
			scriptFile.delete();
		}
	}
}
