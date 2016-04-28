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
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
public class Assembling_SealingBandController implements Initializable, Observer {

    private static Assembling_SealingBandController instance;
    @FXML
    private TextField tf_blend;
    @FXML
    private ComboBox<Product> cb_product;
    @FXML
    private TextField tf_priceLinearMeter;
    @FXML
    private Label lb_blend;
    @FXML
    private Label lb_sealingBand;
    @FXML
    private TextField tf_duration;
    @FXML
    private TextField tf_price;
    @FXML
    private Label lb_productCosts;
    @FXML
    private Label lb_montageCosts;
    @FXML
    private Label lb_totalCosts;
    @FXML
    private TableView<Component> tv_rafter;
    @FXML
    private TableColumn<Component, String> cl_name;
    @FXML
    private TableColumn<Component, String> cl_length;

    private double price;

    private Worth blend;

    private Worth workerCosts;
    private Worth duration;

    private Worth waste;
    private Worth sealingBand;

    private Worth productCosts;
    private Worth montageCosts;
    private Worth totalCosts;

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
        cb_product.setItems(FXCollections.observableArrayList(new ProductController().findByProductTypeOrderByName(ProductType.SEALINGBAND)));
        Project_ConstructionMaterialListController.getInstance().addObserver(this);

        ParameterController parameterController = new ParameterController();
        blend = new Worth(parameterController.findParameterPByShortTerm("VDP"));
        workerCosts = new Worth(parameterController.findParameterPByShortTerm("KPD"));
        duration = new Worth(parameterController.findParameterPByShortTerm("ZPD"));
        waste = new Worth(parameterController.findParameterPByShortTerm("DP"));
        sealingBand = new Worth(parameterController.findParameterPByShortTerm("ND"));
        productCosts = new Worth(parameterController.findParameterPByShortTerm("KProD"));
        montageCosts = new Worth(parameterController.findParameterPByShortTerm("KMonD"));
        totalCosts = new Worth(parameterController.findParameterPByShortTerm("GKND"));

