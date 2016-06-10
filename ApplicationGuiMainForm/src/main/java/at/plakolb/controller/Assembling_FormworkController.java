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
import at.plakolb.calculationlogic.db.entity.Product;
import at.plakolb.calculationlogic.db.entity.Project;
import at.plakolb.calculationlogic.db.entity.Worth;
import at.plakolb.calculationlogic.eunmeration.ProductType;
import at.plakolb.calculationlogic.util.UtilityFormat;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Level;

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
 * Over this view it's possible to calculate the formwork.
 *
 * @author Kepplinger
 */
public class Assembling_FormworkController implements Initializable, Observer {

    private static Assembling_FormworkController instance;

    @FXML
    private ComboBox<Product> cb_Formwork;
    @FXML
    private TextField tf_Price;
    @FXML
    private Label lb_Waste;
    @FXML
    private TextField tf_Wage;
    @FXML
    private Label lb_Formwork;
    @FXML
    private TextField tf_Time;
    @FXML
    private Label lb_ProductCosts;
    @FXML
    private Label lb_AssebmlyCosts;
    @FXML
    private Label lb_TotalCosts;
    @FXML
    private Label lb_RoofArea;
    @FXML
    private TextField tf_Blend;

    private double price;
    private double roofArea;

    private Worth blend;
    private Worth time;
    private Worth wage;

    private Worth waste;
    private Worth formwork;
    private Worth productCosts;
    private Worth costsMontage;
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
        cb_Formwork.setItems(FXCollections.observableArrayList(new ProductController().findByProductTypeOrderByName(ProductType.FORMWORK)));

        ParameterController parameterController = new ParameterController();
        waste = new Worth(parameterController.findParameterPByShortTerm("VS"));
        formwork = new Worth(parameterController.findParameterPByShortTerm("S"));
        productCosts = new Worth(parameterController.findParameterPByShortTerm("SPROK"));
        blend = new Worth(parameterController.findParameterPByShortTerm("VSP"));
        wage = new Worth(parameterController.findParameterPByShortTerm("KPS"));
        time = new Worth(parameterController.findParameterPByShortTerm("ZPS"));
        costsMontage = new Worth(parameterController.findParameterPByShortTerm("KMS"));
        totalCosts = new Worth(parameterController.findParameterPByShortTerm("GKS"));

        lb_Waste.setText(UtilityFormat.getStringForLabel(waste));
        lb_Formwork.setText(UtilityFormat.getStringForLabel(formwork));
        lb_ProductCosts.setText(UtilityFormat.getStringForLabel(productCosts));
        lb_AssebmlyCosts.setText(UtilityFormat.getStringForLabel(costsMontage));
        lb_TotalCosts.setText(UtilityFormat.getStringForLabel(totalCosts));
        tf_Blend.setText(UtilityFormat.getStringForTextField(blend));
        tf_Wage.setText(UtilityFormat.getStringForTextField(wage));
        tf_Time.setText(UtilityFormat.getStringForTextField(time));

        cb_Formwork.getSelectionModel().selectedItemProperty().addListener((source, oldValue, newValue) -> {
            tf_Price.setText(UtilityFormat.getStringForTextField(newValue.getPriceUnit()));
        });

        tf_Price.textProperty().addListener((observable, oldValue, newValue) -> {
            setPrice();
            calculate();
            ModifyController.getInstance().setAssembling_formwork(Boolean.TRUE);
        });

        tf_Time.textProperty().addListener((observable, oldValue, newValue) -> {
            UtilityFormat.setWorthFromTextField(tf_Time, time);
            calculate();
            ModifyController.getInstance().setAssembling_formwork(Boolean.TRUE);
        });

        tf_Wage.textProperty().addListener((observable, oldValue, newValue) -> {
            UtilityFormat.setWorthFromTextField(tf_Wage, wage);
            calculate();
            ModifyController.getInstance().setAssembling_formwork(Boolean.TRUE);
        });

        tf_Blend.textProperty().addListener((observable, oldValue, newValue) -> {
            UtilityFormat.setWorthFromTextField(tf_Blend, blend);
            calculate();
            ModifyController.getInstance().setAssembling_formwork(Boolean.TRUE);
        });

