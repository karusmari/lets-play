package com.letsplay.dto;

public class UpdateUserRequest {
    private String name;
    private String email;
    private String password; // only if the user wants to change it
    private String role;     // only for the admin to change user roles

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
