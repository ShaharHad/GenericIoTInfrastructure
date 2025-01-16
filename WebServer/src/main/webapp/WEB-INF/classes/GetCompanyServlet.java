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
import java.util.Map;

@WebServlet("/get_company")
public class GetCompanyServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, String[]> map = req.getParameterMap();
        if(map.isEmpty()){
            try {
                req.getRequestDispatcher("/pages/GetCompanyPage.html").include(req, resp);
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

        String json = "{'DB_type': 'mysql', 'data': {'Company_ID': " + companyID + "}}";
        JsonObject jsonObject = adminDBManager.getCompany(new Gson().fromJson(json, JsonObject.class));
        System.out.println("Respond: "  +jsonObject);

        if(jsonObject.get("status").getAsInt() == 200){
            try {
                if(jsonObject.get("data").getAsJsonObject().isEmpty()){
                    HandleRespond.showSuccess(req, resp, "{'title': 'Company not found'");
                }
                else{
                    JsonObject jsonToSend = new JsonObject();
                    jsonToSend.add("data", jsonObject.get("data").getAsJsonObject());
                    jsonToSend.addProperty("title", "Company");

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
                HandleRespond.showError(req, resp, "Fail to get company");
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
