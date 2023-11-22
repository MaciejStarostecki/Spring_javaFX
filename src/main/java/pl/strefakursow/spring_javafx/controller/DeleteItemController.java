package pl.strefakursow.spring_javafx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pl.strefakursow.spring_javafx.factory.PopupFactory;
import pl.strefakursow.spring_javafx.rest.ItemRestClient;
import pl.strefakursow.spring_javafx.table.EmployeeTableModel;
import pl.strefakursow.spring_javafx.table.ItemTableModel;

import java.net.URL;
import java.util.ResourceBundle;

public class DeleteItemController implements Initializable {

    @FXML
    private Button cancelButton;

    @FXML
    private Button deleteButton;

    @FXML
    private BorderPane deleteItemBorderPane;

    @FXML
    private Label nameLabel;

    @FXML
    private Label quanityLabel;

    private final ItemRestClient itemRestClient;
    private final PopupFactory popupFactory;
    private Long idItemToDelete;

    public DeleteItemController() {
        itemRestClient = new ItemRestClient();
        popupFactory = new PopupFactory();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeDeleteButton();
        initializeCancelButton();
    }

    private void initializeDeleteButton() {

        deleteButton.setOnAction(x -> {
            Stage waitingPopup = popupFactory.createWaitingPopup("Deleting item...");
            waitingPopup.show();
            Thread thread = new Thread(() -> itemRestClient.deleteItem(idItemToDelete, () -> Platform.runLater(() -> {
                waitingPopup.close();
                Stage infoPopup = popupFactory.createInfoPopup("Item has been deleted.", () ->
                        getStage().close());
                infoPopup.show();
            })));
            thread.start();
        });

    }

    private void initializeCancelButton() {
        cancelButton.setOnAction(x ->
                getStage().close());
    }

    public void loadItemData(ItemTableModel item) {
        this.idItemToDelete = item.getIdItem();
        nameLabel.setText(item.getName());
        quanityLabel.setText(item.getQuantity() + " " + item.getQuantityType());

    }

    private Stage getStage() {
        return (Stage) deleteItemBorderPane.getScene().getWindow();
    }
}