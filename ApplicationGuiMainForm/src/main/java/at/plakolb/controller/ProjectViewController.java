package at.plakolb.controller;

//import SavestateListener.Utilredo;
import at.plakolb.calculationlogic.db.controller.ClientController;
import at.plakolb.calculationlogic.db.controller.ProjectController;
import at.plakolb.calculationlogic.db.exceptions.NonexistentEntityException;
import at.plakolb.calculationlogic.entity.Client;
import at.plakolb.calculationlogic.entity.Project;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author Andreas
 */
public class ProjectViewController implements Initializable {

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

        tb_MainPane.addEventFilter(MouseEvent.MOUSE_PRESSED, (MouseEvent event) -> {
            String t = event.getTarget().toString().toLowerCase();
            if (t.contains("tabpaneskin")
                    || t.contains("text")) {
                return;
            }
            Tab actTab = tb_MainPane.getSelectionModel().getSelectedItem();
            if (!actTab.getText().contains("*")) {
                actTab.setText(actTab.getText() + " *");
            }
        });
        if (projectOpened) {
            Project_InformationsController.getInstance().openProject(openedProject);
            bt_Dismiss.setText("Änderungen verwerfen");
            bt_Save.setText("Änderungen speichern");
        } else {
            openedProject = new Project();
        }
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
    private void saveProject(ActionEvent event) {
        Project_InformationsController informations = Project_InformationsController.getInstance();
        Client client = null;

        if (informations.getProjectName().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Bitte geben Sie einen Projektnamen ein.").showAndWait();
        } else {

            if (informations.getClientName().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Sie haben keinen Auftragsgebernamen eingegeben. Deshalb kann kein neuer Auftragsgeber erstellt werden. Möchten Sie fortfahren?",
                        ButtonType.YES, ButtonType.CANCEL);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.CANCEL) {
                    return;
                }
            } else {
                client = findClient(informations);
            }

            ProjectController projectController = new ProjectController();

            if (projectOpened) {
                try {
                    openedProject.setClient(client);
                    openedProject.setProjectName(informations.getProjectName());
                    openedProject.setDescription(informations.getDescription());
                    openedProject.setInvoiceNumber(informations.getInvoiceNumber());
                    openedProject.setConstructionType(informations.getConstructionType());
                    openedProject.setRoofForm(informations.getRoofForm());
                    projectController.edit(openedProject);
                } catch (NonexistentEntityException ex) {
                }
            } else {
                openedProject.setClient(client);
                openedProject.setProjectName(informations.getProjectName());
                openedProject.setDescription(informations.getDescription());
                openedProject.setInvoiceNumber(informations.getInvoiceNumber());
                openedProject.setConstructionType(informations.getConstructionType());
                openedProject.setRoofForm(informations.getRoofForm());
                projectController.create(openedProject);
            }

            Project_ResultAreaController.getInstance().persistArea();
            Project_ConstructionMaterialListController.getInstance().persistComponents();
            Project_TransportController.getInstance().persistTransportCosts();

            MainFormController.getInstance().loadFxmlIntoPane("MainForm.fxml");
            projectOpened = false;
            openedProject = null;
        }
    }

    /**
     * Dismisses all changes that were made and returns to the main form.
     *
     * @param event
     */
    @FXML
    private void dismiss(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Sind die sicher? Alle nichtgespeicherten Änderungen gehen verloren.",
                ButtonType.YES, ButtonType.CANCEL);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            MainFormController.getInstance().loadFxmlIntoPane("MainForm.fxml");
            projectOpened = false;
            openedProject = null;
            //TODO
            //Utilredo.undo();
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

    public TabPane getTb_MainPane() {
        return tb_MainPane;
    }
}
