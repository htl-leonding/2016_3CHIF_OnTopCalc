/*	HTL Leonding	*/
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author User
 */
public class Project_ColourController implements Initializable {

    private static Project_ColourController instance;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
    }

    public static Project_ColourController getInstance() {
        return instance;
    }
}
