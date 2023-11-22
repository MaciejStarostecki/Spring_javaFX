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
import pl.strefakursow.spring_javafx.handler.ProcessFinishedHandler;
import pl.strefakursow.spring_javafx.rest.ItemRestClient;

import java.net.URL;
import java.util.ResourceBundle;

public class EditItemController implements Initializable {

    @FXML
    private Button cancelButton;

    @FXML
    private Button editButton;

    @FXML
    private BorderPane editItemBorderPane;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField quantityTextField;

    @FXML
    private ComboBox<QuantityTypeDto> quantityTypeComboBox;

    private final PopupFactory popupFactory;
    private final ItemRestClient itemRestClient;
    private WarehouseDto warehouseDto;

    private Long idItem;

    public EditItemController() {
        popupFactory = new PopupFactory();
        itemRestClient = new ItemRestClient();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initializeEditButton();
        initializeCancelButton();

    }

    private void initializeEditButton() {

        editButton.setOnAction(x -> {
            Stage waitingPopup = popupFactory.createWaitingPopup("Connecting to the server...");
            waitingPopup.show();
            Thread thread = new Thread(() -> {
                ItemSaveDto dto = createItemSaveDto();
                itemRestClient.saveItem(dto, () -> Platform.runLater(() -> {
                    waitingPopup.close();
                    Stage infoPopup = popupFactory.createInfoPopup("Employee has been updated!", () ->
                            getStage().close());
                    infoPopup.show();
                }));
            });
            thread.start();
        });

    }

    private ItemSaveDto createItemSaveDto() {
        ItemSaveDto dto = new ItemSaveDto();
        dto.setName(nameTextField.getText());
        dto.setQuantity(Double.valueOf(quantityTextField.getText()));
        dto.setIdQuantityType(quantityTypeComboBox.getSelectionModel().getSelectedItem().getIdQuantityType());
        dto.setIdItem(idItem);
        itemRestClient.saveItem(dto, () ->
                getStage().close());
        return dto;
    }

    public void loadItemData(Long idItem, ProcessFinishedHandler handler) {
        Thread thread = new Thread(() -> {
            ItemEditViewDto dto = itemRestClient.getEditItemData(idItem);
            Platform.runLater(() -> {
                this.idItem = dto.getIdItem();
                nameTextField.setText(dto.getName());
                quantityTextField.setText(dto.getQuantity().toString());
                quantityTypeComboBox.setItems(FXCollections.observableArrayList(dto.getQuantityTypeDtoList()));
                System.out.println(dto.getQuantityTypeDtoList());
                for (int i = 0; i < quantityTypeComboBox.getItems().size(); i++) {
                    QuantityTypeDto quantityTypeDto = quantityTypeComboBox.getItems().get(i);
                    System.out.println(quantityTypeDto);
                    System.out.println(quantityTypeDto.getIdQuantityType());
                    if (quantityTypeDto.getIdQuantityType().equals(dto.getIdQuantityType())) {
                        quantityTypeComboBox.getSelectionModel().select(i);
                    }
                }
                quantityTypeComboBox.getSelectionModel().select(0);
                handler.handle();
            });
        });
        thread.start();
    }


    private void initializeCancelButton() {
        cancelButton.setOnAction(x ->
                getStage().close());
    }


    private Stage getStage() {
        return (Stage) editItemBorderPane.getScene().getWindow();
    }

}
