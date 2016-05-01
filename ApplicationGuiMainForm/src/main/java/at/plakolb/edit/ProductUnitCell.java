/*	HTL Leonding	*/
package at.plakolb.edit;

import at.plakolb.calculationlogic.db.controller.UnitController;
import at.plakolb.calculationlogic.entity.Product;
import at.plakolb.calculationlogic.entity.Unit;
import at.plakolb.calculationlogic.eunmeration.ProductType;
import at.plakolb.controller.ProductListController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author Kepplinger
 */
public class ProductUnitCell extends TableCell<Product, Unit> {

    private ComboBox<Unit> comboBox;

    public ProductUnitCell() {
    }

    @Override
    public void startEdit() {
        super.startEdit();

        if (((Product)getTableRow().getItem()).getProductType().equals(ProductType.COLOR)) {
            cancelEdit();
        }
        
        if (comboBox == null) {
            createComboBox();
        }

        setGraphic(comboBox);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        comboBox.requestFocus();
        comboBox.show();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        setText(String.valueOf(getItem()));
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    public void updateItem(Unit item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else if (isEditing()) {
            if (comboBox != null) {
                comboBox.setValue(getUnit());
            }
            setGraphic(comboBox);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        } else {
            setText(getUnit().toString());
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
    }

    private void createComboBox() {
        comboBox = new ComboBox<>(FXCollections.observableArrayList(new UnitController().findAll()));
        comboBox.getSelectionModel().select(this.getItem());
        comboBox.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);

        comboBox.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                ProductListController.getInstance().setUnitFromCell(this);
            }
        });

        comboBox.setOnKeyPressed((KeyEvent t) -> {
            if (t.getCode() == KeyCode.ENTER) {
                commitEdit(comboBox.getSelectionModel().getSelectedItem());
            } else if (t.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        });
    }
    
    public Unit getSelectedUnit(){
        return comboBox.getSelectionModel().getSelectedItem();
    }

    private Unit getUnit() {
        return getItem();
    }
}
