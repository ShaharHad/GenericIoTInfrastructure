package il.co.ilrd.GenericIoTInfrastructure.plug_and_play_service.DirectoryMonitor;

import il.co.ilrd.GenericIoTInfrastructure.plug_and_play_service.DirectoryMonitor.Observer.Publisher;

import java.io.IOException;
import java.nio.file.*;

public class DirectoryMonitor extends Publisher<String> {
    private WatchService watchService;
    private Path path;
    private WatchKey watchKey;

    public DirectoryMonitor(){
        try {
            watchService  = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerPath(String str){
        path = Paths.get(str); // create Path to the directory
        try {
            path.register( // register events to path
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void observe(){
        try {
            while((watchKey = watchService.take()) != null){// wait until the key is signaled
                eventTask();
                resetKey();
            }

        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close(){
        try {
            watchService.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void eventTask() throws IOException {

        for(WatchEvent<?> event : watchKey.pollEvents()){ // poll events from the watchkey
            this.produce(event.context().toString());
        }
    }

    private void resetKey(){
        if(null == watchKey){
            throw new NullPointerException("watch key is null");
        }
        watchKey.reset(); // return the key into the queue again
    }
}

class Main{
    public static void main(String[] args) {
        DirectoryMonitor dirMonitor = new DirectoryMonitor();
        String patToWatch = "/home/shahar/git/fs/il/co/ILRD/";
        while(true){
            dirMonitor.registerPath(patToWatch);
            dirMonitor.observe();
        }
    }
}
