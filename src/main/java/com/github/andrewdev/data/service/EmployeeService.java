package com.github.andrewdev.data.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import com.github.andrewdev.data.dao.EmployeeCredentialDOA;
import com.github.andrewdev.data.dao.EmployeeDAO;
import com.github.andrewdev.data.dao.impl.EmployeeCredentialDAOImpl;
import com.github.andrewdev.data.dao.impl.EmployeeDAOImpl;
import com.github.andrewdev.models.Employee;
import com.github.andrewdev.models.EmployeeCredentials;
import com.github.andrewdev.utilities.HashingUtils;

public class EmployeeService {
    EmployeeDAO employeeDAO;
    EmployeeCredentialDOA credentialDAO;

    public EmployeeService(){
        this.employeeDAO = new EmployeeDAOImpl();
        this.credentialDAO = new EmployeeCredentialDAOImpl();
    }

    public Employee createEmployee(Employee employee, String credentials) {
        employeeDAO.create(employee);
        credentialDAO.create(buildCredentialsWithHash(credentials));

        return employee;
    }

    public List<Employee> findAll() {
        return employeeDAO.getAll();
    }

    public EmployeeCredentials buildCredentialsToVerify(String encodedCredentials) {

        String decodedCredentials = decodeBase64(encodedCredentials);

        String[] splitCredentials = decodedCredentials.split(":");

        return new EmployeeCredentials(getID(splitCredentials[0]), splitCredentials[1]);
    }

    public EmployeeCredentials buildCredentialsWithHash(String encodedCredentials) {
        String decodedCredentials = decodeBase64(encodedCredentials);
        String[] splitCredentials = decodedCredentials.split(":");

        return new EmployeeCredentials(getID(splitCredentials[0]), HashingUtils.hashPassword(splitCredentials[1]));
    }

    public boolean verifyCredentials(EmployeeCredentials credentials) {
        return credentialDAO.verify(credentials);
    }

    private Long getID(String email) {
        return employeeDAO.getIdByEmail(email);
    }

    private String decodeBase64(String encodedString) {
        if (encodedString == null || !encodedString.startsWith("Basic")) {
            throw new IllegalArgumentException("Provided authorization is not correct.");
        }

        encodedString = encodedString.substring(6);

        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);

        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}
