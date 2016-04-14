package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ParameterController;
import at.plakolb.calculationlogic.db.controller.ProductController;
import at.plakolb.calculationlogic.entity.Product;
import at.plakolb.calculationlogic.entity.Worth;
import at.plakolb.calculationlogic.eunmeration.ProductType;
import at.plakolb.calculationlogic.util.UtilityFormat;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
        
        cb_Product.setItems(FXCollections.observableArrayList(new ProductController().findByProductTypeOrderByName(ProductType.FOIL)));

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

    
    public void persist(){
        
    }
    public void load() {
        
    }
    public void calculate(){
        
    }
    
    @Override
    public void update(Observable o, Object arg) {
        double ledgeAndRoofArea = Project_ResultAreaController.getInstance().getLedgeAndRoofArea();
        lb_RoofArea.setText(UtilityFormat.getStringForLabel(ledgeAndRoofArea) + " m²");
    }
    
    public static Assembling_FoilController getInstance(){
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
