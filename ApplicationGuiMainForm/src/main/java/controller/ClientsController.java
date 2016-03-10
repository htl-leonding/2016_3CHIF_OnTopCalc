/*	HTL Leonding	*/
package controller;

import db.controller.ClientController;
import entity.Client;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class ClientsController implements Initializable {

    @FXML
    private TableView<Client> tv_Clients;
    @FXML
    private TableColumn<Client, String> tc_Name;
    @FXML
    private TableColumn<Client, String> tc_Street;
    @FXML
    private TableColumn<Client, String> tc_City;
    @FXML
    private TableColumn<Client, String> tc_ZipCode;
    @FXML
    private TableColumn<Client, String> tc_PhoneNumber;
    @FXML
    private TableColumn<Client, String> tc_Projects;
    private ObservableList<Client> clients;
    
    /**
     * Initializes the controller class. Adds all clients from the database
     * to the table view.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        db.controller.ClientController clientController = new ClientController();
        clients = FXCollections.observableArrayList(clientController.findAll());
        
        tc_Name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tc_City.setCellValueFactory(new PropertyValueFactory<>("city"));
        tc_Street.setCellValueFactory(new PropertyValueFactory<>("street"));
        tc_PhoneNumber.setCellValueFactory(new PropertyValueFactory<>("telephoneNumber"));
        tc_ZipCode.setCellValueFactory(new PropertyValueFactory<>("zipCode"));
        tc_Projects.setCellValueFactory(new PropertyValueFactory<>("projectNumbers"));
        tv_Clients.setItems(clients);
    }

}
