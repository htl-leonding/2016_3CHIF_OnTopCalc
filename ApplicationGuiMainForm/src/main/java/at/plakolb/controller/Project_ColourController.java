/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.*;
import at.plakolb.calculationlogic.db.entity.*;
import at.plakolb.calculationlogic.eunmeration.ProductType;
import at.plakolb.calculationlogic.util.Logging;
import at.plakolb.calculationlogic.util.UtilityFormat;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * FXML Controller class
 *
 * @author User
 */
public class Project_ColourController implements Initializable {

    private static Project_ColourController instance;
    @FXML
    private Label lb_VisibleFormwork;
    @FXML
    private ComboBox<Product> cb_Product;
    @FXML
    private TextField tf_ProfiHour;
    @FXML
    private TextField tf_AdittionalColourFactor;
    @FXML
    private TextField tf_TimeOfPainting;
    @FXML
    private TextField tf_PricePerLiter;
    @FXML
    private Label lb_PaintArea;
    @FXML
    private Label lb_ProductCost;
    @FXML
    private Label lb_MontageCost;
    @FXML
    private Label lb_PaintLiter;
    @FXML
    private Label lb_TotalCosts;

    private Worth additionalColourFactor;
    private Worth timeofPainting;
    private double pricePerLiter;
    private Worth paintArea;
    private Worth productCost;
    private Worth montageCost;
    private Worth paintLiter;
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

        cb_Product.setItems(FXCollections.observableArrayList(new ProductController().findByProductTypeOrderByName(ProductType.COLOR)));
        ParameterController parameterController = new ParameterController();
        additionalColourFactor = new Worth(parameterController.findParameterPByShortTerm("FK"));
        profiHour = new Worth(parameterController.findParameterPByShortTerm("PMFP"));
        timeofPainting = new Worth(parameterController.findParameterPByShortTerm("ZPFA"));
        paintArea = new Worth(parameterController.findParameterPByShortTerm("FMM"));
        productCost = new Worth(parameterController.findParameterPByShortTerm("KPFarbe"));
        montageCost = new Worth(parameterController.findParameterPByShortTerm("KMFarbe"));
        paintLiter = new Worth(parameterController.findParameterPByShortTerm("FML"));
        totalCost = new Worth(parameterController.findParameterPByShortTerm("GKFarbe"));

        lb_VisibleFormwork.setText(UtilityFormat.getStringForLabel(Assembling_VisibleFormworkController.getInstance().getVisibleFormwork()) + " m²");
        tf_ProfiHour.setText(UtilityFormat.getStringForTextField(profiHour));
        tf_AdittionalColourFactor.setText(UtilityFormat.getStringForTextField(additionalColourFactor));
        tf_TimeOfPainting.setText(UtilityFormat.getStringForTextField(timeofPainting));
        lb_MontageCost.setText(UtilityFormat.getStringForLabel(montageCost));
        lb_PaintArea.setText(UtilityFormat.getStringForLabel(paintArea));
        lb_PaintLiter.setText(UtilityFormat.getStringForLabel(paintLiter));
        lb_ProductCost.setText(UtilityFormat.getStringForLabel(productCost));
        lb_TotalCosts.setText(UtilityFormat.getStringForLabel(totalCost));

