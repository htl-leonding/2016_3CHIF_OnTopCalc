package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ProductController;
import at.plakolb.calculationlogic.db.controller.UnitController;
import at.plakolb.calculationlogic.entity.Product;
import at.plakolb.calculationlogic.entity.Unit;
import at.plakolb.calculationlogic.eunmeration.ProductType;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

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
    private TableColumn<Product, String> tc_PriceUnit;
    @FXML
    private TableColumn<Product, Unit> tc_Unit;
    @FXML
    private MenuButton mb_ProductTypes;
    @FXML
    private TextField tf_Name;
    @FXML
    private TextField tf_Width;
    @FXML
    private TextField tf_Height;
    @FXML
    private TextField tf_Length;
    @FXML
    private TextField tf_PriceUnit;
    @FXML
    private MenuButton mb_Units;
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
        DecimalFormat decimalFormat = new DecimalFormat("#.####");
        decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));

        tc_Name.setCellValueFactory(new PropertyValueFactory<>("name"));

        tc_Width.setCellValueFactory((TableColumn.CellDataFeatures<Product, String> param) -> {
            if (param.getValue().getWidthProduct() != null) {
                return new ReadOnlyObjectWrapper<>(decimalFormat.format(param.getValue().getWidthProduct()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        tc_Height.setCellValueFactory((TableColumn.CellDataFeatures<Product, String> param) -> {
            if (param.getValue().getHeightProduct() != null) {
                return new ReadOnlyObjectWrapper<>(decimalFormat.format(param.getValue().getHeightProduct()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        tc_Length.setCellValueFactory((TableColumn.CellDataFeatures<Product, String> param) -> {
            if (param.getValue().getLengthProduct() != null) {
                return new ReadOnlyObjectWrapper<>(decimalFormat.format(param.getValue().getLengthProduct()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        tc_PriceUnit.setCellValueFactory((TableColumn.CellDataFeatures<Product, String> param) -> {
            if (param.getValue().getPriceUnit() != null) {
                return new ReadOnlyObjectWrapper<>(decimalFormat.format(param.getValue().getPriceUnit()) + " €");
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        tc_Unit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        tv_Products.setItems(FXCollections.observableArrayList(new ProductController().findAll()));

        tv_Products.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tv_Products.getSelectionModel().getSelectedItem() != null) {
                Parent root;
                try {
                    root = FXMLLoader.load(getClass().getResource("/fxml/ProductModifier.fxml"));
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setTitle("Product");
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException ex) {
                }

                ProductModifierController.getInstance().loadProductIntoModifier(tv_Products.getSelectionModel().getSelectedItem());
            }
        });

        addProductTypes(mb_ProductTypes);
        addProductTypesWithFilter(mb_ProductTypesFilter);
        addUnits();
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
        Product product = createNewProduct();
        if (product != null) {
            ProductController productController = new ProductController();
            productController.create(createNewProduct());
            refreshTable();
        }
    }

    /**
     * Adds all Product Types to the Menu Button.
     *
     * @param menuButton
     */
    private void addProductTypes(MenuButton menuButton) {
        for (ProductType productType : ProductType.values()) {
            MenuItem menuItem = new MenuItem(productType.toString());
            menuItem.setOnAction(eventHandler -> {
                menuButton.setText(menuItem.getText());
            });
            menuButton.getItems().add(menuItem);
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

    /**
     * Adds all Units to the Menu Button
     */
    private void addUnits() {

        new UnitController().findAll().stream().forEach((unit) -> {
            MenuItem menuItem = new MenuItem(unit.getShortTerm());
            menuItem.setOnAction(eventHandler -> {
                mb_Units.setText(menuItem.getText());
            });
            mb_Units.getItems().add(menuItem);
        });
    }

    /**
     * Creates a new Product from the UI elements
     *
     * @return
     */
    private Product createNewProduct() {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        UnitController unitController = new UnitController();
        String errorMessage = "";

        if (tf_Name.getText().isEmpty()) {
            errorMessage += "Bitte geben Sie eine Bezeichnung ein.\n";
        }
        if (tf_PriceUnit.getText().isEmpty()) {
            errorMessage += "Bitte geben Sie den Preis ein.\n";
        }
        if (mb_Units.getText().equals("Einheit")) {
            errorMessage += "Bitte wählen Sie eine Einheit aus.\n";
        }
        if (mb_ProductTypes.getText().equals("Produkt Typ")) {
            errorMessage += "Bitte wählen Sie einen Produkt Typ aus.\n";
        }
        if (errorMessage.equals("")) {
            try {
                return new Product(tf_Name.getText(),
                        tryParseDouble(tf_Width.getText()),
                        tryParseDouble(tf_Height.getText()),
                        tryParseDouble(tf_Length.getText()),
                        Double.parseDouble(tf_PriceUnit.getText()),
                        unitController.findUnitByShortTerm(mb_Units.getText()),
                        ProductType.getProductType(mb_ProductTypes.getText()));
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR,"Der angegeben Preis ist leider nicht gültig.").showAndWait();
            }
        } else {
            alert.setContentText(errorMessage);
            alert.showAndWait();
        }
        return null;
    }

    /**
     * Parses String to Double if possible.
     *
     * @param numberString
     * @return
     */
    private Double tryParseDouble(String numberString) {
        try {
            double number = Double.parseDouble(numberString);
            return number;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Refreshes the Table View.
     */
    public void refreshTable() {
        filterList(ProductType.getProductType(mb_ProductTypesFilter.getText()));
        tv_Products.getColumns().get(0).setVisible(false);
        tv_Products.getColumns().get(0).setVisible(true);
    }
}
