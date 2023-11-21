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
import javafx.scene.layout.BorderPane;
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
        initializeEmployeeButton();
        initializeViewEmployeeButton();
        initializeRefreshButton();
        initializeTableView();


    }

    private void initializeViewEmployeeButton() {
        viewButton.setOnAction(x -> {
            EmployeeTableModel employee = employeeTableView.getSelectionModel().getSelectedItem();
            if(employee == null) {
                return;
            }
            else {
                Stage waitingPopup = popupFactory.createWaitingPopup("Loading employee data...");
                waitingPopup.show();
                Stage viewEmployeeStage = new Stage();
                viewEmployeeStage.initStyle(StageStyle.UNDECORATED);
                viewEmployeeStage.initModality(Modality.APPLICATION_MODAL);
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("view-employee.fxml")));
                try {
                    Scene scene = new Scene((BorderPane)loader.load(), 500, 400);
                    viewEmployeeStage .setScene(scene);
                    ViewEmployeeController controller = loader.<ViewEmployeeController>getController();
                    controller.loadEmployeeData(employee.getIdEmployee(), () -> {
                        waitingPopup.close();
                        viewEmployeeStage.show();
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

    private void initializeRefreshButton() {
        refreshButton.setOnAction(x -> loadEmployeeData());
    }

    private void initializeEmployeeButton() {
        addButton.setOnAction(x -> {
            Stage addEmployeeStage = new Stage();
            addEmployeeStage.initStyle(StageStyle.UNDECORATED);
            addEmployeeStage.initModality(Modality.APPLICATION_MODAL);
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