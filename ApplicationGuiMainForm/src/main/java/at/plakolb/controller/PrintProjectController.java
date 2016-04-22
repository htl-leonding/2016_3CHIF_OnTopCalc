/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ProjectController;
import at.plakolb.calculationlogic.entity.Project;
import at.plakolb.calculationlogic.util.UtilityFormat;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class PrintProjectController implements Initializable {

    @FXML
    private ComboBox<Project> cb_projects;
    
    private static PrintProjectController instance;
    @FXML
    private CheckBox cb_mainInformations;
    @FXML
    private CheckBox cb_materialAndCostList;
    @FXML
    private CheckBox cb_woodmaterialAndConstruction;
    @FXML
    private CheckBox cb_formwork;
    @FXML
    private CheckBox cb_visibleFormwork;
    @FXML
    private CheckBox cb_foil;
    @FXML
    private CheckBox cb_nailSealingTape;
    @FXML
    private CheckBox cb_battens;
    @FXML
    private CheckBox cb_lathing;
    @FXML
    private CheckBox cb_materialMontage;
    @FXML
    private CheckBox cb_color;
    @FXML
    private CheckBox cb_transport;
    @FXML
    private CheckBox cb_costView;
    @FXML
    private TextField tf_dateAndPosition;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        cb_projects.setItems(FXCollections.observableArrayList(new ProjectController().findProjectsByDeletion(false)));
        
        if (cb_projects.getItems().isEmpty()) {
            cb_projects.setPromptText("Es sind keine Projekte zum Drucken vorhanden.");
        }else{
            cb_projects.getSelectionModel().select(0);
        }
        
        tf_dateAndPosition.setText("Rohrbach, am "+UtilityFormat.getDateString(new Date()));
    }    
    
    public static PrintProjectController getInstance() {
        return instance;
    }
    
    public void SetProject(Project project){
        for(Project p:cb_projects.getItems()){
            if(p.getId().equals(project.getId())){
                cb_projects.getSelectionModel().select(p);
                return;
            }
        }
        System.out.println(project.getProjectNameWithId()+" steht nicht zur Druckauswahl!");
    }

    @FXML
    private void deleteSelection(ActionEvent event) {
        cb_battens.setSelected(false);
        cb_color.setSelected(false);
        cb_costView.setSelected(false);
        cb_foil.setSelected(false);
        cb_formwork.setSelected(false);
        cb_lathing.setSelected(false);
        cb_mainInformations.setSelected(false);
        cb_materialAndCostList.setSelected(false);
        cb_materialMontage.setSelected(false);
        cb_nailSealingTape.setSelected(false);
        cb_transport.setSelected(false);
        cb_visibleFormwork.setSelected(false);
        cb_woodmaterialAndConstruction.setSelected(false);
    }

    @FXML
    private void print(ActionEvent event) {
        
    }
}
