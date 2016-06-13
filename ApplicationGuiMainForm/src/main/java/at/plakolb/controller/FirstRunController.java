package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.*;
import at.plakolb.calculationlogic.db.entity.*;
import at.plakolb.calculationlogic.eunmeration.ProductType;
import at.plakolb.main.MainApp;
import at.plakolb.settings.SettingsController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Created by Andreas on 23.05.2016.
 */
public class FirstRunController {
    public ProgressIndicator pi_indicator;
    public Button bt_start;
    public VBox vb_mainContent;

    public void init(ActionEvent actionEvent) {
        Thread t = new Thread(new Runnable() {
            Label info = null;

            @Override
            public void run() {
                Platform.runLater(() -> {
                    bt_start.setText("Beenden");
                    bt_start.setOnAction((t -> {
                        SettingsController.setProperty("firstrun", "true");
                        Platform.exit();
                    }));
                    pi_indicator.setVisible(true);
                    info = new Label("Einstellungen werden zurückgesetzt");
                    info.setStyle("-fx-font-weight: bold");
                    vb_mainContent.getChildren().add(info);
                });

                SettingsController.resetProperties();
                SettingsController.setProperty("firstrun", "false");

                Platform.runLater(() -> {
                    info.setText("Datenbank wird initialisiert");
                });

                //TEST DATA (temporarily)
                insertTestData();

                Platform.runLater(() -> {
                    Parent root = null;
                    try {
                        root = FXMLLoader.load(getClass().getResource("/fxml/MainForm.fxml"));
                        Scene scene = new Scene(root);

                        scene.getStylesheets().add("/styles/main.css");

                        MainApp.getStage().setTitle("OnTopCalc");
                        MainApp.getStage().getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
                        MainApp.getStage().setScene(scene);
                        MainApp.getStage().setResizable(true);
                        MainApp.getStage().centerOnScreen();
                        MainApp.getStage().show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            }
        });
        t.setDaemon(true);
        t.start();

    }

    /**
     * Insert some test data for develpoing purpose.
     */
    private void insertTestData() {

        ClientController clientController = new ClientController();
        ProjectController projectController = new ProjectController();
        if (clientController.findAll().size() == 0 && projectController.findAll().size() == 0) {
            Client client1 = new Client("Mustermann", "Feldstraße", "Linz", "4020", "123456789", "max.m@mail.com");
            Client client2 = new Client("Musterfrau", "Landstraße", "Linz", "4020", "987654321", "erika.m@mail.com");
            clientController.create(client1);
            clientController.create(client2);

            projectController.create(new Project("Testprojekt2", "9827245", "Notiz", "Haus", "Pultdach", client2));
            projectController.create(new Project("Testprojekt1", "8467389", "Notiz", "Haus", "Pultdach", client1));
        }

        UnitController unitController = new UnitController();
        if (unitController.findAll().size() == 0) {
            unitController.create(new Unit("Meter", "m"));
            unitController.create(new Unit("Quadratmeter", "m²"));
            unitController.create(new Unit("Kubikmeter", "m³"));
            unitController.create(new Unit("Stück", "Stk"));
            unitController.create(new Unit("Kilogramm", "kg"));
            unitController.create(new Unit("Stunden", "h"));
            unitController.create(new Unit("Euro", "€"));
            unitController.create(new Unit("Grad", "°"));
            unitController.create(new Unit("Prozent", "%"));
            unitController.create(new Unit("Liter", "l"));
            unitController.create(new Unit("Kilometer", "km"));
            unitController.create(new Unit("Laufmeter", "lfm"));
            unitController.create(new Unit("Zentimeter", "cm"));
            unitController.create(new Unit("Tage", "d"));
            unitController.create(new Unit("Euro pro Stunde", "€/h"));
            unitController.create(new Unit("Euro pro Kubikmeter", "€/m³"));
        }


        ProductController productController = new ProductController();
        if (productController.findAll().size() == 0) {
            productController.create(new Product("Leimholz", 24.0, 16.0, 4.0, 25.0, unitController.findUnit(4L), ProductType.WOOD));
            productController.create(new Product("Leimholz", 24.0, 16.0, 4.0, 25.0, unitController.findUnit(4L), ProductType.WOOD));
            productController.create(new Product("Vollholz", 8.0, 10.0, 12.0, 15.0, unitController.findUnit(4L), ProductType.WOOD));
            productController.create(new Product("Vollholz", 8.0, 12.0, 14.0, 16.0, unitController.findUnit(4L), ProductType.WOOD));
            productController.create(new Product("Vollholz", 8.0, 16.0, 3.0, 13.0, unitController.findUnit(4L), ProductType.WOOD));
            productController.create(new Product("Vollholz", 3.0, 8.0, 5.0, 20.0, unitController.findUnit(4L), ProductType.WOOD));
            productController.create(new Product("Vollholz", 5.0, 8.0, 5.0, 20.0, unitController.findUnit(4L), ProductType.WOOD));
            productController.create(new Product("Nägel 100", null, null, null, 20.0, unitController.findUnit(5L), ProductType.MISCELLANEOUS));
            productController.create(new Product("Dachfolie", 150.0, null, null, 25.0, unitController.findUnit(4L), ProductType.FOIL));
            productController.create(new Product("Maschinenstunde", null, null, null, 10.0, unitController.findUnit(6L), ProductType.MISCELLANEOUS));
            productController.create(new Product("Pfette", 16.0, 24.0, 29.0, 15.0, unitController.findUnit(4L), ProductType.WOOD));
            productController.create(new Product("Pfette", 16.0, 24.0, 14.5, 15.0, unitController.findUnit(4L), ProductType.WOOD));
            productController.create(new Product("Mauerbank", 16.0, 16.0, 14.5, 15.0, unitController.findUnit(4L), ProductType.WOOD));
            productController.create(new Product("Säulen", 16.0, 16.0, 3.0, 15.0, unitController.findUnit(4L), ProductType.WOOD));
            productController.create(new Product("Sparren", 10.0, 16.0, 7.5, 15.0, unitController.findUnit(4L), ProductType.WOOD));
            productController.create(new Product("Schalung", 4.0, null, null, 9.0, unitController.findUnit(2L), ProductType.FORMWORK));
            productController.create(new Product("Schalung", 2.0, null, null, 5.0, unitController.findUnit(2L), ProductType.FORMWORK));
            productController.create(new Product("Nageldichtband", null, null, 20.0, 14.0, unitController.findUnit(1L), ProductType.SEALINGBAND));
            productController.create(new Product("Nageldichtband", null, null, 10.0, 12.0, unitController.findUnit(1L), ProductType.SEALINGBAND));
            productController.create(new Product("Lattung", null, null, 10.0, 17.0, unitController.findUnit(1L), ProductType.BATTEN));
            productController.create(new Product("Konterlattung", null, null, 10.0, 17.0, unitController.findUnit(1L), ProductType.COUNTERBATTEN));

            productController.create(new Product("Rot", 100.0, 25.0, unitController.findUnit(10L), ProductType.COLOR));
            productController.create(new Product("Blau", 215.0, 35.0, unitController.findUnit(10L), ProductType.COLOR));
            productController.create(new Product("Grün", 325.0, 23.0, unitController.findUnit(10L), ProductType.COLOR));
            productController.create(new Product("Braun", 43.0, 28.0, unitController.findUnit(10L), ProductType.COLOR));
        }

        ParameterController parameterController = new ParameterController();
        if (parameterController.findAll().size() == 0) {
            parameterController.create(new ParameterP("Länge", "l", unitController.findUnit(1L), false));
            parameterController.create(new ParameterP("Breite", "b", unitController.findUnit(1L), false));
            parameterController.create(new ParameterP("Grundfläche", "A", unitController.findUnit(2L), false));
            parameterController.create(new ParameterP("Neigung", "N", unitController.findUnit(8L), false));
            parameterController.create(new ParameterP("Dachfläche inkl. Dachvorsprung", "DF", unitController.findUnit(2L), false));
            parameterController.create(new ParameterP("Dachfläche ohne Dachvorsprung", "D", unitController.findUnit(2L), false));
            parameterController.create(new ParameterP("Dachvorsprung", "DV", unitController.findUnit(2L), false));
            parameterController.create(new ParameterP("Dachvorsprung vorne", "dv", unitController.findUnit(1L), true, 1.0));
            parameterController.create(new ParameterP("Dachvorsprung hinten", "dh", unitController.findUnit(1L), true, 1.0));
            parameterController.create(new ParameterP("Dachvorsprung rechts", "dr", unitController.findUnit(1L), true, 0.8));
            parameterController.create(new ParameterP("Dachvorsprung links", "dl", unitController.findUnit(1L), true, 0.8));

            parameterController.create(new ParameterP("Volumen", "V", unitController.findUnit(3L), false));
            parameterController.create(new ParameterP("Preis pro m³ Holz", "PM3H", unitController.findUnit(16L), true, 250.0));
            parameterController.create(new ParameterP("Kubikmeter Zuschnitt Dauer", "KZG", unitController.findUnit(6L), true, 0.5));
            parameterController.create(new ParameterP("Kubikmeter Preis/Stunde Zuschnitt", "KPSZ", unitController.findUnit(15L), true, 40.0));
            parameterController.create(new ParameterP("Kubikmeter Gesamtdauer", "KG", unitController.findUnit(6L), false));
            parameterController.create(new ParameterP("Kumbikmeter Zuschnitt Gesamtpreis", "KZPG", unitController.findUnit(7L), false));
            parameterController.create(new ParameterP("Gesamtpreis Volumen", "GP", unitController.findUnit(7L), false));
            parameterController.create(new ParameterP("Gesamtkosten Material+Zuschnitt Volumen", "GKV", unitController.findUnit(7L), false));

            parameterController.create(new ParameterP("Verschnitt Schalung Prozent", "VSP", unitController.findUnit(9L), true, 15.0));
            parameterController.create(new ParameterP("Verschnitt Schalung", "VS", unitController.findUnit(2L), false));
            parameterController.create(new ParameterP("Schalung", "S", unitController.findUnit(2L), false));
            parameterController.create(new ParameterP("Schalung Produkt Kosten", "SPROK", unitController.findUnit(7L), false));
            parameterController.create(new ParameterP("Kosten Parameter Schalung", "KPS", unitController.findUnit(15L), true, 20.0));
            parameterController.create(new ParameterP("Zeit Parameter Schalung", "ZPS", unitController.findUnit(6L), true, 0.5));
            parameterController.create(new ParameterP("Kosten Montage Schalung", "KMS", unitController.findUnit(7L), false));
            parameterController.create(new ParameterP("Gesamtkosten Schalung", "GKS", unitController.findUnit(7L), false));

            parameterController.create(new ParameterP("Verschnitt sichbare Schalung Prozent", "VSSP", unitController.findUnit(9L), true, 15.0));
            parameterController.create(new ParameterP("Verschnitt sichtbare Schalung", "VSS", unitController.findUnit(2L), false));
            parameterController.create(new ParameterP("sichtbare Schalung", "SS", unitController.findUnit(2L), false));
            parameterController.create(new ParameterP("sichtbare Schalung Produkt Kosten", "SSPROK", unitController.findUnit(7L), false));
            parameterController.create(new ParameterP("Kosten Parameter sichtbare Schalung", "KPSS", unitController.findUnit(15L), true, 25.0));
            parameterController.create(new ParameterP("Zeit Parameter sichtbare Schalung", "ZPSS", unitController.findUnit(6L), true, 0.5));
            parameterController.create(new ParameterP("Kosten Montage sichtbare Schalung", "KMSS", unitController.findUnit(7L), false));
            parameterController.create(new ParameterP("Gesamtkosten sichtbare Schalung", "GKSS", unitController.findUnit(7L), false));

            parameterController.create(new ParameterP("Folie Überlappung Prozent", "FUEP", unitController.findUnit(9L), true, 15.0));
            parameterController.create(new ParameterP("Folie Überlappung", "FUE", unitController.findUnit(2L), false));
            parameterController.create(new ParameterP("Folie", "F", unitController.findUnit(2L), false));
            parameterController.create(new ParameterP("Kosten Parameter Folie", "KPF", unitController.findUnit(15L), true, 40.0));
            parameterController.create(new ParameterP("Zeit Parameter Folie", "ZPF", unitController.findUnit(6L), true, 0.3));
            parameterController.create(new ParameterP("Kosten Produkt Folie", "KProF", unitController.findUnit(7L), false));
            parameterController.create(new ParameterP("Kosten Montage Folie", "KMF", unitController.findUnit(7L), false));
            parameterController.create(new ParameterP("Gesamtkosten Folie", "GKF", unitController.findUnit(7L), false));

            parameterController.create(new ParameterP("Länge der Dachsparren", "LD", unitController.findUnit(1L), false));
            parameterController.create(new ParameterP("Verschnitt Nageldichtband Prozent", "VDP", unitController.findUnit(9L), true, 16.0));
            parameterController.create(new ParameterP("Verschnitt Nageldichtband", "DP", unitController.findUnit(1L), false));
            parameterController.create(new ParameterP("Nageldichtband", "ND", unitController.findUnit(1L), false));
            parameterController.create(new ParameterP("Kosten Parameter Nageldichtband", "KPD", unitController.findUnit(15L), true, 5.0));
            parameterController.create(new ParameterP("Zeit Parameter Nageldichtband", "ZPD", unitController.findUnit(6L), true, 0.2));
            parameterController.create(new ParameterP("Kosten Produkt Nageldichtband", "KProD", unitController.findUnit(7L), false));
            parameterController.create(new ParameterP("Kosten Montage Nageldichtband", "KMonD", unitController.findUnit(7L), false));
            parameterController.create(new ParameterP("Gesamtkosten Nageldichtband", "GKND", unitController.findUnit(7L), false));

            parameterController.create(new ParameterP("Verschnitt Konterlattung Prozent", "VKLP", unitController.findUnit(9L), true, 17.0));
            parameterController.create(new ParameterP("Verschnitt Konterlattung", "VKL", unitController.findUnit(1L), false));
            parameterController.create(new ParameterP("Konterlattung", "KL", unitController.findUnit(1L), false));
            parameterController.create(new ParameterP("Kosten Parameter Konterlattung", "KPKL", unitController.findUnit(15L), true, 7.0));
            parameterController.create(new ParameterP("Zeit Parameter Konterlattung", "ZPKL", unitController.findUnit(6L), true, 0.1));
            parameterController.create(new ParameterP("Kosten Produkt Konterlattung", "KProKL", unitController.findUnit(7L), false));
            parameterController.create(new ParameterP("Kosten Montage Konterlattung", "KMonKL", unitController.findUnit(7L), false));
            parameterController.create(new ParameterP("Gesamtkosten Konterlattung", "GKKL", unitController.findUnit(7L), false));

            parameterController.create(new ParameterP("Lattenabstand", "LA", unitController.findUnit(13L), false));
            parameterController.create(new ParameterP("Länge der Dachlatten ohne Verschnitt", "LDOV", unitController.findUnit(1L), false));
            parameterController.create(new ParameterP("Verschnitt Lattung oder Vollschalung in Prozent", "VLVP", unitController.findUnit(9L), true, 16.0));
            parameterController.create(new ParameterP("Verschnitt Lattung", "VL", unitController.findUnit(1L), false));
            parameterController.create(new ParameterP("Länge Lattung", "LL", unitController.findUnit(1L), false));
            parameterController.create(new ParameterP("Verschnitt Vollschalung", "VVS", unitController.findUnit(2L), false));
            parameterController.create(new ParameterP("Vollschalung", "VollS", unitController.findUnit(2L), false));

            parameterController.create(new ParameterP("Kosten Parameter Lattung oder Vollschalung", "KPLV", unitController.findUnit(15L), true, 6.0));
            parameterController.create(new ParameterP("Zeit Parameter Lattung oder Vollschalung", "ZPLV", unitController.findUnit(6L), true, 0.03));
            parameterController.create(new ParameterP("Kosten Produkt Lattung oder Vollschalung", "KPLatVoll", unitController.findUnit(7L), false));
            parameterController.create(new ParameterP("Kosten Montage Lattung oder Vollschalung", "KMLatVoll", unitController.findUnit(7L), false));
            parameterController.create(new ParameterP("Gesamtkosten Lattung oder Vollschalung", "GKLatVoll", unitController.findUnit(7L), false));

            parameterController.create(new ParameterP("Gesamtpreis Material für Montage", "GMFM", unitController.findUnit(7L), false));

            parameterController.create(new ParameterP("Farbfaktor", "FK", unitController.findUnit(2L), false));
            parameterController.create(new ParameterP("Farbmenge in m²", "FMM", unitController.findUnit(2L), false));
            parameterController.create(new ParameterP("Farbmenge in l", "FML", unitController.findUnit(10L), false));
            parameterController.create(new ParameterP("Zeit Montage Farbe Paramter", "ZPFA", unitController.findUnit(6L), true, 1.5));
            parameterController.create(new ParameterP("Preis Montage Farbe Paramter", "PMFP", unitController.findUnit(7L), true, 40.0));
            parameterController.create(new ParameterP("Kosten Produkt Farbe", "KPFarbe", unitController.findUnit(7L), false));
            parameterController.create(new ParameterP("Kosten Montage Farbe", "KMFarbe", unitController.findUnit(7L), false));
            parameterController.create(new ParameterP("Gesamtkosten Farbe", "GKFarbe", unitController.findUnit(7L), false));

            parameterController.create(new ParameterP("Tage Aufenthalt", "TA", unitController.findUnit(14L), false));
            parameterController.create(new ParameterP("Kilometergeld", "KMG", unitController.findUnit(7L), true, 2.5));
            parameterController.create(new ParameterP("Preis LKW/Stunde", "PLS", unitController.findUnit(15L), true, 50.0));
            parameterController.create(new ParameterP("Entfernung Transport", "ET", unitController.findUnit(11L), false));
            parameterController.create(new ParameterP("Dauer Transport", "DT", unitController.findUnit(6L), false));
            parameterController.create(new ParameterP("Kosten Transport", "KT", unitController.findUnit(7L), false));
            parameterController.create(new ParameterP("Kosten Aufenthalt", "KA", unitController.findUnit(7L), false));
            parameterController.create(new ParameterP("Gesamtpreis Transport", "GPT", unitController.findUnit(7L), false));
        }

        CategoryController categoryController = new CategoryController();
        if (categoryController.findAll().size() == 0) {
            categoryController.create(new Category("Konstruktion", "K"));
            categoryController.create(new Category("Konstruktion Dach", "KD"));
            categoryController.create(new Category("Schalung", "S"));
            categoryController.create(new Category("sichtbare Schalung", "SS"));
            categoryController.create(new Category("Nageldichtband", "ND"));
            categoryController.create(new Category("Konterlattung", "KL"));
            categoryController.create(new Category("Lattung", "L"));
            categoryController.create(new Category("Vollschalung", "VS"));
            categoryController.create(new Category("Folie", "F"));
            categoryController.create(new Category("Diverses", "X"));
        }
    }
}
