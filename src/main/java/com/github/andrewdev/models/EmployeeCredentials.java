package com.github.andrewdev.models;

public class EmployeeCredentials {
    private final Long id;
    private final String password;
    private final String salt;

    public EmployeeCredentials(Long id, String password, String salt) {
        this.id = id;
        this.password = password;
        this.salt = salt;
    }

    public Long getId() {
        return this.id;
    }

    public String getPassword() {
        return this.password;
    }

    public String getSalt() {
        return this.salt;
    }
}
