package at.plakolb.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    public SideViewController() {
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
        } catch (IOException e) {
        } catch (Exception e) {
        }
    }

    @FXML
    private void showAllProjects(MouseEvent event) {
        MainFormController.getInstance().loadFxmlIntoPane("AllProjects.fxml");
    }

    @FXML
    private void createNewProject(MouseEvent event) {
        MainFormController.getInstance().loadFxmlIntoPane("ProjectView.fxml");
    }

    @FXML
    private void showProductList(MouseEvent event) {
        MainFormController.getInstance().loadFxmlIntoPane("ProductList.fxml");
    }

    @FXML
    private void showParameter(MouseEvent event) {
        MainFormController.getInstance().loadFxmlIntoPane("Parameter.fxml");
    }

    @FXML
    private void showClients(MouseEvent event) {
        MainFormController.getInstance().loadFxmlIntoPane("Clients.fxml");
    }

    @FXML
    private void showOptions(MouseEvent event) {
        MainFormController.getInstance().loadFxmlIntoPane("Options.fxml");
    }

    @FXML
    private void showMainForm(MouseEvent event) {
        MainFormController.getInstance().loadFxmlIntoPane("MainForm.fxml");
    }

}
