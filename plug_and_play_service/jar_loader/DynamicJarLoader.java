package plug_and_play_service.jar_loader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class DynamicJarLoader {
    String pathToJar;
    String interfaceName;

    public DynamicJarLoader(String interfaceName, String pathToJar){
        this.pathToJar = pathToJar;
        this.interfaceName = interfaceName;
    }

    public Set<Class<?>> load() {

        Set<Class<?>> set = new HashSet<Class<?>>() {
        };

        URL[] urls; // need array of urls because newInstance method in URLClassLoader required array of urls
        try {
            urls = new URL[]{ new URL("jar:file:" + pathToJar+"!/") }; //
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        try (JarFile jerFile = new JarFile(pathToJar);  URLClassLoader urlClaassLoader = new URLClassLoader(urls)){
            Enumeration<JarEntry> listOfJrEntry = jerFile.entries();
            while(listOfJrEntry.hasMoreElements()){
                JarEntry jerEntry = listOfJrEntry.nextElement();
                if(!jerEntry.isDirectory() && jerEntry.getName().endsWith(".class")){
                    String className = jerEntry.getName().substring(0,jerEntry.getName().length()-6);
                    className = className.replace('/', '.');
                    Class<?> cl = urlClaassLoader.loadClass(className); // load classes from jar

                    if(isClassSubImplementOfInterface(cl)){
                        set.add(cl);
                    }
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return set;
    }

    private boolean isClassSubImplementOfInterface(Class<?> cl){
        while(null != cl){
            for(Class<?> interfaceClass: cl.getInterfaces()){
                int startIndex = interfaceClass.toString().length() - interfaceName.length();
                if(interfaceClass.toString().substring(startIndex).equals(interfaceName)){
                    return true;
                }
            }
            cl = cl.getSuperclass();
        }

        return false;
    }
}
