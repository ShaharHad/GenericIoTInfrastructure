package classes;

import classes.customException.ServerDownException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import il.co.ilrd.GenericIoTInfrastructure.WebServerAdminDBManager.AdminDBManager;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/register_product")
public class RegisterProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            req.getRequestDispatcher("/pages/RegisterProductPage.html").include(req, resp);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        System.out.println(req.getParameterMap());
        Map<String, String[]> map = req.getParameterMap();

        AdminDBManager adminDBManager = AdminDBManager.getAdminDBManagerInstance();

        int companyID = Integer.parseInt(map.get("company_id")[0]);
        String productName = map.get("product_name")[0];
        String description = map.get("description")[0];


        String json = "{'DB_type': 'mysql', 'data': {'Product_Name': '" + productName +
                "', 'Company_ID': " + companyID +
                ", 'Description': '" + description + "'}}";

        JsonObject jsonObject = adminDBManager.registerProduct(new Gson().fromJson(json, JsonObject.class));
        System.out.println("Respond: "  +jsonObject);

        if(jsonObject.get("status").getAsInt() == 200){
            try {
                int productID = jsonObject.get("data").getAsJsonObject().get("Product_ID").getAsInt();
               registerProductInGatewayServer(productName, productID, companyID);
                HandleRespond.showSuccess(req, resp, "{'title': 'Success register product', 'message': 'Product id is " +  productID + "'}");
            } catch (Exception e) {
                HandleRespond.showError(req, resp, e.getMessage());
            }
        }
        else{
            try {
                HandleRespond.showError(req, resp, "Fail to Register product");
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        }
        resp.getWriter().write(jsonObject.toString());
    }

    private void registerProductInGatewayServer(String productName, int productID, int companyID) throws ServerDownException {
        String targetURL = "http://10.0.0:4000/company/" + companyID + "/product";

        try(CloseableHttpClient httpclient = HttpClients.createDefault()){
            HttpPost request = new HttpPost(targetURL);

            request.setHeader("Content-Type", "application/json");

            // define request body
            String jsonPayload = "{\"product_name\": " + productName + ",\"product_id\": " + productID + ",\"company_id\": " + companyID + "}";
            StringEntity entity = new StringEntity(jsonPayload);
            request.setEntity(entity);

            try(CloseableHttpResponse response = httpclient.execute(request)){
                String responseBody = EntityUtils.toString(response.getEntity());
                int statusLine = response.getCode();
                System.out.println("Response body: " + responseBody + " Status code: " + statusLine);

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
