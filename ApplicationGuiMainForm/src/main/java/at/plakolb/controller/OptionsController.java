package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ProjectController;
import at.plakolb.calculationlogic.db.exceptions.NonexistentEntityException;
import at.plakolb.calculationlogic.entity.Project;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import sun.java2d.pipe.hw.AccelDeviceEventNotifier;

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
        cl_client.setCellValueFactory(new PropertyValueFactory<>("id"));
        cl_type.setCellValueFactory(new PropertyValueFactory<>("constructionType"));
        cl_roofType.setCellValueFactory(new PropertyValueFactory<>("roofform"));
        tooltip = new Tooltip("Drücken Sie zwei Mal auf ein Projekt um es wiederherzustellen");
        updateData();

        tv_paperbin.setOnMouseEntered(event -> {
            double x = tv_paperbin.localToScreen(tv_paperbin.getLayoutBounds()).getMinX()+tv_paperbin.getWidth()/2-tooltip.getWidth()/2;
            double y = tv_paperbin.localToScreen(tv_paperbin.getLayoutBounds()).getMinY()+tv_paperbin.getHeight()+10;
            tooltip.show(tv_paperbin, x, y);
        });
        tv_paperbin.setOnMouseExited(event->{
            tooltip.hide();
        });
        tv_paperbin.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2
                    && tv_paperbin.getSelectionModel().getSelectedItem() != null) {
                Project p = tv_paperbin.getSelectionModel().getSelectedItem();
                p.setDeletion(false);
                try {
                    new ProjectController().edit(p);
                    updateData();
                } catch (NonexistentEntityException ex) {
                    Logger.getLogger(OptionsController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public void updateData() {
        tv_paperbin.setItems(FXCollections.observableArrayList(new ProjectController().findProjectsByDeletion(true)));
    }
}
