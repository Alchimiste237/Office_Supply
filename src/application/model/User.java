package application.model;

public class User {
	private int userId;
    private String username;
    private String address;
    private String date;
    private String password;
    private String salt;
     // Store the *hashed* password
    private String role; // e.g., "admin", "user"

    public User() {}

    public User(String username, String address, String password, String role) {
        
        this.username = username;
        this.address = address;
        this.role = role;
        this.password = password;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }
    
    
    
    
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
