/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.Logging;
import at.plakolb.calculationlogic.db.controller.ProductController;
import at.plakolb.calculationlogic.db.controller.UnitController;
import at.plakolb.calculationlogic.entity.Product;
import at.plakolb.calculationlogic.entity.Unit;
import at.plakolb.calculationlogic.eunmeration.ProductType;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class ProductCreatorController implements Initializable {

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
    }

    /**
     * Saves the changes that were made.
     *
     * @param event
     */
    @FXML
    private void create(ActionEvent event) {

        String errorMessage = "";

        if (tf_Name.getText().isEmpty()) {
            errorMessage += "Geben Sie bitte einen Namen ein.\n";
        }
        if (tf_PriceUnit.getText().isEmpty()) {
            errorMessage += "Geben Sie bitte einen Preis ein.\n";
        } else if (tf_PriceUnit.getText().contains("-")) {
            errorMessage += "Der Preis darf nicht negativ sein.";
        } else {
            try {
                Double.parseDouble(tf_PriceUnit.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Der Preis hat ein ungültiges Format.\n";
            }
        }

        if (errorMessage.isEmpty()) {
            try {
                Product product = new Product();

                if (cb_ProductType.getSelectionModel().getSelectedItem() == ProductType.COLOR) {
                    product.setName(tf_Name.getText());
                    product.setWidthProduct(null);
                    product.setHeightProduct(null);
                    product.setLengthProduct(null);
                    product.setColorFactor(tryParseDouble(tf_ColorFactor.getText()));
                    product.setPriceUnit(tryParseDouble(tf_PriceUnit.getText()));
                    product.setUnit(cb_Unit.getValue());
                    product.setProductType(cb_ProductType.getValue());
                } else {
                    product.setName(tf_Name.getText());
                    product.setWidthProduct(tryParseDouble(tf_Width.getText()));
                    product.setHeightProduct(tryParseDouble(tf_Height.getText()));
                    product.setLengthProduct(tryParseDouble(tf_Length.getText()));
                    product.setColorFactor(null);
                    product.setPriceUnit(tryParseDouble(tf_PriceUnit.getText()));
                    product.setUnit(cb_Unit.getValue());
                    product.setProductType(cb_ProductType.getValue());
                }
                new ProductController().create(product);

            } catch (Exception ex) {
                Logging.getLogger().log(Level.SEVERE, "", ex);
            }

            ProductListController.getInstance().refreshTable();
            ((Stage) (((Node) event.getSource()).getScene().getWindow())).close();
        } else {
            new Alert(AlertType.ERROR, errorMessage).showAndWait();
        }
    }

    /**
     * Closes window.
     *
     * @param event
     */
    @FXML
    private void cancel(ActionEvent event) {
        ((Stage) (((Node) event.getSource()).getScene().getWindow())).close();
    }

    /**
     * Gets called when the product type ComboBox selection is changed.
     *
     * @param event
     */
    @FXML
    private void productTypeChanged(ActionEvent event) {
        if (cb_ProductType.getSelectionModel().getSelectedItem() == ProductType.COLOR) {
            cb_Unit.getSelectionModel().select(new UnitController().findUnitByShortTerm("l"));
            cb_Unit.setDisable(true);
            tf_Height.setDisable(true);
            tf_Length.setDisable(true);
            tf_Width.setDisable(true);
            tf_ColorFactor.setDisable(false);
        } else {
            cb_Unit.getSelectionModel().select(0);
            cb_Unit.setDisable(false);
            tf_Height.setDisable(false);
            tf_Length.setDisable(false);
            tf_Width.setDisable(false);
            tf_ColorFactor.setDisable(true);
        }
    }

    /**
     * Parses String to Double if possible.
     *
     * @param numberString
     * @return
     */
    private Double tryParseDouble(String numberString) {
        try {
            numberString = numberString.replace(",", ".");
            return Double.parseDouble(numberString);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

}
