/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ClientController;
import at.plakolb.calculationlogic.db.entity.Client;
import at.plakolb.calculationlogic.db.entity.Project;
import at.plakolb.calculationlogic.util.Logging;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FXML Controller class
 *
 * @author Andreas
 */
public class Project_InformationsController implements Initializable, Observer {

    private static Project_InformationsController instance;
    @FXML
    public TextField tf_ProjectName;
    @FXML
    public TextField tf_InvoiceNumber;
    @FXML
    public TextArea tf_Description;
    @FXML
    public TextField tf_ClientName;
    @FXML
    public TextField tf_Street;
    @FXML
    public TextField tf_ZipCode;
    @FXML
    public TextField tf_City;
    @FXML
    public TextField tf_PhoneNumber;
    @FXML
    public TextField tf_Email;
    @FXML
    public ListView<String> lv_ConstructionType;
    @FXML
    public ListView<String> lv_RoofForm;
    @FXML
    public Label lb_Id;
    @FXML
    public ImageView iv_calcualtionType;
    @FXML
    private Label lb_typeOfCalculation;
    @FXML
    private Button bt_NewClient;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;

        tf_City.textProperty().addListener((observable, oldValue, newValue) -> {
            ModifyController.getInstance().setProject_information(Boolean.TRUE);
        });
        tf_ClientName.textProperty().addListener((observable, oldValue, newValue) -> {
            ModifyController.getInstance().setProject_information(Boolean.TRUE);
        });
        tf_Description.textProperty().addListener((observable, oldValue, newValue) -> {
            ModifyController.getInstance().setProject_information(Boolean.TRUE);
        });
        tf_Email.textProperty().addListener((observable, oldValue, newValue) -> {
            ModifyController.getInstance().setProject_information(Boolean.TRUE);
        });
        tf_InvoiceNumber.textProperty().addListener((observable, oldValue, newValue) -> {
            ModifyController.getInstance().setProject_information(Boolean.TRUE);
        });
        tf_ProjectName.textProperty().addListener((observable, oldValue, newValue) -> {
            ModifyController.getInstance().setProject_information(Boolean.TRUE);
        });
        tf_PhoneNumber.textProperty().addListener((observable, oldValue, newValue) -> {
            ModifyController.getInstance().setProject_information(Boolean.TRUE);
        });
        tf_Street.textProperty().addListener((observable, oldValue, newValue) -> {
            ModifyController.getInstance().setProject_information(Boolean.TRUE);
        });
        tf_ZipCode.textProperty().addListener((observable, oldValue, newValue) -> {
            ModifyController.getInstance().setProject_information(Boolean.TRUE);
        });

        lv_ConstructionType.getSelectionModel().selectedItemProperty().addListener((event) -> {
            ModifyController.getInstance().setProject_information(Boolean.TRUE);
        });
        lv_RoofForm.getSelectionModel().selectedItemProperty().addListener((event) -> {
            ModifyController.getInstance().setProject_information(Boolean.TRUE);
        });

    }

    public static Project_InformationsController getInstance() {
        return instance;
    }

    public String getProjectName() {
        return tf_ProjectName.getText();
    }

    public String getInvoiceNumber() {
        return tf_InvoiceNumber.getText();
    }

    public String getDescription() {
        return tf_Description.getText();
    }

    public String getClientName() {
        return tf_ClientName.getText();
    }

    public String getStreet() {
        return tf_Street.getText();
    }

    public String getZipCode() {
        return tf_ZipCode.getText();
    }

    public String getCity() {
        return tf_City.getText();
    }

    public String getPhoneNumber() {
        return tf_PhoneNumber.getText();
    }

    public String getEmail() {
        return tf_Email.getText();
    }

    public String getConstructionType() {
        return lv_ConstructionType.getSelectionModel().getSelectedItem();
    }

    public String getRoofForm() {
        return lv_RoofForm.getSelectionModel().getSelectedItem();
    }

    /**
     * Adds all informations from the given client to the text fields.
     *
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        Client client = (Client) arg;
        tf_ClientName.setText(client.getName());
        tf_City.setText(client.getCity());
        tf_Street.setText(client.getStreet());
        tf_ZipCode.setText(client.getZipCode());
        tf_PhoneNumber.setText(client.getTelephoneNumber());
        tf_Email.setText(client.getEmail());
        tf_City.setDisable(true);
        tf_ClientName.setDisable(true);
        tf_Street.setDisable(true);
        tf_PhoneNumber.setDisable(true);
        tf_ZipCode.setDisable(true);
        tf_Email.setDisable(true);
        bt_NewClient.setDisable(false);
        ModifyController.getInstance().setProject_information(Boolean.TRUE);
    }

    /**
     * Opens the passed project and loads all informations into the TextFields.
     *
     * @param project
     */
    public void openProject(Project project) {
        lb_Id.setText(String.valueOf(project.getId()));
        lb_typeOfCalculation.setText(project.getModeOfCalculation());
        if (project.getModeOfCalculation().equals("Vorkalkulation")) {
            iv_calcualtionType.setImage(new Image("/images/forward.png"));
        } else {
            iv_calcualtionType.setImage(new Image("/images/backward.png"));
        }
        tf_ProjectName.setText(project.getProjectName());
        tf_Description.setText(project.getDescription());
        tf_InvoiceNumber.setText(project.getInvoiceNumber());
        lv_ConstructionType.getSelectionModel().select(project.getConstructionType());
        lv_RoofForm.getSelectionModel().select(project.getRoofForm());

        if (project.getClient() != null) {
            bt_NewClient.setDisable(false);
            tf_City.setText(project.getClient().getCity());
            tf_ClientName.setText(project.getClient().getName());
            tf_Street.setText(project.getClient().getStreet());
            tf_PhoneNumber.setText(project.getClient().getTelephoneNumber());
            tf_ZipCode.setText(project.getClient().getZipCode());
            tf_Email.setText(project.getClient().getEmail());
            tf_City.setDisable(true);
            tf_ClientName.setDisable(true);
            tf_Street.setDisable(true);
            tf_PhoneNumber.setDisable(true);
            tf_ZipCode.setDisable(true);
            tf_Email.setDisable(true);

        }
        ModifyController.getInstance().setProject_information(Boolean.FALSE);
    }

    /**
     * Opens a new View, where an aleready existing client can be selected.
     *
     * @param event
     */
    @FXML
    private void openClientView(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/ClientsSmall.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Auftraggeber");
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
        } catch (Exception ex) {
            Logging.getLogger().log(Level.SEVERE, "Couldn't open ClientsSmall.fxml", ex);
        }
        ClientsSmallController.getInstance().addObserver(this);
    }

    /**
     * Clears the TextFields.
     *
     * @param event
     */
    @FXML
    private void newClient(ActionEvent event) {
        bt_NewClient.setDisable(true);
        tf_City.setText("");
        tf_ClientName.setText("");
        tf_Street.setText("");
        tf_PhoneNumber.setText("");
        tf_ZipCode.setText("");
        tf_Email.setText("");
        tf_City.setDisable(false);
        tf_ClientName.setDisable(false);
        tf_Street.setDisable(false);
        tf_PhoneNumber.setDisable(false);
        tf_ZipCode.setDisable(false);
        tf_Email.setDisable(false);
        ModifyController.getInstance().setProject_information(Boolean.TRUE);
    }
}
