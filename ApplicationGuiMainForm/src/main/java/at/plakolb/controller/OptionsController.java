package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ProjectController;
import at.plakolb.calculationlogic.db.exceptions.NonexistentEntityException;
import at.plakolb.calculationlogic.entity.Project;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Andreas
 */
public class OptionsController implements Initializable {

    @FXML
    private TableColumn<Project, Integer> cl_id;
    @FXML
    private TableColumn<Project, String> cl_pname;
    @FXML
    private TableColumn<Project, String> cl_type;
    @FXML
    private TableColumn<Project, String> cl_roofType;
    @FXML
    private TableColumn<Project, Integer> cl_client;
    @FXML
    private TableView<Project> tv_paperbin;

    private Tooltip tooltip;
    @FXML
    private TableColumn cl_options;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cl_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        cl_pname.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        cl_client.setCellValueFactory(new PropertyValueFactory<>("client"));
        cl_type.setCellValueFactory(new PropertyValueFactory<>("constructionType"));
        cl_roofType.setCellValueFactory(new PropertyValueFactory<>("roofForm"));
        updateData();

        cl_options.setCellValueFactory(new PropertyValueFactory<>("Buttons"));

        Callback<TableColumn<Project, String>, TableCell<Project, String>> cellFactory
                = new Callback<TableColumn<Project, String>, TableCell<Project, String>>() {
                    @Override
                    public TableCell call(final TableColumn<Project, String> param) {
                        final TableCell<Project, String> cell = new TableCell<Project, String>() {

                            final Label l_restore = new Label();
                            final Label l_delFinal = new Label();
                            final HBox box = new HBox(l_restore, l_delFinal);

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    l_restore.setId("restore");
                                    l_delFinal.setId("deleteFinal");
                                    box.setId("box");

                                    l_restore.setTooltip(new Tooltip("Projekt wiederherstellen"));
                                    l_delFinal.setTooltip(new Tooltip("Projekt entgültig löschen"));

                                    l_restore.setOnMouseClicked(event -> {
                                        Project project = getTableView().getItems().get(getIndex());
                                        project.setDeletion(false);
                                        try {
                                            new ProjectController().edit(project);
                                            updateData();
                                        } catch (NonexistentEntityException ex) {
                                            Logger.getLogger(OptionsController.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        updateData();
                                    });
                                    l_delFinal.setOnMouseClicked(event -> {
                                        Project p = getTableView().getItems().get(getIndex());
                                        ProjectController c = new ProjectController();
                                        c.delete(p.getId());
                                        updateData();
                                    });
                                    setGraphic(box);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };

        cl_options.setCellFactory(cellFactory);
    }

    public void updateData() {
        tv_paperbin.setItems(FXCollections.observableArrayList(new ProjectController().findProjectsByDeletion(true)));
    }
}
