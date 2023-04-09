package org.example.user.model;

public class UserCredential {
    public String email;
    public String password;

    public UserCredential(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static UserCredential from(User user) {
        return new UserCredential(user.getEmail(), user.getPassword());
    }
}
