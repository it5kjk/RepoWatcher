package service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class PathSnapshot {
	public ArrayList<Path> dirCache = new ArrayList<Path>();
	public ArrayList<Path> fileCache = new ArrayList<Path>();
	
	public PathSnapshot(Path dir) {
		try {
			Stream<Path> rawDirContent = Files.walk(
					dir, 1);
			
			Object[] dirContent = rawDirContent.toArray();
			rawDirContent.close();
			
			sortIntoCache(dirContent, dir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sortIntoCache(Object[] dirContent, Path rootdir) {
		for (Object object : dirContent) {
			//create path from element
			Path objectPath = Paths.get(object.toString());
			//skip start path / the root directory
			if (object.equals(rootdir)) {
				continue;
			} else if (Files.isRegularFile(objectPath)) {
				fileCache.add(objectPath);
			} else if (Files.isDirectory(objectPath)) {
				dirCache.add(objectPath);
			}
		} 
	}
	
	public boolean isInFileCache(Path path) {
		if (fileCache.contains(path)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isInDirCache(Path path) {
		if (dirCache.contains(path)) {
			return true;
		} else {
			return false;
		}
	}
	
	public PathSnapshot updateSnapshot(Path dir){
		return new PathSnapshot(dir);
	}
}
