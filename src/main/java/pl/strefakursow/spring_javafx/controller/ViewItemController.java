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
import pl.strefakursow.spring_javafx.dto.EmployeeDto;
import pl.strefakursow.spring_javafx.dto.ItemDto;
import pl.strefakursow.spring_javafx.handler.ProcessFinishedHandler;
import pl.strefakursow.spring_javafx.rest.ItemRestClient;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewItemController implements Initializable {


    @FXML
    private TextField nameTextField;

    @FXML
    private Button okButton;

    @FXML
    private TextField quantityTextField;

    @FXML
    private ComboBox<String> quantityTypeComboBox;

    @FXML
    private BorderPane viewItemBorderPane;

    private final ItemRestClient itemRestClient;

    public ViewItemController() {
        itemRestClient = new ItemRestClient();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeControls();
        initializeOkButton();

    }

    private void initializeControls() {
        nameTextField.setEditable(false);
        quantityTextField.setEditable(false);
        quantityTypeComboBox.setEditable(false);
    }

    public void loadItemData(Long idItem, ProcessFinishedHandler handler) {
        Thread thread = new Thread(() -> {
            ItemDto dto = itemRestClient.getItem(idItem);
            Platform.runLater(() -> {
                nameTextField.setText(dto.getName());
                quantityTextField.setText(String.valueOf(dto.getQuantity()));
                quantityTypeComboBox.setItems(FXCollections.observableArrayList(dto.getQuantityType()));
                quantityTypeComboBox.getSelectionModel().select(0);
                handler.handle();
            });
        });
        thread.start();

    }

    private void initializeOkButton() {
        okButton.setOnAction(x ->
                getStage().close());
    }

    private Stage getStage() {
        return (Stage) viewItemBorderPane.getScene().getWindow();
    }
}
