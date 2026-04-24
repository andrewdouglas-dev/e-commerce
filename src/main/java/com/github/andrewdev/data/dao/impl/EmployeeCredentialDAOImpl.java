package com.github.andrewdev.data.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.andrewdev.data.DatabaseManager;
import com.github.andrewdev.data.dao.EmployeeCredentialDOA;
import com.github.andrewdev.models.EmployeeCredentials;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class EmployeeCredentialDAOImpl implements EmployeeCredentialDOA{
    private static final Logger logger = Logger.getLogger(EmployeeCredentialDAOImpl.class.getName());
    private static final Argon2 argon2 = Argon2Factory.create();

    @Override
    public void create(EmployeeCredentials employee, Connection connection) {
        String statement = "Insert into employeeCredentials (id, password, created_at, updated_at) values (?,?,?,?);";

        try (PreparedStatement pStatement = connection.prepareStatement(statement);) {
            pStatement.setLong(1,employee.getId());
            pStatement.setString(2,employee.getPassword());
            pStatement.setDate(3, java.sql.Date.valueOf(java.time.LocalDate.now()));
            pStatement.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now()));

            boolean recordInserted = pStatement.executeUpdate() == 1;

            if (!recordInserted) {
                throw new RuntimeException("Error occured while adding employee credentials: " + employee.getId());
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error adding employee credential.", e);
            throw new RuntimeException("Error occured while adding employee: " + employee.getId());
        }
    }

    @Override
    public void update(EmployeeCredentials employee) {
        String statement = "Update employeeCredentials SET password = ?, updated_at = ?) where id = ?;";

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement pStatement = connection.prepareStatement(statement);) {
            pStatement.setString(1,employee.getPassword());
            pStatement.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now()));
            pStatement.setLong(3,employee.getId());

            boolean recordUpdated = pStatement.executeUpdate() == 1;

            if (!recordUpdated) {
                throw new RuntimeException("Error occured while updating employee credentials: " + employee.getId());
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating employee credential.", e);
            throw new RuntimeException("Error occured while updating employee credentials: " + employee.getId());
        }
    }

    @Override
    public boolean verify(EmployeeCredentials employee) {
        String statement = "Select password from employeeCredentials where id = ?;";

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement pStatement = connection.prepareStatement(statement);) {
            pStatement.setLong(1, employee.getId());

            try (ResultSet resultSet = pStatement.executeQuery()) {
                if (resultSet.next()) {
                    return argon2.verify(resultSet.getString("password"), employee.getPassword().toCharArray());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("error occured while verifying employee credentials");
        }

        return false;
    }
}