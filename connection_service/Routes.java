package connection_service;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import il.co.ilrd.GenericIoTInfrastructure.RPS;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

class Routes implements HttpHandler {

    private RouteUrlGroup urlBegin;
    private RPS rps;

    public Routes(RPS rps){
        this.rps = rps;
        initRoutes();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String url = String.valueOf(httpExchange.getRequestURI());
        String urlWithoutID = url.replaceAll("\\d+", "{id}");
        urlWithoutID = urlWithoutID.replaceAll("/$", "");
        try{
            urlBegin.handleRequest(httpExchange, urlWithoutID);
        }
        catch(RuntimeException e){
            throw new RuntimeException(e);
        }


    }

    void initRoutes(){

        urlBegin = new RouteUrlGroup("/");
        RouteUrlGroup companyUrl = new RouteUrlGroup("/company");
        RouteUrlGroup productUrl = new RouteUrlGroup("/company/{id}/product");
        RouteUrlGroup iotUrl = new RouteUrlGroup("/company/{id}/product/{id}/iot");
        RouteUrlGroup iotUpdateUrl = new RouteUrlGroup("/company/{id}/product/{id}/iot/{id}/update");

        urlBegin.addRoute(companyUrl);
        companyUrl.addRoute(productUrl);
        productUrl.addRoute(iotUrl);
        iotUrl.addRoute(iotUpdateUrl);

        companyUrl.addRoute(new Endpoint("/company", CompanyHandler::new));
        productUrl.addRoute(new Endpoint("/company/{id}/product", ProductHandler::new));
        iotUrl.addRoute(new Endpoint("/company/{id}/product/{id}/iot", IotHandler::new));
        iotUpdateUrl.addRoute(new Endpoint("/company/{id}/product/{id}/iot/{id}/update", IotUpdateHandler::new));


        RouteUrlGroup companiesUrl = new RouteUrlGroup("/companies");
        urlBegin.addRoute(companiesUrl);
        companiesUrl.addRoute(new Endpoint("/companies", CompaniesHandler::new));
    }

    // interface composite
    interface RouteUrl{
        boolean handleRequest(HttpExchange httpExchange, String url);
    }

    // composite leaf
    class Endpoint implements RouteUrl{

        private String str;
        private Function<RPS, HttpHandler> callback;


        public Endpoint(String str, Function<RPS, HttpHandler> callback){
            this.str = str;
            this.callback = callback;
        }

        @Override
        public boolean handleRequest(HttpExchange httpExchange,String url) {

            if(!url.equals(str) && !url.equals(str + "/{id}")){
                return false;
            }

            try {
                // create new handler which get the HttpExchange
                this.callback.apply(rps).handle(httpExchange);
                return true;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // composite component
    class RouteUrlGroup implements RouteUrl{

        private String path;
        private List<RouteUrl> routes = new ArrayList<>();

        public RouteUrlGroup(String path){
            this.path = path;
        }

        public void addRoute( RouteUrl routeUrl){
            routes.add(routeUrl);
        }

        @Override
        public boolean handleRequest(HttpExchange httpExchange, String url) {
            if(!url.startsWith(path)){
                return false;
            }
            for(RouteUrl route: routes){
                //if URL valid we will not continue to check other urls
                if(route.handleRequest(httpExchange, url)){
                    return true;
                }
            }
            //sent error of url not valid if not found any handler for the url
            if(path.equals("/")){
                try {
                    ByteBuffer msg = ByteBuffer.wrap( "{'info': 'Not valid URL'}".getBytes());
                    Headers responseHeader = httpExchange.getResponseHeaders();
                    responseHeader.add("Content-Type", "application/json; charset=UTF-8");

                    httpExchange.sendResponseHeaders(400, msg.array().length);
                    OutputStream responseStream = httpExchange.getResponseBody();

                    responseStream.write(msg.array());
                    responseStream.flush();
                    responseStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return false;
        }
    }
}




