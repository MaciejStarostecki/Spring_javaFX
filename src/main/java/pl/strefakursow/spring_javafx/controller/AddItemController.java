package pl.strefakursow.spring_javafx.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pl.strefakursow.spring_javafx.dto.*;
import pl.strefakursow.spring_javafx.factory.PopupFactory;
import pl.strefakursow.spring_javafx.rest.ItemRestClient;
import pl.strefakursow.spring_javafx.rest.QuantytyTypeRestClient;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AddItemController implements Initializable {

    @FXML
    private BorderPane addItemBorderPane;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField quantityTextField;

    @FXML
    private ComboBox<QuantityTypeDto> quantityTypeComboBox;

    @FXML
    private Button saveButton;

    private WarehouseDto warehouseDto;
    private final ItemRestClient itemRestClient;
    private final QuantytyTypeRestClient quantytyTypeRestClient;

    private final PopupFactory popupFactory;

    public AddItemController() {
        itemRestClient = new ItemRestClient();
        quantytyTypeRestClient = new QuantytyTypeRestClient();
        popupFactory = new PopupFactory();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeCancelButton();
        initializeSaveButton();
    }

    private void initializeSaveButton() {
        saveButton.setOnAction(x -> {
            ItemSaveDto dto = createItemSaveDto();
            Stage waitingPopup = popupFactory.createWaitingPopup("Adding item to the server...");
            waitingPopup.show();
            Thread thread = new Thread(() -> itemRestClient.saveItem(dto, () -> Platform.runLater(() -> {
                waitingPopup.close();
                Stage infoPopup = popupFactory.createInfoPopup("Item has been saved.", () ->
                        getStage().close());
                infoPopup.show();
            })));
            thread.start();
        });
    }

    public void loadQuantityTypes() {
        Thread thread = new Thread(() -> {
            List<QuantityTypeDto> quantityTypes = quantytyTypeRestClient.getQuantityTypes();
            Platform.runLater(() -> quantityTypeComboBox.setItems(FXCollections.observableArrayList(quantityTypes)));
        });
        thread.start();
    }

    private ItemSaveDto createItemSaveDto() {
        ItemSaveDto dto = new ItemSaveDto();
        dto.setName(nameTextField.getText());
        dto.setQuantity(Double.valueOf(quantityTextField.getText()));
        dto.setIdQuantityType(quantityTypeComboBox.getSelectionModel().getSelectedItem().getIdQuantityType());
        dto.setIdWarehouse(warehouseDto.getIdWarehouse());
        return dto;
    }



    private void initializeCancelButton() {
        cancelButton.setOnAction(x ->
                getStage().close());
    }

    private Stage getStage() {
        return (Stage) addItemBorderPane.getScene().getWindow();
    }

    public void setWarehouseDto(WarehouseDto selectedWarehouseDto) {
        warehouseDto = selectedWarehouseDto;
    }
}
