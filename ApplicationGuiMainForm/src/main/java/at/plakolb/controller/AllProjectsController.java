package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ProjectController;
import at.plakolb.calculationlogic.db.exceptions.NonexistentEntityException;
import at.plakolb.calculationlogic.entity.Client;
import at.plakolb.calculationlogic.entity.Project;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
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
     * Initializes the controller class. Adds all projects from the database to
     * the table view.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tc_Id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tc_PrecalcId.setCellValueFactory(new PropertyValueFactory<>("preCalculation"));
        tc_ProjectName.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        tc_RoofForm.setCellValueFactory(new PropertyValueFactory<>("roofForm"));
        tc_Client.setCellValueFactory(new PropertyValueFactory<>("client"));
        tc_Type.setCellValueFactory(new PropertyValueFactory<>("modeOfCalculation"));
        
        updateData();
        
        tv_ProjectList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tv_ProjectList.getSelectionModel().getSelectedItem() != null) {
                MainFormController.getInstance().loadFxmlIntoPane("ProjectView.fxml");
                ProjectViewController.getInstance().openProject(tv_ProjectList.getSelectionModel().getSelectedItem());
            }
        });

        tc_action.setCellValueFactory(new PropertyValueFactory<>("Buttons"));

        Callback<TableColumn<Project, String>, TableCell<Project, String>> cellFactory
                = new Callback<TableColumn<Project, String>, TableCell<Project, String>>() {
                    @Override
                    public TableCell call(final TableColumn<Project, String> param) {
                        final TableCell<Project, String> cell = new TableCell<Project, String>() {

                            final Label openP = new Label();
                            final Label printP = new Label();
                            final Label costingP = new Label();
                            final Label copyP = new Label();
                            final Label deleteP = new Label();
                            final HBox box = new HBox(openP, printP, costingP, copyP, deleteP);

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    openP.setId("edit");
                                    printP.setId("print");
                                    costingP.setId("costing");
                                    copyP.setId("copy");
                                    deleteP.setId("delete");
                                    box.setId("box");
                                    
                                    openP.setTooltip(new Tooltip("Projekt editieren"));
                                    printP.setTooltip(new Tooltip("Projekt drucken"));
                                    costingP.setTooltip(new Tooltip("Nachkalkulation erstellen"));
                                    copyP.setTooltip(new Tooltip("Projekt kopieren"));
                                    deleteP.setTooltip(new Tooltip("Projekt in den Mülleimer verschieben"));

                                    openP.setOnMouseClicked((MouseEvent event) -> {
                                        Project p = getTableView().getItems().get(getIndex());
                                        MainFormController.getInstance().loadFxmlIntoPane("ProjectView.fxml");
                                        ProjectViewController.getInstance().openProject(p);
                                    });
                                    printP.setOnMouseClicked((MouseEvent event) -> {
                                        Project p = getTableView().getItems().get(getIndex());
                                        try {
                                            Parent root = FXMLLoader.load(getClass().getResource("/fxml/PrintProject.fxml"));
                                            Scene scene = new Scene(root);
                                            Stage stage = new Stage();
                                            stage.setTitle("Projekt drucken");
                                            stage.setResizable(false);
                                            stage.setScene(scene);
                                            stage.show();
                                        } catch (IOException e) {
                                        } catch (Exception e) {
                                        }
                                    });
                                    costingP.setOnMouseClicked((MouseEvent event) -> {
                                        Project project = getTableView().getItems().get(getIndex());
                                        Long orginalProjectId = project.getId();
                                        project.setId(null);
                                        project.setPreCalculation(orginalProjectId);
                                        project.setModeOfCalculation("Nachkalkulation");    //TODO - Property File
                                        project.setLastUpdate(new Date());
                                        project.setCreationDate(new Date());
                                        ProjectController projectController = new ProjectController();
                                        projectController.createCosting(project, orginalProjectId);
                                        updateData();

                                    });
                                    copyP.setOnMouseClicked((MouseEvent event) -> {
                                        Project project = getTableView().getItems().get(getIndex());
                                        long orginalProjectId = project.getId();
                                        project.setId(null);
                                        project.setPreCalculation(null);
                                        project.setModeOfCalculation("Vorkalkulation"); //TODO - Property Files
                                        project.setLastUpdate(new Date());
                                        project.setCreationDate(new Date());
                                        ProjectController projectController = new ProjectController();
                                        projectController.copy(project, orginalProjectId);
                                        updateData();
                                    });
                                    deleteP.setOnMouseClicked((MouseEvent event) -> {
                                        Project p = getTableView().getItems().get(getIndex());
                                        ProjectController c = new ProjectController();
                                        try {
                                            c.sendToRecyclebin(p.getId());
                                            tv_ProjectList.setItems(FXCollections.observableArrayList(new ProjectController().findProjectsByDeletion(false)));
                                        } catch (NonexistentEntityException ex) {
                                            Logger.getLogger(AllProjectsController.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    });
                                    setGraphic(box);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };

        tc_action.setCellFactory(cellFactory);

    }

    public void updateData() {
        tv_ProjectList.setItems(
                FXCollections.observableArrayList(
                        new ProjectController().findProjectsByDeletion(false)));
    }
}
