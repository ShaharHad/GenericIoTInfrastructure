package commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import il.co.ilrd.GenericIoTInfrastructure.GatewayServerAdminDBManager.GatewayAdminDBManager;
import il.co.ilrd.GenericIoTInfrastructure.connection_service.RespondableChannel;

import java.nio.ByteBuffer;

public class RegisterIOT implements Command{
    private JsonObject jsonObject;

    public RegisterIOT(JsonObject jsonObject){
        this.jsonObject = jsonObject;
    }

    public void execute(RespondableChannel respondableChannel) {

        GatewayAdminDBManager handler = GatewayAdminDBManager.getGatewayAdminDBManagerInstance();
        JsonObject companyJson = new JsonObject();
        companyJson.addProperty("DB_type", "mongodb");
        companyJson.add("data", jsonObject);
        JsonObject jsonbject = handler.registerIoT(companyJson);

        System.out.println("Register Product - data is: " + jsonbject);

        String respond = new Gson().toJson(jsonbject);

        respondableChannel.respond(ByteBuffer.wrap(respond.getBytes()));
        System.out.println("Server sent response to client");
    }
}
