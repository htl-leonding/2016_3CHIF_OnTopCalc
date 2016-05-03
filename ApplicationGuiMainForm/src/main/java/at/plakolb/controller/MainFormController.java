package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ProjectController;
import at.plakolb.calculationlogic.entity.Project;
import at.plakolb.settings.SettingsController;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class The main form controller is responsible to load the
 * correct fxml file into the main pane when a button is pressed.
 *
 * @author Andreas
 */
public class MainFormController implements Initializable {

    @FXML
    private AnchorPane mainPane;    //container for the fxml files
    private static MainFormController instance;
    @FXML
    private MenuButton mb_openProjects;
    @FXML
    private Hyperlink hl_lastBackup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;

        ProjectController projectController = new ProjectController();
        List<Project> projects = projectController.findLastFiveProjects();
        List<MenuItem> items = new LinkedList<>();

        //Adds the five most recent used projects.
        for (int i = 0; i < Math.min(5, projects.size()); i++) {
            MenuItem menuItem = new MenuItem(projects.get(i).getProjectName());
            menuItem.setOnAction(eventHandler -> {
                ProjectViewController.openProject(projectController.findLastFiveProjects().get(items.indexOf(menuItem)));
                loadFxmlIntoPane("ProjectView.fxml");
            });
            items.add(menuItem);
        }

        //Adds an item for all other projects if there are more than 5 projects.        
        if (projectController.getCount() > 5) {
            MenuItem menuItem = new MenuItem("Weitere Projekte");
            menuItem.setOnAction(EventHandler -> {
                loadFxmlIntoPane("AllProjects.fxml");
            });
            items.add(menuItem);
        }

        mb_openProjects.getItems().addAll(items);
        
        if(SettingsController.getBooleanProperty("remindBackup")==true && SettingsController.getDateProperty("lastBackup").getTime()+
                Integer.valueOf(SettingsController.getProperty("remindBackupWeeks"))*604800000 <= new Date().getTime()){
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            hl_lastBackup.setText("Die letzte Sicherung wurde am "+sdf.format(SettingsController.getDateProperty("lastBackup"))+" erstellt. Erstellen Sie jetzt eine Sicherung!");
            hl_lastBackup.setOnAction((event)->{
                loadFxmlIntoPane("Options.fxml");
                OptionsController.getInstance().createBackup();
            });
        }
    }

    public static MainFormController getInstance() {
        return instance;
    }

    public AnchorPane getMainPane() {
        return mainPane;
    }

    /**
     * Loads the given fxml file into the main pane.
     *
     * @param fxmlURL
     */
    public void loadFxmlIntoPane(String fxmlURL) {
        try {
            mainPane.getChildren().clear();
            mainPane.getChildren().add((Node) FXMLLoader.load(getClass().getResource("/fxml/" + fxmlURL)));
        } catch (IOException e) {
        } catch (Exception e) {
        }
    }

    @FXML
    private void printProject(ActionEvent event) {
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
    private void showParameter(ActionEvent event) {
        loadFxmlIntoPane("Parameter.fxml");
    }

    @FXML
    private void showAllProjects(ActionEvent event) {
        loadFxmlIntoPane("AllProjects.fxml");
    }

    @FXML
    private void showProductList(ActionEvent event) {
        loadFxmlIntoPane("ProductList.fxml");
    }

    @FXML
    private void showClients(ActionEvent event) {
        loadFxmlIntoPane("Clients.fxml");
    }

    @FXML
    private void showOptions(ActionEvent event) {
        loadFxmlIntoPane("Options.fxml");
    }

    @FXML
    private void createNewProject(ActionEvent event) {
        loadFxmlIntoPane("ProjectView.fxml");
    }

    @FXML
    private void openAboutBox(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/About.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("OnTopCalc ©2016");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
        } catch (IOException e) {
        } catch (Exception e) {
        }
    }
}
