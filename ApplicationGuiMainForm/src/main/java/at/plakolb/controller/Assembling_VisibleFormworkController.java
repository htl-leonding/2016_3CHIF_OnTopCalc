package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ParameterController;
import at.plakolb.calculationlogic.db.controller.ProductController;
import at.plakolb.calculationlogic.db.controller.WorthController;
import at.plakolb.calculationlogic.eunmeration.ProductType;
import at.plakolb.calculationlogic.entity.Product;
import at.plakolb.calculationlogic.entity.Project;
import at.plakolb.calculationlogic.entity.Worth;
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

    private Worth pricePerSquare;
    private Worth abatementPercent;
    private Worth workerCosts;
    private Worth assemblingDuration;
    private Worth abatementArea;
    private Worth visibleFormwork;
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
        cb_Product.setItems(FXCollections.observableArrayList(new ProductController().findByProductTypeOrderByName(ProductType.FORMWORK)));

        ParameterController parameterController = new ParameterController();
        abatementPercent = new Worth(parameterController.findParameterPByShortTerm("VSSP"));
        abatementArea = new Worth(parameterController.findParameterPByShortTerm("VSS"));
        visibleFormwork = new Worth(parameterController.findParameterPByShortTerm("SS"));
        productCosts = new Worth(parameterController.findParameterPByShortTerm("SSPROK"));
        pricePerSquare = new Worth(parameterController.findParameterPByShortTerm("KPSS"));
        assemblingDuration = new Worth(parameterController.findParameterPByShortTerm("ZPSS"));
        assemblingCosts = new Worth(parameterController.findParameterPByShortTerm("KMSS"));
        totalCosts = new Worth(parameterController.findParameterPByShortTerm("GKSS"));
        //workerCosts = new Worth(parameterController.findParameterPByShortTerm(""));
        workerCosts = new Worth();

        tf_AbatementPercent.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            setAbatementPercent();
            calculateFormwork();
        });

        tf_AssemblingDuration.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            setAssemblingDuration();
            calculateFormwork();
        });

        tf_PricePerSquare.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            setPricePerSquare();
            calculateFormwork();
        });

        tf_WorkerCosts.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            setWorkerCosts();
            calculateFormwork();
        });

        if (ProjectViewController.getOpenedProject() != null) {
            loadValuesFromDataBase();
        }
    }

    public static Assembling_VisibleFormworkController getInstance() {
        return instance;
    }

    public void setPricePerSquare() {
        pricePerSquare.setWorth(tf_PricePerSquare.getText().isEmpty() || !tf_PricePerSquare.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_PricePerSquare.getText().replace(',', '.')));
    }

    public void setAbatementPercent() {
        abatementPercent.setWorth(tf_AbatementPercent.getText().isEmpty() || !tf_AbatementPercent.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_AbatementPercent.getText().replace(',', '.')));
    }

    public void setWorkerCosts() {
        workerCosts.setWorth(tf_WorkerCosts.getText().isEmpty() || !tf_WorkerCosts.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_WorkerCosts.getText().replace(',', '.')));
    }

    public void setAssemblingDuration() {
        assemblingDuration.setWorth(tf_AssemblingDuration.getText().isEmpty() || !tf_AssemblingDuration.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_AssemblingDuration.getText().replace(',', '.')));
    }

    private void loadValuesFromDataBase() {
        ParameterController parameterController = new ParameterController();
        WorthController worthController = new WorthController();
        Project openedProject = ProjectViewController.getOpenedProject();

        abatementPercent = (worthController.findWorthByShortTermAndProjectId("VSSP", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("VSSP", openedProject.getId()) : abatementPercent;
        abatementArea = (worthController.findWorthByShortTermAndProjectId("VSS", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("VSS", openedProject.getId()) : abatementArea;
        visibleFormwork = (worthController.findWorthByShortTermAndProjectId("SS", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("SS", openedProject.getId()) : visibleFormwork;
        productCosts = (worthController.findWorthByShortTermAndProjectId("SSPROK", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("SSPROK", openedProject.getId()) : productCosts;
        pricePerSquare = (worthController.findWorthByShortTermAndProjectId("KPSS", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("KPSS", openedProject.getId()) : pricePerSquare;
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
    }

    public void persistVisibleFormwork() {
        WorthController worthController = new WorthController();

        if (!ProjectViewController.isProjectOpened()) {
            abatementPercent.setProject(ProjectViewController.getOpenedProject());
            abatementArea.setProject(ProjectViewController.getOpenedProject());
            visibleFormwork.setProject(ProjectViewController.getOpenedProject());
            productCosts.setProject(ProjectViewController.getOpenedProject());
            pricePerSquare.setProject(ProjectViewController.getOpenedProject());
            assemblingDuration.setProject(ProjectViewController.getOpenedProject());
            assemblingCosts.setProject(ProjectViewController.getOpenedProject());
            totalCosts.setProject(ProjectViewController.getOpenedProject());
            workerCosts.setProject(ProjectViewController.getOpenedProject());

            worthController.create(pricePerSquare);
            worthController.create(abatementPercent);
            worthController.create(workerCosts);
            worthController.create(assemblingDuration);
            worthController.create(abatementArea);
            worthController.create(visibleFormwork);
            worthController.create(productCosts);
            worthController.create(assemblingCosts);
            worthController.create(totalCosts);
        } else {
            try {
                worthController.edit(pricePerSquare);
                worthController.edit(abatementPercent);
                worthController.edit(workerCosts);
                worthController.edit(assemblingDuration);
                worthController.edit(abatementArea);
                worthController.edit(visibleFormwork);
                worthController.edit(productCosts);
                worthController.edit(assemblingCosts);
                worthController.edit(totalCosts);
            } catch (Exception e) {
            }
        }
    }

    private void calculateFormwork() {

    }

    @Override
    public void update(Observable o, Object arg) {
        double roofArea = Project_ResultAreaController.getInstance().getRoofArea();
        lb_RoofArea.setText(UtilityFormat.getStringForLabel(roofArea) + " m²");
    }
}
