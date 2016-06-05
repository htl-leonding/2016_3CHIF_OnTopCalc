/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.util.Logging;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class The side view controller is responsible to load the
 * correct fxml files into the main form controller's pane when a button is
 * pressed.
 *
 * @author Andreas
 */
public class SideViewController implements Initializable {

    private static SideViewController controller;
    private static int selectedPage = -1;

    @FXML
    private Label lb_showProjects;
    @FXML
    private Label lb_newProject;
    @FXML
    private Label lb_produktList;
    @FXML
    private Label lb_parameter;
    @FXML
    private Label lb_clients;
    @FXML
    private Label lb_options;

    public SideViewController() {
        controller = this;
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selectCurrentPage();
    }

    @FXML
    private void printProject(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/PrintProject.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Projekt drucken");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
        } catch (Exception ex) {
            Logging.getLogger().log(Level.SEVERE, "Couldn't open PrintProject.fxml.", ex);
        }
    }

    @FXML
    private void showAllProjects(MouseEvent event) {
        selectedPage = 1;
        MainFormController.getInstance().loadFxmlIntoPane("AllProjects.fxml");
    }

    @FXML
    private void createNewProject(MouseEvent event) {
        selectedPage = -1;
        MainFormController.getInstance().loadFxmlIntoPane("ProjectView.fxml");
    }

    @FXML
    private void showProductList(MouseEvent event) {
        selectedPage = 3;
        MainFormController.getInstance().loadFxmlIntoPane("ProductList.fxml");
    }

    @FXML
    private void showParameter(MouseEvent event) {
        selectedPage = 4;
        MainFormController.getInstance().loadFxmlIntoPane("Parameter.fxml");
    }

    @FXML
    private void showClients(MouseEvent event) {
        selectedPage = 5;
        MainFormController.getInstance().loadFxmlIntoPane("Clients.fxml");
    }

    @FXML
    private void showOptions(MouseEvent event) {
        selectedPage = 6;
        MainFormController.getInstance().loadFxmlIntoPane("Options.fxml");
    }

    @FXML
    private void showMainForm(MouseEvent event) {
        selectedPage = -1;
        MainFormController.getInstance().loadFxmlIntoPane("MainForm.fxml");
    }

    public void selectCurrentPage(){
        switch (selectedPage) {
            case 1:
                lb_showProjects.getStyleClass().add("currentPage");
                break;
            case 3:
                lb_produktList.getStyleClass().add("currentPage");
                break;
            case 4:
                lb_parameter.getStyleClass().add("currentPage");
                break;
            case 5:
                lb_clients.getStyleClass().add("currentPage");
                break;
            case 6:
                lb_options.getStyleClass().add("currentPage");
                break;
            default:
                break;
        }
    }
    
    public void setCurrentPage(int i){
        selectedPage = i;
        selectCurrentPage();
    }
    
    public static SideViewController getInstance(){
        return controller;
    }
}
