package com.github.andrewdev.dto;

public class EmployeeRequest {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;

    public EmployeeRequest(){}

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return String.format("Employee ID: %s, Name: %s %s, Email: %s, Username: %s", id, firstName, lastName, email, username);
    }

    public void validate() {
        if (this.firstName == null) {
            throw new IllegalArgumentException("Provided employee first name cannot be a empty.");
        }
        if (this.lastName == null) {
            throw new IllegalArgumentException("Provided employee last name cannot be a empty.");
        }
        if (this.email == null) {
            throw new IllegalArgumentException("Provided employee email cannot be a empty.");
        }
        if (this.username == null) {
            throw new IllegalArgumentException("Provided employee username cannot be a empty.");
        }
    }
}