        tf_blend.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            setBlend();
            calculate();
            ModifyController.getInstance().setAssembling_sealingBand(Boolean.TRUE);
        });

        tf_duration.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            setDuration();
            calculate();
            ModifyController.getInstance().setAssembling_sealingBand(Boolean.TRUE);
        });

        tf_priceLinearMeter.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            setPricePerLinearMeter();
            calculate();
            ModifyController.getInstance().setAssembling_sealingBand(Boolean.TRUE);
        });

        tf_price.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            setWorkCosts();
            calculate();
            ModifyController.getInstance().setAssembling_sealingBand(Boolean.TRUE);
        });

        cb_product.getSelectionModel().selectedItemProperty().addListener((source, oldValue, newValue) -> {
            tf_priceLinearMeter.setText(UtilityFormat.getStringForTextField(newValue.getPriceUnit()));
            calculate();
            ModifyController.getInstance().setAssembling_sealingBand(Boolean.TRUE);
        });

        cl_name.setCellValueFactory((TableColumn.CellDataFeatures<Component, String> param) -> {
            return new ReadOnlyObjectWrapper<>(param.getValue().getDescription());
        });
        cl_length.setCellValueFactory((TableColumn.CellDataFeatures<Component, String> param) -> {
            return new ReadOnlyObjectWrapper<>(String.valueOf(UtilityFormat.getStringForLabel(param.getValue().getLengthComponent())) + " m");
        });

        if (ProjectViewController.getOpenedProject() != null) {
            load();
        } else {
            component = new Component();
        }
    }

    public static Assembling_SealingBandController getInstance() {
        return instance;
    }

    private void setBlend() {
        blend.setWorth(tf_blend.getText().isEmpty() || !tf_blend.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_blend.getText().replace(',', '.')));
    }

    private void setDuration() {
        duration.setWorth(tf_duration.getText().isEmpty() || !tf_duration.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_duration.getText().replace(',', '.')));
    }

    private void setPricePerLinearMeter() {
        price = (tf_priceLinearMeter.getText().isEmpty() || !tf_priceLinearMeter.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_priceLinearMeter.getText().replace(',', '.')));
    }

    private void setWorkCosts() {
        workerCosts.setWorth(tf_price.getText().isEmpty() || !tf_price.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_price.getText().replace(',', '.')));
    }
    
    public Component getComponent(){
        return component;
    }
    
    public Worth getMaterial(){
        return productCosts;
    }
    
    public Worth getWage(){
        return montageCosts;
    }
    
    public Worth getTotalCosts(){
        return totalCosts;                
    }

    public void load() {

        Project project = ProjectViewController.getOpenedProject();
        if (project != null) {
            WorthController worthController = new WorthController();
            ComponentController componentController = new ComponentController();
            CategoryController categoryController = new CategoryController();
            Category category = categoryController.findCategoryByShortTerm("ND");

            component = componentController.findComponentByProjectIdAndComponentTypeAndCategoryId(project.getId(),
                    "Produkt", category.getId());

            if (component != null) {
                cb_product.getSelectionModel().select(component.getProduct());
                //Bin mir nicht sicher ob das so richtig ist (Keppi) TODO
                //tf_price.setText(UtilityFormat.getStringForLabel(component.getPriceComponent()));
            }
            else{
                component = new Component();
                component.setCategory(category);
                component.setComponentType("Produkt");
            }

            blend = (worthController.findWorthByShortTermAndProjectId("VDP", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("VDP", project.getId()) : blend;
            workerCosts = (worthController.findWorthByShortTermAndProjectId("KPD", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("KPD", project.getId()) : workerCosts;
            duration = (worthController.findWorthByShortTermAndProjectId("ZPD", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("ZPD", project.getId()) : duration;
            waste = (worthController.findWorthByShortTermAndProjectId("DP", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("DP", project.getId()) : waste;
            sealingBand = (worthController.findWorthByShortTermAndProjectId("ND", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("ND", project.getId()) : sealingBand;
            productCosts = (worthController.findWorthByShortTermAndProjectId("KProD", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("KProD", project.getId()) : productCosts;

            montageCosts = (worthController.findWorthByShortTermAndProjectId("KMonD", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("KMonD", project.getId()) : montageCosts;
            totalCosts = (worthController.findWorthByShortTermAndProjectId("GKND", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("GKND", project.getId()) : totalCosts;

            tf_blend.setText(UtilityFormat.getStringForTextField(blend));
            tf_price.setText(UtilityFormat.getStringForTextField(workerCosts));
            tf_duration.setText(UtilityFormat.getStringForTextField(duration));

            lb_blend.setText(UtilityFormat.getStringForLabel(waste));
            lb_sealingBand.setText(UtilityFormat.getStringForLabel(sealingBand));
            lb_productCosts.setText(UtilityFormat.getStringForLabel(productCosts));
            lb_montageCosts.setText(UtilityFormat.getStringForLabel(montageCosts));
            lb_totalCosts.setText(UtilityFormat.getStringForLabel(totalCosts));

            tv_rafter.setItems(FXCollections.observableArrayList(Project_ConstructionMaterialListController.getInstance().getRafterList()));
            
            ModifyController.getInstance().setAssembling_sealingBand(Boolean.FALSE);
        }
    }

        public void calculate() {
        //Verschnitt Nageldichtband
        //Alte Formel-ID: VD
        waste.setWorth(Project_ConstructionMaterialListController.getInstance().getTotalRafterLength() * blend.getWorth() / 100);
        lb_blend.setText(UtilityFormat.getStringForLabel(waste));
        //Nageldichtband
        //Alte Formel-ID: ND
        sealingBand.setWorth(Project_ConstructionMaterialListController.getInstance().getTotalRafterLength() + waste.getWorth());
        lb_sealingBand.setText(UtilityFormat.getStringForLabel(sealingBand));
        //Produktkosten
        //Alte Formel-ID: KPND
        productCosts.setWorth(price * sealingBand.getWorth());
        lb_productCosts.setText(UtilityFormat.getStringForLabel(productCosts));
        //Montagekosten
        //Alte Formel-ID: KMND
        montageCosts.setWorth(workerCosts.getWorth() * duration.getWorth());
        lb_montageCosts.setText(UtilityFormat.getStringForLabel(montageCosts));
        //Totalcosts
        //Alte Formel-ID: GKND
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
            component.setDescription("Nageldichtband");
            component.setLengthComponent(null);
            component.setWidthComponent(null);
            component.setHeightComponent(null);
            component.setProduct(null);
            component.setUnit(null);
        }
        component.setNumberOfProducts(sealingBand.getWorth());
        component.setPriceComponent(price);
    }
    
    public void persist() {
        WorthController worthController = new WorthController();
        ComponentController componentController = new ComponentController();

        if (!ProjectViewController.isProjectOpened()) {
            blend.setProject(ProjectViewController.getOpenedProject());
            waste.setProject(ProjectViewController.getOpenedProject());
            sealingBand.setProject(ProjectViewController.getOpenedProject());
            duration.setProject(ProjectViewController.getOpenedProject());
            productCosts.setProject(ProjectViewController.getOpenedProject());
            montageCosts.setProject(ProjectViewController.getOpenedProject());
            totalCosts.setProject(ProjectViewController.getOpenedProject());
            workerCosts.setProject(ProjectViewController.getOpenedProject());
            component.setProject(ProjectViewController.getOpenedProject());

            worthController.create(blend);
            worthController.create(workerCosts);
            worthController.create(waste);
            worthController.create(sealingBand);
            worthController.create(duration);
            worthController.create(montageCosts);
            worthController.create(productCosts);
            worthController.create(totalCosts);
            componentController.create(component);
        } else {
            try {
                worthController.edit(blend);
                worthController.edit(workerCosts);
                worthController.edit(waste);
                worthController.edit(sealingBand);
                worthController.edit(duration);
                worthController.edit(montageCosts);
                worthController.edit(productCosts);
                worthController.edit(totalCosts);
                componentController.edit(component);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        tv_rafter.setItems(FXCollections.observableArrayList(Project_ConstructionMaterialListController.getInstance().getRafterList()));
        calculate();
    }

}
