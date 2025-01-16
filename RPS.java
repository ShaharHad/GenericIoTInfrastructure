
import com.google.gson.JsonObject;
import Factory.CommandFactory;
import ThreadPool.ThreadPool;
import commands.Command;
import connection_service.RespondableChannel;
import plug_and_play_service.PlugAndPlayService;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class RPS {

    private final ThreadPool threadPool = new ThreadPool(3);
    private final Parser parser;
    private CommandFactory<String, Command, JsonObject> factory = new CommandFactory<>();
    private PlugAndPlayService plugAndPlayService = new PlugAndPlayService(this);

    public RPS(Parser parser){
        if(null == parser){
            throw new RuntimeException("parser in RPS is null");
        }
        this.parser = parser;
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                plugAndPlayService.startPlugAndPlayService();
            }
        });
        plugAndPlayService.initJarFilesInDirectory();
    }

    public void addCommandsFromJar(Set<Class<?>> setOfClass) {
        if(null == setOfClass){
            throw new RuntimeException("setOfClass in RPS.addCommandsFromJar is null");
        }
        for(Class<?> cl: setOfClass){
            Constructor<?>[] constructors = cl.getDeclaredConstructors();
            String keyInFactory = cl.getName().substring(cl.getName().lastIndexOf(".") + 1);
            Function<JsonObject, ? extends Command> f = (s)-> {
                try {
                    return ((Command)constructors[0].newInstance(s));
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
            factory.add(keyInFactory, f);
        }

    }

    public void shutdown(){
        plugAndPlayService.stopPlugAndPlayService();
        threadPool.shutdown();
        threadPool.awaitTermination();
    }

    /*
         get the request data from connection service and
        run parser and then factory and then execute command
     */
    public void  handleRequest(ByteBuffer jsonString, RespondableChannel resChannel){
        if(null == jsonString || null == resChannel){
            throw new RuntimeException("jsonString or resChannel in RPS.handleRequest are null");
        }
        threadPool.submit(createTask(jsonString, resChannel));
        System.out.println("request processed");
    }

    private Runnable createTask(ByteBuffer jsonByteArray, RespondableChannel resChannel) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try{
                    Map.Entry<String, JsonObject> entry = parser.parse(jsonByteArray);
                    Command command = factory.create(entry.getKey(), entry.getValue());
                    command.execute(resChannel);
                } catch(Exception e){
                    resChannel.respond(ByteBuffer.wrap("{'info': 'Server error in Task', 'statusCode':500}".getBytes()));
                    e.printStackTrace();
                }
                if(true){

                }
            }
        };
        return runnable;
    }
}
