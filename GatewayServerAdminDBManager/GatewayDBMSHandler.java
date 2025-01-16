package GatewayServerAdminDBManager;

import com.google.gson.JsonObject;

public interface GatewayDBMSHandler {
    public JsonObject registerCompany(JsonObject jsonObject);
    public JsonObject registerProduct(JsonObject jsonObject);
    public JsonObject registerIoT(JsonObject jsonObject);
    public JsonObject iotDeviceUpdate(JsonObject jsonObject);
    public JsonObject getIotDevices(JsonObject jsonObject);
    public JsonObject getIotUpdates(JsonObject jsonObject);
}
