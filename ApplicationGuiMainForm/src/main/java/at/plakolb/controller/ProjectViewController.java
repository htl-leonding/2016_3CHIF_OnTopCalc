/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ClientController;
import at.plakolb.calculationlogic.db.controller.ProjectController;
import at.plakolb.calculationlogic.db.entity.Client;
import at.plakolb.calculationlogic.db.entity.Project;
import at.plakolb.calculationlogic.util.Logging;
import at.plakolb.main.MainApp;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * FXML Controller class
 *
 * @author Andreas
 */
public class ProjectViewController extends Observable implements Initializable, Observer {

    private static ProjectViewController instance;
    private static Project openedProject;
    private static boolean projectOpened;

    @FXML
    private TabPane tb_MainPane;
    @FXML
    private Button bt_Prev;
    @FXML
    private Button bt_Next;
    @FXML
    private Button bt_Dismiss;
    @FXML
    private Button bt_Save;
    @FXML
    private Tab tab_Montage;
    @FXML
    private Tab tab_Material;
    @FXML
    private Tab tab_MaterialCosts;
    @FXML
    private Tab tab_Colour;
    @FXML
    private Tab tab_Overview;

    private TabPane tb_Assebmling;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        configurateStage(true);

        addObserver(Assembling_FormworkController.getInstance());
        addObserver(Assembling_VisibleFormworkController.getInstance());
        addObserver(Assembling_FoilController.getInstance());
        ModifyController.getInstance().addObserver(this);

        if (projectOpened) {
            Project_InformationsController.getInstance().openProject(openedProject);
            bt_Dismiss.setText("Abbrechen");
            bt_Save.setText("Speichern");
        } else {
            openedProject = new Project();
        }

