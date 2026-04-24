package com.github.andrewdev.data.dao;

import java.sql.Connection;
import java.util.List;

import com.github.andrewdev.models.Employee;

public interface EmployeeDAO {
    public Long create(Employee employee, Connection connection);
    public Employee getById(Long id);
    public Long getIdByEmail(String email);
    public List<Employee> getAll();
    public void update(Employee emloyee);
    public void delete(Long id);
}
