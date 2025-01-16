package il.co.ilrd.GenericIoTInfrastructure.plug_and_play_service.DirectoryMonitor.Observer;

import java.util.ArrayList;
import java.util.List;

public class Dispatcher<T> {

    private List<Callback<T>> listOfCallback = new ArrayList<>();

    public Dispatcher() {

    }

    public void register(Callback<T> cb) {
        listOfCallback.add(cb);
    }
    public void unregister(Callback<T> cb) {
        listOfCallback.remove(cb);

    } //maybe receive Object
    public void updateAll(T data) {
        for(Callback<T> cb: listOfCallback){
            cb.update(data);
        }
    }

    public void stopService() {
        for(Callback<T> cb: listOfCallback){
            cb.runnableStopUpdate.run();
        }

        listOfCallback.clear();
    }
}