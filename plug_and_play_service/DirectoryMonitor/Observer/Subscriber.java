package il.co.ilrd.GenericIoTInfrastructure.plug_and_play_service.DirectoryMonitor.Observer;

import java.util.function.Consumer;

public class Subscriber<T> {
    private Callback<T> callback;
    private T data;


    public Subscriber(Consumer<T> c, Runnable runnable) {
        callback = new Callback<>(c, runnable);
    }

    public void register(Publisher<T> publisher) {
        publisher.register(callback);
    }
    public void unregister() {
        callback.unregister();
    }
    public T getData() {
        return data;
    }
}