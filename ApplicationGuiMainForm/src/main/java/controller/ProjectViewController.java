package controller;

import db.controller.ClientController;
import db.controller.ProjectController;
import db.exceptions.NonexistentEntityException;
import entity.Client;
import entity.Project;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;

/**
 * FXML Controller class
 *
 * @author Andreas
 */
public class ProjectViewController implements Initializable {

    @FXML
    private TabPane tb_MainPane;
    @FXML
    private Button bt_Prev;
    @FXML
    private Button bt_Next;
    private TabPane tb_Assebmling;
    private static ProjectViewController instance;
    @FXML
    private Button bt_Dismiss;
    @FXML
    private Button bt_Save;
    private boolean projectOpened;
    private Project openedProject;

    public ProjectViewController() {
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
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
        Client client = findClient(informations);

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
            Project project = new Project(informations.getProjectName(),
                    informations.getInvoiceNumber(),
                    informations.getDescription(),
                    informations.getConstructionType(),
                    informations.getRoofForm(),
                    client);
            projectController.create(project);
        }
        MainFormController.getInstance().loadFxmlIntoPane("MainForm.fxml");
        projectOpened = false;
    }

    /**
     * Dismisses all changes that were made and returns to the main form.
     *
     * @param event
     */
    @FXML
    private void dismiss(ActionEvent event) {
        MainFormController.getInstance().loadFxmlIntoPane("MainForm.fxml");
        projectOpened = false;
    }

    /**
     * Opens the passed project.
     *
     * @param project
     */
    public void openProject(Project project) {
        projectOpened = true;
        openedProject = project;
        Project_InformationsController.getInstance().openProject(project);
        bt_Dismiss.setText("Änderungen verwerfen");
        bt_Save.setText("Änderungen speichern");
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
}
