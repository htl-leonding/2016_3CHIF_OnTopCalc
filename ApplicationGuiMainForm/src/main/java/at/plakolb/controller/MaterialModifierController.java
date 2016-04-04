/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ComponentController;
import at.plakolb.calculationlogic.entity.Component;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class MaterialModifierController implements Initializable {

    private static MaterialModifierController instance;

    @FXML
    private Label lb_Category;
    @FXML
    private Label lb_Product;
    @FXML
    private TextField tf_Count;
    @FXML
    private TextField tf_PriceUnit;
    @FXML
    private TextField tf_TotalPrice;
    @FXML
    private TextField tf_CuttingTime;
    @FXML
    private TextField tf_CuttingPricePerHour;

    private Component openedComponent;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;

        tf_Count.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            try {
                if (tf_Count.getText().contains(",")) {
                    tf_Count.setText(tf_Count.getText().substring(0, tf_Count.getText().lastIndexOf(",")));
                }
                if (tf_Count.getText().contains(".")) {
                    tf_Count.setText(tf_Count.getText().substring(0, tf_Count.getText().lastIndexOf(".")));
                }
                
                Integer.parseInt(tf_Count.getText());
            } catch (NumberFormatException e) {
                tf_Count.setText("1");
            }
            if (tf_Count.getText().isEmpty() || tf_Count.getText().equals("0") || tf_Count.getText().contains("-")) {
                tf_Count.setText("1");
            }
        });

        tf_PriceUnit.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            tf_PriceUnit.setText(tf_PriceUnit.getText().replace(",", "."));
            try {
                Double.parseDouble(tf_PriceUnit.getText());
            } catch (NumberFormatException e) {
                tf_PriceUnit.setText("0");
            }
            if (tf_PriceUnit.getText().isEmpty() || tf_PriceUnit.getText().contains("-")) {
                tf_PriceUnit.setText("0");
            }
        });

        tf_TotalPrice.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            tf_TotalPrice.setText(tf_TotalPrice.getText().replace(",", "."));
            try {
                Double.parseDouble(tf_TotalPrice.getText());
            } catch (NumberFormatException e) {
                tf_TotalPrice.setText("0");
            }
            if (tf_TotalPrice.getText().isEmpty() || tf_TotalPrice.getText().contains("-")) {
                tf_TotalPrice.setText("0");
            }
        });

        tf_CuttingTime.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            tf_CuttingTime.setText(tf_CuttingTime.getText().replace(",", "."));
            try {
                Double.parseDouble(tf_CuttingTime.getText());
            } catch (NumberFormatException e) {
                tf_CuttingTime.setText("0");
            }
            if (tf_CuttingTime.getText().isEmpty() || tf_CuttingTime.getText().contains("-")) {
                tf_CuttingTime.setText("0");
            }
        });

        tf_CuttingPricePerHour.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            tf_CuttingPricePerHour.setText(tf_CuttingPricePerHour.getText().replace(",", "."));
            try {
                Double.parseDouble(tf_CuttingPricePerHour.getText());
            } catch (NumberFormatException e) {
                tf_CuttingPricePerHour.setText("0");
            }
            if (tf_CuttingPricePerHour.getText().isEmpty() || tf_CuttingPricePerHour.getText().contains("-")) {
                tf_CuttingPricePerHour.setText("0");
            }
        });

        tf_Count.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            try {
                Double.parseDouble(tf_Count.getText());
                DecimalFormat decimalFormat = new DecimalFormat("#.####");
                decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
                if (!tf_PriceUnit.getText().isEmpty() && !tf_Count.getText().isEmpty() && !tf_Count.getText().contains("-")) {
                    tf_TotalPrice.setText(decimalFormat.format(tryParseDouble(tf_PriceUnit.getText()) * tryParseInteger(tf_Count.getText())));
                } else {
                    tf_TotalPrice.setText("0");
                }
            } catch (NumberFormatException e) {
            } catch (Exception e){
            }
        });

        tf_PriceUnit.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            try {
                Double.parseDouble(tf_PriceUnit.getText());
                DecimalFormat decimalFormat = new DecimalFormat("#.####");
                decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
                if (!tf_PriceUnit.getText().isEmpty() && !tf_Count.getText().isEmpty() && !tf_PriceUnit.getText().contains("-")) {
                    tf_TotalPrice.setText(decimalFormat.format(tryParseDouble(tf_PriceUnit.getText()) * tryParseInteger(tf_Count.getText())));
                } else {
                    tf_TotalPrice.setText("0");
                }
            } catch (NumberFormatException e) {
            } catch (Exception e){
            }
        });

        tf_TotalPrice.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            try {
                Double.parseDouble(tf_TotalPrice.getText());
                DecimalFormat decimalFormat = new DecimalFormat("#.####");
                decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
                if (!tf_TotalPrice.getText().isEmpty() && !tf_Count.getText().isEmpty() && !tf_TotalPrice.getText().contains("-")) {
                    tf_PriceUnit.setText(decimalFormat.format(tryParseDouble(tf_TotalPrice.getText()) / tryParseInteger(tf_Count.getText())));
                } else {
                    tf_PriceUnit.setText("0");
                }
            } catch (NumberFormatException e) {
            } catch (Exception e){
            }
        });
    }

    public static MaterialModifierController getInstance() {
        return instance;
    }

    public void loadProductIntoModifier(Component component) {
        openedComponent = component;
        lb_Category.setText(openedComponent.getCategory().getLongTerm());
        lb_Product.setText(openedComponent.getProduct().getFullName());
        tf_Count.setText(String.valueOf(openedComponent.getNumberOfProducts()));
        tf_PriceUnit.setText(parseString(openedComponent.getPriceComponent() / component.getNumberOfProducts()));
        tf_TotalPrice.setText(parseString(openedComponent.getPriceComponent()));
        tf_CuttingTime.setText(parseString(openedComponent.getTailoringHours()));
        tf_CuttingPricePerHour.setText(parseString(openedComponent.getTailoringPricePerHour()));
    }

    @FXML
    private void save(ActionEvent event) {
        try {
            openedComponent.setNumberOfProducts(tryParseInteger(tf_Count.getText()));
            openedComponent.setPriceComponent(tryParseDouble(tf_TotalPrice.getText()));
            openedComponent.setTailoringHours(tryParseDouble(tf_CuttingTime.getText()));
            openedComponent.setTailoringPricePerHour(tryParseDouble(tf_CuttingPricePerHour.getText()));
            new ComponentController().edit(openedComponent);
        } catch (Exception e) {
        }

        Project_ConstructionMaterialListController.getInstance().refreshTable();
        ((Stage) (((Node) event.getSource()).getScene().getWindow())).close();

    }

    @FXML
    private void cancel(ActionEvent event) {
        ((Stage) (((Node) event.getSource()).getScene().getWindow())).close();
    }

    private String parseString(Double number) {
        if (number == null) {
            return "";
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.####");
        decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
        return String.valueOf(decimalFormat.format(number));
    }

    private Double tryParseDouble(String numberString) {
        try {
            return Double.parseDouble(numberString);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer tryParseInteger(String numberString) {
        try {
            return Integer.parseInt(numberString);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
