package classes;

import classes.customException.ServerDownException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import il.co.ilrd.GenericIoTInfrastructure.WebServerAdminDBManager.AdminDBManager;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/get_iots_devices")
public class GetIoTsDevicesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, String[]> map = req.getParameterMap();
        if(map.isEmpty()){
            try {
                req.getRequestDispatcher("/pages/GetIoTsDevicesPage.html").include(req, resp);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            getRequestWithParam(req, resp, map);
        }
    }

    private void getRequestWithParam(HttpServletRequest req, HttpServletResponse resp, Map<String, String[]> map) throws IOException {

        AdminDBManager adminDBManager = AdminDBManager.getAdminDBManagerInstance();

        int companyID = Integer.parseInt(map.get("company_id")[0]);
        int productID = Integer.parseInt(map.get("product_id")[0]);

        String json = "{'DB_type': 'mysql', 'data': {'Product_ID': " + productID + "}}";
        JsonObject jsonObject = adminDBManager.getProduct(new Gson().fromJson(json, JsonObject.class));
        System.out.println("Respond: " + jsonObject);

        if(jsonObject.get("status").getAsInt() == 200){
            try {
                if(jsonObject.get("data").getAsJsonObject().isEmpty()){
                    HandleRespond.showSuccess(req, resp, "{'title': 'Product not found'}");
                }
                else{

                    JsonObject js = handleRequestToGatewayServer(companyID, productID);

                    JsonObject jsonToSend = new JsonObject();
                    jsonToSend.add("data", js.get("data").getAsJsonObject());
                    jsonToSend.addProperty("title", "IoTs Devices");

                    String jsonString = new Gson().toJson(jsonToSend);

                    req.setAttribute("jsonString", jsonString);
                    HandleRespond.showData(req, resp);
                }
            } catch (ServletException | ServerDownException e) {
                try {
                    HandleRespond.showError(req, resp, "Fail to get Iots devices");
                } catch (ServletException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        else{
            try {
                HandleRespond.showError(req, resp, "Fail to get Iots devices");
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private JsonObject handleRequestToGatewayServer(int companyID, int productID) throws ServerDownException {

        String targetURL = "http://127.0.0.1:4000/company/" + companyID + "/product/" + productID + "/iot";

        try(CloseableHttpClient httpclient = HttpClients.createDefault()){
            HttpGet request = new HttpGet(targetURL);

            request.setHeader("Content-Type", "application/json");

            try(CloseableHttpResponse response = httpclient.execute(request)){
                String responseBody = EntityUtils.toString(response.getEntity());
                int statusLine = response.getCode();
                if(statusLine == 200){
                    return new Gson().fromJson(responseBody, JsonObject.class);
                }

                throw new RuntimeException("Fail to get Iots devices");

            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            if(e.getMessage().contains("Connection refused")){

                throw new ServerDownException("Fail connect to server");
            }
            throw new RuntimeException(e);
        }
    }

}
