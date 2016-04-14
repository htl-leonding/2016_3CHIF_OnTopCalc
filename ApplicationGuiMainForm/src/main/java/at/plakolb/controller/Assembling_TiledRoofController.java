package at.plakolb.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class Assembling_TiledRoofController implements Initializable {

    private static Assembling_TiledRoofController instance;
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
    }    
    public void persist(){
        
    }
    public void load() {
        
    }
    public void calculate(){
        
    }
    public static Assembling_TiledRoofController getInstance(){
        return instance;
    }
    
}
