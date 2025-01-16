package connection_service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import il.co.ilrd.GenericIoTInfrastructure.RPS;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

class MethodReqAndResHandler {

    private static final Gson GSON = new Gson();

    static void handlePostRequest(RPS rps, HttpExchange httpExchange,
                                  RespondableChannel resChannel, String CommandName) throws IOException {
        try(InputStream inputStream = httpExchange.getRequestBody()){
            JsonObject dataJsonObject = JsonParser.parseReader(new InputStreamReader(inputStream,
                                                                StandardCharsets.UTF_8)).getAsJsonObject();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("Key", CommandName);
            jsonObject.add("Data", dataJsonObject);

            rps.handleRequest(ByteBuffer.wrap(GSON.toJson(jsonObject).getBytes()), resChannel);
        }
    }

    static void handleGetRequest(RPS rps, HttpExchange httpExchange,
                                 RespondableChannel resChannel, String CommandName, int numOfPathParam){
        String requestURL = String.valueOf(httpExchange.getRequestURI());
        String[] requestParts = requestURL.split("/");

        JsonObject jsonObject = new JsonObject();
        JsonObject dataJsonObject = new JsonObject();
        for(int i = 0; i < numOfPathParam; ++i){
            // get the path parameters as key - value
            dataJsonObject.addProperty(requestParts[i * 2 + 1], requestParts[i * 2 + 2]);
        }

        jsonObject.addProperty("Key", CommandName);
        jsonObject.add("Data", dataJsonObject);

        rps.handleRequest(ByteBuffer.wrap(GSON.toJson(jsonObject).getBytes()), resChannel);

    }

    static void handlePutRequest(RPS rps, HttpExchange httpExchange, RespondableChannel resChannel){
        System.out.println("Not implemented yet");
    }

    static void handleDeleteRequest(RPS rps, HttpExchange httpExchange, RespondableChannel resChannel){
        System.out.println("Not implemented yet");
    }

    static void respond(HttpExchange httpExchange, RespondableChannel resChannel, ByteBuffer bytes){
//// extract the status code from bytes and then create new bytes without the status code
        String jsonStringFromByteArray = new String(bytes.array(), StandardCharsets.UTF_8);
        JsonObject jsonObject = new Gson().fromJson(jsonStringFromByteArray, JsonObject.class);
        int statusCode = jsonObject.get("statusCode").getAsInt();
        bytes.clear();
        jsonObject.remove("statusCode");
        bytes = ByteBuffer.wrap(new Gson().toJson(jsonObject).getBytes());
        //////////////////////////////////////////////////////////////////////////////

        Headers responseHeader = httpExchange.getResponseHeaders();
        responseHeader.add("Content-Type", "application/json; charset=UTF-8");

        try {
            httpExchange.sendResponseHeaders(statusCode, bytes.arrayOffset());
            OutputStream responseStream = httpExchange.getResponseBody();

            responseStream.write(bytes.array());
            responseStream.flush();
            responseStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
