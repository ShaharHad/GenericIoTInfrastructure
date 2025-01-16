package commands;

import com.google.gson.JsonObject;
import il.co.ilrd.GenericIoTInfrastructure.connection_service.RespondableChannel;

import java.nio.ByteBuffer;

public class GetProduct implements Command{
    private JsonObject jsonObject;

    public GetProduct(JsonObject jsonObject){
        this.jsonObject = jsonObject;
    }

    public void execute(RespondableChannel respondableChannel) {
        System.out.println("Get Product - data is: " + jsonObject);

        String respond = "{'status': true, 'info': 'Server success return product', 'statusCode': 200}";

        respondableChannel.respond(ByteBuffer.wrap(respond.getBytes()));
        System.out.println("Server sent response to client");
    }
}
