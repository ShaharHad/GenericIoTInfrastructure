package plug_and_play_service.DirectoryMonitor.Observer;

public class Publisher<T> {

    private Dispatcher<T> dispatcher = new Dispatcher<>();

    public Publisher() {}

    public void register(Callback<T> cb) {
        dispatcher.register(cb);
    }
    public void produce(T data) {
        dispatcher.updateAll(data);
    }

    public void close() {
        dispatcher.stopService();
    }
}
