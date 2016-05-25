package at.plakolb.controller;

import at.plakolb.settings.SettingsController;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Andreas on 24.05.2016.
 */
public class AboutController implements Initializable {

    public Label lb_version;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lb_version.setText(lb_version.getText()+ SettingsController.getProperty("version"));
    }
}
