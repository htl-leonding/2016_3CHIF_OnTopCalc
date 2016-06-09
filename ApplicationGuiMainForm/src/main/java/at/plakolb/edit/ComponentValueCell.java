/*	HTL Leonding	*/
package at.plakolb.edit;

import at.plakolb.calculationlogic.db.entity.Component;
import at.plakolb.calculationlogic.util.UtilityFormat;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;

/**
 * @author Kepplinger
 */
public class ComponentValueCell extends TableCell<Component, String> {

    private TextField textField;

    public ComponentValueCell() {
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

        String item = getString();

        textField = new TextField(item);
        textField.setAlignment(Pos.CENTER);
        textField.setPrefWidth(this.getWidth() - 5);

        textField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            textField.setText(textField.getText().replaceAll(",", ".").replaceAll("[^\\d.]", ""));
            textField.setText(UtilityFormat.removeUnnecessaryCommas(textField.getText()));
        });

        textField.setOnKeyReleased((KeyEvent t) -> {
            if (t.getCode() == KeyCode.ENTER) {

                try {
                    textField.setText(textField.getText().replace(",", "."));
                    double number = Double.parseDouble(textField.getText());
                    if (getTableColumn().getId().equals("tc_Amount") && UtilityFormat.getStringForTableColumn(number).equals("0")) {
                        textField.setText("1");
                    }
                } catch (NumberFormatException ex) {
                    cancelEdit();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Ungültige Eingabe!");
                    alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
                    alert.showAndWait();
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
