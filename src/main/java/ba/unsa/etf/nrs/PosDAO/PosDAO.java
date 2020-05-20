package ba.unsa.etf.nrs.PosDAO;

import ba.unsa.etf.nrs.DataClasses.*;
import ba.unsa.etf.nrs.NoInternetException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
//Todo: u bazu ubaci date kao null (pogledati npr na ruti /pos/orders) - to treba formatirati ispravno i riješiti
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
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3308/dbnrs_pos20?useSSL=false"; //podložno promjenama shodno koji port koristi server
            conn = DriverManager.getConnection(url, "root", "");
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

    public ArrayList<Product> getProducts() {
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

    public Product getProduct(int id) {
        URL url = null;
        Product product = null;
        try {
            url = new URL("http://localhost:8080/pos/product/" + id);
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
            product = new Product(jo.getInt("id"), jo.getString("name"), jo.getInt("stockQuantity"), jo.getString("description"), jo.getString("status"),
                    jo.getInt("unitPrice"), jo.getInt("sellingPrice"), getCategory(jo.getInt("categoryId")));
        } catch (IOException e) {
            new NoInternetException();
        }
        return product;
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


    public ArrayList<Category> getCategories() {
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

    public User getUser(int id) {
        URL url = null;
        User user = null;
        try {
            url = new URL("http://localhost:8080/pos/user/" + id);
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
            LocalDate date = LocalDate.parse(jo.getString("birthDate"), formatter);
            user = new User(jo.getInt("id"), jo.getString("firstName"), jo.getString("lastName"), jo.getString("username"), jo.getString("password"), jo.getString("email"), jo.getString("phone"), jo.getString("address"), jo.getString("picture"), date, jo.getString("loginProvider"));
        } catch (IOException e) {
            new NoInternetException();
        }
        return user;
    }


    public ArrayList<User> getUsers() {
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


    public PaymentType getPaymentType(int id) {
        URL url = null;
        PaymentType paymentType = null;
        try {
            url = new URL("http://localhost:8080/pos/paymentType/" + id);
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
            paymentType = new PaymentType(jo.getInt("id"), jo.getString("paymentTypeProvider"), jo.getString("description"));
        } catch (IOException e) {
            new NoInternetException();
        }
        return paymentType;
    }

    public ArrayList<Integer> getSubTotals(int orderId) {
        ArrayList<Integer> result = new ArrayList<>();
        JSONArray jsonArray = connectToURL("subTotals/" + orderId);
        if (jsonArray == null) return null;
        for (int i = 0; i < jsonArray.length(); i++) {
            int subTotalForOneProduct = 0;
            JSONObject jo = jsonArray.getJSONObject(i);
            Product product = getProduct(jo.getInt("productId"));
            subTotalForOneProduct = product.getSellingPrice() * jo.getInt("quantity");
            result.add(subTotalForOneProduct);
        }
        return result;
    }

    //POST zahtjevi

    public void addUser(User user) {
        URL url = null;
        try {
            url = new URL("http://localhost:8080/pos/user");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        JSONObject jsonUser = new JSONObject();
        jsonUser.put("id", user.getId());
        jsonUser.put("firstName", user.getFirstName());
        jsonUser.put("lastName", user.getLastName());
        jsonUser.put("username", user.getUsername());
        jsonUser.put("password", user.getPassword());
        jsonUser.put("email", user.getEmail());
        jsonUser.put("phone", user.getPhone());
        jsonUser.put("address", user.getAddress());
        jsonUser.put("picture", user.getPicture());
        jsonUser.put("birthDate", user.getBirthDate());
        jsonUser.put("loginProvider", user.getLoginProvider());

        int id = addViaHttp(jsonUser, url);
        user.setId(id);
    }

    private int addViaHttp(JSONObject jsonObject, URL url) {
        HttpURLConnection con = null;
        JSONObject jsonObjectReturn = null;
        try {
            byte[] data = jsonObject.toString().getBytes();
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.write(data);
            out.flush();
            out.close();

            BufferedReader entry = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String json = "", line = "";
            while ((line = entry.readLine()) != null) {
                json = json + line;
            }
            jsonObjectReturn = new JSONObject(json);
            entry.close();
        } catch (IOException e) {
            new NoInternetException();
        }
        return jsonObjectReturn.getInt("id");
    }

    public void addCategory(Category category) {
        URL url = null;
        try {
            url = new URL("http://localhost:8080/pos/category");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        JSONObject jsonCategory = new JSONObject();

        jsonCategory.put("id", category.getId());
        jsonCategory.put("name", category.getName());
        jsonCategory.put("description", category.getDescription());

        int id = addViaHttp(jsonCategory, url);
        category.setId(id);
    }

    public void addProduct(Product product) {
        URL url = null;
        try {
            url = new URL("http://localhost:8080/pos/product");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        JSONObject jsonProduct = new JSONObject();

        jsonProduct.put("id", product.getProductId());
        jsonProduct.put("name", product.getName());
        jsonProduct.put("stockQuantity", product.getStockQuantity());
        jsonProduct.put("status", product.getStatus());
        jsonProduct.put("description", product.getDescription());
        jsonProduct.put("unitPrice", product.getUnitPrice());
        jsonProduct.put("sellingPrice", product.getSellingPrice());
        jsonProduct.put("categoryId", product.getCategory().getId());

        int id = addViaHttp(jsonProduct, url);
        product.setProductId(id);
    }

    public void addPos(POS pos) {
        URL url = null;
        try {
            url = new URL("http://localhost:8080/pos/pos");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        JSONObject jsonPos = new JSONObject();
        jsonPos.put("id", pos.getId());
        jsonPos.put("orderId", pos.getOrder().getId());
        jsonPos.put("totalSum", pos.getTotalSum());
        jsonPos.put("fiscalNumber", pos.getFiscalNumber());

        int id = addViaHttp(jsonPos, url);
        pos.setId(id);
    }



}
