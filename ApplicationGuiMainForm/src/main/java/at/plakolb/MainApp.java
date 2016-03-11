package at.plakolb;

import db.controller.ClientController;
import db.controller.ProjectController;
import entity.Client;
import entity.Project;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    /**
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {

        //TEST DATA
        db.controller.ClientController clientController = new ClientController();
        Client client1 = new Client("Musterman", "Feldstraße", "Linz", "4020", "123456789");
        Client client2 = new Client("Musterfrau", "Landstraße", "Linz", "4020", "987654321");
        clientController.create(client1);
        clientController.create(client2);

        ProjectController projectController = new ProjectController();
        projectController.create(new Project("Testprojekt1", "8467389", "Notiz", "Haus", "Pultdach", client1));
        projectController.create(new Project("Testprojekt2", "9827245", "Notiz", "Haus", "Pultdach", client2));

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainForm.fxml"));

        Scene scene = new Scene(root);

        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            scene.getStylesheets().add("/styles/Style_Windows.css");
        } else {
            scene.getStylesheets().add("/styles/Style_MacOS.css");
        }

        scene.getStylesheets().add("/styles/main.css");

        stage.setTitle("Kalkulations-Assistent");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
