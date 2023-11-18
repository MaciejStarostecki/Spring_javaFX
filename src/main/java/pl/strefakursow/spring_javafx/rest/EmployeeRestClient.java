package pl.strefakursow.spring_javafx.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import pl.strefakursow.spring_javafx.dto.EmployeeDto;

import java.util.Arrays;
import java.util.List;

public class EmployeeRestClient {

    private static final String GET_EMPLOYEES_URL = "http://localhost:8080/employees";

    private final RestTemplate restTemplate;

    public EmployeeRestClient() {
        restTemplate = new RestTemplate();
    }

    public List<EmployeeDto> getEmployees() {
        ResponseEntity<EmployeeDto[]> employees = restTemplate.getForEntity(GET_EMPLOYEES_URL, EmployeeDto[].class);
        return Arrays.asList(employees.getBody());
    }

}
