package at.plakolb.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class Assembling_FormworkController implements Initializable {

    @FXML
    private TextField tf_Waste;
    @FXML
    private ChoiceBox<?> cb_Formwork;
    @FXML
    private TextField tf_Price;
    @FXML
    private Label lb_Waste;
    @FXML
    private Label lb_Formwork;
    @FXML
    private TextField tf_Time;
    @FXML
    private TextField tf_Wage;
    @FXML
    private Label lb_ProductCosts;
    @FXML
    private Label lb_AssebmlyCosts;
    @FXML
    private Label lb_TotalCosts;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @FXML
    private void calcFormwork(ActionEvent event) {
    }

    @FXML
    private void calcCosts(ActionEvent event) {
    }
    
}
