package ba.unsa.etf.nrs.DAO;

import ba.unsa.etf.nrs.NoInternetException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public abstract  class BaseDAO {
    protected abstract String getBaseUri();

    protected URL getUrl(String uri) {
        URL url = null;
        try {
            url = new URL(this.getBaseUri() + uri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    protected HttpURLConnection getBaseHttpConnection(URL url, String method) throws IOException {
        HttpURLConnection con;
        con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Access-Control-Allow-Origin", "*");
        con.setRequestMethod(method);
        con.setRequestProperty("Content-Type", "application/json");

        return con;
    }

    protected String getReaderJson(InputStream in) {
        try {
            BufferedReader entry = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            StringBuilder json = new StringBuilder();
            String line = "";
            while ((line = entry.readLine()) != null) {
                json.append(line);
            }

            entry.close();
            if (json.length() == 0) return null;
            return (json.toString());
        } catch (IOException e) {
            e.printStackTrace();
            new NoInternetException();
        }
        return null;
    }
}
