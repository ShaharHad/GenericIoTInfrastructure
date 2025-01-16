package Tests;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class TestUDPClient {
    private DatagramChannel udpClient;
    private static final int SERVER_PORT = 12346;
    private static final String SERVER_ADDRESS = "10.10.0.168";

    private boolean isListen = true;
    private Thread threadForListen;

    public void start() throws IOException {
        udpClient = DatagramChannel.open();
        udpClient.connect(new InetSocketAddress(InetAddress.getByName(SERVER_ADDRESS), SERVER_PORT));
        udpClient.configureBlocking(false);
        threadForListen = new Thread(() -> {
            while(isListen){


            }
        });
        threadForListen.start();
    }

    public Object read() throws IOException {
        ByteBuffer  buffer = ByteBuffer.allocate(2048);
        int numberOfBytes = 0;

        while((numberOfBytes = udpClient.read(buffer)) == 0 && isListen){
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
        InetSocketAddress socketAddress = new InetSocketAddress(SERVER_ADDRESS, SERVER_PORT); // for the Request object
        ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());

        while(buffer.hasRemaining()){
            udpClient.send(buffer, socketAddress);
        }
        buffer.clear();
    }

    public void stop(){
        isListen = false;

        try {
            threadForListen.join();
            udpClient.close();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

class TestUDPClientMain{
    public static void main(String[] args) throws IOException {
        TestUDPClient client = new TestUDPClient();
        client.start();

        String jsonString2 = "{'key': 'RegisterCompany', 'data': {'Name': 'Hadar', 'Number of products': 1000}}";
        client.sendMessage(jsonString2);

//        JsonObject jo1 = new JsonObject();
//        jo1.addProperty("Name", "Shahar");
//        jo1.addProperty("Number of products", 5);
//        Request req1 = new Request(jo1, "RegisterCompany");
//        client.sendMessage(req1);
//        System.out.println("Client send message");
//
//
//        JsonObject jo2 = new JsonObject();
//        jo2.addProperty("Name", "Hadar");
//        jo2.addProperty("Number of products", 1000);
//        Request req2 = new Request(jo2, "RegisterCompany");
//        client.sendMessage(req2);
//        System.out.println("Client send message");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Client shutdown...");
        client.stop();
    }
}
