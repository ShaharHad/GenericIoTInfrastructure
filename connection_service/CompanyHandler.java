package connection_service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import il.co.ilrd.GenericIoTInfrastructure.RPS;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

class CompanyHandler implements RespondableChannel, HttpHandler {

    private HttpExchange httpExchange;
    private RPS rps;
    private HashMap<String, Runnable> requestMethodMap;

    public CompanyHandler(RPS rps){
        if(null == rps){
            throw new RuntimeException("httpExchange in ApiCompanyHandler ctor is rps");
        }
        this.rps = rps;
        initApiCompanyHandler();
    }

    private void initApiCompanyHandler(){
        requestMethodMap = new HashMap<>();
        // all methods in map have same signature as run method in runnable interface, so java implement
        // the runnable with each method
        requestMethodMap.put("GET", this::handleGetRequest);
        requestMethodMap.put("POST", this::handlePostRequest);
        requestMethodMap.put("PUT", this::handlePutRequest);
        requestMethodMap.put("DELETE", this::handleDeleteRequest);
    }

    public void handle(HttpExchange httpExchange){
        if(null == httpExchange){
            throw new RuntimeException("httpExchange in ApiCompanyUrlHandler.handle is null");
        }
        this.httpExchange = httpExchange;

        String requestMethod = httpExchange.getRequestMethod();
        Runnable runnable = requestMethodMap.get(requestMethod);
        if(null != runnable){
            runnable.run();
        }
        else{
            System.out.println("Not supported request method");
            handleUnsupportedMethodRequest();
        }
    }


    private void handlePostRequest(){
        System.out.println("Company POST request");
        try{
            MethodReqAndResHandler.handlePostRequest(rps, httpExchange, this, "RegisterCompany");
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

        MethodReqAndResHandler.handleGetRequest(rps, httpExchange, this, "GetCompany", 1);

    }

    private void handlePutRequest(){
        System.out.println("CompanyHandler PUT not implemented");
    }

    private void handleDeleteRequest(){
        System.out.println("CompanyHandler DELETE not implemented");
    }

    private void handleUnsupportedMethodRequest(){
        respond(ByteBuffer.wrap("{'info': 'Not Support request method', 'statusCode': 400}".getBytes()));
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
