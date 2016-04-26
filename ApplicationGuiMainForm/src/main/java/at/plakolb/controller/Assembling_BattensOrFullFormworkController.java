package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.CategoryController;
import at.plakolb.calculationlogic.db.controller.ComponentController;
import at.plakolb.calculationlogic.db.controller.ParameterController;
import at.plakolb.calculationlogic.db.controller.ProductController;
import at.plakolb.calculationlogic.db.controller.WorthController;
import at.plakolb.calculationlogic.entity.Category;
import at.plakolb.calculationlogic.entity.Component;
import at.plakolb.calculationlogic.entity.Product;
import at.plakolb.calculationlogic.entity.Project;
import at.plakolb.calculationlogic.entity.Worth;
import at.plakolb.calculationlogic.eunmeration.ProductType;
import at.plakolb.calculationlogic.util.UtilityFormat;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class Assembling_BattensOrFullFormworkController implements Initializable, Observer {

    private static Assembling_BattensOrFullFormworkController instance;

    @FXML
    private ChoiceBox<String> cb_roofType;
    @FXML
    private Pane contentPane;
    @FXML
    private Label title;

    @FXML
    private AnchorPane ap_general;
    @FXML
    private ChoiceBox<Product> cb_product;
    @FXML
    private TextField tf_assemblingDuration;
    @FXML
    private TextField tf_price;
    @FXML
    private TextField tf_workCosts;
    @FXML
    private Label lb_productCosts;
    @FXML
    private Label lb_assemblingCosts;
    @FXML
    private Label lb_totalCosts;

    int loadedIndex = -1;

    Node tiledRoof;
    Node sheetRoof;

    double price;
    Worth workCosts;
    Worth assemblingDuration;
    Worth productCosts;
    Worth montageCosts;
    Worth totalCosts;

    Worth wastePercent;
    Component component;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        cb_roofType.getItems().addAll("Ziegeldach", "Blechdach");

        //Muss hier geladen werden! | Schlömi
        if (ProjectViewController.isProjectOpened()) {
            wastePercent = (new WorthController().findWorthByShortTermAndProjectId("VLVP", ProjectViewController.getOpenedProject().getId()) != null)
                    ? new WorthController().findWorthByShortTermAndProjectId("VLVP", ProjectViewController.getOpenedProject().getId()) : new Worth(new ParameterController().findParameterPByShortTerm("VLVP"));
        } else {
            wastePercent = new Worth(new ParameterController().findParameterPByShortTerm("VLVP"));
        }

        try {
            tiledRoof = (Node) FXMLLoader.load(getClass().getResource("/fxml/Assembling_TiledRoof.fxml"));
            sheetRoof = (Node) FXMLLoader.load(getClass().getResource("/fxml/Assembling_SheetRoof.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(Assembling_BattensOrFullFormworkController.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Add change listener to change the fxml file of the content pane if the selection has changed.
        cb_roofType.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            ap_general.setVisible(true);
            ProductController productController = new ProductController();
            if (newValue.intValue() == 0) {
                Assembling_SheetRoofController.getInstance().deleteObserver(this);
                Assembling_TiledRoofController.getInstance().addObserver(this);
                contentPane.getChildren().clear();
                contentPane.getChildren().add(tiledRoof);
                title.setText("Lattung");
                ProjectViewController.getOpenedProject().setRoofMaterial("Ziegeldach");
                cb_product.setItems(FXCollections.observableArrayList(productController.findByProductTypeOrderByName(ProductType.BATTEN)));
            } else if (newValue.intValue() == 1) {
                Assembling_TiledRoofController.getInstance().deleteObserver(this);
                Assembling_SheetRoofController.getInstance().addObserver(this);
                contentPane.getChildren().clear();
                contentPane.getChildren().add(sheetRoof);
                title.setText("Vollschalung");
                ProjectViewController.getOpenedProject().setRoofMaterial("Blechdach");
                cb_product.setItems(FXCollections.observableArrayList(productController.findByProductTypeOrderByName(ProductType.FORMWORK)));
            }
            tf_price.setText("");

            price = 0;
            String categoryString = newValue.intValue() == 0 ? "L" : "VS";
            Category category = new CategoryController().findCategoryByShortTerm(categoryString);

            component = new ComponentController().findComponentByProjectIdAndComponentTypeAndCategoryId(ProjectViewController.getOpenedProject().getId(), "Produkt", category.getId());
            if (component != null) {
                cb_product.getSelectionModel().select(component.getProduct());
                tf_price.setText(UtilityFormat.getStringForTextField(component.getPriceComponent()));
            } else {
                component = new Component();
                component.setComponentType("Produkt");
                component.setProject(ProjectViewController.getOpenedProject());
                component.setCategory(new CategoryController().findCategoryByShortTerm(
                        newValue.intValue() == 0 ? "L" : "VS"));
            }

            ModifyController.getInstance().setAssembling_battensOrFullFormwork(Boolean.TRUE);
        });

        tf_assemblingDuration.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            setAbatementDuration();
            calculate();
        });
        tf_price.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            setPrice();
            calculate();
        });
        tf_workCosts.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            setWorkCosts();
            calculate();
        });
        cb_product.getSelectionModel().selectedItemProperty().addListener((source, oldValue, newValue) -> {
            if (newValue != null) {
                tf_price.setText(UtilityFormat.getStringForTextField(newValue.getPriceUnit()));
            }
        });

        if (ProjectViewController.getOpenedProject() != null) {
            load();
        } else {
            component = new Component();
        }

    }

    public static Assembling_BattensOrFullFormworkController getInstance() {
        return instance;
    }

    private void setAbatementDuration() {
        assemblingDuration.setWorth(tf_assemblingDuration.getText().isEmpty() || !tf_assemblingDuration.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_assemblingDuration.getText().replace(',', '.')));
    }

    private void setPrice() {
        price = (tf_price.getText().isEmpty() || !tf_price.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_price.getText().replace(',', '.')));
    }

    private void setWorkCosts() {
        workCosts.setWorth(tf_workCosts.getText().isEmpty() || !tf_workCosts.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_workCosts.getText().replace(',', '.')));
    }

    @Override
    public void update(Observable o, Object arg) {
        calculate();
    }

    public double getWastePercent() {
        return wastePercent.getWorth();
    }

    public void setWastePercent(double worth) {
        wastePercent.setWorth(worth);
    }

    public Component getComponent() {
        return component;
    }

    public Worth getMaterial() {
        return productCosts;
    }

    public Worth getWage() {
        return montageCosts;
    }

    public Worth getTotalCosts() {
        return totalCosts;
    }

    public void load() {
        WorthController worthController = new WorthController();
        ParameterController parameterController = new ParameterController();
        Project project = ProjectViewController.getOpenedProject();

        if (ProjectViewController.getOpenedProject() != null) {
            cb_roofType.getSelectionModel().select(ProjectViewController.getOpenedProject().getRoofMaterial());
            if ("Ziegeldach".equals(ProjectViewController.getOpenedProject().getRoofMaterial())) {
                loadedIndex = 0;
            } else if ("Blechdach".equals(ProjectViewController.getOpenedProject().getRoofMaterial())) {
                loadedIndex = 1;
            }

            String categoryString = loadedIndex == 0 ? "L" : "VS";
            Category category = new CategoryController().findCategoryByShortTerm(categoryString);

            component = new ComponentController().findComponentByProjectIdAndComponentTypeAndCategoryId(project.getId(), "Produkt", category.getId());

            if (component != null) {
                cb_product.getSelectionModel().select(component.getProduct());
                tf_price.setText(UtilityFormat.getStringForTextField(component.getPriceComponent()));
            } else {
                component = new Component();
                component.setComponentType("Produkt");
                component.setCategory(category);
            }

            workCosts = (worthController.findWorthByShortTermAndProjectId("KPLV", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("KPLV", project.getId()) : new Worth(parameterController.findParameterPByShortTerm("KPLV"));
            assemblingDuration = (worthController.findWorthByShortTermAndProjectId("ZPLV", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("ZPLV", project.getId()) : new Worth(parameterController.findParameterPByShortTerm("ZPLV"));
            montageCosts = (worthController.findWorthByShortTermAndProjectId("KMLatVoll", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("KMLatVoll", project.getId()) : new Worth(parameterController.findParameterPByShortTerm("KMLatVoll"));
            productCosts = (worthController.findWorthByShortTermAndProjectId("KPLatVoll", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("KPLatVoll", project.getId()) : new Worth(parameterController.findParameterPByShortTerm("KPLatVoll"));
            totalCosts = (worthController.findWorthByShortTermAndProjectId("GKLatVoll", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("GKLatVoll", project.getId()) : new Worth(parameterController.findParameterPByShortTerm("GKLatVoll"));

            //WastePercent ist im initialize da es in den RoofControllern schon benötigt wird! |Schlömi
            lb_assemblingCosts.setText(UtilityFormat.getStringForLabel(montageCosts));
            lb_productCosts.setText(UtilityFormat.getStringForLabel(productCosts));
            lb_totalCosts.setText(UtilityFormat.getStringForLabel(totalCosts));

            tf_assemblingDuration.setText(UtilityFormat.getStringForTextField(assemblingDuration));
            tf_workCosts.setText(UtilityFormat.getStringForTextField(workCosts));
        }
    }

    public void calculate() {
        int index = cb_roofType.getSelectionModel().getSelectedIndex();
        //Produktkosten
        //Alte Formel-ID: KProdLV
        productCosts.setWorth(price * (index == 0 ? Assembling_TiledRoofController.getInstance().getLength().getWorth()
                : Assembling_SheetRoofController.getInstance().getFormwork().getWorth()));
        lb_productCosts.setText(UtilityFormat.getStringForLabel(productCosts));

        //Montagekosten
        //Alte Formel-ID: KMLV
        montageCosts.setWorth(workCosts.getWorth() * assemblingDuration.getWorth());
        lb_assemblingCosts.setText(UtilityFormat.getStringForLabel(montageCosts));

        //Totalcosts
        //Alte Formel-ID: GKLV
        totalCosts.setWorth(productCosts.getWorth() + montageCosts.getWorth());
        lb_totalCosts.setText(UtilityFormat.getStringForLabel(totalCosts));

        Product product = cb_product.getSelectionModel().getSelectedItem();

        if (product != null) {
            component.setDescription(product.getName());
            component.setLengthComponent(product.getLengthProduct());
            component.setWidthComponent(product.getWidthProduct());
            component.setHeightComponent(product.getHeightProduct());
            component.setProduct(product);
            component.setUnit(product.getUnit());
        } else {
            component.setDescription(title.getText());
            component.setLengthComponent(null);
            component.setWidthComponent(null);
            component.setHeightComponent(null);
            component.setProduct(null);
            component.setUnit(null);
        }
        component.setPriceComponent(price);
        //Keine Ahnung was hinein gehört TODO
        //component.setNumberOfProducts();
    }

    public void persist() {
        if (loadedIndex >= 0 && loadedIndex != cb_roofType.getSelectionModel().selectedIndexProperty().get()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Zuletzt wurde \"" + cb_roofType.getItems().get(loadedIndex) + "\" als Dachmaterial gespeichert.\nWollen Sie nun das Dachmaterial auf \"" + cb_roofType.getSelectionModel().getSelectedItem() + "\" ändern?");
            alert.setOnCloseRequest((event) -> {
                if (alert.getResult() == ButtonType.NO) {
                    if (loadedIndex == 0) {
                        saveTiledRoof();
                    } else {
                        saveSheetRoof();
                    }
                } else if (cb_roofType.getSelectionModel().getSelectedIndex() == 0) {
                    saveTiledRoof();
                } else {
                    saveSheetRoof();
                }
                saveRemaining();
            });
            alert.getButtonTypes().clear();
            alert.getButtonTypes().add(ButtonType.YES);
            alert.getButtonTypes().add(ButtonType.NO);
            alert.showAndWait();
        } else {
            if (cb_roofType.getSelectionModel().getSelectedIndex() == 0) {
                saveTiledRoof();
            } else {
                saveSheetRoof();
            }
            saveRemaining();
        }
    }

    private void saveRemaining() {
        Project project = ProjectViewController.getOpenedProject();
        WorthController worthController = new WorthController();
        ComponentController componentController = new ComponentController();

        try {
            new ComponentController().edit(component);
        } catch (Exception ex) {
            Logger.getLogger(Assembling_BattensOrFullFormworkController.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (project != null && !ProjectViewController.isProjectOpened()) {
            assemblingDuration.setProject(project);
            workCosts.setProject(project);
            montageCosts.setProject(project);
            productCosts.setProject(project);
            totalCosts.setProject(project);
            wastePercent.setProject(project);
            component.setProject(project);

            worthController.create(assemblingDuration);
            worthController.create(workCosts);
            worthController.create(montageCosts);
            worthController.create(productCosts);
            worthController.create(totalCosts);
            worthController.create(wastePercent);
            componentController.create(component);
        } else {
            try {
                worthController.edit(assemblingDuration);
                worthController.edit(workCosts);
                worthController.edit(montageCosts);
                worthController.edit(productCosts);
                worthController.edit(totalCosts);
                worthController.edit(wastePercent);
                componentController.edit(component);
            } catch (Exception ex) {
                Logger.getLogger(Assembling_BattensOrFullFormworkController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void saveSheetRoof() {
        Assembling_SheetRoofController.getInstance().persist();
    }

    private void saveTiledRoof() {
        Assembling_TiledRoofController.getInstance().persist();
    }
}
