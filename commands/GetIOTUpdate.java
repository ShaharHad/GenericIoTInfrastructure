package commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import il.co.ilrd.GenericIoTInfrastructure.GatewayServerAdminDBManager.GatewayAdminDBManager;
import il.co.ilrd.GenericIoTInfrastructure.connection_service.RespondableChannel;

import java.nio.ByteBuffer;

public class GetIOTUpdate implements Command{
    private JsonObject jsonObject;

    public GetIOTUpdate(JsonObject jsonObject){
        this.jsonObject = jsonObject;
    }

    public void execute(RespondableChannel respondableChannel) {
        GatewayAdminDBManager handler = GatewayAdminDBManager.getGatewayAdminDBManagerInstance();
        JsonObject getIotUpdatesJson = new JsonObject();
        getIotUpdatesJson.addProperty("DB_type", "mongodb");
        getIotUpdatesJson.add("data", jsonObject);
        JsonObject jsonbject = handler.getIotUpdates(getIotUpdatesJson);

        System.out.println("Get Iot Updates - data is: " + jsonbject);

        String respond = new Gson().toJson(jsonbject);

        respondableChannel.respond(ByteBuffer.wrap(respond.getBytes()));
        System.out.println("Server sent response to client");
    }
}
