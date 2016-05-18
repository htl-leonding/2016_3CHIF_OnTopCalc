/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.util.Logging;
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
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class Assembling_CounterBattenController implements Observer, Initializable {

    private static Assembling_CounterBattenController instance;
    @FXML
    private TextField tf_waste;
    @FXML
    private ComboBox<Product> cb_counterBattern;
    @FXML
    private TextField tf_pricePerMeter;
    @FXML
    private Label lb_counterBattern;
    @FXML
    private TextField tf_timeMontage;
    @FXML
    private TextField tf_profiHour;
    @FXML
    private Label lb_productCost;
    @FXML
    private Label lb_montageCost;
    @FXML
    private Label lb_totalCost;
    @FXML
    private TableView<Component> tv_dachsparren;
    @FXML
    private TableColumn<Component, String> cl_name;
    @FXML
    private TableColumn<Component, String> cl_dachsparrenLength;
    @FXML
    private Label lb_lengthWaste;

    private Worth lengthWaste;
    private Worth waste;
    private double pricePerMeter;
    private Worth counterBattern;
    private Worth montageCost;
    private Worth productCost;
    private Worth timeMontage;
    private Worth totalCost;
    private Worth profiHour;

    private Component component;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        Project_ConstructionMaterialListController.getInstance().addObserver(this);
        ParameterController parameterController = new ParameterController();
        cb_counterBattern.setItems(FXCollections.observableArrayList(new ProductController().findByProductTypeOrderByName(ProductType.COUNTERBATTEN)));
        waste = new Worth(parameterController.findParameterPByShortTerm("VKLP"));
        lengthWaste = new Worth(parameterController.findParameterPByShortTerm("VKL"));
        productCost = new Worth(parameterController.findParameterPByShortTerm("KProKL"));
        counterBattern = new Worth(parameterController.findParameterPByShortTerm("KL"));
        timeMontage = new Worth(parameterController.findParameterPByShortTerm("ZPKL"));
        profiHour = new Worth(parameterController.findParameterPByShortTerm("KPKL"));
        montageCost = new Worth(parameterController.findParameterPByShortTerm("KMonKL"));
        totalCost = new Worth(parameterController.findParameterPByShortTerm("GKKL"));

        tf_profiHour.setText(UtilityFormat.getStringForTextField(profiHour));
        tf_waste.setText(UtilityFormat.getStringForTextField(waste));
        tf_pricePerMeter.setText(UtilityFormat.getStringForTextField(pricePerMeter));
        tf_timeMontage.setText(UtilityFormat.getStringForTextField(timeMontage));
        lb_montageCost.setText(UtilityFormat.getStringForLabel(montageCost));
        lb_counterBattern.setText(UtilityFormat.getStringForLabel(counterBattern));
        lb_lengthWaste.setText(UtilityFormat.getStringForLabel(lengthWaste));
        lb_productCost.setText(UtilityFormat.getStringForLabel(productCost));
        lb_totalCost.setText(UtilityFormat.getStringForLabel(totalCost));

        cb_counterBattern.getSelectionModel().selectedItemProperty().addListener((source, oldValue, newValue) -> {
            pricePerMeter = newValue.getPriceUnit();
            tf_pricePerMeter.setText(UtilityFormat.getStringForTextField(newValue.getPriceUnit()));
        });
        tf_pricePerMeter.textProperty().addListener((observable, oldValue, newValue) -> {
            setPrice();
            calculate();
            ModifyController.getInstance().setAssembling_counterBattens(Boolean.TRUE);
        });
        tf_profiHour.textProperty().addListener((observable, oldValue, newValue) -> {
            UtilityFormat.setWorthFromTextField(tf_profiHour, profiHour);
            calculate();
            ModifyController.getInstance().setAssembling_counterBattens(Boolean.TRUE);
        });
        tf_waste.textProperty().addListener((observable, oldValue, newValue) -> {
            UtilityFormat.setWorthFromTextField(tf_waste, waste);
            calculate();
            ModifyController.getInstance().setAssembling_counterBattens(Boolean.TRUE);
        });
        tf_timeMontage.textProperty().addListener((observable, oldValue, newValue) -> {
            UtilityFormat.setWorthFromTextField(tf_timeMontage, timeMontage);
            calculate();
            ModifyController.getInstance().setAssembling_counterBattens(Boolean.TRUE);
        });

        cl_name.setCellValueFactory((TableColumn.CellDataFeatures<Component, String> param) -> {
            return new ReadOnlyObjectWrapper<>(param.getValue().getDescription());
        });

        cl_dachsparrenLength.setCellValueFactory((TableColumn.CellDataFeatures<Component, String> param) -> {
            return new ReadOnlyObjectWrapper<>(String.valueOf(UtilityFormat.getStringForLabel(param.getValue().getLengthComponent())) + " m");
        });

        if (ProjectViewController.getOpenedProject() != null) {
            loadFromDb();
        } else {
            component = new Component();
            component.setCategory(new CategoryController().findCategoryByShortTerm("KL"));
            component.setComponentType("Produkt");
        }
    }

    public static Assembling_CounterBattenController getInstance() {
        return instance;
    }

    public void setPrice() {
        tf_pricePerMeter.setText(tf_pricePerMeter.getText().replaceAll(",", ".").replaceAll("[^\\d.]", ""));
        tf_pricePerMeter.setText(UtilityFormat.removeUnnecessaryCommas(tf_pricePerMeter.getText()));

        if (tf_pricePerMeter.getText().isEmpty()) {
            this.pricePerMeter = 0;
        } else if (tf_pricePerMeter.getText().isEmpty() || Double.valueOf(tf_pricePerMeter.getText()) < 0) {
            new Alert(Alert.AlertType.ERROR, "Der Preis muss eine positive Zahl sein!\nEingabe: \"" + pricePerMeter + "\"", ButtonType.OK).showAndWait();
        } else {
            this.pricePerMeter = Double.valueOf(tf_pricePerMeter.getText());
        }
    }

    public Component getComponent() {
        return component;
    }

    public Worth getMaterial() {
        return productCost;
    }

    public Worth getWage() {
        return montageCost;
    }

    public Worth getTotalCosts() {
        return totalCost;
    }

    public void loadFromDb() {
        WorthController worthController = new WorthController();
        Project openedProject = ProjectViewController.getOpenedProject();
        Category category = new CategoryController().findCategoryByShortTerm("KL");

        component = new ComponentController().findComponentByProjectIdAndComponentTypeAndCategoryId(openedProject.getId(),
                "Produkt", category.getId());

        if (component != null) {
            cb_counterBattern.getSelectionModel().select(component.getProduct());
        } else {
            component = new Component();
            component.setDescription("Konterlattung");
            component.setCategory(category);
            component.setComponentType("Produkt");
        }

        timeMontage = (worthController.findWorthByShortTermAndProjectId("ZPKL", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("ZPKL", openedProject.getId()) : timeMontage;
        profiHour = (worthController.findWorthByShortTermAndProjectId("KPKL", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("KPKL", openedProject.getId()) : profiHour;
        counterBattern = (worthController.findWorthByShortTermAndProjectId("KL", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("KL", openedProject.getId()) : counterBattern;
        lengthWaste = (worthController.findWorthByShortTermAndProjectId("VKL", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("VKL", openedProject.getId()) : lengthWaste;
        totalCost = (worthController.findWorthByShortTermAndProjectId("GKKL", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("GKKL", openedProject.getId()) : totalCost;
        waste = (worthController.findWorthByShortTermAndProjectId("VKLP", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("VKLP", openedProject.getId()) : waste;
        productCost = (worthController.findWorthByShortTermAndProjectId("KProKL", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("KProKL", openedProject.getId()) : productCost;
        montageCost = (worthController.findWorthByShortTermAndProjectId("KMonKL", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("KMonKL", openedProject.getId()) : montageCost;

        tf_profiHour.setText(UtilityFormat.getStringForTextField(profiHour));
        tf_waste.setText(UtilityFormat.getStringForTextField(waste));
        tf_pricePerMeter.setText(UtilityFormat.getStringForTextField(pricePerMeter));
        tf_timeMontage.setText(UtilityFormat.getStringForTextField(timeMontage));
        lb_montageCost.setText(UtilityFormat.getStringForLabel(montageCost));
        lb_counterBattern.setText(UtilityFormat.getStringForLabel(counterBattern));
        lb_lengthWaste.setText(UtilityFormat.getStringForLabel(lengthWaste));
        lb_productCost.setText(UtilityFormat.getStringForLabel(productCost));
        lb_totalCost.setText(UtilityFormat.getStringForLabel(totalCost));
        tv_dachsparren.setItems(FXCollections.observableList(Project_ConstructionMaterialListController.getInstance().getRafterList()));

        ModifyController.getInstance().setAssembling_counterBattens(Boolean.FALSE);
    }

    public void calculate() {
        try {
            double sum = Project_ConstructionMaterialListController.getInstance().getTotalRafterLength();
            //Verschnitt in m
            lengthWaste.setWorth(sum * (waste.getWorth() / 100));
            lb_lengthWaste.setText(UtilityFormat.getStringForLabel(lengthWaste));

            //Konterlattung in m
            counterBattern.setWorth(sum + lengthWaste.getWorth());
            lb_counterBattern.setText(UtilityFormat.getStringForLabel(counterBattern));

            //Kosten Konterlattung
            productCost.setWorth(pricePerMeter * counterBattern.getWorth());
            lb_productCost.setText(UtilityFormat.getStringForLabel(productCost));

            //Kosten Montage
            montageCost.setWorth(profiHour.getWorth() * timeMontage.getWorth());
            lb_montageCost.setText(UtilityFormat.getStringForLabel(montageCost));

            //Kosten Gesamt
            totalCost.setWorth(montageCost.getWorth() + productCost.getWorth());
            lb_totalCost.setText(UtilityFormat.getStringForLabel(totalCost));
        } catch (Exception ex) {
            if (ProjectViewController.isProjectOpened()) {
                new Alert(Alert.AlertType.ERROR, "Werte können nicht berechnet werden!\nFehlerinformation: " + ex.getLocalizedMessage(), ButtonType.OK).showAndWait();
                Logging.getLogger().log(Level.SEVERE, "Assembling_CounterBattenController: calculate method didn't work.", ex);
            }
        }

        Product product = cb_counterBattern.getSelectionModel().getSelectedItem();

        if (product != null) {
            component.setDescription(product.getName());
            component.setLengthComponent(product.getLengthProduct());
            component.setWidthComponent(product.getWidthProduct());
            component.setHeightComponent(product.getHeightProduct());
            component.setProduct(product);
            component.setUnit(product.getUnit());
        } else {
            component.setDescription("Konterlattung");
            component.setLengthComponent(null);
            component.setWidthComponent(null);
            component.setHeightComponent(null);
            component.setProduct(null);
            component.setUnit(null);
        }
        component.setNumberOfProducts(counterBattern.getWorth());
        component.setPriceComponent(pricePerMeter);

    }

    public void persist() {
        WorthController worthController = new WorthController();
        ComponentController componentController = new ComponentController();
        try {
            if (!ProjectViewController.isProjectOpened()) {
                component.setProject(ProjectViewController.getOpenedProject());
                profiHour.setProject(ProjectViewController.getOpenedProject());
                counterBattern.setProject(ProjectViewController.getOpenedProject());
                timeMontage.setProject(ProjectViewController.getOpenedProject());
                waste.setProject(ProjectViewController.getOpenedProject());
                productCost.setProject(ProjectViewController.getOpenedProject());
                montageCost.setProject(ProjectViewController.getOpenedProject());
                lengthWaste.setProject(ProjectViewController.getOpenedProject());
                totalCost.setProject(ProjectViewController.getOpenedProject());
                component.setProject(ProjectViewController.getOpenedProject());

                worthController.create(profiHour);
                worthController.create(counterBattern);
                worthController.create(timeMontage);
                worthController.create(lengthWaste);
                worthController.create(productCost);
                worthController.create(montageCost);
                worthController.create(waste);
                worthController.create(totalCost);
                componentController.create(component);
            } else {
                worthController.edit(profiHour);
                worthController.edit(counterBattern);
                worthController.edit(timeMontage);
                worthController.edit(lengthWaste);
                worthController.edit(productCost);
                worthController.edit(montageCost);
                worthController.edit(waste);
                worthController.edit(totalCost);
                componentController.edit(component);

            }
        } catch (Exception ex) {
            Logging.getLogger().log(Level.SEVERE, "Assembling_CounterBattenController: persist method didn't work.", ex);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        tv_dachsparren.setItems(FXCollections.observableList(Project_ConstructionMaterialListController.getInstance().getRafterList()));
        calculate();
    }

}
