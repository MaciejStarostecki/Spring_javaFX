package pl.strefakursow.spring_javafx.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pl.strefakursow.spring_javafx.dto.EmployeeDto;
import pl.strefakursow.spring_javafx.factory.PopupFactory;
import pl.strefakursow.spring_javafx.rest.EmployeeRestClient;
import pl.strefakursow.spring_javafx.table.EmployeeTableModel;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class EmployeeController implements Initializable {

    private final EmployeeRestClient employeeRestClient;
    private final ObservableList<EmployeeTableModel> data;
    private final PopupFactory popupFactory;

    @FXML
    private TableView<EmployeeTableModel> employeeTableView;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private Button viewButton;

    @FXML
    private Button refreshButton;


    public EmployeeController() {
        employeeRestClient = new EmployeeRestClient();
        this.data = FXCollections.observableArrayList();
        popupFactory = new PopupFactory();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeAddEmployeeButton();
        initializeViewEmployeeButton();
        initializeEditEmployeeButton();
        initializeDeleteEmployeeButton();
        initializeRefreshButton();
        initializeTableView();


    }

    private void initializeDeleteEmployeeButton() {
        deleteButton.setOnAction(x -> {
            EmployeeTableModel selectedEmployee = employeeTableView.getSelectionModel().getSelectedItem();
            if (selectedEmployee != null) {
                Stage waitingPopup = popupFactory.createWaitingPopup("Loading employee data...");
                waitingPopup.show();
                Stage deleteEmployeeStage = createEmployeeCrudStage();
                try {
                    FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("delete-employee.fxml")));
                    Scene scene = new Scene(loader.load(), 400, 210);
                    deleteEmployeeStage.setScene(scene);
                    DeleteEmployeeController controller = loader.getController();
                    controller.loadEmployeeData(selectedEmployee);
                    waitingPopup.close();
                    deleteEmployeeStage.show();
                } catch (IOException e) {
                    throw new RuntimeException("Can't load fxml file delete-employee.fxml " + e);
                }
            }
        });
    }

    private void initializeEditEmployeeButton() {
        editButton.setOnAction(x -> {
            EmployeeTableModel selectedEmployee = employeeTableView.getSelectionModel().getSelectedItem();
            if (selectedEmployee != null) {
                Stage waitingPopup = popupFactory.createWaitingPopup("Loading employee data...");
                waitingPopup.show();
                Stage editEmployeeStage = createEmployeeCrudStage();
                try {
                    FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("edit-employee.fxml")));
                    Scene scene = new Scene(loader.load(), 500, 400);
                    editEmployeeStage.setScene(scene);
                    EditEmployeeController controller = loader.getController();
                    controller.loadEmployeeData(selectedEmployee.getIdEmployee(), () -> {
                        waitingPopup.close();
                        editEmployeeStage.show();
                    });
                } catch (IOException e) {
                    throw new RuntimeException("Can't load fxml file edit-employee.fxml " + e);
                }
            }
        });
    }

    private Stage createEmployeeCrudStage() {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        return stage;
    }

    private void initializeViewEmployeeButton() {
        viewButton.setOnAction(x -> {
            EmployeeTableModel employee = employeeTableView.getSelectionModel().getSelectedItem();
            if(employee != null) {
                Stage waitingPopup = popupFactory.createWaitingPopup("Loading employee data...");
                waitingPopup.show();
                Stage viewEmployeeStage = createEmployeeCrudStage();
                try {
                    FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("view-employee.fxml")));
                    Scene scene = new Scene(loader.load(), 500, 400);
                    viewEmployeeStage .setScene(scene);
                    ViewEmployeeController controller = loader.getController();
                    controller.loadEmployeeData(employee.getIdEmployee(), () -> {
                        waitingPopup.close();
                        viewEmployeeStage.show();
                    });
                } catch (IOException e) {
                    throw new RuntimeException("Can't load fxml file view-employee.fxml " + e);
                }
            }
        });
    }

    private void initializeRefreshButton() {
        refreshButton.setOnAction(x -> loadEmployeeData());
    }

    private void initializeAddEmployeeButton() {
        addButton.setOnAction(x -> {
            Stage addEmployeeStage = createEmployeeCrudStage();
            try {
                Parent addEmployeeParent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("add-employee.fxml")));
                Scene scene = new Scene(addEmployeeParent, 500, 400);
                addEmployeeStage.setScene(scene);
                addEmployeeStage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void initializeTableView() {
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

        loadEmployeeData();

        employeeTableView.setItems(data);
    }

    private void loadEmployeeData() {
        Thread thread = new Thread(() -> Platform.runLater(() -> {
            List<EmployeeDto> employees = employeeRestClient.getEmployees();
            data.clear();
            data.addAll(employees.stream().map(EmployeeTableModel::of).toList());
        }));
        thread.start();
    }
}