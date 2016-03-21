package controller;

import db.controller.ParameterController;
import entity.ParameterP;
import entity.Unit;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author Andreas
 */
public class ParameterViewController implements Initializable {

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
        tc_LongTerm.setCellValueFactory(new PropertyValueFactory<>("longTerm"));
        tc_DefaultValue.setCellValueFactory(new PropertyValueFactory<>("defaultValue"));
        tc_Unit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        tv_Prameter.setItems(FXCollections.observableArrayList(new ParameterController().findAll()));
    }
}