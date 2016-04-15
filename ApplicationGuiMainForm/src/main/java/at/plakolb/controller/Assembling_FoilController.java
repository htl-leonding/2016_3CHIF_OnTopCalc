package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.CategoryController;
import at.plakolb.calculationlogic.db.controller.ComponentController;
import at.plakolb.calculationlogic.db.controller.ParameterController;
import at.plakolb.calculationlogic.db.controller.ProductController;
import at.plakolb.calculationlogic.db.controller.UnitController;
import at.plakolb.calculationlogic.db.controller.WorthController;
import at.plakolb.calculationlogic.entity.Category;
import at.plakolb.calculationlogic.entity.Component;
import at.plakolb.calculationlogic.entity.Product;
import at.plakolb.calculationlogic.entity.Project;
import at.plakolb.calculationlogic.entity.Unit;
import at.plakolb.calculationlogic.entity.Worth;
import at.plakolb.calculationlogic.eunmeration.ProductType;
import at.plakolb.calculationlogic.util.UtilityFormat;
import java.util.logging.Logger;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class Assembling_FoilController implements Initializable, Observer {

    private static Assembling_FoilController instance;

    @FXML
    private Label lb_RoofArea;
    @FXML
    private ComboBox<Product> cb_Product;
    @FXML
    private TextField tf_price;
    @FXML
    private TextField tf_blend;
    @FXML
    private Label lb_blend;
    @FXML
    private Label lb_foil;
    @FXML
    private TextField tf_workerCosts;
    @FXML
    private TextField tf_time;
    @FXML
    private Label lb_productCosts;
    @FXML
    private Label lb_montageCosts;
    @FXML
    private Label lb_totalCosts;

    private double pricePerSquare;
    private Worth abatementPercent;
    private Worth workerCosts;
    private Worth assemblingDuration;

    private Worth abatementArea;
    private Worth foil;
    private Worth productCosts;
    private Worth assemblingCosts;
    private Worth totalCosts;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        Project_ResultAreaController.getInstance().addObserver(this);

        ParameterController parameterController = new ParameterController();
        abatementPercent = new Worth(parameterController.findParameterPByShortTerm("FUEP"));
        workerCosts = new Worth(parameterController.findParameterPByShortTerm("KPF"));
        assemblingDuration = new Worth(parameterController.findParameterPByShortTerm("ZPF"));

        abatementArea = new Worth(parameterController.findParameterPByShortTerm("FUE"));
        foil = new Worth(parameterController.findParameterPByShortTerm("F"));
        productCosts = new Worth(parameterController.findParameterPByShortTerm("KProF"));
        assemblingCosts = new Worth(parameterController.findParameterPByShortTerm("KMF"));
        totalCosts = new Worth(parameterController.findParameterPByShortTerm("GKF"));

        tf_blend.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            setAbatementPercent();
            calculate();
        });

        tf_time.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            setAssemblingDuration();
            calculate();
        });

        tf_price.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            setPricePerSquare();
            calculate();
        });

        tf_workerCosts.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            setWorkerCosts();
            calculate();
        });

        cb_Product.getSelectionModel().selectedItemProperty().addListener((source, oldValue, newValue) -> {
            tf_price.setText(UtilityFormat.getStringForTextField(newValue.getPriceUnit()));
        });

        if (ProjectViewController.getOpenedProject() != null) {
            load();
        }
    }

    public void persist() {
        try {
            WorthController worthController = new WorthController();
            Product product = cb_Product.getSelectionModel().getSelectedItem();
            Project project = ProjectViewController.getOpenedProject();

            ComponentController componentController = new ComponentController();
            CategoryController categoryController = new CategoryController();

            Category category = categoryController.findCategoryByShortTerm("F");
            Component component = componentController.findComponentByProjectIdAndComponentTypeAndCategoryId(
                    project.getId(),
                    "Produkt",
                    category.getId());

            if (component == null) {
                component = new Component();
            }
            if (product != null) {
                component.setDescription(product.getName());
                component.setLengthComponent(product.getLengthProduct());
                component.setWidthComponent(product.getWidthProduct());
                component.setHeightComponent(product.getHeightProduct());
                component.setProduct(product);
                component.setCategory(category);
                component.setComponentType("Produkt");
                component.setProject(project);
                component.setNumberOfProducts((int) foil.getWorth());
                component.setUnit(product.getUnit());
                component.setPriceComponent(pricePerSquare);
            }
            

            if (!ProjectViewController.isProjectOpened()) {
                abatementArea.setProject(ProjectViewController.getOpenedProject());
                assemblingCosts.setProject(ProjectViewController.getOpenedProject());
                assemblingDuration.setProject(ProjectViewController.getOpenedProject());
                productCosts.setProject(ProjectViewController.getOpenedProject());
                abatementPercent.setProject(ProjectViewController.getOpenedProject());
                foil.setProject(ProjectViewController.getOpenedProject());
                workerCosts.setProject(ProjectViewController.getOpenedProject());
                totalCosts.setProject(ProjectViewController.getOpenedProject());

                worthController.create(abatementArea);
                worthController.create(assemblingCosts);
                worthController.create(assemblingDuration);
                worthController.create(productCosts);
                worthController.create(abatementPercent);
                worthController.create(foil);
                worthController.create(workerCosts);
                worthController.create(totalCosts);
                componentController.create(component);
            } else {
                try {
                    worthController.edit(abatementArea);
                    worthController.edit(assemblingCosts);
                    worthController.edit(assemblingDuration);
                    worthController.edit(productCosts);
                    worthController.edit(abatementPercent);
                    worthController.edit(foil);
                    worthController.edit(workerCosts);
                    worthController.edit(totalCosts);
                    componentController.edit(component);
                } catch (Exception e) {
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Assembling_FormworkController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void load() {
        ProductController productController = new ProductController();

        ObservableList<Product> list = FXCollections.observableArrayList(productController.findByProductTypeOrderByName(ProductType.FOIL));
        if (list != null) {
            cb_Product.setItems(list);
        }

        Project project = ProjectViewController.getOpenedProject();
        if (project != null) {
            WorthController worthController = new WorthController();
            ComponentController componentController = new ComponentController();
            CategoryController categoryController = new CategoryController();

            Category category = categoryController.findCategoryByShortTerm("F");
            Component component = componentController.findComponentByProjectIdAndComponentTypeAndCategoryId(project.getId(),
                    "Produkt", category.getId());

            if (component != null) {
                cb_Product.getSelectionModel().select(component.getProduct());
                tf_price.setText(UtilityFormat.getStringForTextField(component.getPriceComponent()));
            }

            abatementPercent = (worthController.findWorthByShortTermAndProjectId("FUEP", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("FUEP", project.getId()) : abatementPercent;
            workerCosts = (worthController.findWorthByShortTermAndProjectId("KPF", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("KPF", project.getId()) : workerCosts;
            assemblingDuration = (worthController.findWorthByShortTermAndProjectId("ZPF", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("ZPF", project.getId()) : assemblingDuration;
            abatementArea = (worthController.findWorthByShortTermAndProjectId("FUE", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("FUE", project.getId()) : abatementArea;
            foil = (worthController.findWorthByShortTermAndProjectId("F", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("F", project.getId()) : foil;
            productCosts = (worthController.findWorthByShortTermAndProjectId("KProF", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("KProF", project.getId()) : productCosts;
            assemblingCosts = (worthController.findWorthByShortTermAndProjectId("KMF", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("KMF", project.getId()) : assemblingCosts;
            totalCosts = (worthController.findWorthByShortTermAndProjectId("GKF", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("GKF", project.getId()) : totalCosts;

            lb_RoofArea.setText(UtilityFormat.worthWithTwoDecimalPlaces(Project_ResultAreaController.getInstance().getRoofArea()) + " m²");
            lb_blend.setText(UtilityFormat.getStringForLabel(abatementArea));
            lb_foil.setText(UtilityFormat.getStringForLabel(foil));
            lb_montageCosts.setText(UtilityFormat.getStringForLabel(assemblingCosts));
            lb_productCosts.setText(UtilityFormat.getStringForLabel(productCosts));
            lb_totalCosts.setText(UtilityFormat.getStringForLabel(totalCosts));
            
            tf_blend.setText(UtilityFormat.getStringForTextField(abatementPercent));
            tf_time.setText(UtilityFormat.getStringForTextField(assemblingDuration));
            tf_workerCosts.setText(UtilityFormat.getStringForTextField(workerCosts));
        }
    }

    public void calculate() {
        //Fläche (mit Verschnitt) in m²
        //Alte Formel-ID: FUE
        abatementArea.setWorth(Project_ResultAreaController.getInstance().getLedgeAndRoofArea() * abatementPercent.getWorth() / 100);
        lb_blend.setText(UtilityFormat.getStringForLabel(abatementArea));

        //Folie
        //Alte Formel-ID: F
        foil.setWorth(Project_ResultAreaController.getInstance().getLedgeAndRoofArea() + abatementArea.getWorth());
        lb_foil.setText(UtilityFormat.getStringForLabel(foil));

        //Produktkosten
        //Alte Formel-ID: KPF
        productCosts.setWorth(pricePerSquare*foil.getWorth());
        lb_productCosts.setText(UtilityFormat.getStringForLabel(productCosts));
        
        //Montage Kosten
        //Alte Formel-ID: KMF
        assemblingCosts.setWorth(workerCosts.getWorth()*assemblingDuration.getWorth());
        lb_montageCosts.setText(UtilityFormat.getStringForLabel(assemblingCosts));
        
        //Totalcosts
        //Alte Formel-ID: GKF
        totalCosts.setWorth(productCosts.getWorth()+assemblingCosts.getWorth());
        lb_totalCosts.setText(UtilityFormat.getStringForLabel(totalCosts));
    }

    @Override
    public void update(Observable o, Object arg) {
        double ledgeAndRoofArea = Project_ResultAreaController.getInstance().getLedgeAndRoofArea();
        lb_RoofArea.setText(UtilityFormat.getStringForLabel(ledgeAndRoofArea) + " m²");
        calculate();
    }

    public static Assembling_FoilController getInstance() {
        return instance;
    }

    public void setPricePerSquare() {
        pricePerSquare = tf_price.getText().isEmpty() || !tf_price.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_price.getText().replace(',', '.'));
    }

    public void setAbatementPercent() {
        abatementPercent.setWorth(tf_blend.getText().isEmpty() || !tf_blend.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_blend.getText().replace(',', '.')));
    }

    public void setWorkerCosts() {
        workerCosts.setWorth(tf_workerCosts.getText().isEmpty() || !tf_workerCosts.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_workerCosts.getText().replace(',', '.')));
    }

    public void setAssemblingDuration() {
        assemblingDuration.setWorth(tf_time.getText().isEmpty() || !tf_time.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_time.getText().replace(',', '.')));
    }
}
