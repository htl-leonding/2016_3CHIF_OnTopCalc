/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ProjectController;
import at.plakolb.calculationlogic.db.entity.Project;
import at.plakolb.calculationlogic.util.Logging;
import at.plakolb.settings.SettingsController;
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
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * FXML Controller class The main form controller is responsible to load the
 * correct fxml file into the main pane when a button is pressed.
 *
 * @author Andreas
 */
public class MainFormController implements Initializable {

    private static MainFormController instance;

    @FXML
    private AnchorPane mainPane;
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

        if (projects.size() == 0) {
            mb_openProjects.setDisable(true);
        } else {
            //Adds the five most recent used projects.
            for (int i = 0; i < Math.min(5, projects.size()); i++) {
                MenuItem menuItem = new MenuItem(projects.get(i).toString());
                menuItem.setOnAction(eventHandler -> {
                    ProjectViewController.openProject(projectController.findLastFiveProjects().get(items.indexOf(menuItem)));
                    loadFxmlIntoPane("ProjectView.fxml");
                });
                items.add(menuItem);
            }

            //Adds an item for all other projects if there are more than 5 projects.
            if (projectController.findProjectsByDeletion(false).size() > 5) {
                MenuItem menuItem = new MenuItem("Weitere Projekte");
                menuItem.setOnAction(EventHandler -> {
                    loadFxmlIntoPane("AllProjects.fxml");
                });
                items.add(new SeparatorMenuItem());
                items.add(menuItem);
            }

            mb_openProjects.getItems().addAll(items);
        }

        LocalDate lastBackup = SettingsController.getDateProperty("lastBackup");

        if (SettingsController.getBooleanProperty("remindBackup") == true && lastBackup.plusWeeks(Long.parseLong(SettingsController.getProperty("remindBackupWeeks"))).isBefore(LocalDate.now())) {
            hl_lastBackup.setText("Die letzte Sicherung wurde am " + lastBackup.getDayOfMonth() + "." + lastBackup.getMonthValue() + "." + lastBackup.getYear() + " erstellt. Erstellen Sie jetzt eine Sicherung!");
            hl_lastBackup.setOnAction((event) -> {
                loadFxmlIntoPane("Options.fxml");
                OptionsController.getInstance().createBackup();
            });
            hl_lastBackup.setVisible(true);
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
            mainPane.getChildren().add(FXMLLoader.load(getClass().getResource("/fxml/" + fxmlURL)));
        } catch (Exception ex) {
            Logging.getLogger().log(Level.SEVERE, "Couldn't load fxml file into pane.", ex);
        }
    }

    @FXML
    private void printProject(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/PrintProject.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Projekt drucken");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
        } catch (Exception ex) {
            Logging.getLogger().log(Level.SEVERE, "Couldn't open print view.", ex);
        }
    }

    @FXML
    private void showParameter(ActionEvent event) {
        loadFxmlIntoPane("Parameter.fxml");
        if (SideViewController.getInstance() != null) {
            SideViewController.getInstance().setCurrentPage(4);
        }
    }

    @FXML
    private void showAllProjects(ActionEvent event) {
        loadFxmlIntoPane("AllProjects.fxml");
        if (SideViewController.getInstance() != null) {
            SideViewController.getInstance().setCurrentPage(1);
        }
    }

    @FXML
    private void showProductList(ActionEvent event) {
        loadFxmlIntoPane("ProductList.fxml");
        if (SideViewController.getInstance() != null) {
            SideViewController.getInstance().setCurrentPage(3);
        }
    }

    @FXML
    private void showClients(ActionEvent event) {
        loadFxmlIntoPane("Clients.fxml");
        if (SideViewController.getInstance() != null) {
            SideViewController.getInstance().setCurrentPage(5);
        }
    }

    @FXML
    private void showOptions(ActionEvent event) {
        loadFxmlIntoPane("Options.fxml");
        if (SideViewController.getInstance() != null) {
            SideViewController.getInstance().setCurrentPage(6);
        }
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
        } catch (Exception ex) {
            Logging.getLogger().log(Level.SEVERE, "Couldn't open About.fxml.", ex);
        }
    }

    @FXML
    private void helpWindow(KeyEvent event) {
        if (event.getCode() == KeyCode.F1) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/Help.fxml"));
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setTitle("OnTopCalc - Hilfe");
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
                stage.setScene(scene);
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(((Node) event.getSource()).getScene().getWindow());
                stage.show();
            } catch (Exception ex) {
                Logging.getLogger().log(Level.SEVERE, "Couldn't open help view.", ex);
            }
        }
    }
}
