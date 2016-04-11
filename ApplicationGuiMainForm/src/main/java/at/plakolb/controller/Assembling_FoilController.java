package at.plakolb.controller;

import at.plakolb.calculationlogic.util.UtilityFormat;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class Assembling_FoilController implements Initializable, Observer {

    private static Assembling_FoilController instance;
    
    @FXML
    private Label lb_RoofArea;

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

    @Override
    public void update(Observable o, Object arg) {
        double roofArea = Project_ResultAreaController.getInstance().getRoofArea();
        lb_RoofArea.setText(UtilityFormat.getStringForLabel(roofArea) + " m²");
    }
    
    public static Assembling_FoilController getInstance(){
        return instance;
    }

}
