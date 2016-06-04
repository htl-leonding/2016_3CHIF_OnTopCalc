package at.plakolb.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Andreas on 04.06.2016.
 */
public class StatisticController implements Initializable{

    @FXML
    public StackPane stackPane;
    @FXML
    public VBox settingsContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stackPane.getChildren().remove(settingsContainer);
    }

    public void closeSettings(ActionEvent actionEvent) {
        stackPane.getChildren().remove(settingsContainer);
    }

    public void openSettings(ActionEvent actionEvent) {
        stackPane.getChildren().add(settingsContainer);
    }
}
