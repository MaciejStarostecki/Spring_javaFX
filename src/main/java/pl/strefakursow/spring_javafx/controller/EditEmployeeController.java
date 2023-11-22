package pl.strefakursow.spring_javafx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pl.strefakursow.spring_javafx.dto.EmployeeDto;
import pl.strefakursow.spring_javafx.factory.PopupFactory;
import pl.strefakursow.spring_javafx.handler.ProcessFinishedHandler;
import pl.strefakursow.spring_javafx.rest.EmployeeRestClient;

import java.net.URL;
import java.util.ResourceBundle;

public class EditEmployeeController implements Initializable {

    private final EmployeeRestClient employeeRestClient;
    private final PopupFactory popupFactory;

    @FXML
    private Button cancelButton;

    @FXML
    private Button editButton;

    @FXML
    private BorderPane editEmployeeBorderPane;

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private TextField salaryTextField;

    private Long idEmployee;

    public EditEmployeeController() {
        employeeRestClient = new EmployeeRestClient();
        popupFactory = new PopupFactory();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeCancelButton();
        initializeEditButton();
    }

    private void initializeEditButton() {
        editButton.setOnAction(x -> {
            Stage waitingPopup = popupFactory.createWaitingPopup("Connecting to the server...");
            waitingPopup.show();
            Thread thread = new Thread(() -> {
                EmployeeDto dto = createEmployeeDto();
                employeeRestClient.saveEmployee(dto, () -> Platform.runLater(() -> {
                    waitingPopup.close();
                    Stage infoPopup = popupFactory.createInfoPopup("Employee has been updated!", () ->
                            getStage().close());
                    infoPopup.show();
                }));
            });
            thread.start();
        });
    }

    private EmployeeDto createEmployeeDto() {
        String firstname = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String salary = salaryTextField.getText();
        EmployeeDto dto = new EmployeeDto();
        dto.setIdEmployee(idEmployee);
        dto.setFirstName(firstname);
        dto.setLastName(lastName);
        dto.setSalary(salary);
        return dto;
    }

    public void loadEmployeeData(Long idEmployee, ProcessFinishedHandler handler) {
        Thread thread = new Thread(() -> {
            EmployeeDto dto = employeeRestClient.getEmployee(idEmployee);
            Platform.runLater(() -> {
                this.idEmployee = idEmployee;
                firstNameTextField.setText(dto.getFirstName());
                lastNameTextField.setText(dto.getLastName());
                salaryTextField.setText(dto.getSalary());
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
        return (Stage) editEmployeeBorderPane.getScene().getWindow();
    }

}
