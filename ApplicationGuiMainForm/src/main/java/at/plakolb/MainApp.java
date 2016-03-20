package at.plakolb;

import db.controller.ClientController;
import db.controller.ProductController;
import db.controller.ProjectController;
import db.controller.UnitController;
import entity.Client;
import entity.Product;
import entity.Project;
import entity.Unit;
import eunmeration.ProductType;
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
        insertTestData();

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainForm.fxml"));

        Scene scene = new Scene(root);

        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            scene.getStylesheets().add("/styles/Style_Windows.css");
        } else {
            scene.getStylesheets().add("/styles/Style_MacOS.css");
        }

        scene.getStylesheets().add("/styles/main.css");
        
        stage.setTitle("OnTopCalc");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void insertTestData() {
        db.controller.ClientController clientController = new ClientController();
        Client client1 = new Client("Mustermann", "Feldstraße", "Linz", "4020", "123456789", "max.m@mail.com");
        Client client2 = new Client("Musterfrau", "Landstraße", "Linz", "4020", "987654321", "erika.m@mail.com");
        clientController.create(client1);
        clientController.create(client2);

        ProjectController projectController = new ProjectController();
        projectController.create(new Project("Testprojekt1", "8467389", "Notiz", "Haus", "Pultdach", client1));
        projectController.create(new Project("Testprojekt2", "9827245", "Notiz", "Haus", "Pultdach", client2));

        UnitController unitController = new UnitController();
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

        ProductController productController = new ProductController();
        productController.create(new Product("Leimholz", 24.0, 16.0, 4.0, 25.0, unitController.findUnitByShortTerm("m³"), ProductType.WOOD));
        productController.create(new Product("Leimholz", 24.0, 16.0, 4.0, 25.0, unitController.findUnitByShortTerm("m³"), ProductType.WOOD));
        productController.create(new Product("Vollholz", 8.0, 10.0, 12.0, 15.0, unitController.findUnitByShortTerm("m³"), ProductType.WOOD));
        productController.create(new Product("Vollholz", 8.0, 12.0, 14.0, 16.0, unitController.findUnitByShortTerm("m³"), ProductType.WOOD));
        productController.create(new Product("Vollholz", 8.0, 16.0, 3.0, 13.0, unitController.findUnitByShortTerm("m³"), ProductType.WOOD));
        productController.create(new Product("Vollholz", 3.0, 8.0, 5.0, 20.0, unitController.findUnitByShortTerm("m³"), ProductType.WOOD));
        productController.create(new Product("Vollholz", 5.0, 8.0, 5.0, 20.0, unitController.findUnitByShortTerm("m³"), ProductType.WOOD));
        productController.create(new Product("Nägel 100", null, null, null, 20.0, unitController.findUnitByShortTerm("Stk"), ProductType.MISCELLANEOUS));
        productController.create(new Product("Dachfolie", 150.0, null, null, 25.0, unitController.findUnitByShortTerm("m³"), ProductType.FOIL));
        productController.create(new Product("Maschinenstunde", null, null, null, 10.0, unitController.findUnitByShortTerm("h"), ProductType.MISCELLANEOUS));
        productController.create(new Product("Pfette", 16.0, 24.0, 29.0, 15.0, unitController.findUnitByShortTerm("m³"), ProductType.WOOD));
        productController.create(new Product("Pfette", 16.0, 24.0, 14.5, 15.0, unitController.findUnitByShortTerm("m³"), ProductType.WOOD));
        productController.create(new Product("Mauerbank", 16.0, 16.0, 14.5, 15.0, unitController.findUnitByShortTerm("m³"), ProductType.WOOD));
        productController.create(new Product("Säulen", 16.0, 16.0, 3.0, 15.0, unitController.findUnitByShortTerm("m³"), ProductType.WOOD));
        productController.create(new Product("Sparren", 10.0, 16.0, 7.5, 15.0, unitController.findUnitByShortTerm("m³"), ProductType.WOOD));
        productController.create(new Product("Schalung", 0.4, null, null, 9.0, unitController.findUnitByShortTerm("m²"), ProductType.FORMWORK));
        productController.create(new Product("Schalung", 0.2, null, null, 5.0, unitController.findUnitByShortTerm("m²"), ProductType.FORMWORK));
        productController.create(new Product("Nageldichtband", null, null, 20.0, 14.0, unitController.findUnitByShortTerm("m"), ProductType.SEALINGBAND));
        productController.create(new Product("Nageldichtband", null, null, 10.0, 12.0, unitController.findUnitByShortTerm("m"), ProductType.SEALINGBAND));
        productController.create(new Product("Lattung", null, null, 10.0, 17.0, unitController.findUnitByShortTerm("m"), ProductType.BATTEN));
        productController.create(new Product("Konterlattung", null, null, 10.0, 17.0, unitController.findUnitByShortTerm("m"), ProductType.COUNTERBATTEN));

        productController.create(new Product("Rot", 100.0, 25.0, unitController.findUnitByShortTerm("l"), ProductType.COLOR));
        productController.create(new Product("Blau", 215.0, 35.0, unitController.findUnitByShortTerm("l"), ProductType.COLOR));
        productController.create(new Product("Grün", 325.0, 23.0, unitController.findUnitByShortTerm("l"), ProductType.COLOR));
        productController.create(new Product("Braun", 43.0, 28.0, unitController.findUnitByShortTerm("l"), ProductType.COLOR));

    }
}
