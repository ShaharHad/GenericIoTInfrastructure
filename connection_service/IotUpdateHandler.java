package connection_service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import il.co.ilrd.GenericIoTInfrastructure.RPS;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.function.Consumer;

class IotUpdateHandler implements HttpHandler, RespondableChannel {
    private HttpExchange httpExchange;
    private RPS rps;
    private HashMap<String, Consumer<HttpExchange>> map;

    public IotUpdateHandler(RPS rps) {
        if(null == rps){
            throw new RuntimeException("httpExchange in IotHandler ctor is rps");
        }
        this.rps = rps;
        initApiProductHandler();
    }

    private void initApiProductHandler(){
        map = new HashMap<>();
        // all methods in map have same signature as run method in runnable interface, so java implement
        // the runnable with  each method
        map.put("GET", this::handleGetRequest);
        map.put("POST", this::handlePostRequest);
        map.put("PUT", this::handlePutRequest);
        map.put("DELETE", this::handleDeleteRequest);
    }

    public void handle(HttpExchange httpExchange){
        if(null == httpExchange){
            throw new RuntimeException("httpExchange in IotUpdateHandler.handle is null");
        }
        this.httpExchange = httpExchange;
        String requestMethod = httpExchange.getRequestMethod();
        Consumer<HttpExchange> consumer = map.get(requestMethod);
        if(null != consumer){
            consumer.accept(httpExchange);
        }
        else{
            System.out.println("Not supported request method");
            handleUnsupportedMethodRequest();
        }
    }

    private void handlePostRequest(HttpExchange httpExchange){
        System.out.println("IotUpdate POST request");
        try{
            MethodReqAndResHandler.handlePostRequest(rps, httpExchange, this, "UpdateIOT");

        } catch (IOException e) {
            respond(ByteBuffer.wrap("{'info': 'Cannot read Json format from string', 'statusCode': 400}".getBytes()));
        } catch (IllegalStateException e) {
            handleUnsupportedMethodRequest();
        }
        catch (RuntimeException e) {
            respond(ByteBuffer.wrap("{'info': 'Server error', 'statusCode': 500}".getBytes()));
        }

    }

    private void handleGetRequest(HttpExchange httpExchange){
        System.out.println("IotUpdate GET request");
        MethodReqAndResHandler.handleGetRequest(rps, httpExchange, this, "GetIOTUpdate", 3);

    }

    private void handlePutRequest(HttpExchange httpExchange){
        System.out.println(" IOTUpdate PUT Not implement yet");
    }

    private void handleDeleteRequest(HttpExchange httpExchange){
        System.out.println(" IOTUpdate DELETE Not implement yet");
    }

    private void handleUnsupportedMethodRequest(){
        respond(ByteBuffer.wrap("data is not in json format".getBytes()));
    }

    @Override
    public void respond(ByteBuffer bytes) {
        if(null == bytes){
            MethodReqAndResHandler.respond(httpExchange, this,
                    ByteBuffer.wrap("{'info': 'Server Error', 'statusCode': 500}".getBytes()));
        }
        else{
            MethodReqAndResHandler.respond(httpExchange, this, bytes);
        }
    }
}
