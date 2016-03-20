package controller;

import db.controller.ProjectController;
import entity.Client;
import entity.Project;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author Andreas
 */
public class AllProjectsController implements Initializable {

    @FXML
    private TableView<Project> tv_ProjectList;
    @FXML
    private TableColumn<Project, String> tc_Id;
    @FXML
    private TableColumn<Project, String> tc_PrecalcId;
    @FXML
    private TableColumn<Project, String> tc_ProjectName;
    @FXML
    private TableColumn<Project, String> tc_Type;
    @FXML
    private TableColumn<Project, String> tc_RoofForm;
    @FXML
    private TableColumn<Project, Client> tc_Client;
    
    private ObservableList<Project> projects;

    /**
     * Initializes the controller class. Adds all projects from the database
     * to the table view.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ProjectController projectController = new ProjectController();
        projects =  FXCollections.observableArrayList(projectController.findAll());
                
        tc_Id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tc_PrecalcId.setCellValueFactory(new PropertyValueFactory<>("preCalculation"));
        tc_ProjectName.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        tc_RoofForm.setCellValueFactory(new PropertyValueFactory<>("roofForm"));
        tc_Client.setCellValueFactory(new PropertyValueFactory<>("client"));
        tc_Type.setCellValueFactory(new PropertyValueFactory<>("modeOfCalculation"));
        tv_ProjectList.setItems(projects);
        
        tv_ProjectList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tv_ProjectList.getSelectionModel().getSelectedItem() != null) {
                MainFormController.getInstance().loadFxmlIntoPane("ProjectView.fxml");
                ProjectViewController.getInstance().openProject(tv_ProjectList.getSelectionModel().getSelectedItem());
            }
        });
    }    
}
