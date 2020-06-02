package ba.unsa.etf.nrs.DAO;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class EmailDao extends BaseDAO {
    private static EmailDao instance;

    public JSONObject getData(String email) throws IOException {
        URL location = new URL("https://api.email-validator.net/api/verify?EmailAddress=" + email +
                "&APIKey=ev-8e447efe7fc80859c2328a1b795475bc");
        String json = this.getReaderJson(location.openStream());
        return new JSONObject(json);
    }

    private EmailDao() {

    }

    @Override
    protected String getBaseUri() {
        return "";
    }


    public static EmailDao getInstance() {
        if(instance == null) instance = new EmailDao();
        return instance;
    }
}
