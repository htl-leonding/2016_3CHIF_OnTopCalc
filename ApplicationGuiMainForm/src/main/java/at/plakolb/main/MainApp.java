/*	HTL Leonding	*/
package at.plakolb.main;

import at.plakolb.calculationlogic.db.controller.*;
import at.plakolb.calculationlogic.db.entity.*;
import at.plakolb.calculationlogic.eunmeration.ProductType;
import at.plakolb.settings.SettingsController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import sun.applet.Main;

import java.io.IOException;

public class MainApp extends Application {

    private static Stage rootStage;
    private static MainApp singelton;

    public static Stage getStage() {
        return rootStage;
    }

    /**
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        singelton = this;
        startApplication(stage);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public void startApplication(Stage stage) throws Exception {
        rootStage = stage;

        Parent root = null;

        if(SettingsController.getBooleanProperty("firstrun")){
            root = FXMLLoader.load(getClass().getResource("/fxml/FirstRun.fxml"));
        }
        else {
            root = FXMLLoader.load(getClass().getResource("/fxml/MainForm.fxml"));
        }

        Scene scene = new Scene(root);

        scene.getStylesheets().add("/styles/main.css");

        stage.setTitle("OnTopCalc");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
        stage.setScene(scene);
        if(SettingsController.getBooleanProperty("firstrun")){
            stage.setResizable(false);
            stage.setTitle("OnTopCalc - Erste Schritte");
        }
        stage.centerOnScreen();
        stage.show();
    }
    public static MainApp getInstance(){
        return singelton;
    }
}
