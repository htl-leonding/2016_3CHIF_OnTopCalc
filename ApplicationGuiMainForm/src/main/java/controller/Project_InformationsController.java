package controller;

import entity.Client;
import entity.Project;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

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
    public ListView<String> lv_ConstructionType;
    @FXML
    public ListView<String> lv_RoofForm;
    @FXML
    public Label lb_Id;

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

    public String getConstructionType() {
        return lv_ConstructionType.getSelectionModel().getSelectedItem();
    }

    public String getRoofForm() {
        return lv_RoofForm.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void openClientView(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/ClientsSmall.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Auftraggeber");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
        } catch (Exception e) {
        }
        ClientsSmallController.getInstance().addObserver(this);
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
    }

    /**
     * Opens the passed project.
     * @param project 
     */
    public void openProject(Project project) {
        lb_Id.setTextFill(Color.BLACK);
        lb_Id.setText(String.valueOf(project.getId()));
        tf_ProjectName.setText(project.getProjectName());
        tf_City.setText(project.getClient().getCity());
        tf_ClientName.setText(project.getClient().getName());
        tf_Description.setText(project.getDescription());
        tf_InvoiceNumber.setText(project.getInvoiceNumber());
        tf_Street.setText(project.getClient().getStreet());
        tf_PhoneNumber.setText(project.getClient().getTelephoneNumber());
        tf_ZipCode.setText(project.getClient().getZipCode());
    }
}
