package at.plakolb.controller;

/*	HTL Leonding	*/

import at.plakolb.calculationlogic.db.controller.CategoryController;
import at.plakolb.calculationlogic.db.controller.ProductController;
import at.plakolb.calculationlogic.entity.Category;
import at.plakolb.calculationlogic.entity.Component;
import at.plakolb.calculationlogic.entity.Product;
import at.plakolb.calculationlogic.eunmeration.ProductType;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author in130079
 */
public class Project_ConstructionmaterialListController implements Initializable {

    private static Project_ConstructionmaterialListController instance;
    @FXML
    private TableView<Component> tv_Materials;
    @FXML
    private TableColumn<Component, Category> tc_Category;
    @FXML
    private TableColumn<Component, String> tc_ProductName;
    @FXML
    private TableColumn<Component, Double> tc_Length;
    @FXML
    private TableColumn<Component, Integer> tc_Amount;
    @FXML
    private TableColumn<Component, Double> tc_Volume;
    @FXML
    private TableColumn<Component, Double> tc_PricePerCubic;
    @FXML
    private TableColumn<Component, Double> tc_Price;
    @FXML
    private TableColumn<Component, Double> tc_CuttingHours;
    @FXML
    private TableColumn<Component, Double> tc_CuttingPricePerHours;
    @FXML
    private TableColumn<Component, Double> tc_CuttingPrice;
    @FXML
    private ComboBox<Category> cb_Category;
    @FXML
    private ComboBox<Product> cb_Product;
    @FXML
    private TextField tf_Amount;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        
        tc_Category.setCellValueFactory(new PropertyValueFactory<>("category"));
        tc_ProductName.setCellValueFactory(new PropertyValueFactory<>("product"));
        tc_Length.setCellValueFactory(new PropertyValueFactory<>("lengthComponent"));        
        
        cb_Category.setItems(FXCollections.observableArrayList(new CategoryController().findAll()));
        cb_Product.setItems(FXCollections.observableArrayList(new ProductController().findByProductTypeOrderByName(ProductType.WOOD)));
        cb_Category.getSelectionModel().select(0);
        cb_Product.getSelectionModel().select(0);
    }

    public static Project_ConstructionmaterialListController getInstance() {
        return instance;
    }

    @FXML
    private void addProduct(ActionEvent event) {
    }
}
