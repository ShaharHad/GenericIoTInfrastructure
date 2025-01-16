package il.co.ilrd.GenericIoTInfrastructure.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import il.co.ilrd.GenericIoTInfrastructure.GatewayServerAdminDBManager.GatewayAdminDBManager;
import il.co.ilrd.GenericIoTInfrastructure.WebServerAdminDBManager.AdminDBManager;
import il.co.ilrd.GenericIoTInfrastructure.connection_service.RespondableChannel;

import java.nio.ByteBuffer;

public class GetIOT implements Command{
    private JsonObject jsonObject;

    public GetIOT(JsonObject jsonObject){
        this.jsonObject = jsonObject;
    }

    public void execute(RespondableChannel respondableChannel) {
        GatewayAdminDBManager handler = GatewayAdminDBManager.getGatewayAdminDBManagerInstance();
        JsonObject getIotsDevicesJson = new JsonObject();
        getIotsDevicesJson.addProperty("DB_type", "mongodb");
        getIotsDevicesJson.add("data", jsonObject);
        JsonObject jsonbject = handler.getIotDevices(getIotsDevicesJson);

        System.out.println("Get Iots Devices - data is: " + jsonbject);

        String respond = new Gson().toJson(jsonbject);

        respondableChannel.respond(ByteBuffer.wrap(respond.getBytes()));
        System.out.println("Server sent response to client");
    }
}