        cb_Product.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Product> observable, Product oldValue, Product newValue) -> {
            if (newValue != null) {
                tf_AdittionalColourFactor.setText(UtilityFormat.getStringForTextField(newValue.getColorFactor()));
                tf_PricePerLiter.setText(UtilityFormat.getStringForTextField(newValue.getPriceUnit()));
            }
            calculate();
            ModifyController.getInstance().setProject_colour(Boolean.TRUE);
        });

        tf_PricePerLiter.textProperty().addListener((observable, oldValue, newValue) -> {
            setPricePerLiter();
            calculate();
            ModifyController.getInstance().setProject_colour(Boolean.TRUE);
        });

        tf_ProfiHour.textProperty().addListener((observable, oldValue, newValue) -> {
            UtilityFormat.setWorthFromTextField(tf_ProfiHour, profiHour);
            calculate();
            ModifyController.getInstance().setProject_colour(Boolean.TRUE);
        });

        tf_AdittionalColourFactor.textProperty().addListener((observable, oldValue, newValue) -> {
            UtilityFormat.setWorthFromTextField(tf_AdittionalColourFactor, additionalColourFactor);
            calculate();
            ModifyController.getInstance().setProject_colour(Boolean.TRUE);
        });

        tf_TimeOfPainting.textProperty().addListener((observable, oldValue, newValue) -> {
            UtilityFormat.setWorthFromTextField(tf_TimeOfPainting, timeofPainting);
            calculate();
            ModifyController.getInstance().setProject_colour(Boolean.TRUE);
        });

        if (ProjectViewController.getOpenedProject() != null) {
            loadColourValues();
        } else {
            component = new Component();
            component.setDescription("Farbe");
            component.setCategory(new CategoryController().findCategoryByShortTerm("X"));
            component.setComponentType(ProductType.COLOR.toString());
        }
    }

    public Component getComponent() {
        return component;
    }

    public void setPricePerLiter() {
        tf_PricePerLiter.setText(tf_PricePerLiter.getText().replace('.',',').replaceAll("[^\\d,]", ""));
        tf_PricePerLiter.setText(UtilityFormat.removeUnnecessaryCommas(tf_PricePerLiter.getText()));

        if (tf_PricePerLiter.getText().isEmpty()) {
            this.pricePerLiter = 0;
        } else if (tf_PricePerLiter.getText().isEmpty() || Double.valueOf(tf_PricePerLiter.getText().replace(',','.')) < 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Der Preis muss eine positive Zahl sein!\nEingabe: \"" + pricePerLiter + "\"", ButtonType.OK);
            alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
            alert.showAndWait();
        } else {
            this.pricePerLiter = Double.valueOf(tf_PricePerLiter.getText().replace(',','.'));
            component.setPriceComponent(pricePerLiter);
        }
    }

    public static Project_ColourController getInstance() {
        return instance;
    }

    public Worth getWage() {
        return montageCost;
    }

    public Worth getMaterial() {
        return productCost;
    }

    public Worth getTotalCosts() {
        return totalCost;
    }

    public void loadColourValues() {

        WorthController worthController = new WorthController();
        Project openedProject = ProjectViewController.getOpenedProject();

        timeofPainting = (worthController.findWorthByShortTermAndProjectId("ZPFA", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("ZPFA", openedProject.getId()) : timeofPainting;

        profiHour = (worthController.findWorthByShortTermAndProjectId("PMFP", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("PMFP", openedProject.getId()) : profiHour;
        additionalColourFactor = (worthController.findWorthByShortTermAndProjectId("FK", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("FK", openedProject.getId()) : additionalColourFactor;
        paintArea = (worthController.findWorthByShortTermAndProjectId("FMM", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("FMM", openedProject.getId()) : paintArea;
        totalCost = (worthController.findWorthByShortTermAndProjectId("GKFarbe", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("GKFarbe", openedProject.getId()) : totalCost;
        paintLiter = (worthController.findWorthByShortTermAndProjectId("FML", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("FML", openedProject.getId()) : paintLiter;
        productCost = (worthController.findWorthByShortTermAndProjectId("KPFarbe", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("KPFarbe", openedProject.getId()) : productCost;
        montageCost = (worthController.findWorthByShortTermAndProjectId("KMFarbe", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("KMFarbe", openedProject.getId()) : montageCost;

        Category category = new CategoryController().findCategoryByShortTerm("X");
        component = new ComponentController().findComponentByProjectIdAndComponentTypeAndCategoryId(ProjectViewController.getOpenedProject().getId(),
                ProductType.COLOR.toString(),category.getId());

        if (component != null) {
            Double componentPrice = component.getPriceComponent();
            cb_Product.getSelectionModel().select(component.getProduct());
            if (componentPrice != null) {
                tf_PricePerLiter.setText(UtilityFormat.getStringForTextField(componentPrice));
            }
        } else {
            component = new Component();
            component.setDescription("Farbe");
            component.setComponentType(ProductType.COLOR.toString());
            component.setCategory(category);
        }

        lb_VisibleFormwork.setText(UtilityFormat.getStringForLabel(Assembling_VisibleFormworkController.getInstance().getVisibleFormwork()) + " m²");
        tf_ProfiHour.setText(UtilityFormat.getStringForTextField(profiHour));
        tf_AdittionalColourFactor.setText(UtilityFormat.getStringForTextField(additionalColourFactor));
        tf_TimeOfPainting.setText(UtilityFormat.getStringForTextField(timeofPainting));
        lb_MontageCost.setText(UtilityFormat.getStringForLabel(montageCost));
        lb_PaintArea.setText(UtilityFormat.getStringForLabel(paintArea));
        lb_PaintLiter.setText(UtilityFormat.getStringForLabel(paintLiter));
        lb_ProductCost.setText(UtilityFormat.getStringForLabel(productCost));
        lb_TotalCosts.setText(UtilityFormat.getStringForLabel(totalCost));
        ModifyController.getInstance().setProject_colour(Boolean.FALSE);
    }

    public void calculate() {
        try {
            //Farbmenge in m²
            paintArea.setWorth(Assembling_VisibleFormworkController.getInstance().getVisibleFormwork() + additionalColourFactor.getWorth());
            lb_PaintArea.setText(UtilityFormat.getStringForLabel(paintArea));

            //Farbmenge in Liter
            paintLiter.setWorth(paintArea.getWorth() / 10);
            lb_PaintLiter.setText(UtilityFormat.getStringForLabel(paintLiter));

            //Kosten Montage Farbe
            montageCost.setWorth(profiHour.getWorth() * timeofPainting.getWorth());
            lb_MontageCost.setText(UtilityFormat.getStringForLabel(montageCost));

            //Kosten Produkt Farbe
            productCost.setWorth(pricePerLiter * paintLiter.getWorth());
            lb_ProductCost.setText(UtilityFormat.getStringForLabel(productCost));

            //Gesamtkosten Farbe
            totalCost.setWorth(productCost.getWorth() + montageCost.getWorth());
            lb_TotalCosts.setText(UtilityFormat.getStringForLabel(totalCost));

        } catch (Exception ex) {
            Logging.getLogger().log(Level.SEVERE, "Project_BaseAndRoofAreaController: calculate method didn't work.", ex);
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
            component.setDescription("Farbe");
            component.setLengthComponent(null);
            component.setWidthComponent(null);
            component.setHeightComponent(null);
            component.setProduct(null);
            component.setUnit(null);
        }

        component.setPriceComponent(pricePerLiter);
        component.setNumberOfProducts(paintLiter.getWorth());
    }

    public void persist() {
        WorthController worthController = new WorthController();
        ComponentController componentController = new ComponentController();

        try {
            if (!ProjectViewController.isProjectOpened()) {
                profiHour.setProject(ProjectViewController.getOpenedProject());
                additionalColourFactor.setProject(ProjectViewController.getOpenedProject());
                timeofPainting.setProject(ProjectViewController.getOpenedProject());
                paintArea.setProject(ProjectViewController.getOpenedProject());
                productCost.setProject(ProjectViewController.getOpenedProject());
                montageCost.setProject(ProjectViewController.getOpenedProject());
                paintLiter.setProject(ProjectViewController.getOpenedProject());
                totalCost.setProject(ProjectViewController.getOpenedProject());
                component.setProject(ProjectViewController.getOpenedProject());

                worthController.create(profiHour);
                worthController.create(additionalColourFactor);
                worthController.create(timeofPainting);
                worthController.create(paintArea);
                worthController.create(productCost);
                worthController.create(montageCost);
                worthController.create(paintLiter);
                worthController.create(totalCost);
                componentController.create(component);
            } else {
                worthController.edit(profiHour);
                worthController.edit(additionalColourFactor);
                worthController.edit(timeofPainting);
                worthController.edit(paintArea);
                worthController.edit(productCost);
                worthController.edit(montageCost);
                worthController.edit(paintLiter);
                worthController.edit(totalCost);
                componentController.edit(component);
            }
        } catch (Exception ex) {
            Logging.getLogger().log(Level.SEVERE, "Project_ColourController: persist method didn't work.", ex);
        }
    }

    public void updateVisibleFormwork() {
        double oldVal = Double.parseDouble(lb_VisibleFormwork.getText().substring(0, lb_VisibleFormwork.getText().length() - 3).replace(',','.'));
        double newVal = Assembling_VisibleFormworkController.getInstance().getVisibleFormwork();
        if (oldVal != newVal && ModifyController.getInstance().getAssembling() == true
                && !(tf_AdittionalColourFactor.getText().isEmpty() || tf_PricePerLiter.getText().isEmpty())) {
            ModifyController.getInstance().setProject_colour(Boolean.TRUE);
        }
        lb_VisibleFormwork.setText(UtilityFormat.getStringForLabel(newVal) + " m²");
        calculate();
    }
}
