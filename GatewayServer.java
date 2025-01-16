package il.co.ilrd.GenericIoTInfrastructure;

import il.co.ilrd.GenericIoTInfrastructure.connection_service.ConnectionService;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class GatewayServer {

    private RPS rps;
    private ConnectionService connectionService;
    private final static int UDP_PORT = 6000;
    private final static int TCP_PORT = 5000;
    private final static int HTTP_PORT = 4000;


    public GatewayServer() throws UnknownHostException {
        rps = new RPS(new ParserIMP());
        connectionService = new ConnectionService(rps);
    }

    public int handleRequest(){
        try {
            connectionService.addTCP(TCP_PORT, InetAddress.getByName("127.0.0.1"));
            connectionService.addUDP(UDP_PORT, InetAddress.getByName("127.0.0.1"));
            connectionService.addHTTP(HTTP_PORT, "10.1.0.15");
            connectionService.start();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

    public void shutdown(){
        connectionService.stop();
        rps.shutdown();
    }
}

class GatewayServerMain{
    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        GatewayServer server = new GatewayServer();
        System.out.println("Server start running");
        server.handleRequest();

//        Thread.sleep(8000);
//
//        System.out.println("Server shutdown...");
//        server.shutdown();

    }
}
