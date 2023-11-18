package pl.strefakursow.spring_javafx.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import pl.strefakursow.spring_javafx.dto.EmployeeDto;
import pl.strefakursow.spring_javafx.rest.EmployeeRestClient;
import pl.strefakursow.spring_javafx.table.EmployeeTableModel;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AppController implements Initializable {

    private final EmployeeRestClient employeeRestClient;

    @FXML
    private TableView<EmployeeTableModel> employeeTableView;

    public AppController() {
        employeeRestClient = new EmployeeRestClient();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        employeeTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setMinWidth(100);
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<EmployeeTableModel, String>("firstName"));

        TableColumn lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setMinWidth(100);
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<EmployeeTableModel, String>("lastName"));

        TableColumn salaryColumn = new TableColumn<>("Salary");
        salaryColumn.setMinWidth(100);
        salaryColumn.setCellValueFactory(new PropertyValueFactory<EmployeeTableModel, String>("salary"));

        employeeTableView.getColumns().addAll(firstNameColumn, lastNameColumn, salaryColumn);

        ObservableList<EmployeeTableModel> data = FXCollections.observableArrayList();

        loadEmployeeData(data);

        employeeTableView.setItems(data);



    }

    private void loadEmployeeData(ObservableList<EmployeeTableModel> data) {
        Thread thread = new Thread(() -> {
            Platform.runLater(() -> {
                List<EmployeeDto> employees = employeeRestClient.getEmployees();
                data.addAll(employees.stream().map(EmployeeTableModel::of).collect(Collectors.toList()));
            });
        });
        thread.start();
    }
}
