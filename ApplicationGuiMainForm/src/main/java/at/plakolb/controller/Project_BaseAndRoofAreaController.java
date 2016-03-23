/*	HTL Leonding	*/
package at.plakolb.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class Project_BaseAndRoofAreaController implements Initializable {

    @FXML
    private TextField tf_Width;
    @FXML
    private TextField tf_Length;
    @FXML
    private TextField tf_Angle;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @FXML
    private void calcBaseArea(ActionEvent event) {
    }

    @FXML
    private void calcRoofArea(ActionEvent event) {
    }
    
}
