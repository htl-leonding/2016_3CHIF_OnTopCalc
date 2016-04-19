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
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class Assembling_FormworkController implements Initializable, Observer {

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

    private static Assembling_FormworkController instance;

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

    private void setPrice(String price) {
        price = price.replace(',', '.');
        if (price.isEmpty() || !price.matches("[0-9]*.[0-9]*") || Double.valueOf(price) < 0) {
            new Alert(Alert.AlertType.ERROR, "Der Preis muss eine positive Zahl sein!\nEingabe: \"" + price + "\"", ButtonType.OK).showAndWait();
        } else {
            this.price = Double.valueOf(price);
        }
    }

    private void setBlend() {
        blend.setWorth(tf_Blend.getText().isEmpty() || !tf_Blend.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_Blend.getText().replace(',', '.')));
    }

    private void setWage() {
        wage.setWorth(tf_Wage.getText().isEmpty() || !tf_Wage.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_Wage.getText().replace(',', '.')));
    }

    private void setTime() {
        time.setWorth(tf_Time.getText().isEmpty() || !tf_Time.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_Time.getText().replace(',', '.')));
    }

    public Component getComponent() {
        return component;
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;

        ParameterController parameterController = new ParameterController();
        waste = new Worth(parameterController.findParameterPByShortTerm("VS"));
        formwork = new Worth(parameterController.findParameterPByShortTerm("S"));
        productCosts = new Worth(parameterController.findParameterPByShortTerm("SPROK"));
        blend = new Worth(parameterController.findParameterPByShortTerm("VSP"));
        wage = new Worth(parameterController.findParameterPByShortTerm("KPS"));
        time = new Worth(parameterController.findParameterPByShortTerm("ZPS"));
        costsMontage = new Worth(parameterController.findParameterPByShortTerm("KMS"));
        totalCosts = new Worth(parameterController.findParameterPByShortTerm("GKS"));

        cb_Formwork.getSelectionModel().selectedItemProperty().addListener((source, oldValue, newValue) -> {
            tf_Price.setText(UtilityFormat.getStringForTextField(newValue.getPriceUnit()));
        });
        tf_Price.textProperty().addListener((observable, oldValue, newValue) -> {
            setPrice(newValue);
            calculate();
        });
        tf_Time.textProperty().addListener((observable, oldValue, newValue) -> {
            setTime();
            calculate();
        });
        tf_Wage.textProperty().addListener((observable, oldValue, newValue) -> {
            setWage();
            calculate();
        });
        tf_Blend.textProperty().addListener((observable, oldValue, newValue) -> {
            setBlend();
            calculate();
        });
        refresh();
    }

    public void refresh() {
        ProductController productController = new ProductController();

        ObservableList<Product> list = FXCollections.observableArrayList(productController.findByProductTypeOrderByName(ProductType.FORMWORK));
        if (list != null) {
            cb_Formwork.setItems(list);
        }

        Project project = ProjectViewController.getOpenedProject();
        if (project != null) {
            WorthController worthController = new WorthController();
            ComponentController componentController = new ComponentController();
            CategoryController categoryController = new CategoryController();

            Category category = categoryController.findCategoryByShortTerm("S");
            component = componentController.findComponentByProjectIdAndComponentTypeAndCategoryId(project.getId(),
                    "Produkt", category.getId());

            if (component != null) {
                cb_Formwork.getSelectionModel().select(component.getProduct());
                tf_Price.setText(UtilityFormat.getStringForTextField(component.getPriceComponent()));
            } else {
                component = new Component();
                component.setCategory(new CategoryController().findCategoryByShortTerm("S"));
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

            if (waste == null || formwork == null || productCosts == null || blend == null || wage == null || time == null || costsMontage == null || totalCosts == null) {
                new Alert(Alert.AlertType.ERROR, "Fehler beim Laden der Werte für die Schalung!").showAndWait();
            } else {
                lb_RoofArea.setText(UtilityFormat.worthWithTwoDecimalPlaces(Project_ResultAreaController.getInstance().getRoofArea()) + " m²");
                lb_Waste.setText(UtilityFormat.getStringForLabel(waste));
                lb_Formwork.setText(UtilityFormat.getStringForLabel(formwork));
                lb_ProductCosts.setText(UtilityFormat.getStringForLabel(productCosts));
                lb_AssebmlyCosts.setText(UtilityFormat.getStringForLabel(costsMontage));
                lb_TotalCosts.setText(UtilityFormat.getStringForLabel(totalCosts));
                tf_Blend.setText(UtilityFormat.getStringForTextField(blend));
                tf_Wage.setText(UtilityFormat.getStringForTextField(wage));
                tf_Time.setText(UtilityFormat.getStringForTextField(time));
            }
        }
    }

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
                new Alert(Alert.AlertType.ERROR, "Werte können nicht berechnet werden!\nFehlerinformation: " + ex.getLocalizedMessage(), ButtonType.OK).showAndWait();
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
            component.setDescription("");
            component.setLengthComponent(null);
            component.setWidthComponent(null);
            component.setHeightComponent(null);
            component.setProduct(null);
            component.setUnit(null);
        }
        component.setNumberOfProducts((int) formwork.getWorth());
        component.setPriceComponent(price);
    }

    public void persist() {
        try {
            WorthController worthController = new WorthController();

            ComponentController componentController = new ComponentController();
            CategoryController categoryController = new CategoryController();

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
                try {
                    worthController.edit(blend);
                    worthController.edit(time);
                    worthController.edit(formwork);
                    worthController.edit(productCosts);
                    worthController.edit(wage);
                    worthController.edit(waste);
                    worthController.edit(costsMontage);
                    worthController.edit(totalCosts);
                    componentController.edit(component);
                } catch (Exception e) {
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Assembling_FormworkController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Assembling_FormworkController getInstance() {
        return instance;
    }

    @Override
    public void update(Observable o, Object arg) {
        roofArea = Project_ResultAreaController.getInstance().getRoofArea();
        lb_RoofArea.setText(UtilityFormat.getStringForLabel(roofArea) + " m²");
        calculate();
    }
}
