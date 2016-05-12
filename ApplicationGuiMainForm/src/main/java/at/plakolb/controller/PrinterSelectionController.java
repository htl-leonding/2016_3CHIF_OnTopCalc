package at.plakolb.controller;

/*	HTL Leonding	*/

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/**
 * FXML Controller class
 *
 * @author Andreas
 */
public class PrinterSelectionController implements Initializable {
    @FXML
    private Button bt_ok;
    @FXML
    private Button bt_cancel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void printerserviceChanged(ActionEvent event) {
    }

    @FXML
    private void submit(ActionEvent event) {
    }

    @FXML
    private void cancel(ActionEvent event) {
    }

    @FXML
    private void cntChanged(ActionEvent event) {
    }
    
}
