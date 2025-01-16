package il.co.ilrd.GenericIoTInfrastructure.GatewayServerAdminDBManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.MergeOptions;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Aggregates.merge;
import static java.util.Arrays.asList;

public class GatewayMongoDBHandler implements GatewayDBMSHandler{

    private final String MONGODB_CONNECTION_URL = "mongodb+srv://shahar:cjOgfRwiPM7mu282@cluster0.k66nm.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
    private MongoClient mongoClient;

    public GatewayMongoDBHandler() {
        mongoClient = MongoClients.create(MONGODB_CONNECTION_URL);
    }

    @Override
    public JsonObject registerCompany(JsonObject jsonObject) {
        //        String companyName = jsonObject.get("name").getAsString();
//        int companyID = jsonObject.get("company_id").getAsInt();
//
//        MongoDatabase db = mongoClient.getDatabase("Company" + companyID);
//        MongoCollection<Document> collection = db.getCollection("Products");
//        Document doc = new Document("products", new ArrayList<>());
//        doc.append("company_id", companyID);
//        doc.append("company_name", companyName);
//        collection.insertOne(doc);
//
//        return respond(200, new Gson().fromJson("{'info': 'Success Register company'}", JsonObject.class));
        return null;
    }

    @Override
    public JsonObject registerProduct(JsonObject jsonObject) {
        String productName = jsonObject.get("product_name").getAsString();
        int companyID = jsonObject.get("company_id").getAsInt();
        int productID = jsonObject.get("product_id").getAsInt();

        MongoDatabase db = mongoClient.getDatabase("Company" + companyID);
        MongoCollection<Document> collection = db.getCollection("Products");
        Document doc = new Document("_id", productID);
        doc.append("product_name", productName );

        try{
            collection.insertOne(doc);
        } catch (MongoException e){
            if(11000 == e.getCode()){
                return respond(400, new Gson().fromJson("{'info': 'product id already exist'}", JsonObject.class));
            }
            return respond(500, new Gson().fromJson("{'info': 'Server fail'}", JsonObject.class));
        }

        return respond(200, new Gson().fromJson("{'info': 'Success Register product'}", JsonObject.class));
    }

    @Override
    public JsonObject registerIoT(JsonObject jsonObject) {

        int companyID = jsonObject.get("company_id").getAsInt();
        int productID = jsonObject.get("product_id").getAsInt();

        MongoDatabase db = mongoClient.getDatabase("Company" + companyID);
        MongoCollection<Document> productCollection = db.getCollection("Products");
        long count = productCollection.countDocuments(new Document("_id", productID));
        if(count == 0){
            return respond(400, new Gson().fromJson("{'info': 'Product of iot is not found'}", JsonObject.class));
        }

        String iotName = jsonObject.get("iot_name").getAsString();
        int iotID = jsonObject.get("iot_id").getAsInt();
        MongoCollection<Document> iotCollection = db.getCollection("IoTs" + productID);

        try{
            iotCollection.insertOne(new Document().append("_id", iotID).append("iot_name", iotName));
        } catch (MongoException e){
            if(11000 == e.getCode()){
                return respond(400, new Gson().fromJson("{'info': 'iot device id already exist'}", JsonObject.class));
            }
            return respond(500, new Gson().fromJson("{'info': 'Server fail'}", JsonObject.class));
        }

        return respond(200, new Gson().fromJson("{'info': 'Success Register iot'}", JsonObject.class));
    }

    @Override
    public JsonObject iotDeviceUpdate(JsonObject jsonObject) {
        int companyID = jsonObject.get("company_id").getAsInt();
        int productID = jsonObject.get("product_id").getAsInt();
        int iotID = jsonObject.get("iot_id").getAsInt();

        MongoDatabase db = mongoClient.getDatabase("Company" + companyID);
        MongoCollection<Document> iotCollection = db.getCollection("IoTs" + productID);

        long count = iotCollection.countDocuments(new Document("_id", iotID));
        if(count == 0){
            return respond(400, new Gson().fromJson("{'info': 'Product of iot is not found'}", JsonObject.class));
        }

        String iotUpdateID = jsonObject.get("iot_update_id").getAsString();
        String iotUpdate = jsonObject.get("iot_update").getAsString();
        MongoCollection<Document> iotUpdateCollection = db.getCollection("IoT" + iotID);

        try{
            iotUpdateCollection.insertOne(new Document().append("_id", iotUpdateID)
                    .append("iot_update", iotUpdate));
        }
        catch (MongoException e){
            if(11000 == e.getCode()){
                return respond(400, new Gson().fromJson("{'info': 'iot update id already exist'}", JsonObject.class));
            }
            return respond(500, new Gson().fromJson("{'info': 'Server fail'}", JsonObject.class));
        }

        return respond(200, new Gson().fromJson("{'info': 'Success insert iot update'}", JsonObject.class));
    }

    @Override
    public JsonObject getIotDevices(JsonObject jsonObject) {
        int companyID = jsonObject.get("company").getAsInt();// webserver check if company id is valid
        int productID = jsonObject.get("product").getAsInt();// webserver check if product id is valid

        MongoDatabase db = mongoClient.getDatabase("Company" + companyID);
        MongoCollection<Document> iotCollection;
        try{
            iotCollection = db.getCollection("IoTs" + productID);
        } catch(MongoException e){
            return respond(500, new Gson().fromJson("{'info': 'Server issue'}", JsonObject.class));
        }

        int i = 1;
        JsonObject data = new JsonObject();
        for(Document doc: iotCollection.find()){
            JsonObject rowData = JsonParser.parseString(doc.toJson()).getAsJsonObject();
            data.add("Row" + i, rowData);
            ++i;
        }

        return respond(200, new Gson().fromJson(data.toString(), JsonObject.class));
    }

    @Override
    public JsonObject getIotUpdates(JsonObject jsonObject) {

        int companyID = jsonObject.get("company").getAsInt();// webserver check if company id is valid
        int iotID = jsonObject.get("iot").getAsInt();

        MongoDatabase db = mongoClient.getDatabase("Company" + companyID);
        MongoCollection<Document> iotCollection;
        try{
            iotCollection = db.getCollection("IoT" + iotID);
        } catch(MongoException e){
            return respond(500, new Gson().fromJson("{'info': 'Server issue'}", JsonObject.class));
        }

        int i = 1;
        JsonObject data = new JsonObject();
        for(Document doc: iotCollection.find()){
            JsonObject rowData = JsonParser.parseString(doc.toJson()).getAsJsonObject();
            data.add("Row" + i, rowData);
            ++i;
        }

        return respond(200, new Gson().fromJson(data.toString(), JsonObject.class));
    }

    private JsonObject respond(int statusCode, JsonObject data){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("statusCode", statusCode);
        jsonObject.add("data", data);
        return jsonObject;
    }
}
