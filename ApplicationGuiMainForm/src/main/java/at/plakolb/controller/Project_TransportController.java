package at.plakolb.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class Project_TransportController implements Initializable {

    private static Project_TransportController instance;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
    }    

    public static Project_TransportController getInstance() {
        return instance;
    }
}
