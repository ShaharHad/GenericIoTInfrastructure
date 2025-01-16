package il.co.ilrd.GenericIoTInfrastructure.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import il.co.ilrd.GenericIoTInfrastructure.WebServerAdminDBManager.AdminDBManager;
import il.co.ilrd.GenericIoTInfrastructure.connection_service.RespondableChannel;

import java.nio.ByteBuffer;

public class RegisterCompany implements Command{

    private JsonObject jsonObject;

    public RegisterCompany(JsonObject jsonObject){
        this.jsonObject = jsonObject;
    }

    public void execute(RespondableChannel respondableChannel) {
        AdminDBManager handler = AdminDBManager.getAdminDBManagerInstance();
        JsonObject companyJson = new JsonObject();
        companyJson.addProperty("DB_type", "mongodb");
        companyJson.add("data", jsonObject);
        JsonObject jsonbject = handler.registerCompany(companyJson);

        System.out.println("Register Company - data is: " + jsonbject);

        String respond = new Gson().toJson(jsonbject);

        respondableChannel.respond(ByteBuffer.wrap(respond.getBytes()));
        System.out.println("Server sent response to client");
    }
}
