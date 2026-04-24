package com.github.andrewdev.data.dao;

import java.sql.Connection;

import com.github.andrewdev.models.EmployeeCredentials;

public interface EmployeeCredentialDOA {
    public void create(EmployeeCredentials employee, Connection connection);
    public void  update(EmployeeCredentials employee);
    public boolean verify(EmployeeCredentials employee);
}