package pl.strefakursow.spring_javafx.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import pl.strefakursow.spring_javafx.dto.ItemDto;
import pl.strefakursow.spring_javafx.dto.ItemSaveDto;
import pl.strefakursow.spring_javafx.handler.ProcessFinishedHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ItemRestClient {

    private static final String ITEMS_URL = "http://localhost:8080/items";

    private final RestTemplate restTemplate;

    public ItemRestClient() {
        restTemplate = new RestTemplate();
    }

    public List<ItemDto> getItems() {
        ResponseEntity<ItemDto[]> itemResponseEntity = restTemplate.getForEntity(ITEMS_URL, ItemDto[].class);
        return Arrays.asList(Objects.requireNonNull(itemResponseEntity.getBody()));
    }

    public void saveItem(ItemSaveDto dto, ProcessFinishedHandler processFinishedHandler) {
        ResponseEntity<ItemDto> responseEntity = restTemplate.postForEntity(ITEMS_URL, dto, ItemDto.class);
        if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            processFinishedHandler.handle();
        }
        else
            throw new RuntimeException("Can't same dto: " + dto);
    }
}
