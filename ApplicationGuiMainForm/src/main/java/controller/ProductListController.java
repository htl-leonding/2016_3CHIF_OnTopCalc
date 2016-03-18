package controller;

import db.controller.ProductController;
import db.controller.UnitController;
import entity.Product;
import entity.Unit;
import eunmeration.ProductType;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author Andreas
 */
public class ProductListController implements Initializable {

    @FXML
    private TableView<Product> tv_Products;
    @FXML
    private TableColumn<Product, String> tc_Name;
    @FXML
    private TableColumn<Product, Double> tc_Width;
    @FXML
    private TableColumn<Product, Double> tc_Height;
    @FXML
    private TableColumn<Product, Double> tc_Length;
    @FXML
    private TableColumn<Product, Double> tc_PriceUnit;
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

    private ObservableList<Product> products;
    @FXML
    private MenuButton mb_ProductTypesFilter;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ProductController productController = new ProductController();
        products = FXCollections.observableArrayList(productController.findAll());

        tc_Name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tc_Width.setCellValueFactory(new PropertyValueFactory<>("widthProduct"));
        tc_Height.setCellValueFactory(new PropertyValueFactory<>("heightProduct"));
        tc_Length.setCellValueFactory(new PropertyValueFactory<>("lengthProduct"));
        tc_PriceUnit.setCellValueFactory(new PropertyValueFactory<>("priceUnit"));
        tc_Unit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        tv_Products.setItems(products);

        addProductTypes(mb_ProductTypes);
        addProductTypesWithFilter(mb_ProductTypesFilter);
        addUnits();
    }

    @FXML
    private void addProduct(ActionEvent event) {
        Product product = createNewProduct();
        if (product != null) {
            ProductController productController = new ProductController();
            productController.create(createNewProduct());
            filterList(ProductType.getProductType(mb_ProductTypesFilter.getText()));
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
                filterList(ProductType.getProductType(menuItem.getText()));
                menuButton.setText(menuItem.getText());
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

        if (tf_Name.getText().equals("")) {
            errorMessage += "Bitte geben Sie eine Bezeichnung ein.\n";
        }
        if (mb_Units.getText().equals("Einheit")) {
            errorMessage += "Bitte wählen Sie eine Einheit aus.\n";
        }
        if (mb_ProductTypes.getText().equals("Produkt Typ")) {
            errorMessage += "Bitte wählen Sie einen Produkt Typ aus.\n";
        }
        if (errorMessage.equals("")) {
            return new Product(tf_Name.getText(),
                    tryParseDouble(tf_Width.getText()),
                    tryParseDouble(tf_Height.getText()),
                    tryParseDouble(tf_Length.getText()),
                    tryParseDouble(tf_PriceUnit.getText()),
                    unitController.findUnitByShortTerm(mb_Units.getText()),
                    ProductType.getProductType(mb_ProductTypes.getText()));
        }
        else{
            alert.setContentText(errorMessage);
            alert.showAndWait();
        }
        return null;
    }
    
    /**
     * Parses String to Double if possible.
     * @param numberString
     * @return 
     */
    private Double tryParseDouble(String numberString){
        try {
            double number = Double.parseDouble(numberString);
            return number;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
