/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ClientController;
import at.plakolb.calculationlogic.entity.Client;
import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Andreas
 */
public class ClientsSmallController extends Observable implements Initializable {

    private static ClientsSmallController instance;
    private ObservableList<Client> clients;
    @FXML
    private TableView<Client> tv_ClientTable;
    @FXML
    private TableColumn<Client, String> tb_Name;
    @FXML
    private TableColumn<Client, String> tb_City;

    /**
     * Initializes the controller class. Adds all clients from the database
     * to the table view.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        at.plakolb.calculationlogic.db.controller.ClientController clientController = new ClientController();
        clients = FXCollections.observableArrayList(clientController.findAll());

        tb_Name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tb_City.setCellValueFactory(new PropertyValueFactory<>("city"));
        tv_ClientTable.setItems(clients);

        tv_ClientTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tv_ClientTable.getSelectionModel().getSelectedItem() != null) {
                setChanged();
                notifyObservers(tv_ClientTable.getSelectionModel().getSelectedItem());
                ((Stage)(((Node)event.getSource()).getScene().getWindow())).close();
            }
        });
    }

    public static ClientsSmallController getInstance() {
        return instance;
    }

    /**
     * Gives the selected Client to the "Project Information Controller".
     *
     * @param event
     */
    @FXML
    private void chooseClient(ActionEvent event) {
        if (tv_ClientTable.getSelectionModel().getSelectedItem() != null) {
            setChanged();
            notifyObservers(tv_ClientTable.getSelectionModel().getSelectedItem());
            ((Stage)(((Node)event.getSource()).getScene().getWindow())).close();
        }
    }
}