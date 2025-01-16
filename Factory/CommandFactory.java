package il.co.ilrd.GenericIoTInfrastructure.Factory;

import java.util.HashMap;
import java.util.function.Function;

public class CommandFactory<K, T, D>{
    private HashMap<K, Function<D, ? extends T>> map = new HashMap<>();

    public void add(K key, Function<D, ? extends T> func) {
        map.put(key, func);
    }

    public T create(K key, D args) {

        return map.get(key).apply(args);
    }
}