        if (ProjectViewController.getOpenedProject() != null) {
            load();
        } else {
            component = new Component();
            component.setDescription("Schalung");
            component.setCategory(new CategoryController().findCategoryByShortTerm("S"));
            component.setComponentType("Produkt");
        }
    }

    public static Assembling_FormworkController getInstance() {
        return instance;
    }

    private void setPrice() {
        tf_Price.setText(tf_Price.getText().replaceAll(",", ".").replaceAll("[^\\d.]", ""));
        tf_Price.setText(UtilityFormat.removeUnnecessaryCommas(tf_Price.getText()));

        if (tf_Price.getText().isEmpty()) {
            this.price = 0;
        } else if (Double.valueOf(tf_Price.getText()) < 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Der Preis muss eine positive Zahl sein!\nEingabe: \"" + price + "\"", ButtonType.OK);
            alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
            alert.showAndWait();
        } else {
            this.price = Double.valueOf(tf_Price.getText());
            component.setPriceComponent(price);
        }
    }

    public Component getComponent() {
        return component;
    }

    public Worth getWage() {
        return costsMontage;
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
    public void load() {
        Project project = ProjectViewController.getOpenedProject();
        if (project != null) {
            WorthController worthController = new WorthController();
            ComponentController componentController = new ComponentController();
            CategoryController categoryController = new CategoryController();
            Category category = categoryController.findCategoryByShortTerm("S");

            component = componentController.findComponentByProjectIdAndComponentTypeAndCategoryId(project.getId(),
                    "Produkt", category.getId());


            if (component != null) {
                Double componentPrice = component.getPriceComponent();
                cb_Formwork.getSelectionModel().select(component.getProduct());
                if (componentPrice != null) {
                    tf_Price.setText(UtilityFormat.getStringForTextField(componentPrice));
                }
            } else {
                component = new Component();
                component.setDescription("Schalung");
                component.setCategory(category);
                component.setComponentType("Produkt");
            }

            waste = (worthController.findWorthByShortTermAndProjectId("VS", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("VS", project.getId()) : waste;
            formwork = (worthController.findWorthByShortTermAndProjectId("S", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("S", project.getId()) : formwork;
            productCosts = (worthController.findWorthByShortTermAndProjectId("SPROK", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("SPROK", project.getId()) : productCosts;
            blend = (worthController.findWorthByShortTermAndProjectId("VSP", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("VSP", project.getId()) : blend;
            wage = (worthController.findWorthByShortTermAndProjectId("KPS", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("KPS", project.getId()) : wage;
            time = (worthController.findWorthByShortTermAndProjectId("ZPS", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("ZPS", project.getId()) : time;
            costsMontage = (worthController.findWorthByShortTermAndProjectId("KMS", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("KMS", project.getId()) : costsMontage;
            totalCosts = (worthController.findWorthByShortTermAndProjectId("GKS", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("GKS", project.getId()) : totalCosts;

            lb_RoofArea.setText(UtilityFormat.getStringForLabel(Project_ResultAreaController.getInstance().getRoofArea()) + " m²");
            lb_Waste.setText(UtilityFormat.getStringForLabel(waste));
            lb_Formwork.setText(UtilityFormat.getStringForLabel(formwork));
            lb_ProductCosts.setText(UtilityFormat.getStringForLabel(productCosts));
            lb_AssebmlyCosts.setText(UtilityFormat.getStringForLabel(costsMontage));
            lb_TotalCosts.setText(UtilityFormat.getStringForLabel(totalCosts));
            tf_Blend.setText(UtilityFormat.getStringForTextField(blend));
            tf_Wage.setText(UtilityFormat.getStringForTextField(wage));
            tf_Time.setText(UtilityFormat.getStringForTextField(time));

            ModifyController.getInstance().setAssembling_formwork(Boolean.FALSE);
        }
    }

    /**
     * Calculates all required values.
     */
    public void calculate() {

        try {
            //Verschnittberechnung
            //########################
            //select 
            //(select NULLIF(w.worth, 0) from Worth w join
            // ParameterP p on w.parameter_id = p.id where w.project_id = ? 
            //and p.shortterm = ''D'' and w.shortterm is null) * 
            //(select NULLIF(w.worth, 0)/100 from Worth w join ParameterP p on
            //w.parameter_id = p.id where w.project_id = ? and p.shortterm = ''VSP'')
            //from sysibm.sysdummy1
            waste.setWorth(blend.getWorth() / 100 * roofArea);
            lb_Waste.setText(UtilityFormat.getStringForLabel(waste));

            //Schalung-Berechnung
            //-------------------------------------------
            //select (select NULLIF(w.worth, 0) from Worth 
            //w join ParameterP p on w.parameter_id = p.id 
            //where w.project_id = ? and p.shortterm = ''D'' and w.shortterm is null) 
            //+ (select NULLIF(w.worth, 0) from Worth w 
            //join ParameterP p on w.parameter_id = p.id where w.project_id = ? 
            //and p.shortterm = ''VS'') from sysibm.sysdummy1
            formwork.setWorth(roofArea + waste.getWorth());
            lb_Formwork.setText(UtilityFormat.getStringForLabel(formwork));

            //Produktkosten
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            //select (select c.pricecomponent from component c where c.project_id = ? 
            //and c.category_id = ? and c.componenttype = ''Produkt'') * 
            //(select NULLIF(w.worth, 0) from Worth w join ParameterP p on w.parameter_id = p.id 
            //where w.project_id = ? and p.shortterm = ''S'') from sysibm.sysdummy1
            productCosts.setWorth(price * formwork.getWorth());
            lb_ProductCosts.setText(UtilityFormat.getStringForLabel(productCosts));

            //Montagekosten
            //´´´´´´´´´´´´´´´´´´´´´´´´´´´´´´´´
            //select (select NULLIF(w.worth, 0) from Worth w join ParameterP p 
            //on w.parameter_id = p.id where w.project_id = ? and p.shortterm = ''KPS'')
            //* (select NULLIF(w.worth, 0) from Worth w join ParameterP p 
            //on w.parameter_id = p.id where w.project_id = ? and p.shortterm = ''ZPS'')
            //from sysibm.sysdummy1
            costsMontage.setWorth(wage.getWorth() * time.getWorth());
            lb_AssebmlyCosts.setText(UtilityFormat.getStringForLabel(costsMontage));

            //Gesamtkosten
            //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
            totalCosts.setWorth(costsMontage.getWorth() + productCosts.getWorth());
            lb_TotalCosts.setText(UtilityFormat.getStringForLabel(totalCosts));

        } catch (Exception ex) {
            if (ProjectViewController.isProjectOpened()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Werte können nicht berechnet werden!\nFehlerinformation: " + ex.getLocalizedMessage(), ButtonType.OK);
                alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
                alert.showAndWait();
                Logging.getLogger().log(Level.SEVERE, "Assembling_FormworkController: calculate method didn't work.", ex);
            }
        }

        Product product = cb_Formwork.getSelectionModel().getSelectedItem();

        if (product != null) {
            component.setDescription(product.getName());
            component.setLengthComponent(product.getLengthProduct());
            component.setWidthComponent(product.getWidthProduct());
            component.setHeightComponent(product.getHeightProduct());
            component.setProduct(product);
            component.setUnit(product.getUnit());
        } else {
            component.setDescription("Schalung");
            component.setLengthComponent(null);
            component.setWidthComponent(null);
            component.setHeightComponent(null);
            component.setProduct(null);
            component.setUnit(null);
        }
        component.setNumberOfProducts(formwork.getWorth());
        component.setPriceComponent(price);
    }

    /**
     * Persists all values from the view to the database.
     */
    public void persist() {
        try {
            WorthController worthController = new WorthController();
            ComponentController componentController = new ComponentController();

            if (!ProjectViewController.isProjectOpened()) {
                blend.setProject(ProjectViewController.getOpenedProject());
                time.setProject(ProjectViewController.getOpenedProject());
                formwork.setProject(ProjectViewController.getOpenedProject());
                productCosts.setProject(ProjectViewController.getOpenedProject());
                wage.setProject(ProjectViewController.getOpenedProject());
                waste.setProject(ProjectViewController.getOpenedProject());
                costsMontage.setProject(ProjectViewController.getOpenedProject());
                totalCosts.setProject(ProjectViewController.getOpenedProject());
                component.setProject(ProjectViewController.getOpenedProject());

                worthController.create(blend);
                worthController.create(time);
                worthController.create(formwork);
                worthController.create(productCosts);
                worthController.create(wage);
                worthController.create(waste);
                worthController.create(costsMontage);
                worthController.create(totalCosts);
                componentController.create(component);
            } else {
                worthController.edit(blend);
                worthController.edit(time);
                worthController.edit(formwork);
                worthController.edit(productCosts);
                worthController.edit(wage);
                worthController.edit(waste);
                worthController.edit(costsMontage);
                worthController.edit(totalCosts);
                componentController.edit(component);
            }
        } catch (Exception ex) {
            Logging.getLogger().log(Level.SEVERE, "Assembling_FormworkController: perist method didn't work.", ex);
        }
    }

    /**
     * Refreshes the roof area when values have been changed.
     *
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        roofArea = Project_ResultAreaController.getInstance().getRoofArea();
        lb_RoofArea.setText(UtilityFormat.getStringForLabel(roofArea) + " m²");
        calculate();
    }
}
