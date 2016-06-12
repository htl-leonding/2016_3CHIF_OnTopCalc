/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ProjectController;
import at.plakolb.calculationlogic.db.entity.Client;
import at.plakolb.calculationlogic.db.entity.Project;
import at.plakolb.calculationlogic.db.exceptions.NonexistentEntityException;
import at.plakolb.calculationlogic.util.Logging;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * In this view, all projects are displayed and they can be managed.
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
    @FXML
    private TableColumn tc_action;

    boolean doubleClickAvailable = true;

    /**
     * Initializes the controller class. Adds all projects from the database to
     * the table view. Adds buttons to every project.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Hyperlink lookInBin = new Hyperlink("Im Papierkorb nachsehen");
        lookInBin.setOnAction((event) -> {
            MainFormController.getInstance().loadFxmlIntoPane("Options.fxml");
        });
        VBox placeholder = new VBox(new ImageView(new Image("/images/cloud.png")), new Label("Keine Daten vorhanden"), lookInBin);
        placeholder.setAlignment(Pos.CENTER);
        tv_ProjectList.setPlaceholder(placeholder);

        tc_Id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tc_PrecalcId.setCellValueFactory(new PropertyValueFactory<>("preCalculation"));
        tc_ProjectName.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        tc_RoofForm.setCellValueFactory(new PropertyValueFactory<>("roofForm"));
        tc_Client.setCellValueFactory(new PropertyValueFactory<>("client"));
        tc_Type.setCellValueFactory(new PropertyValueFactory<>("modeOfCalculation"));

        updateData();

        tv_ProjectList.setRowFactory((TableView<Project> param) -> {
            TableRow<Project> tableRow = new TableRow<>();
            tableRow.setOnMouseClicked((MouseEvent event) -> {
                if (event.getClickCount() == 2 && tableRow.getItem() != null && doubleClickAvailable) {
                    ProjectViewController.openProject(tableRow.getItem());
                    MainFormController.getInstance().loadFxmlIntoPane("ProjectView.fxml");
                }
            });
            return tableRow;
        });

        tc_action.setCellFactory(new Callback<TableColumn<Project, String>, TableCell<Project, String>>() {
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
                                ProjectViewController.openProject(p);
                                MainFormController.getInstance().loadFxmlIntoPane("ProjectView.fxml");
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
                                    stage.initModality(Modality.WINDOW_MODAL);
                                    stage.initOwner(((Node) event.getSource()).getScene().getWindow());
                                    PrintProjectController.getInstance().SetProject(p);
                                    stage.show();
                                } catch (IOException ex) {
                                    Logging.getLogger().log(Level.SEVERE, "Couldn't open PrintProject.fxml.", ex);
                                }
                            });
                            costingP.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent event) {
                                    Project project = getTableView().getItems().get(getIndex());
                                    if (project.getPreCalculation() == null) {
                                        long projectId = project.getId();
                                        project.setId(null);
                                        project.setPreCalculation(projectId);
                                        project.setModeOfCalculation("Nachkalkulation");
                                        project.setLastUpdate(LocalDateTime.now(Clock.systemDefaultZone()));
                                        project.setCreationDate(LocalDateTime.now(Clock.systemDefaultZone()));
                                        ProjectController projectController = new ProjectController();
                                        projectController.createCosting(project, projectId);
                                        updateData();
                                    } else {
                                        Alert alert = new Alert(Alert.AlertType.ERROR, "Nachkalkulationen können nur von Vorkalkulationen erstellt werden.");
                                        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                                        alert.showAndWait();
                                    }
                                }
                            });
                            copyP.setOnMouseClicked((MouseEvent event) -> {
                                Project project = getTableView().getItems().get(getIndex());
                                long projectId = project.getId();
                                project.setId(null);
                                project.setPreCalculation(project.getPreCalculation());
                                project.setModeOfCalculation(project.getModeOfCalculation());
                                project.setLastUpdate(LocalDateTime.now(Clock.systemDefaultZone()));
                                project.setCreationDate(LocalDateTime.now(Clock.systemDefaultZone()));
                                ProjectController projectController = new ProjectController();
                                projectController.copy(project, projectId);
                                updateData();
                            });
                            deleteP.setOnMouseClicked((MouseEvent event) -> {
                                Project p = getTableView().getItems().get(getIndex());
                                ProjectController c = new ProjectController();
                                try {
                                    c.sendToRecyclebin(p.getId());
                                    tv_ProjectList.setItems(FXCollections.observableArrayList(new ProjectController().findProjectsByDeletion(false)));
                                } catch (NonexistentEntityException ex) {
                                    Logging.getLogger().log(Level.SEVERE, "Couldn't find project to delete.", ex);
                                }
                            });

                            box.setOnMouseEntered((event) -> {
                                doubleClickAvailable = false;
                            });
                            box.setOnMouseExited((event) -> {
                                doubleClickAvailable = true;
                            });

                            setGraphic(box);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        });
    }

    public void updateData() {
        tv_ProjectList.setItems(FXCollections.observableArrayList(new ProjectController().findProjectsByDeletion(false)));
    }
}
