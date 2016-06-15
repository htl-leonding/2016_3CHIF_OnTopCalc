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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.logging.*;

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

    public static void restart() {
        StringBuilder deleteDB = new StringBuilder();
        deleteDB.append("#!/bin/bash\n");
        deleteDB.append(String.format("while [ -d %s ] do\n","database"));
        deleteDB.append(String.format("rm -r %s || echo \"Cannot delete database directory.\"\n", "database"));
        deleteDB.append("end\n\n");

        StringBuilder cmd = new StringBuilder();
        cmd.append(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java ");
        for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            cmd.append(jvmArg + " ");
        }
        cmd.append("-jar ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");

        deleteDB.append(cmd.toString());
        try {
            FileWriter fileWriter = new FileWriter("restart.sh");
            fileWriter.write(deleteDB.toString());
            fileWriter.flush();
            fileWriter.close();
            new File("restart.sh").setExecutable(true);
        } catch (IOException e) {
            Logging.getLogger().log(Level.SEVERE, "Couldn`t write to restart file.", e);
        }
        try {
            Runtime.getRuntime().exec("restart.sh");
        } catch (IOException ex) {
            Logging.getLogger().log(Level.SEVERE, "Application couldn't be restarted.", ex);
        }
        System.exit(0);
    }
}
