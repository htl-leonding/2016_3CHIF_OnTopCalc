/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.util.Logging;
import at.plakolb.calculationlogic.db.controller.CategoryController;
import at.plakolb.calculationlogic.db.controller.ComponentController;
import at.plakolb.calculationlogic.db.controller.ParameterController;
import at.plakolb.calculationlogic.db.controller.ProductController;
import at.plakolb.calculationlogic.db.controller.WorthController;
import at.plakolb.calculationlogic.db.entity.Category;
import at.plakolb.calculationlogic.db.entity.Component;
import at.plakolb.calculationlogic.eunmeration.ProductType;
import at.plakolb.calculationlogic.db.entity.Product;
import at.plakolb.calculationlogic.db.entity.Project;
import at.plakolb.calculationlogic.db.entity.Worth;
import at.plakolb.calculationlogic.util.UtilityFormat;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;

/**
 * Over this view it's possible to calculate the visible formwork.
 *
 * @author Kepplinger
 */
public class Assembling_VisibleFormworkController implements Initializable, Observer {

    public static Assembling_VisibleFormworkController instance;

    @FXML
    private ComboBox<Product> cb_Product;
    @FXML
    private TextField tf_PricePerSquare;
    @FXML
    private TextField tf_AbatementPercent;
    @FXML
    private Label lb_AbatementArea;
    @FXML
    private Label lb_VisibleFormwork;
    @FXML
    private TextField tf_WorkerCosts;
    @FXML
    private TextField tf_AssemblingDuration;
    @FXML
    private Label lb_ProductCosts;
    @FXML
    private Label lb_AssemblingCosts;
    @FXML
    private Label lb_TotalCosts;
    @FXML
    private Label lb_RoofArea;

    private double pricePerSquare;
    private Worth abatementPercent;
    private Worth workerCosts;
    private Worth assemblingDuration;

    private Worth abatementArea;
    private Worth visibleFormwork;
    private Worth productCosts;
    private Worth assemblingCosts;
    private Worth totalCosts;

    private Component component;

