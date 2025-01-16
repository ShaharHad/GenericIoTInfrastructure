package il.co.ilrd.GenericIoTInfrastructure.GatewayServerAdminDBManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import il.co.ilrd.GenericIoTInfrastructure.WebServerAdminDBManager.AdminDBManager;


import java.util.HashMap;

public class GatewayAdminDBManager implements GatewayDBMSHandler{

    private HashMap<String, GatewayDBMSHandler> map = new HashMap<>();
    private static GatewayAdminDBManager gatewayAdminDBManager;

    private GatewayAdminDBManager(){
        map.put("mysql", new GatewayMysqlHandler());
        map.put("mongodb", new GatewayMongoDBHandler());
    }

    public static GatewayAdminDBManager getGatewayAdminDBManagerInstance(){
        if(null == gatewayAdminDBManager){
            synchronized (AdminDBManager.class){
                if(null == gatewayAdminDBManager){
                    gatewayAdminDBManager = new GatewayAdminDBManager();
                }
            }
        }
        return gatewayAdminDBManager;
    }


    @Override
    public JsonObject registerCompany(JsonObject jsonObject) {
        String dbType = jsonObject.get("DB_type").getAsString();
        GatewayDBMSHandler handler = map.get(dbType);
        if(null == handler){
            return respond(400, new Gson().fromJson("{'info':'Not handler for the db type'}", JsonObject.class));
        }

        return handler.registerCompany(jsonObject.get("data").getAsJsonObject());
    }

    @Override
    public JsonObject registerProduct(JsonObject jsonObject) {
        String dbType = jsonObject.get("DB_type").getAsString();
        GatewayDBMSHandler handler = map.get(dbType);
        if(null == handler){
            return respond(400, new Gson().fromJson("{'info':'Not handler for the db type'}", JsonObject.class));
        }

        return handler.registerProduct(jsonObject.get("data").getAsJsonObject());
    }

    @Override
    public JsonObject registerIoT(JsonObject jsonObject) {
        String dbType = jsonObject.get("DB_type").getAsString();
        GatewayDBMSHandler handler = map.get(dbType);
        if(null == handler){
            return respond(400, new Gson().fromJson("{'info':'Not handler for the db type'}", JsonObject.class));
        }

        return handler.registerIoT(jsonObject.get("data").getAsJsonObject());
    }

    @Override
    public JsonObject iotDeviceUpdate(JsonObject jsonObject) {
        String dbType = jsonObject.get("DB_type").getAsString();
        GatewayDBMSHandler handler = map.get(dbType);
        if(null == handler){
            return respond(400, new Gson().fromJson("{'info':'Not handler for the db type'}", JsonObject.class));
        }

        return handler.iotDeviceUpdate(jsonObject.get("data").getAsJsonObject());
    }

    @Override
    public JsonObject getIotDevices(JsonObject jsonObject) {
        String dbType = jsonObject.get("DB_type").getAsString();
        GatewayDBMSHandler handler = map.get(dbType);
        if(null == handler){
            return respond(400, new Gson().fromJson("{'info':'Not handler for the db type'}", JsonObject.class));
        }

        return handler.getIotDevices(jsonObject.get("data").getAsJsonObject());
    }

    @Override
    public JsonObject getIotUpdates(JsonObject jsonObject) {
        String dbType = jsonObject.get("DB_type").getAsString();
        GatewayDBMSHandler handler = map.get(dbType);
        if(null == handler){
            return respond(400, new Gson().fromJson("{'info':'Not handler for the db type'}", JsonObject.class));
        }

        return handler.getIotUpdates(jsonObject.get("data").getAsJsonObject());
    }

    private JsonObject respond(int statusCode, JsonObject data){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", statusCode);
        jsonObject.add("data", data);
        return jsonObject;
    }
}
