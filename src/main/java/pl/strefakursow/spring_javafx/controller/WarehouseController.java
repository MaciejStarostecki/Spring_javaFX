package pl.strefakursow.spring_javafx.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import pl.strefakursow.spring_javafx.dto.EmployeeDto;
import pl.strefakursow.spring_javafx.dto.ItemDto;
import pl.strefakursow.spring_javafx.rest.ItemRestClient;
import pl.strefakursow.spring_javafx.table.EmployeeTableModel;
import pl.strefakursow.spring_javafx.table.ItemTableModel;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class WarehouseController implements Initializable {

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private Button refreshButton;

    @FXML
    private Button viewButton;

    @FXML
    private BorderPane warehouseBorderPane;

    @FXML
    private TableView<ItemTableModel> warehouseTableView;

    private final ItemRestClient itemRestClient;

    private final ObservableList<ItemTableModel> data;

    public WarehouseController() {
        itemRestClient = new ItemRestClient();
        this.data = FXCollections.observableArrayList();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeTableView();

    }

    private void initializeTableView() {
        warehouseTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn nameColumn = new TableColumn<>("Name");
        nameColumn.setMinWidth(100);
        nameColumn.setCellValueFactory(new PropertyValueFactory<EmployeeTableModel, String>("name"));

        TableColumn quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setMinWidth(100);
        quantityColumn.setCellValueFactory(new PropertyValueFactory<EmployeeTableModel, Double>("quantity"));

        TableColumn quantityTypeColumn = new TableColumn<>("Quantity Type");
        quantityTypeColumn.setMinWidth(100);
        quantityTypeColumn.setCellValueFactory(new PropertyValueFactory<EmployeeTableModel, String>("quantityType"));

        warehouseTableView.getColumns().addAll(nameColumn, quantityColumn, quantityTypeColumn);

        loadItemData();

        warehouseTableView.setItems(data);
    }

    private void loadItemData() {
        Thread thread = new Thread(() -> Platform.runLater(() -> {
            List<ItemDto> items = itemRestClient.getItems();
            data.clear();
            data.addAll(items.stream().map(ItemTableModel::of).toList());
        }));
        thread.start();
    }
}
