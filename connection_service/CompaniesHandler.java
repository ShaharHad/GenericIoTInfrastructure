package il.co.ilrd.GenericIoTInfrastructure.connection_service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import il.co.ilrd.GenericIoTInfrastructure.RPS;

import java.nio.ByteBuffer;
import java.util.HashMap;

class CompaniesHandler implements HttpHandler, RespondableChannel {
    private HttpExchange httpExchange;
    private RPS rps;
    private HashMap<String, Runnable> requestMethodMap;

    public CompaniesHandler(RPS rps){
        if(null == rps){
            throw new RuntimeException("httpExchange in ApiCompanyHandler ctor is rps");
        }
        this.rps = rps;
        initCompaniesHandler();
    }

    private void initCompaniesHandler(){
        requestMethodMap = new HashMap<>();
        // all methods in map have same signature as run method in runnable interface, so java implement
        // the runnable with each method
        requestMethodMap.put("GET", this::handleGetRequest);
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

    private void handleGetRequest(){

        System.out.println("Companies GET request");
        MethodReqAndResHandler.handleGetRequest(rps, httpExchange, this, "GetCompanies", 0);

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
