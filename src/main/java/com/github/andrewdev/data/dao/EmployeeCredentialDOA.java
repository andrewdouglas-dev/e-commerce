package com.github.andrewdev.data.dao;

import com.github.andrewdev.models.EmployeeCredentials;

public interface EmployeeCredentialDOA {
    public void create(EmployeeCredentials e);
    public void  update(EmployeeCredentials e);
    public boolean verify(EmployeeCredentials e);
}