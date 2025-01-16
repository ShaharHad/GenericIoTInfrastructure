package il.co.ilrd.GenericIoTInfrastructure.commands;

import com.google.gson.JsonObject;

public class TestForJarFile {

    public static void main(String[] args) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("CreateIOTCommand", "null");
        Command c1 = new RegisterIOT(jsonObject);
        Command c2 = new UpdateIOT(jsonObject);
        Command c3 = new RegisterCompany(jsonObject);
        Command c4 = new RegisterProduct(jsonObject);
        System.out.println(c1);
        System.out.println(c2);
    }
}
