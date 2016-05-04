/*	HTL Leonding	*/
package at.plakolb.edit;

import at.plakolb.calculationlogic.entity.Product;
import at.plakolb.calculationlogic.eunmeration.ProductType;
import at.plakolb.calculationlogic.util.UtilityFormat;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author Kepplinger
 */
public class ProductValueCell extends TableCell<Product, String> {
    
    private TextField textField;
    
    public ProductValueCell() {
        
    }
    
    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            createTextField();
            setGraphic(textField);
            textField.requestFocus();
            
            if ((!((Product) getTableRow().getItem()).getProductType().equals(ProductType.COLOR) && getTableColumn().getId().equals("tc_ColourFactor"))
                    || ((Product) getTableRow().getItem()).getProductType().equals(ProductType.COLOR) && (getTableColumn().getId().equals("tc_Width") || getTableColumn().getId().equals("tc_Height") || getTableColumn().getId().equals("tc_Length"))) {
                cancelEdit();
            }
        }
    }
    
    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText((String) getItem());
        setGraphic(null);
    }
    
    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else if (isEditing()) {
            if (textField != null) {
                textField.setText(getString());
            }
            setText(null);
            setGraphic(textField);
        } else {
            setText(getString());
            setGraphic(null);
        }
    }
    
    private void createTextField() {
        
        String item = getString();
        
        if (getTableColumn().getId().equals("tc_PriceUnit") && item.length() >= 2) {
            item = item.substring(0, item.length() - 2);
        }
        
        textField = new TextField(item);
        textField.setAlignment(Pos.CENTER);
        textField.setPrefWidth(this.getWidth() - 5);
        
        textField.setOnKeyReleased((KeyEvent t) -> {
            if (t.getCode() == KeyCode.ENTER) {
                
                if (!(!getTableColumn().getId().equals("tc_PriceUnit") && textField.getText().isEmpty())) {
                    try {
                        textField.setText(textField.getText().replace(",", "."));
                        double number = Double.parseDouble(textField.getText());
                        if (getTableColumn().getId().equals("tc_PriceUnit") && number < 0) {
                            new Alert(Alert.AlertType.ERROR, "Der Preis darf nicht negativ sein.").showAndWait();
                        }
                        if (!getTableColumn().getId().equals("tc_PriceUnit") && UtilityFormat.getStringForTableColumn(number).equals("0")) {
                            textField.setText("");
                        }
                    } catch (NumberFormatException e) {
                        cancelEdit();
                        new Alert(Alert.AlertType.ERROR, "Die eingegbene Zahl ist nicht im richtigen Format.").showAndWait();
                        return;
                    }
                }
                
                if (getTableColumn().getId().equals("tc_PriceUnit")) {
                    textField.setText(textField.getText() + " €");
                }
                
                commitEdit(textField.getText());
            } else if (t.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        });
    }
    
    private String getString() {
        return getItem() == null ? "" : getItem();
    }
}