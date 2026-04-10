package com.github.andrewdev.data.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.andrewdev.data.DatabaseManager;
import com.github.andrewdev.data.dao.EmployeeDAO;
import com.github.andrewdev.models.Employee;

public class EmployeeDAOImpl implements EmployeeDAO{
    private static final Logger logger = Logger.getLogger(EmployeeDAOImpl.class.getName());

    @Override
    public Long create(Employee E) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Employee getById(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Employee> getAll() {
        String statement = "Select id,first_name,last_name,email,username from employees;";
        List<Employee> employees = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement pStatement = connection.prepareStatement(statement);
            ResultSet resultSet= pStatement.executeQuery();) {

            while (resultSet.next()) {
                employees.add(mapResultSetToEmployee(resultSet));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving employees.", e);
            throw new RuntimeException("Error occured while finding all employees",e);
        }

        return employees;
    }

    @Override
    public void update(Employee e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public Employee mapResultSetToEmployee(ResultSet rs) throws SQLException{
        return new Employee(
            rs.getLong("id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("email"),
            rs.getString("username")
        );
    }
}
