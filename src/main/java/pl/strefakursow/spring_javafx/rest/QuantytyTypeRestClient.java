package pl.strefakursow.spring_javafx.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import pl.strefakursow.spring_javafx.dto.QuantityTypeDto;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class QuantytyTypeRestClient {

    private static final String QUANTITY_TYPES_URL = "http://localhost:8080/quantity_types";
    private final RestTemplate restTemplate;

    public QuantytyTypeRestClient() {
        restTemplate = new RestTemplate();
    }

    public List<QuantityTypeDto> getQuantityTypes() {
        ResponseEntity<QuantityTypeDto[]> quantityTypeResponseEntity = restTemplate.getForEntity(QUANTITY_TYPES_URL, QuantityTypeDto[].class);
        return Arrays.asList(Objects.requireNonNull(quantityTypeResponseEntity.getBody()));
    }
}
