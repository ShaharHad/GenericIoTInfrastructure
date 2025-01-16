package il.co.ilrd.GenericIoTInfrastructure.connection_service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import il.co.ilrd.GenericIoTInfrastructure.RPS;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

class ProductHandler implements RespondableChannel, HttpHandler {

    private HttpExchange httpExchange;
    private RPS rps;
    private HashMap<String, Runnable> map;

    public ProductHandler(RPS rps){
        if(null == rps){
            throw new RuntimeException("httpExchange in ApiProductHandler ctor is rps");
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
            throw new RuntimeException("httpExchange in ApiProductHandler.handle is null");
        }

        this.httpExchange = httpExchange;
        String requestMethod = httpExchange.getRequestMethod();
        Runnable runnable = map.get(requestMethod);
        if(null != runnable){
            runnable.run();
        }
        else{
            System.out.println("Not supported request method");
            handleUnsupportedMethodRequest();
        }
    }

    private void handlePostRequest(){
        System.out.println("Product POST request");
        try{
            MethodReqAndResHandler.handlePostRequest(rps, httpExchange, this, "RegisterProduct");

        } catch (IOException e) {
            respond(ByteBuffer.wrap("{'info': 'Cannot read Json format from string', 'statusCode': 400}".getBytes()));
            throw new RuntimeException("Cannot read Json format from string. Error: " + e.getMessage());
        } catch (IllegalStateException e) {
            handleUnsupportedMethodRequest();
            throw new RuntimeException("data is not in json format");
        }
        catch (RuntimeException e) {
            respond(ByteBuffer.wrap("{'info': 'Server error', 'statusCode': 500}".getBytes()));
            throw new RuntimeException(e);
        }

    }

    private void handleGetRequest(){
        System.out.println("Product GET request");
        MethodReqAndResHandler.handleGetRequest(rps, httpExchange, this, "GetProduct", 2);

    }

    private void handlePutRequest(){
        System.out.println("ProductHandler PUT not implemented");
    }

    private void handleDeleteRequest(){
        System.out.println("ProductHandler DELETE not implemented");
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
