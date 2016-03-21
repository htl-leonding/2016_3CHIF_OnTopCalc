package controller;

import com.sun.prism.impl.shape.OpenPiscesPrismUtils;
import db.controller.ProjectController;
import entity.Client;
import entity.Project;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

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
    @FXML
    private TableColumn tc_action;

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
        
        tc_action.setCellValueFactory( new PropertyValueFactory<>( "DUMMY" ) );

        Callback<TableColumn<Project, String>, TableCell<Project, String>> cellFactory = //
                new Callback<TableColumn<Project, String>, TableCell<Project, String>>()
                {
                    @Override
                    public TableCell call( final TableColumn<Project, String> param )
                    {
                        final TableCell<Project, String> cell = new TableCell<Project, String>()
                        {

                            final Label openP = new Label();
                            final Label printP = new Label();
                            final Label costingP = new Label();
                            final Label copyP = new Label();
                            final Label deleteP = new Label();
                            final HBox box = new HBox(openP,printP,costingP,copyP,deleteP);
                            
                            @Override
                            public void updateItem( String item, boolean empty )
                            {
                                super.updateItem( item, empty );
                                if ( empty )
                                {
                                    setGraphic( null );
                                    setText( null );
                                }
                                else
                                {
                                    openP.setId("edit");
                                    printP.setId("print");
                                    costingP.setId("costing");
                                    copyP.setId("copy");
                                    deleteP.setId("delete");
                                    box.setId("box");
                                    
                                    openP.setOnMouseClicked((MouseEvent event) -> {
                                        Project p = getTableView().getItems().get( getIndex() );
                                        //TODO
                                    });
                                    printP.setOnMouseClicked((MouseEvent event) -> {
                                        Project p = getTableView().getItems().get( getIndex() );
                                        //TODO
                                    });
                                    costingP.setOnMouseClicked((MouseEvent event) -> {
                                        Project p = getTableView().getItems().get( getIndex() );
                                        //TODO
                                    });
                                    copyP.setOnMouseClicked((MouseEvent event) -> {
                                        Project p = getTableView().getItems().get( getIndex() );
                                        //TODO
                                    });
                                    deleteP.setOnMouseClicked((MouseEvent event) -> {
                                        Project p = getTableView().getItems().get( getIndex() );
                                        //TODO
                                    });
                                    setGraphic(box);
                                    setText( null );
                                }
                            }
                        };
                        return cell;
                    }
                };

        tc_action.setCellFactory( cellFactory );
        
    }    
}
