package il.co.ilrd.GenericIoTInfrastructure.WebServerAdminDBManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;

public class AdminDBManager {
    private HashMap<String, DBMSHandler> map = new HashMap<>();
    private static AdminDBManager adminDBManager;

    private AdminDBManager(){
        map.put("mysql", new MysqlHandler());
        map.put("mongodb", new MongoDBHandler());
    }

    public static AdminDBManager getAdminDBManagerInstance(){
        if(null == adminDBManager){
            synchronized (AdminDBManager.class){
                if(null == adminDBManager){
                    adminDBManager = new AdminDBManager();
                }
            }
        }
        return adminDBManager;
    }

    public JsonObject registerCompany(JsonObject jsonObject){
        String dbType = (jsonObject.get("DB_type").getAsString());
        DBMSHandler handler = map.get(dbType);
        if(null == handler){

            return respond(400, new Gson().fromJson("{'info':'Not handler for the db type'}", JsonObject.class));
        }

        return handler.registerCompany(jsonObject.get("data").getAsJsonObject());
    }

    public JsonObject registerProduct(JsonObject jsonObject){
        String dbType = (jsonObject.get("DB_type").getAsString());
        DBMSHandler handler = map.get(dbType);
        if(null == handler){
            return respond(400, new Gson().fromJson("{'info':'Not handler for the db type'}", JsonObject.class));
        }

        return handler.registerProduct(jsonObject.get("data").getAsJsonObject());
    }

    public JsonObject getCompany(JsonObject jsonObject){
        String dbType = (jsonObject.get("DB_type").getAsString());
        DBMSHandler handler = map.get(dbType);
        if(null == handler){
            return respond(400, new Gson().fromJson("{'info':'Not handler for the db type'}", JsonObject.class));
        }

        return handler.getCompany(jsonObject.get("data").getAsJsonObject());
    }

    public JsonObject getCompanies(JsonObject jsonObject){
        String dbType = (jsonObject.get("DB_type").getAsString());
        DBMSHandler handler = map.get(dbType);
        if(null == handler){
            return respond(400, new Gson().fromJson("{'info':'Not handler for the db type'}", JsonObject.class));
        }

        return handler.getCompanies(null);
    }

    public JsonObject getProduct(JsonObject jsonObject){
        String dbType = (jsonObject.get("DB_type").getAsString());
        DBMSHandler handler = map.get(dbType);
        if(null == handler){
            return respond(400, new Gson().fromJson("{'info':'Not handler for the db type'}", JsonObject.class));
        }

        return handler.getProduct(jsonObject.get("data").getAsJsonObject());
    }

    public JsonObject getProducts(JsonObject jsonObject){
        String dbType = (jsonObject.get("DB_type").getAsString());
        DBMSHandler handler = map.get(dbType);
        if(null == handler){
            return respond(400, new Gson().fromJson("{'info':'Not handler for the db type'}", JsonObject.class));
        }

        return handler.getProducts(null);
    }

    private JsonObject respond(int statusCode, JsonObject data){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", statusCode);
        jsonObject.add("data", data);
        return jsonObject;
    }
}
