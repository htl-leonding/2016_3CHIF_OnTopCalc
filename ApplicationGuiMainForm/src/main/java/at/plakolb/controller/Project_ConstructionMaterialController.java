/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ComponentController;
import at.plakolb.calculationlogic.db.controller.ProductController;
import at.plakolb.calculationlogic.entity.Component;
import at.plakolb.calculationlogic.entity.Product;
import at.plakolb.calculationlogic.eunmeration.ProductType;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author User
 */
public class Project_ConstructionMaterialController implements Initializable {

    private static Project_ConstructionMaterialController instance;
    @FXML
    private ComboBox<Product> cb_Product;
    @FXML
    private TextField tf_Amount;
    @FXML
    private TextField tf_Price;
    @FXML
    private ComboBox<Component> cb_Component;

    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        cb_Product.setItems(FXCollections.observableArrayList(new ProductController().findAll()));
//        cb_Component.setItems(FXCollections.observableArrayList(new ComponentController().findColorByProjectId(ProjectViewController.getOpenedProject().getId())));
    }

    public static Project_ConstructionMaterialController getInstance() {
        return instance;
    }    
}