        tab_Montage.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            Project_ColourController.getInstance().updateVisibleFormwork();
            Project_MaterialAndCostController.getInstance().refreshTableView();
            Project_ColourController.getInstance().updateVisibleFormwork();
            Project_OverviewController.getInstance().refreshValues();
            setChanged();
            notifyObservers();
        });

        tab_Material.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            Project_ConstructionMaterialController.getInstance().refreshComponents();
        });

        tab_MaterialCosts.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            Project_ColourController.getInstance().updateVisibleFormwork();
            Project_MaterialAndCostController.getInstance().refreshTableView();
            Project_ColourController.getInstance().updateVisibleFormwork();
            Project_OverviewController.getInstance().refreshValues();
            setChanged();
            notifyObservers();
            Project_MaterialAndCostController.getInstance().refreshTableView();
        });

        tab_Colour.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            setChanged();
            notifyObservers();
            Project_ColourController.getInstance().updateVisibleFormwork();
        });

        tab_Overview.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            setChanged();
            notifyObservers();
            Project_ColourController.getInstance().updateVisibleFormwork();
            Project_MaterialAndCostController.getInstance().refreshTableView();
            Project_ColourController.getInstance().updateVisibleFormwork();
            Project_OverviewController.getInstance().refreshValues();
        });
    }

    public static ProjectViewController getInstance() {
        return instance;
    }

    /**
     * Selects the previous tab in the right tab pane.
     *
     * @param event
     */
    @FXML
    private void selectPreviousTab(ActionEvent event) {

        tb_Assebmling = AssemblingController.getInstance().getTb_AssemblingPane();

        //If the third tab is selected, the assembling tab pane has to be changed.
        if (tb_MainPane.getSelectionModel().isSelected(3) && tb_Assebmling.getSelectionModel().getSelectedIndex() != 0) {
            tb_Assebmling.getSelectionModel().selectPrevious();
        } else {
            tb_MainPane.getSelectionModel().selectPrevious();
            if (tb_MainPane.getSelectionModel().isSelected(3)) {
                tb_Assebmling.getSelectionModel().selectLast();
            }
        }
    }

    /**
     * Selects the next tab in the right tab pane.
     *
     * @param event
     */
    @FXML
    private void selectNextTab(ActionEvent event) {

        tb_Assebmling = AssemblingController.getInstance().getTb_AssemblingPane();

        //If the third tab is selected, the assembling tab pane has to be changed.
        if (tb_MainPane.getSelectionModel().isSelected(3) && tb_Assebmling.getSelectionModel().getSelectedIndex() != tb_Assebmling.getTabs().size() - 1) {
            tb_Assebmling.getSelectionModel().selectNext();
        } else {
            tb_MainPane.getSelectionModel().selectNext();
            if (tb_MainPane.getSelectionModel().isSelected(3)) {
                tb_Assebmling.getSelectionModel().selectFirst();
            }
        }
    }

    /**
     * Creates with all informations from the informationController a new
     * project which gets created in the database.
     *
     * @param event
     */
    @FXML
    public boolean saveProject(ActionEvent event) {
        Project_InformationsController informations = Project_InformationsController.getInstance();
        Client client = null;

        if (informations.getProjectName().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Bitte geben Sie einen Projektnamen ein.");
            alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
            alert.showAndWait();
            return false;
        } else {

            if (informations.getClientName().isEmpty() && !projectOpened) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Sie haben keinen Auftragsgebernamen eingegeben. Deshalb kann kein neuer Auftragsgeber erstellt werden. Möchten Sie fortfahren?",
                        ButtonType.YES, ButtonType.CANCEL);
                alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
                alert.showAndWait();
                if (alert.getResult() == ButtonType.CANCEL) {
                    return false;
                }
            } else if (!informations.getClientName().isEmpty()) {
                client = findClient(informations);
            }

            ProjectController projectController = new ProjectController();
            try {
                if (projectOpened) {

                    openedProject.setClient(client);
                    openedProject.setProjectName(informations.getProjectName());
                    openedProject.setDescription(informations.getDescription());
                    openedProject.setInvoiceNumber(informations.getInvoiceNumber());
                    openedProject.setConstructionType(informations.getConstructionType());
                    openedProject.setRoofForm(informations.getRoofForm());
                    projectController.edit(openedProject);

                } else {
                    openedProject.setClient(client);
                    openedProject.setProjectName(informations.getProjectName());
                    openedProject.setDescription(informations.getDescription());
                    openedProject.setInvoiceNumber(informations.getInvoiceNumber());
                    openedProject.setConstructionType(informations.getConstructionType());
                    openedProject.setRoofForm(informations.getRoofForm());
                    projectController.create(openedProject);
                }
            } catch (Exception ex) {
                Logging.getLogger().log(Level.SEVERE, "Couldn't persist project.", ex);
            }

            if (openedProject.getWorths().isEmpty()) {
                projectOpened = false;
            }

            if (!projectOpened) {
                bt_Dismiss.setText("Abbrechen");
                bt_Save.setText("Speichern");
            }

            Project_ResultAreaController.getInstance().persist();
            Project_ConstructionMaterialListController.getInstance().persist();
            Project_TransportController.getInstance().persist();
            AssemblingController.getInstance().persist();
            Project_ColourController.getInstance().persist();
            Project_ConstructionMaterialController.getInstance().persist();

            ModifyController.getInstance().reset();

            projectOpened = true;
        }
        return true;
    }

    /**
     * Dismisses all changes that were made and returns to the main form.
     *
     * @param event
     */
    @FXML
    private void dismiss(ActionEvent event) {
        if (ModifyController.getInstance().isModified()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Sind Sie sicher? Alle nichtgespeicherten Änderungen gehen verloren.",
                    ButtonType.YES, ButtonType.CANCEL);
            alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                ModifyController.getInstance().reset();
                configurateStage(false);
                MainFormController.getInstance().loadFxmlIntoPane("MainForm.fxml");
                projectOpened = false;
                openedProject = null;
            }
        } else {
            ModifyController.getInstance().reset();
            configurateStage(false);
            MainFormController.getInstance().loadFxmlIntoPane("MainForm.fxml");
            projectOpened = false;
            openedProject = null;
        }

    }

    /**
     * Opens the passed project.
     *
     * @param project
     */
    public static void openProject(Project project) {
        projectOpened = true;
        openedProject = project;
    }

    /**
     * Searches for the selected client in the database. If its not availabe, a
     * new client is created
     *
     * @param informations
     * @return
     */
    private Client findClient(Project_InformationsController informations) {
        Client client;
        ClientController clientController = new ClientController();

        for (Client dbClient : clientController.findAll()) {
            if (dbClient.getName().equals(informations.getClientName())
                    && dbClient.getStreet().equals(informations.getStreet())
                    && dbClient.getCity().equals(informations.getCity())
                    && dbClient.getZipCode().equals(informations.getZipCode())
                    && dbClient.getTelephoneNumber().equals(informations.getPhoneNumber())) {
                client = dbClient;
                return client;
            }
        }

        client = new Client(informations.getClientName(),
                informations.getStreet(),
                informations.getCity(),
                informations.getZipCode(),
                informations.getPhoneNumber(),
                informations.getEmail());
        clientController.create(client);
        return client;
    }

    /**
     * Manges the availabilty of the buttons at the first and the last tabs.
     *
     * @param event
     */
    @FXML
    private void firstTabSelected(Event event) {
        bt_Prev.setDisable(true);
        bt_Next.setDisable(false);
    }

    /**
     * Manges the availabilty of the buttons at the first and the last tabs.
     *
     * @param event
     */
    @FXML
    private void lastTabSelected(Event event) {
        bt_Next.setDisable(true);
        bt_Prev.setDisable(false);
    }

    /**
     * Manges the availabilty of the buttons at the first and the last tabs.
     *
     * @param event
     */
    @FXML
    private void normalTabSelected(Event event) {
        bt_Prev.setDisable(false);
        bt_Next.setDisable(false);
    }

    /**
     * Returns the current opened project.
     *
     * @return
     */
    public static Project getOpenedProject() {
        return openedProject;
    }

    public static boolean isProjectOpened() {
        return projectOpened;
    }

    public TabPane getTb_MainPane() {
        return tb_MainPane;
    }

    @Override
    public void update(Observable o, Object arg) {
        List<Boolean> modified = ModifyController.getInstance().getModifiedList();
        int idx = 0;
        for (Tab tab : tb_MainPane.getTabs()) {
            String text = tab.getText();
            tab.getStyleClass().remove("changed");
            if (idx < modified.size() && modified.get(idx) == true) {
                if (!text.contains("*")) {
                    tab.setText("*" + text);
                }
                tab.getStyleClass().add("changed");
            } else if (text.contains("*")) {
                tab.setText(text.substring(1));
            }
            idx++;
        }

        if (ModifyController.getInstance().isModified() && !bt_Save.getStyleClass().contains("modified")) {
            bt_Save.getStyleClass().add(0, "modified");
        } else if (!ModifyController.getInstance().isModified() && bt_Save.getStyleClass().contains("modified")) {
            bt_Save.getStyleClass().removeAll("modified");
        }
    }

    private void configurateStage(boolean b) {
        if (b) {
            MainApp.getStage().setOnCloseRequest((event) -> {
                if (ModifyController.getInstance().isModified()) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setContentText("Möchten Sie die Änderungen speichern?");
                    alert.getButtonTypes().clear();
                    alert.getButtonTypes().add(new ButtonType("Speichern"));
                    alert.getButtonTypes().add(new ButtonType("Verwerfen"));
                    alert.getButtonTypes().add(ButtonType.CANCEL);
                    alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
                    alert.showAndWait();
                    switch (alert.getResult().getText()) {
                        case "Speichern":
                            if (!saveProject(null)) {
                                event.consume();
                            }
                            break;
                        case "Verwerfen":
                            break;
                        default:
                            event.consume();
                            break;
                    }
                }
            });
            MainApp.getStage().getScene().setOnKeyPressed((event) -> {
                if (event.isShortcutDown()) {
                    if (event.getCode() == KeyCode.S) {
                        saveProject(null);
                    }
                } else if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.D) {
                    selectNextTab(null);
                } else if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.A) {
                    selectPreviousTab(null);
                }
            });
        } else {
            MainApp.getStage().setOnCloseRequest(null);
        }
    }
}
