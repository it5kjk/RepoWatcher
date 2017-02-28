
package service;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;

/**
 * Slightly modified version of Oracle
 * example file WatchDir.java
 * 
 * @author JNK 
 * 
 * @param dir directory to be monitored
 * 
 * @see 
 * <a href=
 * "https://docs.oracle.com/javase/tutorial/essential/io/examples/WatchDir.java"
 * >Java Tutorials Code Sample</a>
 * @since Version 0.9
 */
public class DirectoryWatcher {
	private final Path path;
	private final WatchService watcher;
	private final Map<WatchKey,Path> keys;
	private PathSnapshot pathSnapshot;
    private boolean trace = false;
	
    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }
    
    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }
    
	public DirectoryWatcher(Path dir) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey,Path>();
        this.path = dir;
        this.pathSnapshot = new PathSnapshot(dir);
        
        register(dir);
        // enable trace after initial registration
        this.trace = true;
    }
	
	/**
     * Process all events for keys queued to the watcher
     */
	void processEvents() {
        for (;;) {

            // wait for key to be signaled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event: key.pollEvents()) {
                Kind<?> kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }
                
                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);
                this.updateDirContent();
                /*
                 * currently: creating file events are neglected 
                 * but deleting a file creates an event which is printed
                 * TODO: disregard delete event if sent from file
                 */
                boolean isFile = Files.isRegularFile(child);
                if (pathSnapshot.isInFileCache(child)|| isFile) {
                	//disregard the event if file
                	event = null;
				} else {
					// print out event
					System.out.format("%s: %s\n", event.kind().name(), child);
				}
            }
            
            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

	private void updateDirContent() {
		this.pathSnapshot = pathSnapshot.updateSnapshot(path);
		
	}
}
