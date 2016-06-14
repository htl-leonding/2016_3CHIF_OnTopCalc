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
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
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
        rootStage = stage;

        Parent root;

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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public static void restart() {
        StringBuilder cmd = new StringBuilder();
        cmd.append(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java ");
        for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            cmd.append(jvmArg + " ");
        }
        cmd.append("-jar ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");
        try {
            Runtime.getRuntime().exec(cmd.toString());
        } catch (IOException ex) {
            Logging.getLogger().log(Level.SEVERE, "Application couldn't be restarted.", ex);
        }
        System.exit(0);
    }
}
