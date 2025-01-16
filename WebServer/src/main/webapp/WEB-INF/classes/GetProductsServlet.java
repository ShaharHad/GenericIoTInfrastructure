package classes;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import il.co.ilrd.GenericIoTInfrastructure.WebServerAdminDBManager.AdminDBManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/get_products")
public class GetProductsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        AdminDBManager adminDBManager = AdminDBManager.getAdminDBManagerInstance();

        String json = "{'DB_type': 'mysql'}";
        JsonObject jsonObject = adminDBManager.getProducts(new Gson().fromJson(json, JsonObject.class));
        System.out.println("Respond: "  +jsonObject);

        if(jsonObject.get("status").getAsInt() == 200){
            try {
                if(jsonObject.get("data").getAsJsonObject().isEmpty()){
                    HandleRespond.showSuccess(req, resp, "{'title':'Products not found'}");
                }
                else{
                    JsonObject jsonToSend = new JsonObject();
                    jsonToSend.add("data", jsonObject.get("data").getAsJsonObject());
                    jsonToSend.addProperty("path", "/get_product?product_id=");
                    jsonToSend.addProperty("title", "Products");
                    String jsonString = new Gson().toJson(jsonToSend);
                    req.setAttribute("jsonString", jsonString);
                    HandleRespond.showData(req, resp);
                }
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            try {
                HandleRespond.showError(req, resp, "Fail to get products");
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
