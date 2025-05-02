public abstract class User {
    protected int userId;
    protected String username;
    protected String password;
    protected String name;
    protected String email;
    protected String contactInfo;

    abstract void login();
    abstract void logout();
    abstract void updateProfile();
}
