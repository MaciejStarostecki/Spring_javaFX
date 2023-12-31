package pl.strefakursow.spring_javafx.dto;

import lombok.Data;

@Data
public class QuantityTypeDto {

    private Long idQuantityType;
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
