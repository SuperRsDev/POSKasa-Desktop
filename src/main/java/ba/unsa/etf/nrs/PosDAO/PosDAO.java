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
import java.net.ProtocolException;
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
            url = new URL("http://localhost:8080/api/products/" + id);
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
            url = new URL("http://localhost:8080/api/categories/" + id);
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

    public Category getCategoryByName(String name) {
        URL url = null;
        Category category = null;
        try {
            url = new URL("http://localhost:8080/api/categories/" + name);
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

    public ArrayList<Product> getProductsForCategory(Category category) {
        ArrayList<Product> result = new ArrayList<>();
        JSONArray jsonArray = connectToURL("products/" + category.getName());
        if (jsonArray == null) return null;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jo = jsonArray.getJSONObject(i);
            Product product = new Product(jo.getInt("id"), jo.getString("name"), jo.getInt("stockQuantity"),
                    jo.getString("status"), jo.getString("description"), jo.getInt("unitPrice"),
                    jo.getInt("sellingPrice"), category);
            result.add(product);
        }
        return result;
    }

    public ArrayList<Product> getProductsByName(String name) {
        ArrayList<Product> result = new ArrayList<>();
        
        JSONArray jsonArray = connectToURL("products/" + name);
        if (jsonArray == null) return null;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jo = jsonArray.getJSONObject(i);
            Product p = getProduct(jo.getInt("id"));
            Product product = new Product(jo.getInt("id"), jo.getString("name"), jo.getInt("stockQuantity"),
                    jo.getString("status"), jo.getString("description"), jo.getInt("unitPrice"),
                    jo.getInt("sellingPrice"), p.getCategory());
            result.add(product);
        }
        return result;
    }

    public User getUser(int id) {
        URL url = null;
        User user = null;
        try {
            url = new URL("http://localhost:8080/api/users/" + id);
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
            url = new URL("http://localhost:8080/api/paymentTypes/" + id);
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

    public Order getOrder(int id) {
        URL url = null;
        Order order = null;
        try {
            url = new URL("http://localhost:8080/api/orders/" + id);
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
            LocalDate date = LocalDate.parse(jo.getString("date"), formatter);
            order = new Order(jo.getInt("id"), getUser(jo.getInt("employeeId")), getPaymentType(jo.getInt("paymentTypeId")), date, jo.getString("status"), jo.getString("orderType"));
        } catch (IOException e) {
            new NoInternetException();
        }
        return order;
    }

    public ArrayList<Order> getOrders() {
        ArrayList<Order> result = new ArrayList<>();
        JSONArray jsonArray = connectToURL("orders");
        if (jsonArray == null) return null;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jo = jsonArray.getJSONObject(i);
            LocalDate date = LocalDate.parse(jo.getString("date"), formatter);
            Order order = new Order(jo.getInt("id"), getUser(jo.getInt("employeeId")), getPaymentType(jo.getInt("paymentTypeId")), date, jo.getString("status"), jo.getString("orderType"));
            result.add(order);
        }
        return result;
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

    public String getUserRole(String username) {
        URL url = null;
        String role = null;
        try {
            url = new URL("http://localhost:8080/api/userRoles/" + username);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader entry = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            String json = "", line = "", json1 = "";
            while ((line = entry.readLine()) != null) {
                    json = json + line;
            }
            if(json.contains("[") && json.contains("]")) {
               json1 = json.substring(1, json.length()-1);
            }
            if (json1.isEmpty()) return null;
            JSONObject jo = new JSONObject(json1);
            role = jo.getString("name");
        } catch (IOException e) {
            new NoInternetException();
        }
        return role;
    }

    /*
    **************************        POST zahtjevi         ***************************+
     */

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

    public void addUser(User user) {
        URL url = null;
        try {
            url = new URL("http://localhost:8080/api/users");
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

    public void addCategory(Category category) {
        URL url = null;
        try {
            url = new URL("http://localhost:8080/api/categories");
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
            url = new URL("http://localhost:8080/api/products");
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
            url = new URL("http://localhost:8080/api/pos");
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

    public void addOrder(Order order) {
        URL url = null;
        try {
            url = new URL("http://localhost:8080/api/orders");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        JSONObject jsonOrder = new JSONObject();
        jsonOrder.put("id", order.getId());
        jsonOrder.put("employeeId", order.getUser().getId());
        jsonOrder.put("paymentTypeId", order.getPaymentType().getId());
        jsonOrder.put("date", order.getDate());
        jsonOrder.put("status", order.getUser().getId());
        jsonOrder.put("orderType", order.getOrderType());

        int id = addViaHttp(jsonOrder, url);
        order.setId(id);
    }

    public void addProductOrder(Product product, Order order) {
        URL url = null;
        try {
            url = new URL("http://localhost:8080/api/productOrders");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        JSONObject jsonOrder = new JSONObject();
        jsonOrder.put("id", order.getId());
        jsonOrder.put("employeeId", order.getUser().getId());
        jsonOrder.put("paymentTypeId", order.getPaymentType().getId());
        jsonOrder.put("date", order.getDate());
        jsonOrder.put("status", order.getUser().getId());
        jsonOrder.put("orderType", order.getOrderType());

        int id = addViaHttp(jsonOrder, url);
        order.setId(id);
    }


    /*
     **************************        DELETE zahtjevi         ***************************+
     */

    private void deleteViaHttp (int id, URL url) {
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("DELETE");
            con.setRequestProperty("Content-Type", "application/application/json");
            con.setDoOutput(true);
            con.connect();
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.write(id);
            out.flush();
            out.close();

            BufferedReader entry = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String json = "", line = "";
            while ((line = entry.readLine()) != null) {
                json = json + line;
            }
            entry.close();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(int id) {
        URL url = null;
        HttpURLConnection con = null;
        try {
            url = new URL("http://localhost:8080/api/users/" + id);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (id > getUsers().size()) return;
        deleteViaHttp(id, url);
    }

    public void deleteCategory(int id) {
        URL url = null;
        HttpURLConnection con = null;
        try {
            url = new URL("http://localhost:8080/api/categories/" + id);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (id > getCategories().size()) return;
        deleteViaHttp(id, url);
    }

    public void deleteProduct(int id) {
        URL url = null;
        HttpURLConnection con = null;
        try {
            url = new URL("http://localhost:8080/api/products/" + id);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (id > getProducts().size()) return;
        deleteViaHttp(id, url);
    }


    /*
     **************************        PUT zahtjevi         ***************************+
     */

    private void updateViaHttp (JSONObject jo, URL url) {
        HttpURLConnection con = null;
        try {
            byte[] data = jo.toString().getBytes();
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            con.connect();
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.write(data);
            out.flush();
            out.close();

            BufferedReader entry = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String json = "", line = "";
            while ((line = entry.readLine()) != null) {
                json = json + line;
            }
            entry.close();
        } catch (IOException e) {
            new NoInternetException();
        }
    }

    public void updateUser(int id, String firstName, String lastName, String username, String password, String email, String phone, String address, String picture, LocalDate birthDate, String loginProvider) {
        URL url = null;
        HttpURLConnection con = null;
        try {
            url = new URL("http://localhost:8080/api/users/" + id);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        JSONObject userObj = new JSONObject();
        userObj.put("firstName", firstName);
        userObj.put("lastName", lastName);
        userObj.put("username", username);
        userObj.put("password", password);
        userObj.put("email", email);
        userObj.put("phone", phone);
        userObj.put("address", address);
        userObj.put("picture", picture);
        userObj.put("birthDate", birthDate);
        userObj.put("loginProvider", loginProvider);
        updateViaHttp(userObj, url);
    }

    public void updateProduct(int id, String name, int stockQuantity, String status, String description, int unitPrice, int sellingPrice, Category category) {
        URL url = null;
        HttpURLConnection con = null;
        try {
            url = new URL("http://localhost:8080/api/products/" + id);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        JSONObject productObj = new JSONObject();
        productObj.put("name", name);
        productObj.put("stockQuantity", stockQuantity);
        productObj.put("status", status);
        productObj.put("description", description);
        productObj.put("unitPrice", unitPrice);
        productObj.put("sellingPrice", sellingPrice);
        productObj.put("categoryId", category.getId());

        updateViaHttp(productObj, url);
    }

    public boolean loginValid(String username, String password) {
        URL url = null;
        JSONObject object = null;
        try {
            url = new URL("http://localhost:8080/api/users/" + username + "/" + password);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader entry = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            String json = "", line = "";
            while ((line = entry.readLine()) != null) {
                json = json + line;
            }
            if (json.isEmpty()) return false;
            //JSONObject jo = new JSONObject(json);
            String role = getUserRole(username);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


}
