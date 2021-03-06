/*	HTL Leonding	*/
package at.plakolb.main;

import at.plakolb.calculationlogic.util.Logging;
import at.plakolb.settings.SettingsController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.logging.Level;

public class MainApp extends Application {

    private static Stage rootStage;

    public static Stage getStage() {
        return rootStage;
    }

    /**
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        try {
            rootStage = stage;

            Parent root;

            if (SettingsController.getBooleanProperty("firstrun")) {
                root = FXMLLoader.load(getClass().getResource("/fxml/FirstRun.fxml"));
            } else {
                root = FXMLLoader.load(getClass().getResource("/fxml/MainForm.fxml"));
            }

            Scene scene = new Scene(root);

            scene.getStylesheets().add("/styles/main.css");

            stage.setTitle("OnTopCalc");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
            stage.setScene(scene);
            if (SettingsController.getBooleanProperty("firstrun")) {
                stage.setResizable(false);
                stage.setTitle("OnTopCalc - Erste Schritte");
            }
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            Logging.getLogger().log(Level.SEVERE, null, e);
            throw e;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            Logging.getLogger().log(Level.SEVERE, null, e);
        }
    }
}
