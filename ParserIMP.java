
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.nio.ByteBuffer;
import java.util.AbstractMap;
import java.util.Map;

public class ParserIMP implements Parser{
    public Map.Entry<String, JsonObject> parse(ByteBuffer jsonBytes){
        if(null == jsonBytes){
            throw new RuntimeException("jsonBytes in ParserIMP is null");
        }
        Gson gson = new Gson();
        String jsonString = new String(jsonBytes.array());
        JsonObject jsonObject = gson.fromJson(jsonString.trim(), JsonObject.class);
        String key = jsonObject.get("Key").getAsString();


        if(null == key){
            throw new RuntimeException("key is null in json");
        }

        JsonObject data = jsonObject.getAsJsonObject("Data");

//        if(null == data){
//            throw new RuntimeException("data is null in json");
//        }

        return new AbstractMap.SimpleEntry<>(key, data);
    }
}
