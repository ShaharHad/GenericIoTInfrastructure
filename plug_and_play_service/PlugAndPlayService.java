/*************
 Code by: Shahar Hadad
 Project: plugAndPlay
 Review by: Amit
 Approved by: Amit
 *************/


package il.co.ilrd.GenericIoTInfrastructure.plug_and_play_service;

import il.co.ilrd.GenericIoTInfrastructure.RPS;
import il.co.ilrd.GenericIoTInfrastructure.plug_and_play_service.DirectoryMonitor.DirectoryMonitor;
import il.co.ilrd.GenericIoTInfrastructure.plug_and_play_service.DirectoryMonitor.Observer.Subscriber;
import il.co.ilrd.GenericIoTInfrastructure.plug_and_play_service.jar_loader.DynamicJarLoader;

import java.io.File;
import java.util.Set;
import java.util.function.Consumer;

public class PlugAndPlayService {

    private DirectoryMonitor dirMonitor = new DirectoryMonitor();
    private DynamicJarLoader jarLoader;
    private final String PATH_TO_JARS = "/home/shahar/git/fs/il/co/ilrd/GenericIoTInfrastructure/JARs/";
    private RPS rps;
    Subscriber<String> subscriber;



    public PlugAndPlayService(RPS rps){
        if(null == rps){
            throw new RuntimeException("rpd in PlugAndPlayService ctor is null");
        }
        this.rps = rps;
        initPublisher();
        initSubscriber();
    }

    private void initPublisher(){
        dirMonitor.registerPath(PATH_TO_JARS);
    }

    private void initSubscriber(){

        Consumer<String> consumer = new Consumer<String>() {
            @Override
            public void accept(String s) {
                jarAdded(s);
            }
        };

        Runnable runnable = new Runnable() {
            @Override
            public void run() {System.out.println("subscribe1 Stop subscribe");
            }
        };
        subscriber = new Subscriber<>(consumer, runnable);
        subscriber.register(dirMonitor);
    }

    public void initJarFilesInDirectory(){
        File dir = new File(PATH_TO_JARS);
        File[] listOfFiles = dir.listFiles();
        if(null != listOfFiles){
            for(File file: listOfFiles){
                jarAdded(file.getName());
            }
        }
        else{
            System.out.println("Directory is empty");
        }
    }


    public void startPlugAndPlayService(){
        dirMonitor.observe();
    }

    public void stopPlugAndPlayService(){
        dirMonitor.close();
    }

    private void jarAdded(String jarFile){
        if(null == jarFile){
            throw new RuntimeException("jarFile in p&p.jarAdded is null");
        }
        if(jarFile.endsWith(".jar")){
            jarLoader = new DynamicJarLoader("Command", PATH_TO_JARS + jarFile);
            Set<Class<?>> set = jarLoader.load();
            rps.addCommandsFromJar(set);
        }
    }
}
