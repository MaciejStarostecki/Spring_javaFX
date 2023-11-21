package pl.strefakursow.spring_javafx.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import pl.strefakursow.spring_javafx.dto.EmployeeDto;
import pl.strefakursow.spring_javafx.dto.ItemDto;
import pl.strefakursow.spring_javafx.dto.WarehouseDto;
import pl.strefakursow.spring_javafx.dto.WarehouseModuleDto;
import pl.strefakursow.spring_javafx.rest.ItemRestClient;
import pl.strefakursow.spring_javafx.rest.WarehouseRestClient;
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

    @FXML
    private ComboBox<WarehouseDto> warehouseComboBox;

    private final ItemRestClient itemRestClient;

    private final WarehouseRestClient warehouseRestClient;

    private final ObservableList<ItemTableModel> data;

    public WarehouseController() {
        itemRestClient = new ItemRestClient();
        this.data = FXCollections.observableArrayList();
        warehouseRestClient = new WarehouseRestClient();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeTableView();
        initializeComboBox();

    }

    private void initializeComboBox() {
        warehouseComboBox.valueProperty().addListener(((observableValue, oldValue, newValue) -> {
            if (newValue == null)
                return;
            if (!newValue.equals(oldValue) && oldValue != null) {
                WarehouseDto warehouseDto = warehouseComboBox.getSelectionModel().getSelectedItem();
                loadItemData(warehouseDto);
            }
        }));
    }

    private void initializeTableView() {
        warehouseTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn nameColumn = new TableColumn<>("Name");
        nameColumn.setMinWidth(100);
        nameColumn.setCellValueFactory(new PropertyValueFactory<ItemTableModel, String>("name"));

        TableColumn quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setMinWidth(100);
        quantityColumn.setCellValueFactory(new PropertyValueFactory<ItemTableModel, Double>("quantity"));

        TableColumn quantityTypeColumn = new TableColumn<>("Quantity Type");
        quantityTypeColumn.setMinWidth(100);
        quantityTypeColumn.setCellValueFactory(new PropertyValueFactory<ItemTableModel, String>("quantityType"));

        warehouseTableView.getColumns().addAll(nameColumn, quantityColumn, quantityTypeColumn);

        loadItemData();

        warehouseTableView.setItems(data);
    }

    private void loadItemData() {
        Thread thread = new Thread(() -> Platform.runLater(() -> {
            WarehouseModuleDto warehouseModuleDto = warehouseRestClient.getWarehouseModuleData();
            data.clear();
            setWarehouseComboBoxItems(warehouseModuleDto);
            data.addAll(warehouseModuleDto.getItemDtoList().stream().map(ItemTableModel::of).toList());
        }));
        thread.start();
    }

    private void loadItemData(WarehouseDto warehouseDto) {
        Thread thread = new Thread(() -> Platform.runLater(() -> {
            WarehouseModuleDto warehouseModuleDto = warehouseRestClient.getWarehouseModuleData(warehouseDto.getIdWarehouse());
            data.clear();
            setWarehouseComboBoxItems(warehouseModuleDto);
            data.addAll(warehouseModuleDto.getItemDtoList().stream().map(ItemTableModel::of).toList());
        }));
        thread.start();
    }

    private void setWarehouseComboBoxItems(WarehouseModuleDto warehouseModuleDto) {
        List<WarehouseDto> warehouseDtoList = warehouseModuleDto.getWarehouseDtoList();
        ObservableList<WarehouseDto> warehouseComboBoxItems = FXCollections.observableArrayList();
        warehouseComboBoxItems.addAll(warehouseDtoList);
        warehouseComboBox.setItems(warehouseComboBoxItems);
        warehouseComboBox.getSelectionModel().select(warehouseDtoList.indexOf(warehouseModuleDto.getSelectedWarehouse()));
    }
}
