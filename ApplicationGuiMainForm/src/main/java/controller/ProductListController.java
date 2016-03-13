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
        
        for (ProductType productType : ProductType.values()) {
            mb_ProductTypes.getItems().add(new MenuItem(productType.toString()));
        }
        
        new UnitController().findAll().stream().forEach((unit) -> {
            mb_Units.getItems().add(new MenuItem(unit.getShortTerm()));
        });
    }

    @FXML
    private void addProduct(ActionEvent event) {
        //TODO
        //products.add(new Product(tf_Name.getText(), Double.valueOf(tf_Width.getText()),Double.valueOf(tf_Width.getText()), Double.valueOf(tf_Width.getText()), Double.valueOf(tf_Width.getText()), unit, ProductType.WOOD));
    }

}
