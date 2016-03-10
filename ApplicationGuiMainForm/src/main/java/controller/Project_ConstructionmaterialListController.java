package controller;

/*	HTL Leonding	*/

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author in130079
 */
public class Project_ConstructionmaterialListController implements Initializable {

    private static Project_ConstructionmaterialListController instance;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
    }    

    public static Project_ConstructionmaterialListController getInstance() {
        return instance;
    }
}
