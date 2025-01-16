package Tests;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class TestTCPClient {
    private SocketChannel tcpClient;
    private static final int port = 5000;
    private static final String address = "127.0.0.1";
    private boolean isListen = true;
    private Thread threadForListen;

    public void start() throws IOException {
        tcpClient = SocketChannel.open(new InetSocketAddress(address ,port));
        tcpClient.configureBlocking(false);
        threadForListen = new Thread(() -> {
            while(isListen){

            }
        });
        threadForListen.start();
    }

    public Object read() throws IOException {
        ByteBuffer  buffer = ByteBuffer.allocate(2048);
        int numberOfBytes = 0;

        while((numberOfBytes = tcpClient.read(buffer)) == 0 && isListen){
            Thread.yield();
        }

        if(numberOfBytes == -1 || !isListen){
            return null;
        }
        else{
            buffer.flip();
            byte[] arr = new byte[buffer.remaining()];
            buffer.get(arr);
            String jsonString = new String(arr);
            System.out.println(jsonString);

            return null;
        }

    }

    public void sendMessage(String str) throws IOException {

        ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());

        while(buffer.hasRemaining()){
            tcpClient.write(buffer);
        }
        buffer.clear();
    }

    public void stop(){
        isListen = false;

        try {
            threadForListen.join();
            tcpClient.close();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

class TestTCPClientMain{
    public static void main(String[] args) throws IOException {
        TestTCPClient client = new TestTCPClient();
        client.start();
        String jsonString1 = "{'key': 'RegCompany', 'data': {'Name': 'Shahar', 'Number of products': 5}}";
        client.sendMessage(jsonString1);

//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//        String jsonString2 = "{'key': 'RegisterCompany', 'data': {'Name': 'Hadar', 'Number of products': 1000}}";
//        client.sendMessage(jsonString2);
//
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Client shutdown...");
        client.stop();

    }
}