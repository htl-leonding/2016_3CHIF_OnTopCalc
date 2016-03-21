/*	HTL Leonding	*/
package controller;

import db.controller.ProductController;
import db.controller.UnitController;
import db.exceptions.NonexistentEntityException;
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
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class ProductModifierController implements Initializable {

    private static ProductModifierController instance;

    @FXML
    private Label lb_Id;
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
    private ComboBox<Unit> cb_Unit;
    @FXML
    private ComboBox<ProductType> cb_ProductType;
    @FXML
    private TextField tf_ColorFactor;

    private ObservableList<Unit> units;
    private ObservableList<ProductType> productTypes;
    private Product openedProduct;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        units = FXCollections.observableArrayList(new UnitController().findAll());
        productTypes = FXCollections.observableArrayList(ProductType.values());
        cb_Unit.setItems(units);
        cb_ProductType.setItems(productTypes);
        instance = this;
    }

    public static ProductModifierController getInstance() {
        return instance;
    }

    /**
     * Loads a product into the Modifier.
     *
     * @param product
     */
    public void loadProductIntoModifier(Product product) {
        openedProduct = product;
        lb_Id.setText(String.valueOf(openedProduct.getId()));
        tf_Name.setText(openedProduct.getName());
        tf_Width.setText(parseString(openedProduct.getWidthProduct()));
        tf_Height.setText(parseString(openedProduct.getHeightProduct()));
        tf_Length.setText(parseString(openedProduct.getLengthProduct()));
        tf_PriceUnit.setText(parseString(openedProduct.getPriceUnit()));
        cb_Unit.setValue(openedProduct.getUnit());
        cb_ProductType.setValue(openedProduct.getProductType());
    }

    /**
     * Saves the changes that were made.
     *
     * @param event
     */
    @FXML
    private void save(ActionEvent event) {

        try {
            if (cb_ProductType.getSelectionModel().getSelectedItem() == ProductType.COLOR) {
                openedProduct.setName(tf_Name.getText());
                openedProduct.setWidthProduct(null);
                openedProduct.setHeightProduct(null);
                openedProduct.setLengthProduct(null);
                openedProduct.setColorFactor(tryParseDouble(tf_ColorFactor.getText()));
                openedProduct.setPriceUnit(tryParseDouble(tf_PriceUnit.getText()));
                openedProduct.setUnit(cb_Unit.getValue());
                openedProduct.setProductType(cb_ProductType.getValue());
            } else {
                openedProduct.setName(tf_Name.getText());
                openedProduct.setWidthProduct(tryParseDouble(tf_Width.getText()));
                openedProduct.setHeightProduct(tryParseDouble(tf_Height.getText()));
                openedProduct.setLengthProduct(tryParseDouble(tf_Length.getText()));
                openedProduct.setColorFactor(null);
                openedProduct.setPriceUnit(tryParseDouble(tf_PriceUnit.getText()));
                openedProduct.setUnit(cb_Unit.getValue());
                openedProduct.setProductType(cb_ProductType.getValue());
            }

            new ProductController().edit(openedProduct);

        } catch (Exception ex) {
        }

        ProductListController.getInstance().refreshTable();
        ((Stage)(((Node)event.getSource()).getScene().getWindow())).close();
    }

    @FXML
    private void cancel(ActionEvent event) {
        ((Stage)(((Node)event.getSource()).getScene().getWindow())).close();
    }

    /**
     * Parses a double into a string and returns "" if the number is null.
     *
     * @param number
     * @return
     */
    private String parseString(Double number) {
        String parsedString = String.valueOf(number);

        if (parsedString.equals("null")) {
            parsedString = "";
        }
        return parsedString;
    }

    /**
     * Enables and disables the right TextFields when the Product Type is
     * changed.
     */
    private void manageTextFields() {
        if (cb_ProductType.getSelectionModel().getSelectedItem() == ProductType.COLOR) {
            tf_Height.setDisable(true);
            tf_Length.setDisable(true);
            tf_Width.setDisable(true);
            tf_ColorFactor.setDisable(false);
        } else {
            tf_Height.setDisable(false);
            tf_Length.setDisable(false);
            tf_Width.setDisable(false);
            tf_ColorFactor.setDisable(true);
        }
    }

    @FXML
    private void productTypeChanged(ActionEvent event) {
        manageTextFields();
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
     * Deletes current opened product permanentley.
     *
     * @param event
     * @throws NonexistentEntityException
     */
    @FXML
    private void deleteProduct(ActionEvent event) throws NonexistentEntityException {
        Alert alert = new Alert(AlertType.CONFIRMATION, "Möchten Sie das Produkt wirklich endgültig löschen. Vorsicht, dieser Vorang kann nicht mehr rückgangig gemacht werden.",
                ButtonType.YES, ButtonType.CANCEL);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            new ProductController().destroy(openedProduct.getId());
            ProductListController.getInstance().refreshTable();
            ((Stage)(((Node)event.getSource()).getScene().getWindow())).close();
        }
    }
}
