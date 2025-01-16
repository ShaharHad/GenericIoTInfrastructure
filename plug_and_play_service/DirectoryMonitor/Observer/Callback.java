package il.co.ilrd.GenericIoTInfrastructure.plug_and_play_service.DirectoryMonitor.Observer;

import java.util.function.Consumer;

public class Callback<T> {

    private Consumer<T> consumer;
    Runnable runnableStopUpdate;
    private Dispatcher<T> dispatcher = new Dispatcher<>();

    public Callback(Consumer<T> consumer, Runnable stopUpdateRunnable) {
        this.runnableStopUpdate = stopUpdateRunnable;
        this.consumer = consumer;
    }

    public void update(T data) {
        consumer.accept(data);
    }

    public void stopUpdate() {
        if(dispatcher != null){
            dispatcher.stopService();
            setDispatcher(null);
        }

    }

    public void setDispatcher(Dispatcher<T> dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void unregister() {
        dispatcher.unregister(this);
    }
}
