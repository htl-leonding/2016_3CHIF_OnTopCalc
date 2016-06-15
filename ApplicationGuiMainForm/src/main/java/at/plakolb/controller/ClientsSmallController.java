/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ClientController;
import at.plakolb.calculationlogic.db.entity.Client;
import at.plakolb.calculationlogic.util.Logging;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * FXML Controller class
 *
 * @author Andreas
 */
public class ClientsSmallController extends Observable implements Initializable {

    private static ClientsSmallController instance;
    public TableColumn tb_Edit;
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

        tb_Edit.setCellValueFactory(new PropertyValueFactory<>("Buttons"));

        Callback<TableColumn<Client, String>, TableCell<Client, String>> cellFactory
                = new Callback<TableColumn<Client, String>, TableCell<Client, String>>() {
            @Override
            public TableCell call(final TableColumn<Client, String> param) {
                final TableCell<Client, String> cell = new TableCell<Client, String>() {

                    final Label lb_edit = new Label();
                    final HBox box = new HBox(lb_edit) ;

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            lb_edit.setId("edit");
                            box.setId("box");
                            box.setAlignment(Pos.CENTER);

                            lb_edit.setTooltip(new Tooltip("Auftraggeber editieren"));

                            lb_edit.setOnMouseClicked(event -> {
                                Parent root;
                                try {
                                    root = FXMLLoader.load(getClass().getResource("/fxml/ClientModifier.fxml"));
                                    Scene scene = new Scene(root);
                                    Stage stage = new Stage();
                                    stage.setTitle("Auftraggeber editieren");
                                    stage.setScene(scene);
                                    stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
                                    stage.initModality(Modality.WINDOW_MODAL);
                                    stage.initOwner(((Node) event.getSource()).getScene().getWindow());
                                    stage.show();
                                } catch (IOException ex) {
                                    Logging.getLogger().log(Level.SEVERE, "Couldn't open ClientModifier.fxml.", ex);
                                }

                                Client client = getTableView().getItems().get(getIndex());
                                ClientModifierController.getInstance().loadClientIntoModifier(tv_ClientTable,client);
                                updateData();
                            });
                            setGraphic(box);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };

        tb_Edit.setCellFactory(cellFactory);
    }

    private void updateData() {
        at.plakolb.calculationlogic.db.controller.ClientController clientController = new ClientController();
        clients = FXCollections.observableArrayList(clientController.findAll());

        tv_ClientTable.getItems().clear();
        tv_ClientTable.setItems(clients);
        tv_ClientTable.refresh();
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