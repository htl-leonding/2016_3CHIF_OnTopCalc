/*	HTL Leonding	*/
package controller;

import db.controller.ClientController;
import entity.Client;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class ClientsController implements Initializable {

    private static ClientsController instance;
    
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

    /**
     * Initializes the controller class. Adds all clients from the database to
     * the table view.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        
        tc_Name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tc_City.setCellValueFactory(new PropertyValueFactory<>("city"));
        tc_Street.setCellValueFactory(new PropertyValueFactory<>("street"));
        tc_PhoneNumber.setCellValueFactory(new PropertyValueFactory<>("telephoneNumber"));
        tc_ZipCode.setCellValueFactory(new PropertyValueFactory<>("zipCode"));
        tc_Projects.setCellValueFactory(new PropertyValueFactory<>("projectNumbers"));
        tv_Clients.setItems(FXCollections.observableArrayList(new ClientController().findAll()));

        tv_Clients.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tv_Clients.getSelectionModel().getSelectedItem() != null) {
                Parent root;
                try {
                    root = FXMLLoader.load(getClass().getResource("/fxml/ClientModifier.fxml"));
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setTitle("OnTopCalc");
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException ex) {
                }
                ClientModifierController.getInstance().loadClientIntoModifier(tv_Clients.getSelectionModel().getSelectedItem());
            }
        });
    }

    public static ClientsController getInstance(){
        return instance;
    }
    
    /**
     * Refreshes the Table View.
     */
    public void refreshTable() {
        tv_Clients.setItems(FXCollections.observableArrayList(new ClientController().findAll()));
        tv_Clients.getColumns().get(0).setVisible(false);
        tv_Clients.getColumns().get(0).setVisible(true);
    }

}
