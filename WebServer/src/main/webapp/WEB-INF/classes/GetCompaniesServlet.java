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

@WebServlet("/get_companies")
public class GetCompaniesServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, String[]> map = req.getParameterMap();

        AdminDBManager adminDBManager = AdminDBManager.getAdminDBManagerInstance();

        String json = "{'DB_type': 'mysql'}";
        JsonObject jsonObject = adminDBManager.getCompanies(new Gson().fromJson(json, JsonObject.class));
        System.out.println("Respond: "  +jsonObject);

        if(jsonObject.get("status").getAsInt() == 200){
            try {
                if(jsonObject.get("data").getAsJsonObject().isEmpty()){
                    HandleRespond.showSuccess(req, resp, "{'title': 'Not found register company'}");
                }
                else{
                    JsonObject jsonToSend = new JsonObject();
                    jsonToSend.add("data", jsonObject.get("data").getAsJsonObject());
                    jsonToSend.addProperty("path", "/get_company?company_id=");
                    jsonToSend.addProperty("title", "Companies");
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
                HandleRespond.showError(req, resp, "Fail to get companies");
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
