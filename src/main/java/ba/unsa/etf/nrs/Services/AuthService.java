package ba.unsa.etf.nrs.Services;

import ba.unsa.etf.nrs.DataClasses.Role;

public class AuthService {
    private static AuthService instance;

    private String token;
    private String username;
    private Role role;

    private AuthService() {}

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public void setData(String token, String username, Role role) {
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
