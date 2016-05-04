package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ProductController;
import at.plakolb.calculationlogic.entity.Product;
import at.plakolb.calculationlogic.entity.Unit;
import at.plakolb.calculationlogic.eunmeration.ProductType;
import at.plakolb.calculationlogic.util.UtilityFormat;
import at.plakolb.edit.ProductNameCell;
import at.plakolb.edit.ProductUnitCell;
import at.plakolb.edit.ProductValueCell;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Andreas
 */
public class ProductListController implements Initializable {

    private static ProductListController instance;

    @FXML
    private TableView<Product> tv_Products;
    @FXML
    private TableColumn<Product, String> tc_Name;
    @FXML
    private TableColumn<Product, String> tc_Width;
    @FXML
    private TableColumn<Product, String> tc_Height;
    @FXML
    private TableColumn<Product, String> tc_Length;
    @FXML
    private TableColumn<Product, String> tc_ColourFactor;
    @FXML
    private TableColumn<Product, String> tc_PriceUnit;
    @FXML
    private TableColumn<Product, Unit> tc_Unit;
    @FXML
    private TableColumn tc_Buttons;
    @FXML
    private MenuButton mb_ProductTypesFilter;

    private ObservableList<Product> products;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        products = null;
        DecimalFormat decimalFormat = new DecimalFormat("#.####");
        decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));

        tv_Products.setEditable(true);

        tc_Name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tc_Name.setCellFactory((TableColumn<Product, String> param) -> new ProductNameCell());
        tc_Name.setOnEditCommit((CellEditEvent<Product, String> event) -> {
            Product product = ((Product) event.getTableView().getItems().get(event.getTablePosition().getRow()));
            product.setName(event.getNewValue());
            refreshTable();
        });

        tc_Width.setCellValueFactory((TableColumn.CellDataFeatures<Product, String> param) -> {
            if (param.getValue() != null) {
                return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(param.getValue().getWidthProduct()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });
        tc_Width.setCellFactory((TableColumn<Product, String> param) -> new ProductValueCell());
        tc_Width.setOnEditCommit((CellEditEvent<Product, String> event) -> {
            Product product = ((Product) event.getTableView().getItems().get(event.getTablePosition().getRow()));
            if (!event.getNewValue().equals("")) {
                product.setWidthProduct(Double.parseDouble(event.getNewValue()));
            } else {
                product.setWidthProduct(null);
            }
            refreshTable();
        });

        tc_Height.setCellValueFactory((TableColumn.CellDataFeatures<Product, String> param) -> {
            if (param.getValue().getHeightProduct() != null) {
                return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(param.getValue().getHeightProduct()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });
        tc_Height.setCellFactory((TableColumn<Product, String> param) -> new ProductValueCell());
        tc_Height.setOnEditCommit((CellEditEvent<Product, String> event) -> {
            Product product = ((Product) event.getTableView().getItems().get(event.getTablePosition().getRow()));
            if (!event.getNewValue().equals("")) {
                product.setHeightProduct(Double.parseDouble(event.getNewValue()));
            } else {
                product.setHeightProduct(null);
            }
            refreshTable();
        });

        tc_Length.setCellValueFactory((TableColumn.CellDataFeatures<Product, String> param) -> {
            if (param.getValue().getLengthProduct() != null) {
                return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(param.getValue().getLengthProduct()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });
        tc_Length.setCellFactory((TableColumn<Product, String> param) -> new ProductValueCell());
        tc_Length.setOnEditCommit((CellEditEvent<Product, String> event) -> {
            Product product = ((Product) event.getTableView().getItems().get(event.getTablePosition().getRow()));
            if (!event.getNewValue().equals("")) {
                product.setLengthProduct(Double.parseDouble(event.getNewValue()));
            } else {
                product.setLengthProduct(null);
            }
            refreshTable();
        });

        tc_PriceUnit.setCellValueFactory((TableColumn.CellDataFeatures<Product, String> param) -> {
            if (param.getValue().getPriceUnit() != null) {
                return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(param.getValue().getPriceUnit()) + " €");
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });
        tc_PriceUnit.setCellFactory((TableColumn<Product, String> param) -> new ProductValueCell());
        tc_PriceUnit.setOnEditCommit((CellEditEvent<Product, String> event) -> {
            String price = "";
            Product product = ((Product) event.getTableView().getItems().get(event.getTablePosition().getRow()));
            if (event.getNewValue().length() >= 2) {
                price = event.getNewValue().substring(0, event.getNewValue().length() - 2);
            }
            product.setPriceUnit(Double.parseDouble(price));
            refreshTable();
        });

        tc_ColourFactor.setCellValueFactory((TableColumn.CellDataFeatures<Product, String> param) -> {
            if (param.getValue().getColorFactor() != null) {
                return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(param.getValue().getColorFactor()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });
        tc_ColourFactor.setCellFactory((TableColumn<Product, String> param) -> new ProductValueCell());
        tc_ColourFactor.setOnEditCommit((CellEditEvent<Product, String> event) -> {
            Product product = ((Product) event.getTableView().getItems().get(event.getTablePosition().getRow()));
            if (!event.getNewValue().equals("")) {
                product.setColorFactor(Double.parseDouble(event.getNewValue()));
            } else {
                product.setColorFactor(null);
            }
            refreshTable();
        });

        tc_Unit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        tc_Unit.setCellFactory((TableColumn<Product, Unit> param) -> new ProductUnitCell());
        tc_Unit.setOnEditCommit((CellEditEvent<Product, Unit> event) -> {
            Product product = ((Product) event.getTableView().getItems().get(event.getTablePosition().getRow()));
            product.setUnit(event.getNewValue());
        });

        tc_Buttons.setCellFactory((new Callback<TableColumn<Product, String>, TableCell<Product, String>>() {
            @Override
            public TableCell call(final TableColumn<Product, String> param) {
                final TableCell<Product, String> cell = new TableCell<Product, String>() {

                    final Label delete = new Label();

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            delete.setId("delete");
                            delete.setTooltip(new Tooltip("Produkt löschen"));
                            delete.setOnMouseClicked((MouseEvent event) -> {
                                try {
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Möchten Sie das Produkt wirklich endgültig löschen. Vorsicht, dieser Vorgang kann nicht mehr rückgängig gemacht werden.",
                                            ButtonType.YES, ButtonType.CANCEL);
                                    alert.showAndWait();
                                    if (alert.getResult() == ButtonType.YES) {
                                        new ProductController().destroy(tv_Products.getSelectionModel().getSelectedItem().getId());
                                        refreshTable();
                                    }
                                } catch (Exception exception) {
                                }
                            });
                            setGraphic(delete);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        }));

        tv_Products.setItems(FXCollections.observableArrayList(new ProductController().findAll()));

        addProductTypesWithFilter(mb_ProductTypesFilter);
        refreshTable();
    }

    public static ProductListController getInstance() {
        return instance;
    }

    /**
     * Adds a new product to the database.
     *
     * @param event
     */
    @FXML
    private void addProduct(ActionEvent event) {

        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/ProductCreator.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Produkt erstellen");
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
        } catch (IOException ex) {
        }
    }

    /**
     * Adds all Product Types to the Menu Button and adds a filter to every
     * item.
     */
    private void addProductTypesWithFilter(MenuButton menuButton) {
        for (ProductType productType : ProductType.values()) {
            MenuItem menuItem = new MenuItem(productType.toString());
            menuItem.setOnAction(eventHandler -> {
                menuButton.setText(menuItem.getText());
                refreshTable();
            });
            menuButton.getItems().add(menuItem);
        }

        MenuItem menuItem = new MenuItem("Alle");
        menuItem.setOnAction(eventHandler -> {
            filterList(null);
            menuButton.setText("Alle");
        });
        menuButton.getItems().add(menuItem);
    }

    /**
     * Filters the List View by the selected Product Type
     *
     * @param productType
     */
    private void filterList(ProductType productType) {
        ProductController productController = new ProductController();

        if (productType == null) {
            products = FXCollections.observableArrayList(productController.findAll());
        } else {
            products = FXCollections.observableArrayList(productController.findByProductTypeOrderByName(productType));
        }
        tv_Products.setItems(products);
    }

    public void setUnitFromCell(ProductUnitCell cell) {
        products.get(cell.getIndex()).setUnit(cell.getSelectedUnit());
        refreshTable();
    }

    public void persistsProducts() {
        ProductController productController = new ProductController();

        if (products != null) {
            products.stream().forEach((product) -> {
                try {
                    if (product.getId() == null) {
                        productController.create(product);
                    } else {
                        productController.edit(product);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ProductListController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }

    /**
     * Refreshes the Table View.
     */
    public void refreshTable() {
        persistsProducts();
        filterList(ProductType.getProductType(mb_ProductTypesFilter.getText()));
        tv_Products.getColumns().get(0).setVisible(false);
        tv_Products.getColumns().get(0).setVisible(true);
    }
}
