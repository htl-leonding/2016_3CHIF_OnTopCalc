/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ParameterController;
import at.plakolb.calculationlogic.entity.ParameterP;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class ParameterModifierController implements Initializable {

    private static ParameterModifierController instance;

    @FXML
    private Label lb_Parametername;
    @FXML
    private TextField tf_DefaultValue;
    @FXML
    private Label lb_Unit;

    private ParameterP openedParameter;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
    }

    public static ParameterModifierController getInstance() {
        return instance;
    }

    /**
     * Loads a parameter into the modifier.
     *
     * @param parameter
     */
    public void loadParameterIntoModifier(ParameterP parameter) {
        DecimalFormat decimalFormat = new DecimalFormat("#.########");
        decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));

        openedParameter = parameter;
        lb_Parametername.setText(parameter.getLongTerm());
        lb_Unit.setText(parameter.getUnit().getShortTerm());
        tf_DefaultValue.setText(decimalFormat.format(parameter.getDefaultValue()));
    }

    /**
     * Saves all the changes that were made.
     *
     * @param event
     */
    @FXML
    private void save(ActionEvent event) {
        try {
            if (tf_DefaultValue.getText().contains("-")) {
                throw new NumberFormatException();
            }
            openedParameter.setDefaultValue(Double.parseDouble(tf_DefaultValue.getText()));
            new ParameterController().edit(openedParameter);
        } catch (NumberFormatException e) {
            ((Stage) (((Node) event.getSource()).getScene().getWindow())).close();
            new Alert(Alert.AlertType.ERROR, "Die Zahl ist ungültig.").showAndWait();
        } catch (Exception e) {
        }
        ParameterViewController.getInstance().refreshTable();
        ((Stage) (((Node) event.getSource()).getScene().getWindow())).close();
    }

    /**
     * Nothing gets saved and the window gets closed.
     *
     * @param event
     */
    @FXML
    private void cancel(ActionEvent event) {
        ((Stage) (((Node) event.getSource()).getScene().getWindow())).close();
    }

}
