package il.co.ilrd.GenericIoTInfrastructure.plug_and_play_service.jar_loader;

import java.util.Arrays;
import java.util.Set;

public class Tests {
    public static void main(String[] args) {
//        URLClassLoader urlClassLoader = new URLClassLoader();
        DynamicJarLoader loader = new DynamicJarLoader("Pizza", "/home/shahar/git/fs/il/co/ILRD/JarFile/Ex3/jar_file.jar");
        Set<Class<?>> list = loader.load();

        for(Class<?> cl: list){
            System.out.println(Arrays.toString(cl.getDeclaredMethods()));

        }
    }
}
