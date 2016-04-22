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
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
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
    private ChoiceBox<Product> cb_counterBattern;
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

    private Component component;
    private Worth lengthWaste;
    private Worth waste;//%
    private double pricePerMeter;
    private Worth counterBattern;
    private Worth montageCost;
    private Worth productCost;
    private Worth timeMontage;
    private Worth totalCost;
    private Worth profiHour;

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

        cb_counterBattern.getSelectionModel().selectedItemProperty().addListener((source, oldValue, newValue) -> {
            pricePerMeter = newValue.getPriceUnit();
            tf_pricePerMeter.setText(UtilityFormat.getStringForTextField(newValue.getPriceUnit()));
            calculate();
        });
        tf_pricePerMeter.textProperty().addListener((observable, oldValue, newValue) -> {
            setPrice();
            calculate();
        });
        tf_profiHour.textProperty().addListener((observable, oldValue, newValue) -> {
            setHour();
            calculate();
        });
        tf_waste.textProperty().addListener((observable, oldValue, newValue) -> {
            setWaste();
            calculate();
        });
        tf_timeMontage.textProperty().addListener((observable, oldValue, newValue) -> {
            setTime();
            calculate();
        });

        cl_name.setCellValueFactory((TableColumn.CellDataFeatures<Component, String> param) -> {
            return new ReadOnlyObjectWrapper<>(param.getValue().getDescription());
        });

        cl_dachsparrenLength.setCellValueFactory((TableColumn.CellDataFeatures<Component, String> param) -> {
            return new ReadOnlyObjectWrapper<>(String.valueOf(UtilityFormat.getStringForLabel(param.getValue().getLengthComponent())) + " m");
        });

        if (ProjectViewController.getOpenedProject() != null) {
            loadFromDb();
        }
    }

    public void persist() {
        WorthController worthController = new WorthController();

        Category category = new CategoryController().findCategoryByShortTerm("KL");//
        component = new ComponentController().findComponent(ProjectViewController.getOpenedProject().getId());

        if (component == null) {
            component = new Component();
        }
        try {
            Product product = new ProductController().findProduct(cb_counterBattern.getSelectionModel().getSelectedItem().getId());

            component.setDescription(product.getName());
            component.setCategory(category);
            component.setComponentType("Produkt");
            component.setPriceComponent(tf_pricePerMeter.getText().isEmpty() || !tf_pricePerMeter.getText().matches("[0-9]*.[0-9]*")
                    ? null : Double.valueOf(tf_pricePerMeter.getText()));
            component.setNumberOfProducts(counterBattern.getWorth());
            component.setUnit(product.getUnit());
            component.setProduct(product);
            component.setComponentType(ProductType.COUNTERBATTEN.toString());
            component.setProject(ProjectViewController.getOpenedProject());
            new ComponentController().edit(component);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

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

            worthController.create(profiHour);
            worthController.create(counterBattern);
            worthController.create(timeMontage);
            worthController.create(lengthWaste);
            worthController.create(productCost);
            worthController.create(montageCost);
            worthController.create(waste);
            worthController.create(totalCost);
        } else {
            try {
                worthController.edit(profiHour);
                worthController.edit(counterBattern);
                worthController.edit(timeMontage);
                worthController.edit(lengthWaste);
                worthController.edit(productCost);
                worthController.edit(montageCost);
                worthController.edit(waste);
                worthController.edit(totalCost);
            } catch (Exception e) {
            }
        }
    }

    public void loadFromDb() {
        ParameterController parameterController = new ParameterController();
        WorthController worthController = new WorthController();
        Project openedProject = ProjectViewController.getOpenedProject();

        timeMontage = (worthController.findWorthByShortTermAndProjectId("ZPKL", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("ZPKL", openedProject.getId()) : timeMontage;
        profiHour = (worthController.findWorthByShortTermAndProjectId("KPKL", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("KPKL", openedProject.getId()) : profiHour;
        counterBattern = (worthController.findWorthByShortTermAndProjectId("KL", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("KL", openedProject.getId()) : counterBattern;
        lengthWaste = (worthController.findWorthByShortTermAndProjectId("VKL", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("VKL", openedProject.getId()) : lengthWaste;
        totalCost = (worthController.findWorthByShortTermAndProjectId("GKKL", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("GKKL", openedProject.getId()) : totalCost;
        waste = (worthController.findWorthByShortTermAndProjectId("VKLP", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("VKLP", openedProject.getId()) : waste;
        productCost = (worthController.findWorthByShortTermAndProjectId("KProKL", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("KProKL", openedProject.getId()) : productCost;
        montageCost = (worthController.findWorthByShortTermAndProjectId("KMonKL", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("KMonKL", openedProject.getId()) : montageCost;
        Category category = new CategoryController().findCategoryByShortTerm("KL");

        component = new ComponentController().findComponent(ProjectViewController.getOpenedProject().getId());
        if (component != null) {
            cb_counterBattern.getSelectionModel().select(component.getProduct());
        }
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

    }

    public void calculate() {
        double sum = 0;
        sum = Project_ConstructionMaterialListController.getInstance().getTotalRafterLength();
        //Verschnitt in m
        lengthWaste.setWorth(sum * (waste.getWorth() / 100));

        lb_lengthWaste.setText(UtilityFormat.getStringForTextField(lengthWaste) + " m");

        //Konterlattung in m
        counterBattern.setWorth(sum + lengthWaste.getWorth());
        lb_counterBattern.setText(UtilityFormat.getStringForTextField(counterBattern) + " m");
        //Kosten Konterlattung
        productCost.setWorth(pricePerMeter * counterBattern.getWorth());
        lb_productCost.setText(UtilityFormat.getStringForTextField(productCost) + " €");

        //Kosten Montage
        montageCost.setWorth(profiHour.getWorth() * timeMontage.getWorth());
        lb_montageCost.setText(UtilityFormat.getStringForTextField(montageCost) + " €");

        //Kosten Gesamt
        totalCost.setWorth(montageCost.getWorth() + productCost.getWorth());
        lb_totalCost.setText(UtilityFormat.getStringForTextField(totalCost) + " €");

    }

    public static Assembling_CounterBattenController getInstance() {
        return instance;
    }

    public void setHour() {
        profiHour.setWorth(tf_profiHour.getText().isEmpty() || !tf_profiHour.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_profiHour.getText().replace(',', '.')));
    }

    public void setPrice() {
        pricePerMeter = (tf_pricePerMeter.getText().isEmpty() || !tf_pricePerMeter.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_pricePerMeter.getText().replace(',', '.')));
    }

    public void setWaste() {
        waste.setWorth(tf_waste.getText().isEmpty() || !tf_waste.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_waste.getText().replace(',', '.')));
    }

    public void setTime() {
        timeMontage.setWorth(tf_timeMontage.getText().isEmpty() || !tf_timeMontage.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_timeMontage.getText().replace(',', '.')));
    }

    @Override
    public void update(Observable o, Object arg) {
        tv_dachsparren.setItems(FXCollections.observableList(Project_ConstructionMaterialListController.getInstance().getRafterList()));
        calculate();
    }

}
