package pl.strefakursow.spring_javafx.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pl.strefakursow.spring_javafx.dto.WarehouseDto;
import pl.strefakursow.spring_javafx.dto.WarehouseModuleDto;
import pl.strefakursow.spring_javafx.factory.PopupFactory;
import pl.strefakursow.spring_javafx.rest.ItemRestClient;
import pl.strefakursow.spring_javafx.rest.WarehouseRestClient;
import pl.strefakursow.spring_javafx.table.EmployeeTableModel;
import pl.strefakursow.spring_javafx.table.ItemTableModel;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
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
    private final PopupFactory popupFactory;

    public WarehouseController() {
        itemRestClient = new ItemRestClient();
        this.data = FXCollections.observableArrayList();
        warehouseRestClient = new WarehouseRestClient();
        popupFactory = new PopupFactory();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeTableView();
        initializeComboBox();
        initializeAddItemButton();
        initializeViewItemButton();
        initializeEditItemButton();

    }

    private void initializeEditItemButton() {
        editButton.setOnAction(x -> {
            ItemTableModel selectedItem = warehouseTableView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                Stage waitingPopup = popupFactory.createWaitingPopup("Loading item data...");
                waitingPopup.show();
                Stage editItemStage = createItemCrudStage();
                try {
                    FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("edit-item.fxml")));
                    Scene scene = new Scene(loader.load(), 500, 400);
                    editItemStage.setScene(scene);
                    EditItemController controller = loader.getController();
                    controller.loadItemData(selectedItem.getIdItem(), () -> {
                        waitingPopup.close();
                        editItemStage.show();
                    });
                } catch (IOException e) {
                    throw new RuntimeException("Can't load fxml file edit-item.fxml " + e);
                }

            }
        });
    }

    private void initializeViewItemButton() {
        viewButton.setOnAction(x -> {
            ItemTableModel item = warehouseTableView.getSelectionModel().getSelectedItem();
            if(item != null) {
                Stage waitingPopup = popupFactory.createWaitingPopup("Loading item data...");
                waitingPopup.show();
                Stage viewItemStage = createItemCrudStage();
                try {
                    FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("view-item.fxml")));
                    Scene scene = new Scene(loader.load(), 500, 400);
                    viewItemStage.setScene(scene);
                    ViewItemController controller = loader.getController();
                    controller.loadItemData(item.getIdItem(), () -> {
                        waitingPopup.close();
                        viewItemStage.show();
                    });
                } catch (IOException e) {
                    throw new RuntimeException("Can't load fxml file view-item.fxml " + e);
                }
            }
        });
    }

    private void initializeAddItemButton() {
        addButton.setOnAction(x -> {
            Stage waitingPopup = popupFactory.createWaitingPopup("Loading item data...");
            waitingPopup.show();
            try {
                Stage stage = createItemCrudStage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("add-item.fxml"));
                Scene scene = new Scene(loader.load(), 500, 400);
                stage.setScene(scene);
                AddItemController controller = loader.getController();
                WarehouseDto selectedWarehouseDto = warehouseComboBox.getSelectionModel().getSelectedItem();
                controller.setWarehouseDto(selectedWarehouseDto);
                controller.loadQuantityTypes();
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Stage createItemCrudStage() {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        return stage;
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
