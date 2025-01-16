package commands;

import com.google.gson.JsonObject;
import il.co.ilrd.GenericIoTInfrastructure.connection_service.RespondableChannel;

import java.nio.ByteBuffer;

public class GetCompanies implements Command{

    public GetCompanies(JsonObject jsonObject){

    }

    public void execute(RespondableChannel respondableChannel) {
        System.out.println("Get Companies");

        String respond = "{'status': true, 'info': 'Server send companies', 'statusCode': 200}";

        respondableChannel.respond(ByteBuffer.wrap(respond.getBytes()));
        System.out.println("Server sent response to client");
    }
}
