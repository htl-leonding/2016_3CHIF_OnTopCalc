package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ParameterController;
import at.plakolb.calculationlogic.entity.ParameterP;
import at.plakolb.calculationlogic.entity.Unit;
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
 * @author Andreas
 */
public class ParameterViewController implements Initializable {

    private static ParameterViewController instance;

    @FXML
    private TableView<ParameterP> tv_Prameter;
    @FXML
    private TableColumn<ParameterP, String> tc_LongTerm;
    @FXML
    private TableColumn<ParameterP, Double> tc_DefaultValue;
    @FXML
    private TableColumn<ParameterP, Unit> tc_Unit;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        tc_LongTerm.setCellValueFactory(new PropertyValueFactory<>("longTerm"));
        tc_DefaultValue.setCellValueFactory(new PropertyValueFactory<>("defaultValue"));
        tc_Unit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        tv_Prameter.setItems(FXCollections.observableArrayList(new ParameterController().findAll()));

        tv_Prameter.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tv_Prameter.getSelectionModel().getSelectedItem() != null && tv_Prameter.getSelectionModel().getSelectedItem().isEditable()) {
                Parent root;
                try {
                    root = FXMLLoader.load(getClass().getResource("/fxml/ParameterModifier.fxml"));
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setTitle("Parameter");
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException ex) {
                }

                ParameterModifierController.getInstance().loadParameterIntoModifier(tv_Prameter.getSelectionModel().getSelectedItem());
            }
        });
    }

    public static ParameterViewController getInstance() {
        return instance;
    }

    /**
     * Refreshes the TableView.
     */
    public void refreshTable() {
        tv_Prameter.getColumns().get(0).setVisible(false);
        tv_Prameter.getColumns().get(0).setVisible(true);
    }
}