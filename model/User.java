package model;

public class User {

    private int userId;
    private String username;
    private String password;

    // Creates an empty user object.
    public User() {
    }

    // Creates a user object with a username and password.
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Creates a user object with all its details.
    public User(int userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    // Returns the user id.
    public int getUserId() {
        return userId;
    }

    // Sets the user id.
    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Returns the username.
    public String getUsername() {
        return username;
    }

    // Sets the username.
    public void setUsername(String username) {
        this.username = username;
    }

    // Returns the password.
    public String getPassword() {
        return password;
    }

    // Sets the password.
    public void setPassword(String password) {
        this.password = password;
    }
}
