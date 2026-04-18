package com.github.andrewdev.data.dao;

import com.github.andrewdev.models.EmployeeCredentials;

public interface EmployeeCredentialDOA {
    public Long create(EmployeeCredentials e);
    public Long update(EmployeeCredentials e);
    public boolean verify(EmployeeCredentials e);
}