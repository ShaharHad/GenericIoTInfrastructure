package il.co.ilrd.GenericIoTInfrastructure.WebServerAdminDBManager;

import com.google.gson.JsonObject;

public interface DBMSHandler {
    public JsonObject registerCompany(JsonObject jsonObject);
    public JsonObject registerProduct(JsonObject jsonObject);
    public JsonObject getCompany(JsonObject jsonObject);
    public JsonObject getCompanies(JsonObject jsonObject);
    public JsonObject getProduct(JsonObject jsonObject);
    public JsonObject getProducts(JsonObject jsonObject);
}
