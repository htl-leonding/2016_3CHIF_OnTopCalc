package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class Project_MaterialAndCostController implements Initializable {

    private static Project_MaterialAndCostController instance;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
    }    

    public static Project_MaterialAndCostController getInstance() {
        return instance;
    }
}
