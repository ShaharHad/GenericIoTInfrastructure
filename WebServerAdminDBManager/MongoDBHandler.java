package il.co.ilrd.GenericIoTInfrastructure.WebServerAdminDBManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mongodb.client.*;
import org.bson.Document;

import java.sql.*;

public class MongoDBHandler implements DBMSHandler {
    private String MongoDBConnectionURL = "mongodb+srv://shahar:cjOgfRwiPM7mu282@cluster0.k66nm.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
    private Connection con;
    private MongoClient mongoClient;

    public MongoDBHandler() {
        mongoClient = MongoClients.create(MongoDBConnectionURL);
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
        Document doc = new Document("product_id", productID);
        doc.append("product_name", productName );
        collection.insertOne(doc);

        return respond(200, new Gson().fromJson("{'info': 'Success Register product'}", JsonObject.class));
    }

    @Override
    public JsonObject getCompany(JsonObject jsonObject) {
        return null;
    }

    @Override
    public JsonObject getCompanies(JsonObject jsonObject) {
        return null;
    }

    @Override
    public JsonObject getProduct(JsonObject jsonObject) {
        return null;
    }

    @Override
    public JsonObject getProducts(JsonObject jsonObject) {
        return null;
    }

    private JsonObject respond(int statusCode, JsonObject data){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("statusCode", statusCode);
        jsonObject.add("data", data);
        return jsonObject;
    }
}