    /**
     * Initializes the controller class and all worth objects. Also adds many
     * change listeners to verify the user input.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        instance = this;
        cb_Product.setItems(FXCollections.observableArrayList(new ProductController().findByProductTypeOrderByName(ProductType.FORMWORK)));

        ParameterController parameterController = new ParameterController();
        abatementPercent = new Worth(parameterController.findParameterPByShortTerm("VSSP"));
        abatementArea = new Worth(parameterController.findParameterPByShortTerm("VSS"));
        visibleFormwork = new Worth(parameterController.findParameterPByShortTerm("SS"));
        productCosts = new Worth(parameterController.findParameterPByShortTerm("SSPROK"));
        assemblingDuration = new Worth(parameterController.findParameterPByShortTerm("ZPSS"));
        assemblingCosts = new Worth(parameterController.findParameterPByShortTerm("KMSS"));
        totalCosts = new Worth(parameterController.findParameterPByShortTerm("GKSS"));
        workerCosts = new Worth(parameterController.findParameterPByShortTerm("KPSS"));

        tf_AbatementPercent.setText(UtilityFormat.getStringForTextField(abatementPercent));
        tf_AssemblingDuration.setText(UtilityFormat.getStringForTextField(assemblingDuration));
        tf_PricePerSquare.setText(UtilityFormat.getStringForTextField(pricePerSquare));
        tf_WorkerCosts.setText(UtilityFormat.getStringForTextField(workerCosts));
        lb_AbatementArea.setText(UtilityFormat.getStringForLabel(abatementArea));
        lb_AssemblingCosts.setText(UtilityFormat.getStringForLabel(assemblingCosts));
        lb_ProductCosts.setText(UtilityFormat.getStringForLabel(productCosts));
        lb_TotalCosts.setText(UtilityFormat.getStringForLabel(totalCosts));
        lb_VisibleFormwork.setText(UtilityFormat.getStringForLabel(visibleFormwork));

        tf_PricePerSquare.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            setPricePerSquare();
            calculate();
            ModifyController.getInstance().setAssembling_visibleFormwork(Boolean.TRUE);
        });

        tf_AbatementPercent.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            UtilityFormat.setWorthFromTextField(tf_AbatementPercent, abatementPercent);
            calculate();
            ModifyController.getInstance().setAssembling_visibleFormwork(Boolean.TRUE);
        });

        tf_AssemblingDuration.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            UtilityFormat.setWorthFromTextField(tf_AssemblingDuration, assemblingDuration);
            calculate();
            ModifyController.getInstance().setAssembling_visibleFormwork(Boolean.TRUE);
        });

        tf_WorkerCosts.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            UtilityFormat.setWorthFromTextField(tf_WorkerCosts, workerCosts);
            calculate();
            ModifyController.getInstance().setAssembling_visibleFormwork(Boolean.TRUE);
        });

        cb_Product.getSelectionModel().selectedItemProperty().addListener((source, oldValue, newValue) -> {
            tf_PricePerSquare.setText(UtilityFormat.getStringForTextField(newValue.getPriceUnit()));
        });

        if (ProjectViewController.getOpenedProject() != null) {
            load();
        } else {
            component = new Component();
            component.setDescription("SichtbareSchalung");
            component.setCategory(new CategoryController().findCategoryByShortTerm("SS"));
            component.setComponentType("Produkt");
        }
    }

    public static Assembling_VisibleFormworkController getInstance() {
        return instance;
    }

    public double getVisibleFormwork() {
        return visibleFormwork.getWorth();
    }

    public Component getComponent() {
        return component;
    }

    public void setPricePerSquare() {
        tf_PricePerSquare.setText(tf_PricePerSquare.getText().replaceAll(",", ".").replaceAll("[^\\d.]", ""));
        tf_PricePerSquare.setText(UtilityFormat.removeUnnecessaryCommas(tf_PricePerSquare.getText()));

        if (tf_PricePerSquare.getText().isEmpty()) {
            this.pricePerSquare = 0;
        } else if (tf_PricePerSquare.getText().isEmpty() || Double.valueOf(tf_PricePerSquare.getText()) < 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Der Preis muss eine positive Zahl sein!\nEingabe: \"" + pricePerSquare + "\"", ButtonType.OK);
            alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
            alert.showAndWait();
        } else {
            this.pricePerSquare = Double.valueOf(tf_PricePerSquare.getText());
        }
    }

    public Worth getWage() {
        return assemblingCosts;
    }

    public Worth getMaterial() {
        return productCosts;
    }

    public Worth getTotalCosts() {
        return totalCosts;
    }

    /**
     * Loads all required values from the database into the view.
     */
    private void load() {
        ParameterController parameterController = new ParameterController();
        WorthController worthController = new WorthController();
        Project openedProject = ProjectViewController.getOpenedProject();
        Category category = new CategoryController().findCategoryByShortTerm("SS");

        component = new ComponentController().findComponentByProjectIdAndComponentTypeAndCategoryId(openedProject.getId(),
                "Produkt", category.getId());

        if (component != null) {
            cb_Product.getSelectionModel().select(component.getProduct());
        } else {
            component = new Component();
            component.setDescription("SichtbareSchalung");
            component.setCategory(category);
            component.setComponentType("Produkt");
        }

        abatementPercent = (worthController.findWorthByShortTermAndProjectId("VSSP", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("VSSP", openedProject.getId()) : abatementPercent;
        abatementArea = (worthController.findWorthByShortTermAndProjectId("VSS", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("VSS", openedProject.getId()) : abatementArea;
        visibleFormwork = (worthController.findWorthByShortTermAndProjectId("SS", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("SS", openedProject.getId()) : visibleFormwork;
        productCosts = (worthController.findWorthByShortTermAndProjectId("SSPROK", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("SSPROK", openedProject.getId()) : productCosts;
        workerCosts = (worthController.findWorthByShortTermAndProjectId("KPSS", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("KPSS", openedProject.getId()) : workerCosts;
        assemblingDuration = (worthController.findWorthByShortTermAndProjectId("ZPSS", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("ZPSS", openedProject.getId()) : assemblingDuration;
        assemblingCosts = (worthController.findWorthByShortTermAndProjectId("KMSS", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("KMSS", openedProject.getId()) : assemblingCosts;
        totalCosts = (worthController.findWorthByShortTermAndProjectId("GKSS", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("GKSS", openedProject.getId()) : totalCosts;

        tf_AbatementPercent.setText(UtilityFormat.getStringForTextField(abatementPercent));
        tf_AssemblingDuration.setText(UtilityFormat.getStringForTextField(assemblingDuration));
        tf_PricePerSquare.setText(UtilityFormat.getStringForTextField(pricePerSquare));
        tf_WorkerCosts.setText(UtilityFormat.getStringForTextField(workerCosts));
        lb_AbatementArea.setText(UtilityFormat.getStringForLabel(abatementArea));
        lb_AssemblingCosts.setText(UtilityFormat.getStringForLabel(assemblingCosts));
        lb_ProductCosts.setText(UtilityFormat.getStringForLabel(productCosts));
        lb_TotalCosts.setText(UtilityFormat.getStringForLabel(totalCosts));
        lb_VisibleFormwork.setText(UtilityFormat.getStringForLabel(visibleFormwork));

        lb_RoofArea.setText(UtilityFormat.getStringForLabel(Project_ResultAreaController.getInstance().getLedge()) + " m²");
        ModifyController.getInstance().setAssembling_visibleFormwork(Boolean.FALSE);
    }

    /**
     * Calculates all required values.
     */
    private void calculate() {
        try {
            //Verschnittsfläche
            //Alte Formel-ID: VSS
            abatementArea.setWorth(Project_ResultAreaController.getInstance().getLedge() * abatementPercent.getWorth() / 100);
            lb_AbatementArea.setText(UtilityFormat.getStringForLabel(abatementArea));

            //sicht. Schalung
            //Alte Formel-ID: SS
            visibleFormwork.setWorth(Project_ResultAreaController.getInstance().getLedge() + abatementArea.getWorth());
            lb_VisibleFormwork.setText(UtilityFormat.getStringForLabel(visibleFormwork));

            //Kosten Produkt
            //Alte Formel-ID: KPSS
            productCosts.setWorth(pricePerSquare * visibleFormwork.getWorth());
            lb_ProductCosts.setText(UtilityFormat.getStringForLabel(productCosts));

            //Kosten Montage
            //Alte Formel-ID: KMSS
            assemblingCosts.setWorth(workerCosts.getWorth() * assemblingDuration.getWorth());
            lb_AssemblingCosts.setText(UtilityFormat.getStringForLabel(assemblingCosts));

            //Gesamtkosten
            //Alte Formel-ID: GKSS
            totalCosts.setWorth(productCosts.getWorth() + assemblingCosts.getWorth());
            lb_TotalCosts.setText(UtilityFormat.getStringForLabel(totalCosts));
        } catch (Exception ex) {
            if (ProjectViewController.isProjectOpened()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Werte können nicht berechnet werden!\nFehlerinformation: " + ex.getLocalizedMessage(), ButtonType.OK);
                alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
                alert.showAndWait();
                Logging.getLogger().log(Level.SEVERE, "Assembling_VisibleFormworkController: calculate method didn't work.", ex);
            }
        }

        Product product = cb_Product.getSelectionModel().getSelectedItem();

        if (product != null) {
            component.setDescription(product.getName());
            component.setLengthComponent(product.getLengthProduct());
            component.setWidthComponent(product.getWidthProduct());
            component.setHeightComponent(product.getHeightProduct());
            component.setProduct(product);
            component.setUnit(product.getUnit());
        } else {
            component.setDescription("SichtbareSchalung");
            component.setLengthComponent(null);
            component.setWidthComponent(null);
            component.setHeightComponent(null);
            component.setProduct(null);
            component.setUnit(null);
        }
        component.setNumberOfProducts(visibleFormwork.getWorth());
        component.setPriceComponent(pricePerSquare);
    }

    /**
     * Persists all values from the view to the database.
     */
    public void persist() {
        WorthController worthController = new WorthController();
        ComponentController componentController = new ComponentController();

        try {
            if (!ProjectViewController.isProjectOpened()) {
                abatementPercent.setProject(ProjectViewController.getOpenedProject());
                abatementArea.setProject(ProjectViewController.getOpenedProject());
                visibleFormwork.setProject(ProjectViewController.getOpenedProject());
                productCosts.setProject(ProjectViewController.getOpenedProject());
                assemblingDuration.setProject(ProjectViewController.getOpenedProject());
                assemblingCosts.setProject(ProjectViewController.getOpenedProject());
                totalCosts.setProject(ProjectViewController.getOpenedProject());
                workerCosts.setProject(ProjectViewController.getOpenedProject());
                component.setProject(ProjectViewController.getOpenedProject());

                worthController.create(abatementPercent);
                worthController.create(workerCosts);
                worthController.create(assemblingDuration);
                worthController.create(abatementArea);
                worthController.create(visibleFormwork);
                worthController.create(productCosts);
                worthController.create(assemblingCosts);
                worthController.create(totalCosts);
                componentController.create(component);
            } else {

                worthController.edit(abatementPercent);
                worthController.edit(workerCosts);
                worthController.edit(assemblingDuration);
                worthController.edit(abatementArea);
                worthController.edit(visibleFormwork);
                worthController.edit(productCosts);
                worthController.edit(assemblingCosts);
                worthController.edit(totalCosts);
                componentController.edit(component);
            }
        } catch (Exception ex) {
            Logging.getLogger().log(Level.SEVERE, "Assembling_VisibleFormworkController: persist method didn't work.", ex);
        }
    }

    /**
     * Refreshes the values.
     *
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        double oldVal = Double.parseDouble(lb_VisibleFormwork.getText().substring(0, lb_VisibleFormwork.getText().length() - 3));
        double newVal = Project_ResultAreaController.getInstance().getLedge();
        if (oldVal != newVal && ModifyController.getInstance().getProject_resultArea() == true
                && !(tf_AbatementPercent.getText().isEmpty() || tf_PricePerSquare.getText().isEmpty())) {
            ModifyController.getInstance().setAssembling_visibleFormwork(Boolean.TRUE);
        }
        lb_RoofArea.setText(UtilityFormat.getStringForLabel(newVal) + " m²");
        calculate();
    }
}
