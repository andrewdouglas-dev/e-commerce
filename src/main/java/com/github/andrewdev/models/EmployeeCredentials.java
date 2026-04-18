package com.github.andrewdev.models;

public class EmployeeCredentials {
    private final Long id;
    private final String password;

    public EmployeeCredentials(Long id, String password) {
        this.id = id;
        this.password = password;
    }

    public Long getId() {
        return this.id;
    }

    public String getPassword() {
        return this.password;
    }
}
