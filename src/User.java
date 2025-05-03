public abstract class User {
    private String userId;
    private String username;
    private String password;
    private String name;
    private String email;
    private String contactInfo;

    public User() {}


    public User(String userId, String username, String password, String name, String email, String contactInfo) {
        this.userId = userId;
        setUsername(username);
        setPassword(password);
        this.name = name;
        setEmail(email);
        this.contactInfo = contactInfo;
    }

    abstract String login();
    abstract String logout();
    abstract String updateProfile();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 charcters long");
        } else {
            this.username = username;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password.length() < 6 ) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        } else if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contains at least one number");
        } else {
            this.password = password;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email.matches(".*@.*")) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("Email is not valid");
        }
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}
