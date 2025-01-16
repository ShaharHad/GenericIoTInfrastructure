package classes;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

class HandleRespond {
    static void showSuccess(HttpServletRequest req, HttpServletResponse res, String jsonString) throws ServletException, IOException {

        JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);
        String jsonStringUpdate = new Gson().toJson(jsonObject);
        req.setAttribute("jsonString", jsonStringUpdate);
        req.getRequestDispatcher("/pages/SuccessMessage.jsp").forward(req, res);
    }

    static void showError(HttpServletRequest req, HttpServletResponse res, String title) throws ServletException, IOException {
        JsonObject jsonToSend = new JsonObject();
        jsonToSend.addProperty("title", title);

        String jsonString = new Gson().toJson(jsonToSend);
        req.setAttribute("jsonString", jsonString);

        req.getRequestDispatcher("/pages/ErrorMessage.jsp?message=" + title).forward(req, res);
    }

    static void showData(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.getRequestDispatcher("/pages/ShowDataPage.jsp").forward(req, res);
    }
}
