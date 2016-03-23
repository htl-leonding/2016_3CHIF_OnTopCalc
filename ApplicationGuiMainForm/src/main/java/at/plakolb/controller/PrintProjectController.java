/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ProjectController;
import at.plakolb.calculationlogic.entity.Project;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class PrintProjectController implements Initializable {

    @FXML
    private ComboBox<Project> cb_projects;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cb_projects.setItems(FXCollections.observableArrayList(new ProjectController().findAll()));
    }    
    
}
