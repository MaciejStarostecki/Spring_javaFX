package pl.strefakursow.spring_javafx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pl.strefakursow.spring_javafx.factory.PopupFactory;
import pl.strefakursow.spring_javafx.rest.EmployeeRestClient;
import pl.strefakursow.spring_javafx.table.EmployeeTableModel;

import java.net.URL;
import java.util.ResourceBundle;

public class DeleteEmployeeController implements Initializable {

    private final EmployeeRestClient employeeRestClient;
    private final PopupFactory popupFactory;


    @FXML
    private Button cancelButton;

    @FXML
    private Button deleteButton;

    @FXML
    private BorderPane deleteEmployeeBorderPane;

    @FXML
    private Label firstNameLabel;

    @FXML
    private Label lastNameLabel;

    private Long idEmployee;

    public DeleteEmployeeController() {
        employeeRestClient = new EmployeeRestClient();
        popupFactory = new PopupFactory();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeCancelButton();
        initializeDeleteButton();
    }

    private void initializeDeleteButton() {
        deleteButton.setOnAction(x -> {
            Stage waitingPopup = popupFactory.createWaitingPopup("Deleting employee...");
            waitingPopup.show();
            Thread thread = new Thread(() -> employeeRestClient.deleteEmployee(idEmployee, () -> Platform.runLater(() -> {
                waitingPopup.close();
                Stage infoPopup = popupFactory.createInfoPopup("Employee has been deleted.", () ->
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


    private Stage getStage() {
        return (Stage) deleteEmployeeBorderPane.getScene().getWindow();
    }

    public void loadEmployeeData(EmployeeTableModel employee) {
                this.idEmployee = employee.getIdEmployee();
                firstNameLabel.setText(employee.getFirstName());
                lastNameLabel.setText(employee.getLastName());

    }
}

