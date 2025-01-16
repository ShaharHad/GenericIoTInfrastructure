package WebServerAdminDBManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.*;

public class MysqlHandler implements DBMSHandler {
    private String MysqlConnectionURL = "jdbc:mysql://localhost:3306/AdminDB? user=shahar&password=shahar";
    private String mysqlDriverName = "com.mysql.cj.jdbc.Driver";
    private Connection con;

    public MysqlHandler(){
        try {
            Class.forName(mysqlDriverName);
            con = DriverManager.getConnection(MysqlConnectionURL);
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public JsonObject registerCompany(JsonObject jsonObject) {
        String query = "insert into Company(Company_Name) values (?)";

        try {
            PreparedStatement preparedStatement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, jsonObject.get("Company_Name").getAsString());
            int rowEffected = preparedStatement.executeUpdate();
            if(0 == rowEffected){
                preparedStatement.close();
                return respond(500, new Gson().fromJson("{'info':'Fail register company'}", JsonObject.class));
            }
            long companyID = -1;
            try(ResultSet generateKeys = preparedStatement.getGeneratedKeys()){
                if(generateKeys.next()){
                    companyID = generateKeys.getLong(1);
                    insertContact(jsonObject, companyID);
                }
                return respond(200, new Gson().fromJson("{'info':'Successfully register company', Company_ID:" + companyID + "}", JsonObject.class));
            }
        } catch (SQLException e) {
            return respond(500, new Gson().fromJson("{'info':'Server issue'}", JsonObject.class));
        } catch (RuntimeException e) {
            return respond(501, new Gson().fromJson("{'info':'Fail register company'}", JsonObject.class));
        }
    }

    private void insertContact(JsonObject jsonObject, long companyID) throws SQLException {
        try{
            String query = "insert into Contacts(Contact_Name, Contact_Number, Company_ID," +
                    " Address, Credit_Card, Expire_Date, Security_Code) values (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, jsonObject.get("Contact_Name").getAsString());
            preparedStatement.setString(2, jsonObject.get("Contact_Number").getAsString());
            preparedStatement.setLong(3, companyID);
            preparedStatement.setString(4, jsonObject.get("Address").getAsString());
            preparedStatement.setString(5, jsonObject.get("Credit_Card").getAsString());
            preparedStatement.setDate(6, Date.valueOf(jsonObject.get("Expire_Date").getAsString()));
            preparedStatement.setString(7, jsonObject.get("Security_Code").getAsString());
            int rowEffected = preparedStatement.executeUpdate();
            preparedStatement.close();
            if(0 == rowEffected){
                throw new RuntimeException("Failed to insert contact");
            }
        } catch(Exception e){
            String deleteQuery = "delete from Company where Company_ID = " + companyID + ";";
            Statement stm  = con.createStatement();
            stm.executeUpdate(deleteQuery);
            throw new RuntimeException();
        }
    }

    @Override
    public JsonObject registerProduct(JsonObject jsonObject) {
        String query = "insert into Product(Company_ID, Product_Name, Description) values (?, ?, ?);";
        try {
            String productName = jsonObject.get("Product_Name").getAsString();
            String description = jsonObject.get("Description").getAsString();
            int companyID = jsonObject.get("Company_ID").getAsInt();
            if(null == productName || null == description){
                return respond(400, new Gson().fromJson("{'info': 'Data not valid'}", JsonObject.class));
            }

            PreparedStatement preparedStatement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, companyID);
            preparedStatement.setString(2, productName);
            preparedStatement.setString(3, description);
            int rowEffected = preparedStatement.executeUpdate();
            if(0 == rowEffected){
                return respond(500, new Gson().fromJson("{'info':'Fail register product'}", JsonObject.class));
            }
            long registerID = -1;
            try(ResultSet generateKeys = preparedStatement.getGeneratedKeys()){
                if(generateKeys.next()){
                    registerID = generateKeys.getLong(1);
                }
                if(-1 == registerID){
                    return respond(400, new Gson().fromJson("{'info':'User error'}", JsonObject.class));
                }
            }
            return respond(200, new Gson().fromJson("{'info':'Successfully register product', 'Product_ID': " + registerID +"}", JsonObject.class));

        }catch (SQLIntegrityConstraintViolationException e){
            return respond(400, new Gson().fromJson("{'info': 'Duplicate Product'}", JsonObject.class));
        }catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e){
            return respond(400, new Gson().fromJson("{'info':'Json data not valid'}", JsonObject.class));
        }
    }

    @Override
    public JsonObject getCompany(JsonObject jsonObject) {
        int companyID = jsonObject.get("Company_ID").getAsInt();
        try {
            String getCompanyQuery = "select * from Company c join Contacts con on c.Company_ID=con.Company_ID where c.Company_ID=?";
            PreparedStatement preparedStatement = con.prepareStatement(getCompanyQuery);
            preparedStatement.setInt(1, companyID);
            ResultSet resultSet = preparedStatement.executeQuery();
            JsonObject respond = new JsonObject();
            int i = 1;
            while(resultSet.next()){
                JsonObject row = new JsonObject();
                row.addProperty("Company_ID", resultSet.getString("Company_ID"));
                row.addProperty("Company_Name", resultSet.getString("Company_Name"));
                row.addProperty("Contact_Name", resultSet.getString("Contact_Name"));
                row.addProperty("Contact_Number", resultSet.getInt("Contact_Number"));
                row.addProperty("Address", resultSet.getString("Address"));
                respond.add("Row" + i, row);
                ++i;
            }

            return respond(200, respond);
        } catch (SQLException e) {
            return respond(400, new Gson().fromJson("{'info':'user error}'", JsonObject.class));
        }
    }

    @Override
    public JsonObject getCompanies(JsonObject jsonObject) {
        try {
            String getCompanyQuery = "select * from Company";
            PreparedStatement preparedStatement = con.prepareStatement(getCompanyQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            JsonObject respond = new JsonObject();
            int i = 1;
            while(resultSet.next()){
                JsonObject row = new JsonObject();
                row.addProperty("Company_ID", resultSet.getString("Company_ID"));
                row.addProperty("Company_Name", resultSet.getString("Company_Name"));
                respond.add("Row" + i, row);
                ++i;
            }

            return respond(200, respond);
        } catch (SQLException e) {
            return respond(400, new Gson().fromJson("{'info':'user error}'", JsonObject.class));
        }
    }

    @Override
    public JsonObject getProduct(JsonObject jsonObject) {
        int productID = jsonObject.get("Product_ID").getAsInt();
        try {
            String getProductQuery = "select Product_ID, Product_Name, Description from Product where Product_ID=?";
            PreparedStatement preparedStatement = con.prepareStatement(getProductQuery);
            preparedStatement.setInt(1, productID);
            ResultSet resultSet = preparedStatement.executeQuery();
            JsonObject respond = new JsonObject();
            if(resultSet.next()){
                JsonObject row = new JsonObject();
                row.addProperty("Product_ID", resultSet.getString("Product_ID"));
                row.addProperty("Product_Name", resultSet.getString("Product_Name"));
                row.addProperty("Description", resultSet.getString("Description"));
                respond.add("Row1", row);
            }
            return respond(200, respond);
        } catch (SQLException e) {
            return respond(400, new Gson().fromJson("{'info':'user error'}", JsonObject.class));
        }
    }

    @Override
    public JsonObject getProducts(JsonObject jsonObject) {
        try {
            String getProductsQuery = "select * from Product";
            PreparedStatement preparedStatement = con.prepareStatement(getProductsQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            JsonObject respond = new JsonObject();
            int i = 1;
            while(resultSet.next()){
                JsonObject row = new JsonObject();
                row.addProperty("Product_ID", resultSet.getString("Product_ID"));
                row.addProperty("Product_Name", resultSet.getString("Product_Name"));
                respond.add("Row" + i, row);
                ++i;
            }

            return respond(200, respond);
        } catch (SQLException e) {
            return respond(400, new Gson().fromJson("{'info':'user error'}", JsonObject.class));
        }
    }

    private JsonObject respond(int statusCode, JsonObject data){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", statusCode);
        jsonObject.add("data", data);
        return jsonObject;
    }
}
