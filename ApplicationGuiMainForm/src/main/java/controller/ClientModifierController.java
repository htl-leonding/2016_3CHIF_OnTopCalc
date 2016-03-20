/*	HTL Leonding	*/
package controller;

import db.controller.ClientController;
import db.controller.ProductController;
import db.exceptions.NonexistentEntityException;
import entity.Client;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class ClientModifierController implements Initializable {

    private static ClientModifierController instance;

    @FXML
    private Label lb_Id;
    @FXML
    private TextField tf_Name;
    @FXML
    private TextField tf_Street;
    @FXML
    private TextField tf_Zipcode;
    @FXML
    private TextField tf_City;
    @FXML
    private TextField tf_Phonenumber;
    @FXML
    private TextField tf_Email;

    private Client openedClient;

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

    public static ClientModifierController getInstance() {
        return instance;
    }

    public void loadClientIntoModifier(Client client) {
        openedClient = client;
        lb_Id.setText(String.valueOf(openedClient.getId()));
        tf_Name.setText(openedClient.getName());
        tf_Street.setText(openedClient.getStreet());
        tf_Zipcode.setText(openedClient.getZipCode());
        tf_City.setText(openedClient.getCity());
        tf_Phonenumber.setText(openedClient.getTelephoneNumber());
        tf_Email.setText(openedClient.getEmail());
    }

    @FXML
    private void deleteClient(ActionEvent event) throws NonexistentEntityException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Möchten Sie diesen Auftraggeber wirklich endgültig löschen. Vorsicht, dieser Vorang kann nicht mehr rückgangig gemacht werden.",
                ButtonType.YES, ButtonType.CANCEL);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            new ClientController().delete(openedClient.getId());
            ClientsController.getInstance().refreshTable();
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void save(ActionEvent event) {
        try {
            openedClient.setName(tf_Name.getText());
            openedClient.setStreet(tf_Street.getText());
            openedClient.setZipCode(tf_Zipcode.getText());
            openedClient.setCity(tf_City.getText());
            openedClient.setTelephoneNumber(tf_Phonenumber.getText());
            openedClient.setEmail(tf_Email.getText());
            new ClientController().edit(openedClient);
        } catch (Exception e) {
        }

        ClientsController.getInstance().refreshTable();
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cancel(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

}
