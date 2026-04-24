package com.github.andrewdev.data.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    public Long create(Employee employee, Connection connection) {
        String statement = "Insert into employees (first_name, last_name, email, username, created_at, updated_at) values (?,?,?,?,?,?);";

        try (PreparedStatement pStatement = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);) {
            pStatement.setString(1, employee.getFirstName());
            pStatement.setString(2, employee.getLastName());
            pStatement.setString(3, employee.getEmail());
            pStatement.setString(4, employee.getUsername());
            pStatement.setDate(5, java.sql.Date.valueOf(java.time.LocalDate.now()));
            pStatement.setDate(6, java.sql.Date.valueOf(java.time.LocalDate.now()));

            boolean recordInserted = pStatement.executeUpdate() == 1;

            if (!recordInserted) {
                throw new RuntimeException("Error occured while adding employee: " + employee.getFirstName());
            }

            try (ResultSet generatedKey = pStatement.getGeneratedKeys()) {
                if (generatedKey.next()) {
                    logger.info("Successfully inserted a record to Employee table");
                    Long newId = generatedKey.getLong(1);

                    employee.setId(newId);

                    return newId;
                } else {
                    logger.log(Level.SEVERE, "Error adding employee. HBERE");
                    throw new SQLException("Failed to retrieve generated ID");
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error occured while adding employee: " + employee.getFirstName(),e);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error adding employee.", e);
            throw new RuntimeException("Error occured while adding employee",e);
        }
    }

    @Override
    public Employee getById(Long id) {
        String statement = "Select id,first_name,last_name,email,username from employees where id = ?;";

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement pStatement = connection.prepareStatement(statement);) {
            pStatement.setLong(1, id);

            ResultSet resultSet = pStatement.executeQuery();

            return mapResultSetToEmployee(resultSet);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occured while finding employee by id.", e);
            throw new RuntimeException("Error occured while finding employee by id.",e);
        }
    }

    @Override
    public Long getIdByEmail(String email) {
        String statement = "Select id from employees where email = ?;";

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement pStatement = connection.prepareStatement(statement)) {
            pStatement.setString(1, email);

            try (ResultSet resultSet = pStatement.executeQuery()) {
                if (resultSet.next()) {
                    return (Long) resultSet.getLong("id");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error occured while finding employee by email.",e);
        }

        return null;
    }

    @Override
    public List<Employee> getAll() {
        String statement = "Select id,first_name,last_name,email,username from employees;";

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement pStatement = connection.prepareStatement(statement);
            ResultSet resultSet= pStatement.executeQuery();) {
            List<Employee> employees = new ArrayList<>();

            while (resultSet.next()) {
                employees.add(mapResultSetToEmployee(resultSet));
            }

            return employees;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving employees.", e);
            throw new RuntimeException("Error occured while finding all employees",e);
        }
    }

    @Override
    public void update(Employee employee) {
        String statement = "Update employees SET first_name = ?, last_name = ?, email = ?, username = ?, updated_at = ? where id = ?";

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement pStatement = connection.prepareStatement(statement);) {

            if (employee.getFirstName() != null) {
                pStatement.setString(1, employee.getFirstName());
            }
            if (employee.getLastName() != null) {
                pStatement.setString(2, employee.getLastName());
            }
            if (employee.getEmail() != null) {
                pStatement.setString(3, employee.getEmail());
            }
            if (employee.getUsername() != null) {
                pStatement.setString(4, employee.getUsername());
            }
            pStatement.setDate(5, java.sql.Date.valueOf(java.time.LocalDate.now()));
            pStatement.setLong(6, employee.getId());

            boolean recordUpdated = pStatement.executeUpdate() == 1;

            if (!recordUpdated) {
                throw new RuntimeException("No employee found with ID: " + employee.getId());
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating employees.", e);
            throw new RuntimeException("Error occured while updating employee",e);
        }
    }

    @Override
    public void delete(Long id) {
        String statement = "Delete from employees where id = ?;";

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement pStatement = connection.prepareStatement(statement);) {

            pStatement.setLong(1, id);

            boolean success = pStatement.executeUpdate() == 1;

            if (!success) {
                throw new IllegalArgumentException("No employee with id: " + id);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving employees.", e);
            throw new RuntimeException("Error occured while finding all employees",e);
        }
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
