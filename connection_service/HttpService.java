package il.co.ilrd.GenericIoTInfrastructure.connection_service;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

class HttpService {

    private HttpServer httpListener;

    public HttpService(String ip, int port) throws IOException {
        httpListener = HttpServer.create();
        httpListener.bind(new InetSocketAddress(InetAddress.getByName(ip), port), 0);
    }

    public void start(){
        httpListener.setExecutor(null);
        httpListener.start();
    }

    public void stop(){
        httpListener.stop(0);
    }

    public void addContext(String path, HttpHandler httpHandler){
        httpListener.createContext(path, httpHandler);
    }
}


