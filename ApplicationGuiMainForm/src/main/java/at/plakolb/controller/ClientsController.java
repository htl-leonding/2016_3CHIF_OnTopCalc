/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.util.Logging;
import at.plakolb.calculationlogic.db.controller.ClientController;
import at.plakolb.calculationlogic.entity.Client;
import at.plakolb.calculationlogic.entity.Product;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

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
    @FXML
    private TableColumn tc_Buttons;

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

        VBox placeholder = new VBox(new ImageView(new Image("/images/cloud.png")),new Label("Keine Daten vorhanden"));
        placeholder.setAlignment(Pos.CENTER);
        tv_Clients.setPlaceholder(placeholder);
        
        tc_Name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tc_City.setCellValueFactory(new PropertyValueFactory<>("city"));
        tc_Street.setCellValueFactory(new PropertyValueFactory<>("street"));
        tc_PhoneNumber.setCellValueFactory(new PropertyValueFactory<>("telephoneNumber"));
        tc_ZipCode.setCellValueFactory(new PropertyValueFactory<>("zipCode"));
        tc_Projects.setCellValueFactory(new PropertyValueFactory<>("projectNumbers"));

        tc_Buttons.setCellFactory((new Callback<TableColumn<Product, String>, TableCell<Product, String>>() {
            @Override
            public TableCell call(final TableColumn<Product, String> param) {
                final TableCell<Product, String> cell = new TableCell<Product, String>() {

                    final Label edit = new Label();
                    final Label delete = new Label();
                    final HBox box = new HBox(edit, delete);

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            edit.setId("edit");
                            delete.setId("deleteFinal");
                            box.setId("box");
                            box.setSpacing(2);
                            box.setAlignment(Pos.CENTER);

                            edit.setTooltip(new Tooltip("Auftraggeber editieren"));
                            delete.setTooltip(new Tooltip("Auftraggeber löschen"));

                            edit.setOnMouseClicked((MouseEvent event) -> {
                                Parent root;
                                try {
                                    root = FXMLLoader.load(getClass().getResource("/fxml/ClientModifier.fxml"));
                                    Scene scene = new Scene(root);
                                    Stage stage = new Stage();
                                    stage.setTitle("Auftraggeber editieren");
                                    stage.setScene(scene);
                                    stage.initModality(Modality.WINDOW_MODAL);
                                    stage.initOwner(((Node) event.getSource()).getScene().getWindow());
                                    stage.show();
                                } catch (IOException ex) {
                                    Logging.getLogger().log(Level.SEVERE, "Couldn't open ClientModifier.fxml.", ex);
                                }

                                ClientModifierController.getInstance().loadClientIntoModifier(tv_Clients.getSelectionModel().getSelectedItem());
                            });
                            delete.setOnMouseClicked((MouseEvent event) -> {
                                try {
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Möchten Sie diesen Auftraggeber wirklich endgültig löschen. Vorsicht, dieser Vorgang kann nicht mehr rückgängig gemacht werden.",
                                            ButtonType.YES, ButtonType.CANCEL);
                                    alert.showAndWait();
                                    if (alert.getResult() == ButtonType.YES) {
                                        new ClientController().delete(tv_Clients.getSelectionModel().getSelectedItem().getId());
                                        refreshTable();
                                    }
                                } catch (Exception ex) {
                                    Logging.getLogger().log(Level.SEVERE, "Couldn't delete client.", ex);
                                }
                            });
                            setGraphic(box);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        }));

        tv_Clients.setItems(FXCollections.observableArrayList(new ClientController().findAll()));

        tv_Clients.setRowFactory((TableView<Client> param) -> {
            TableRow<Client> tableRow = new TableRow<>();
            tableRow.setOnMouseClicked((MouseEvent event) -> {
                if (event.getClickCount() == 2 && tableRow.getItem() != null) {
                    Parent root;
                    try {
                        root = FXMLLoader.load(getClass().getResource("/fxml/ClientModifier.fxml"));
                        Scene scene = new Scene(root);
                        Stage stage = new Stage();
                        stage.setTitle("Auftraggeber editieren");
                        stage.setScene(scene);
                        stage.initModality(Modality.WINDOW_MODAL);
                        stage.initOwner(((Node) event.getSource()).getScene().getWindow());
                        stage.show();
                    } catch (IOException ex) {
                        Logging.getLogger().log(Level.SEVERE, "Couldn't open ClientModifier.fxml.", ex);
                    }
                    ClientModifierController.getInstance().loadClientIntoModifier(tableRow.getItem());
                }
            });

            return tableRow;
        });
    }

    public static ClientsController getInstance() {
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
