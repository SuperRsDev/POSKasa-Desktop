package ba.unsa.etf.nrs.DAO;

import ba.unsa.etf.nrs.DataClasses.*;
import ba.unsa.etf.nrs.NoInternetException;
import ba.unsa.etf.nrs.Services.AuthService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
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
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PosDAO extends BaseDAO {
    private static PosDAO instance;
    private static Connection conn;
    private AuthService authService;
    private static final String baseUri = "https://poskasa-api.herokuapp.com/api/";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

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
            authService = AuthService.getInstance();
            int port = 3308;
            String dbUsername = "root";
            String dbPassword = "root";
            String url = "jdbc:mysql://localhost:" + port + "/dbnrs_pos20?useSSL=false"; //podložno promjenama shodno koji port koristi server
            conn = DriverManager.getConnection(url, dbUsername, dbPassword);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getBaseUri() {
        return baseUri;
    }

    private JSONArray getJsonArrayFromUrl(URL url) {
        JSONArray jsonArray;
        String json = this.getReaderJsonConnectionData(url);
        if (json == null) return null;
        jsonArray = new JSONArray(json);
        return jsonArray;
    }

    private JSONObject getJsonObjectFromUrl(URL url) {
        JSONObject jsonArray;
        String json = this.getReaderJsonConnectionData(url);
        if (json == null) return null;
        jsonArray = new JSONObject(json);
        return jsonArray;
    }

    private JSONObject getJsonObjectData(String path) {
        URL url = this.getUrl(path);

        return this.getJsonObjectFromUrl(url);
    }

    //funkcija koja se povezuje na api pos kase sa rutom prosljeđenom kao parametar funkcije
    private JSONArray getJsonArrayData(String path) {
        URL url = this.getUrl(path);

        return this.getJsonArrayFromUrl(url);
    }


    private HttpURLConnection getHttpConnection(URL url, String method) throws IOException {
        HttpURLConnection con = this.getBaseHttpConnection(url, method);
        String basicAuth = "Bearer " + this.authService.getToken();
        con.setRequestProperty ("Authorization", basicAuth);

        return con;
    }

    private String getReaderJsonConnectionData(URL url) {
        try {
            HttpURLConnection con = this.getHttpConnection(url, "GET");
            InputStream in = con.getInputStream();
            return this.getReaderJson(in);
        } catch (IOException e) {
            e.printStackTrace();
            new NoInternetException();
        }
        return null;
    }

    public ArrayList<Product> getProducts() {
        ArrayList<Product> result = new ArrayList<>();
        JSONArray jsonArray = getJsonArrayData("products");
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
        Product product;
        JSONObject jo = this.getJsonObjectData("products/" + id);
        if (jo == null) return null;
        product = new Product(jo.getInt("id"), jo.getString("name"), jo.getInt("stockQuantity"), jo.getString("description"), jo.getString("status"),
                jo.getInt("unitPrice"), jo.getInt("sellingPrice"), getCategory(jo.getInt("categoryId")));
        return product;
    }

    public Category getCategory(int id) {
        Category category = null;
        JSONObject jo = this.getJsonObjectData("categories/" + id);
        if (jo == null) return null;
        category = new Category(jo.getInt("id"), jo.getString("name"), jo.getString("description"));
        return category;
    }

    public Category getCategoryByName(String name) {
        Category category;
        JSONObject jo = this.getJsonObjectData("categoriesfor/" + name);
        if (jo == null) return null;
        category = new Category(jo.getInt("id"), jo.getString("name"), jo.getString("description"));
        return category;
    }

    public List<Category> getCategories() {
        List<Category> result = new ArrayList<>();
        JSONArray jsonArray = getJsonArrayData("categories");
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
        JSONArray jsonArray = getJsonArrayData("productsfor/" + category.getName());
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
        
        JSONArray jsonArray = getJsonArrayData("products/" + name);
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
        JSONArray jsonArray = getJsonArrayData("users/" + id);
        if (jsonArray == null) return null;
        User user = null;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jo = jsonArray.getJSONObject(i);
            user = this.getUserFromJson(jo);
        }
        return user;
    }

    public User getUserByUsername(String username) {
        JSONObject jo = this.getJsonObjectData("usersfor/" + username);
        if (jo == null) return null;
        User user;
        user = this.getUserFromJson(jo);
        return user;
    }


    public List<User> getUsers() {
        List<User> result = new ArrayList<>();
        JSONArray jsonArray = getJsonArrayData("users");
        if (jsonArray == null) return null;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jo = jsonArray.getJSONObject(i);
            User user = this.getUserFromJson(jo);
            result.add(user);
        }
        return result;
    }

    public PaymentType getPaymentType(int id) {
        JSONObject jo = this.getJsonObjectData("paymentTypes/" + id);
        if (jo == null) return null;
        PaymentType paymentType = null;
        paymentType = new PaymentType(jo.getInt("id"), jo.getString("paymentTypeProvider"), jo.getString("description"));
        return paymentType;
    }

    public Order getOrder(int id) {
        JSONObject jo = this.getJsonObjectData("orders/" + id);
        if (jo == null) return null;
        Order order;
        LocalDate date = LocalDate.parse(jo.getString("date"), formatter);
        order = new Order(jo.getInt("id"), getUser(jo.getInt("employeeId")), getPaymentType(jo.getInt("paymentTypeId")), date,
                jo.getString("status"), jo.getString("orderType"));
        return order;
    }

    public ArrayList<Order> getOrders() {
        ArrayList<Order> result = new ArrayList<>();
        JSONArray jsonArray = getJsonArrayData("orders");
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
        JSONArray jsonArray = getJsonArrayData("subTotals/" + orderId);
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

    public Role getUserRole(User user) {
        JSONArray jsonArray = getJsonArrayData("userRolesfor/" + user.getUsername());
        if (jsonArray == null) return null;
        Role role = null;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jo = jsonArray.getJSONObject(i);
            role = new Role(jo.getInt("id"), jo.getString("description"), jo.getString("name"));
        }
        return role;
    }

    public List<Role> getRoles() {
        List<Role> result = new ArrayList<>();
        JSONArray jsonArray = getJsonArrayData("roles");
        if (jsonArray == null) return null;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jo = jsonArray.getJSONObject(i);
            Role role = new Role(jo.getInt("id"), jo.getString("description"),  jo.getString("name"));
            result.add(role);

        }
        return result;
    }

    /*
    **************************        POST zahtjevi         ***************************+
     */

    private int addViaHttp(JSONObject jsonObject, URL url) {
        JSONObject jsonObjectReturn = this.postViaHttp(jsonObject, url, true);
        return Objects.requireNonNull(jsonObjectReturn).getInt("id");
    }

    private JSONObject addViaHttpWithoutResponseId(JSONObject jsonObject, URL url) {
        return this.postViaHttp(jsonObject, url, true);
    }

    private JSONObject postViaHttp(JSONObject jsonObject, URL url, boolean shouldAuth) {
        HttpURLConnection con;
        JSONObject jsonObjectReturn = null;
        try {
            byte[] data = jsonObject.toString().getBytes();
            if (shouldAuth) {
                con = this.getHttpConnection(url, "POST");
            } else {
                con = this.getBaseHttpConnection(url, "POST");
            }

            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.write(data);
            out.flush();
            out.close();

            String json = this.getReaderJson(con.getInputStream());
            if (json == null) return null;
            jsonObjectReturn = new JSONObject(json);
        } catch (IOException e) {
            new NoInternetException();
        }
        return jsonObjectReturn;
    }

    public int addUser(User user) {
        URL url = this.getUrl("users");
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
        return id;
    }

    public int addCategory(Category category) {
        URL url = this.getUrl("categories");
        JSONObject jsonCategory = new JSONObject();

        jsonCategory.put("id", category.getId());
        jsonCategory.put("name", category.getName());
        jsonCategory.put("description", category.getDescription());

        int id = addViaHttp(jsonCategory, url);
        category.setId(id);
        return id;
    }

    public int addProduct(Product product) {
        URL url = this.getUrl("products");
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
        return id;
    }

    public void addPos(POS pos) {
        URL url = this.getUrl("pos");
        JSONObject jsonPos = new JSONObject();
        jsonPos.put("id", pos.getId());
        jsonPos.put("orderId", pos.getOrder().getId());
        jsonPos.put("totalSum", pos.getTotalSum());
        jsonPos.put("fiscalNumber", pos.getFiscalNumber());

        int id = addViaHttp(jsonPos, url);
        pos.setId(id);
    }

    public void addOrder(Order order) {
        URL url = this.getUrl("orders");
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
        URL url = this.getUrl("productOrders");
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
    public void addUserRole(User user, Role role) {
        URL url = this.getUrl("userRoles");
        JSONObject jsonOrder = new JSONObject();
        jsonOrder.put("userId", user.getId());
        jsonOrder.put("roleId", role.getId());
        JSONObject jo = addViaHttpWithoutResponseId(jsonOrder, url);
    }


    /*
     **************************        DELETE zahtjevi         ***************************+
     */

    private void deleteViaHttp (int id, URL url) {
        HttpURLConnection con = null;
        try {
            con = this.getHttpConnection(url, "DELETE");
            con.setDoOutput(true);
            con.connect();
/*            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.write(id);
            out.flush();
            out.close();
*/
            String json = this.getReaderJson(con.getInputStream());
            if (json == null) return;
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(int id) {
        URL url = this.getUrl("users/" + id);
        HttpURLConnection con = null;
        if (id > getUsers().size()) return;
        deleteViaHttp(id, url);
    }

    public void deleteCategory(int id) {
        URL url = this.getUrl("categories/" + id);
        HttpURLConnection con = null;
        if (id > getCategories().size()) return;
        deleteViaHttp(id, url);
    }

    public void deleteProduct(int id) {
        URL url = this.getUrl("products/" + id);
        HttpURLConnection con = null;
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
            con = this.getHttpConnection(url, "PUT");
            con.setDoOutput(true);
            con.connect();
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.write(data);
            out.flush();
            out.close();

            String json = this.getReaderJson(con.getInputStream());
            if (json == null) return;
        } catch (IOException e) {
            new NoInternetException();
        }
    }

    public void updateUser(int id, String firstName, String lastName, String username, String password, String email, String phone, String address, String picture, LocalDate birthDate, String loginProvider) {
        URL url = this.getUrl("users/" + id);
        HttpURLConnection con = null;
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
        URL url = this.getUrl("products/" + id);
        HttpURLConnection con = null;
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
        URL url = this.getUrl("users/" + username + "/" + password);
        String json = this.getReaderJsonConnectionData(url);
        if (json == null) return false;
        return true;
    }

    public String getToken(String username, String password) {
        URL url = this.getUrl("token");
        JSONObject jsonPos = new JSONObject();
        jsonPos.put("username", username);
        jsonPos.put("password", password);

        JSONObject response = postViaHttp(jsonPos, url, false);
        if (response == null) return null;
        return response.getString("token");
    }

    private User getUserFromJson(JSONObject jo) {
        LocalDate date = LocalDate.parse(jo.getString("birthDate"), formatter);
        return new User(jo.getInt("id"), jo.getString("firstName"), jo.getString("lastName"), jo.getString("username"),
                null, jo.getString("email"), jo.getString("phone"), jo.getString("address"),
                jo.getString("picture"), date, jo.getString("loginProvider"));
    }
}
