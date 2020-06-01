package ba.unsa.etf.nrs.Services;

public class AuthService {
    private static AuthService instance;

    private String token;
    private String username;
    private String role;

    private AuthService() {}

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public void setData(String token, String username, String role) {
        this.role = role;
        this.username = username;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
