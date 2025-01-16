package connection_service;

import RPS;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class ConnectionService {
    private RPS rps;
    private Selector selector;
    private boolean running = true;
    private Thread threadRunSelector;
    private HttpService httpService;


    public ConnectionService(RPS rps){
        if(null == rps){
            throw new NullPointerException("rps is null in ConnectionService");
        }
        this.rps = rps;
        try {
            selector  = Selector.open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void start(){
        threadRunSelector = new Thread(this::runThread);
        threadRunSelector.start();
        addEndpoints();
        httpService.start();
    }

    private void addEndpoints(){
        Routes routes = new Routes(rps);
        httpService.addContext("/company", routes);
        httpService.addContext("/companies", routes);
    }

    private void runThread(){
        try {
            while(running){
                int numberOfKeys = selector.select();
                if(numberOfKeys > 0){
                    Set<SelectionKey> selectedKey = selector.selectedKeys();
                    Iterator<SelectionKey> iter = selectedKey.iterator();
                    while(iter.hasNext()){
                        SelectionKey key = iter.next();
                        ((ChannelHandler)key.attachment()).handle(key.channel());
                        iter.remove();
                    }

                }
            }
        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    public void stop(){
        running = false;
        selector.wakeup();
        try {
            threadRunSelector.join();
            for(SelectionKey key: selector.keys()){
                key.channel().close();
            }
            selector.close();
            httpService.stop();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addTCP(int port, InetAddress ip){
        try {
            ServerSocketChannel tcpServer = ServerSocketChannel.open();
            tcpServer.bind(new InetSocketAddress(ip, port));
            tcpServer.configureBlocking(false);
            tcpServer.register(selector, SelectionKey.OP_ACCEPT, new TCPAcceptor());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addUDP(int port, InetAddress ip){
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(ip, port);
            DatagramChannel udpServer = DatagramChannel.open();
            udpServer.bind(socketAddress);
            udpServer.configureBlocking(false);
            udpServer.register(selector, SelectionKey.OP_READ, new UDPChannel(udpServer, socketAddress));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addHTTP(int port, String ip){
        try {
            httpService = new HttpService(ip, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /////////////////////////////////////  TCP request connection handler   //////////////////////////////////////
    private class TCPAcceptor implements ChannelHandler{

        @Override
        public void handle(SelectableChannel channel) {
            try {
                SocketChannel client = ((ServerSocketChannel)channel).accept();
                client.configureBlocking(false);
                client.register(selector, SelectionKey.OP_READ, new TCPChannel());
                System.out.println("new client connection established" +client);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    //////////////////////////////////  TCP request handler   ///////////////////////////////////////////////////
    private class TCPChannel implements ChannelHandler, RespondableChannel{

        private SocketChannel clientChannel;

        @Override
        public void handle(SelectableChannel channel) {
            ByteBuffer buffer = ByteBuffer.allocate(2048);
            clientChannel = (SocketChannel)channel;
            int numOfBytes = 0;
            try{
                do{
                    numOfBytes = clientChannel.read(buffer);
                }while (numOfBytes > 0);
            }


            catch (IOException e){
                System.out.println("Client closed channel");
                try {
                    clientChannel.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            if(numOfBytes == -1){
                try {
                    clientChannel.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
                rps.handleRequest(buffer, this);
                System.out.println("read from client on TCP protocol");

        }

        @Override
        public void respond(ByteBuffer bytes) {
            while(bytes.hasRemaining()){
                try {
                    clientChannel.write(bytes);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            bytes.clear();
        }
    }

    //////////////////////////////////  UDP request handler   ///////////////////////////////////////////////////
    private class UDPChannel implements ChannelHandler, RespondableChannel{

        private InetSocketAddress address;
        private DatagramChannel udpChannel;

        public UDPChannel(DatagramChannel channel, InetSocketAddress address){
            this.udpChannel = channel;
            this.address = address;
        }

        @Override
        public void handle(SelectableChannel channel) {
            InetSocketAddress clientAddress = null;
            ByteBuffer buffer = ByteBuffer.allocate(2048);
            buffer.clear();
            udpChannel = (DatagramChannel)channel;
            try {
                clientAddress = (InetSocketAddress)udpChannel.receive(buffer);// receive return the client address
                if(clientAddress == null){
                    return;
                }
                rps.handleRequest(buffer, new UDPChannel(udpChannel, clientAddress));
                System.out.println("read from client on UDP protocol");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void respond(ByteBuffer bytes) {
            while(bytes.hasRemaining()){

                try {
                    udpChannel.send(bytes, address);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}









