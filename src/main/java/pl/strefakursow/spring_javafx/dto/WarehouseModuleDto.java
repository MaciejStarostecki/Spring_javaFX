package pl.strefakursow.spring_javafx.dto;

import lombok.Data;

import java.util.List;

@Data
public class WarehouseModuleDto {

    private WarehouseDto selectedWarehouse;
    private List<WarehouseDto> warehouseDtoList;
    private List<ItemDto> itemDtoList;

}
