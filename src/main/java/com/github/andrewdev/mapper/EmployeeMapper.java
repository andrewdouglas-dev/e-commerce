package com.github.andrewdev.mapper;

import com.github.andrewdev.dto.EmployeeRequest;
import com.github.andrewdev.models.Employee;

public class EmployeeMapper {
    private EmployeeMapper(){}

    public static Employee convertToNewEmployee(EmployeeRequest employeeRequest) {
        return new Employee(
            employeeRequest.getFirstName(), 
            employeeRequest.getLastName(), 
            employeeRequest.getEmail(), 
            employeeRequest.getUsername()
        );
    }
}
