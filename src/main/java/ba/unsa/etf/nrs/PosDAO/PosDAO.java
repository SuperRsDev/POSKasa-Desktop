package ba.unsa.etf.nrs.PosDAO;

import ba.unsa.etf.nrs.DataClasses.Category;
import ba.unsa.etf.nrs.DataClasses.Product;
import ba.unsa.etf.nrs.DataClasses.User;
import ba.unsa.etf.nrs.NoInternetException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class PosDAO {
    private static PosDAO instance;
    private static Connection conn;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

    public static PosDAO getInstance() {
        if(instance == null) instance = new PosDAO();
        return instance;
    }

    public static Connection getConn() {
        return conn;
    }

    public static void removeInstance() {
        if (instance == null) return;
        instance.close();
        instance = null;
    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private PosDAO() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/dbnrs_pos20", "root", "");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }


    //funkcija koja se povezuje na api pos kase sa rutom prosljeđenom kao parametar funkcije
    private JSONArray connectToURL(String path) {
        URL url = null;
        JSONArray jsonArray = null;
        try {
            url = new URL("http://localhost:8080/pos/" + path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader entry = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            String json = "", line = "";
            while ((line = entry.readLine()) != null) {
                json = json + line;
            }
            if (json.isEmpty()) return null;
            jsonArray = new JSONArray(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public ArrayList<Product> products() {
        ArrayList<Product> result = new ArrayList<>();
        JSONArray jsonArray = connectToURL("products");
        if (jsonArray == null) return null;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jo = jsonArray.getJSONObject(i);
            Category category = getCategory(jo.getInt("categoryId"));
            Product product = new Product(jo.getInt("id"), jo.getString("name"), jo.getInt("stockQuantity"),
                    jo.getString("status"), jo.getString("description"), jo.getInt("unitPrice"),
                    jo.getInt("sellingPrice"), category);
            result.add(product);
        }
        return result;
    }

    public Category getCategory(int id) {
        URL url = null;
        Category category = null;
        try {
            url = new URL("http://localhost:8080/pos/category/" + id);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader entry = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            String json = "", line = "";
            while ((line = entry.readLine()) != null) {
                json = json + line;
            }
            if (json.isEmpty()) return null;
            JSONObject jo = new JSONObject(json);
            category = new Category(jo.getInt("id"), jo.getString("name"), jo.getString("description"));
        } catch (IOException e) {
            new NoInternetException();
        }
        return category;
    }


    public ArrayList<Category> categories() {
        ArrayList<Category> result = new ArrayList<>();
        JSONArray jsonArray = connectToURL("categories");
        if (jsonArray == null) return null;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jo = jsonArray.getJSONObject(i);
            Category category = new Category(jo.getInt("id"), jo.getString("name"), jo.getString("description"));
            result.add(category);
        }
        return result;
    }

    public ArrayList<User> users() {
        ArrayList<User> result = new ArrayList<>();
        JSONArray jsonArray = connectToURL("users");
        if (jsonArray == null) return null;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jo = jsonArray.getJSONObject(i);
            LocalDate date = LocalDate.parse(jo.getString("birthDate"), formatter);
            User user = new User(jo.getInt("id"), jo.getString("firstName"), jo.getString("lastName"), jo.getString("username"), jo.getString("password"), jo.getString("email"), jo.getString("phone"), jo.getString("address"), jo.getString("picture"), date, jo.getString("loginProvider"));
            result.add(user);
        }
        return result;
    }


}
