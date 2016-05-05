/*	HTL Leonding	*/
package at.plakolb.edit;

import at.plakolb.calculationlogic.entity.ParameterP;
import at.plakolb.calculationlogic.util.UtilityFormat;
import javafx.beans.value.ObservableValue;
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
public class ParameterCell extends TableCell<ParameterP, String> {

    private TextField textField;

    public ParameterCell() {

    }

    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            createTextField();

            setGraphic(textField);
            textField.requestFocus();
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
        textField = new TextField(getString());
        textField.setAlignment(Pos.CENTER);
        textField.setPrefWidth(this.getWidth() - 5);

        textField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            textField.setText(textField.getText().replaceAll(",", ".").replaceAll("[^\\d.]", ""));
            textField.setText(UtilityFormat.removeUnnecessaryCommas(textField.getText()));
        });
        
        textField.setOnKeyReleased((KeyEvent t) -> {
            if (t.getCode() == KeyCode.ENTER) {
                if (textField.getText().isEmpty()) {
                    textField.setText("0");
                }

                try {
                    textField.setText(textField.getText().replace(",", "."));
                    Double.parseDouble(textField.getText());
                } catch (NumberFormatException e) {
                    cancelEdit();
                    new Alert(Alert.AlertType.ERROR, "Die eingegbene Zahl ist nicht im richtigen Format.").showAndWait();
                    return;
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
