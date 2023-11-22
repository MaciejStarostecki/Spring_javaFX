package pl.strefakursow.spring_javafx.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import pl.strefakursow.spring_javafx.dto.EmployeeDto;
import pl.strefakursow.spring_javafx.handler.ProcessFinishedHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EmployeeRestClient {

    private static final String EMPLOYEES_URL = "http://localhost:8080/employees";

    private final RestTemplate restTemplate;

    public EmployeeRestClient() {
        restTemplate = new RestTemplate();
    }

    public List<EmployeeDto> getEmployees() {
        ResponseEntity<EmployeeDto[]> employees = restTemplate.getForEntity(EMPLOYEES_URL, EmployeeDto[].class);
        return Arrays.asList(Objects.requireNonNull(employees.getBody()));
    }

    public void saveEmployee(EmployeeDto dto, ProcessFinishedHandler handler) {
        ResponseEntity<EmployeeDto> responseEntity = restTemplate.postForEntity(EMPLOYEES_URL, dto, EmployeeDto.class);

        if(HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            handler.handle();
        }
        else {
            throw new RuntimeException("Can't save employee.");
        }


    }

    public EmployeeDto getEmployee(Long idEmployee) {
        String url = EMPLOYEES_URL + "/" + idEmployee;
        ResponseEntity<EmployeeDto> responseEntity = restTemplate.getForEntity(url, EmployeeDto.class);
        if(HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            return responseEntity.getBody();
        }
        else {
            //TODO implement
            throw new RuntimeException("Can't load employee.");
        }
    }

    public void deleteEmployee(Long idEmployee, ProcessFinishedHandler handler) {
        restTemplate.delete(EMPLOYEES_URL + "/" + idEmployee);
        handler.handle();
    }
}
