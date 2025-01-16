package classes;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import il.co.ilrd.GenericIoTInfrastructure.WebServerAdminDBManager.AdminDBManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@WebServlet("/register_company")
public class RegisterCompanyServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            req.getRequestDispatcher("/pages/RegisterCompanyPage.html").include(req, resp);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        System.out.println(req.getParameterMap());
        Map<String, String[]> map = req.getParameterMap();

        AdminDBManager adminDBManager = AdminDBManager.getAdminDBManagerInstance();

        String companyName = map.get("company_name")[0];
        String contactName = map.get("contact_name")[0];
        int contactNumber = Integer.parseInt(map.get("contact_number")[0]);
        String address = map.get("address")[0];
        String creditCard = map.get("credit_card")[0];
        String date_card_expired = dateConvertor(map.get("expire_card")[0]);
        String securityCode = map.get("security_card")[0];

        String json = "{'DB_type': 'mysql', 'data': {'Company_Name': '" + companyName +
                "', 'Contact_Number': " + contactNumber +
                ", 'Address': " + address +
                ", 'Credit_Card': " + creditCard +
                ", 'Expire_Date': " + date_card_expired +
                ", 'Security_Code': " + securityCode+
                ", 'Contact_Name': " + contactName + "}}";

        JsonObject jsonObject = adminDBManager.registerCompany(new Gson().fromJson(json, JsonObject.class));
        System.out.println("Respond: "  +jsonObject);

        if(jsonObject.get("status").getAsInt() == 200){
            try {
                int companyID = jsonObject.get("data").getAsJsonObject().get("Company_ID").getAsInt();

                HandleRespond.showSuccess(req, resp, "{'title': 'Success register company', 'message': 'Company id is " +  companyID + "'}");
            }catch (Exception e) {

                HandleRespond.showError(req, resp, e.getMessage());
            }
        }
        else{
            try {
                HandleRespond.showError(req, resp, "Fail to Register company");
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        }
        resp.getWriter().write(jsonObject.toString());
    }

//    private void registerCompanyInGatewayServer(String companyName, int companyID) throws IOException, ServerDownException {
//        String targetURL = "http://127.0.0.1:4000/company";
//
//        try(CloseableHttpClient httpclient = HttpClients.createDefault()){
//            HttpPost request = new HttpPost(targetURL);
//
//            request.setHeader("Content-Type", "application/json");
//
//            // define request body
//            String jsonPayload = "{\"name\": " + companyName + ",\"company_id\": " + companyID + "}";
//            StringEntity entity = new StringEntity(jsonPayload);
//            request.setEntity(entity);
//
//            try(CloseableHttpResponse response = httpclient.execute(request)){
//                String responseBody = EntityUtils.toString(response.getEntity());
//                int statusCode = response.getCode();
//                System.out.println("Response body: " + responseBody + " Status code: " + statusCode);
//
//            } catch (ParseException e) {
//                throw new RuntimeException(e);
//            }
//        } catch (Exception e) {
//            if(e.getMessage().contains("Connection refused")){
//
//                throw new ServerDownException("Fail connect to server");
//            }
//            throw new RuntimeException(e);
//        }
//    }

    private String dateConvertor(String dateToConvert){

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse("01/" + dateToConvert, DateTimeFormatter.ofPattern("dd/MM/yy"));

        return date.format(outputFormatter);
    }
}
