/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ParameterController;
import at.plakolb.calculationlogic.db.controller.WorthController;
import at.plakolb.calculationlogic.entity.Project;
import at.plakolb.calculationlogic.entity.Worth;
import at.plakolb.calculationlogic.util.UtilityFormat;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class Project_TransportController extends java.util.Observable implements Initializable {

    private static Project_TransportController instance;

    @FXML
    private TextField tf_pkwDays;
    @FXML
    private TextField tf_pkwMoney;
    @FXML
    private TextField tf_pkwDistance;
    @FXML
    private TextField tf_lkwPrice;
    @FXML
    private TextField tf_lkwDuration;
    @FXML
    private Label lb_PriceTransport;
    @FXML
    private Label lb_PriceStay;
    @FXML
    private Label lb_PriceComplete;

    private DecimalFormat decimalFormat;

    private Worth kilometerAllowance;
    private Worth distance;
    private Worth days;
    private Worth pricePerHour;
    private Worth duration;
    private Worth transportCosts;
    private Worth abidanceCosts;
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
        decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));

        ParameterController parameterController = new ParameterController();
        kilometerAllowance = new Worth(parameterController.findParameterPByShortTerm("KMG"));
        distance = new Worth(parameterController.findParameterPByShortTerm("ET"));
        days = new Worth(parameterController.findParameterPByShortTerm("TA"));
        pricePerHour = new Worth(parameterController.findParameterPByShortTerm("PLS"));
        duration = new Worth(parameterController.findParameterPByShortTerm("DT"));
        transportCosts = new Worth(parameterController.findParameterPByShortTerm("KT"));
        abidanceCosts = new Worth(parameterController.findParameterPByShortTerm("KA"));
        totalCosts = new Worth(parameterController.findParameterPByShortTerm("GPT"));

        tf_pkwMoney.textProperty().addListener((observable, oldValue, newValue) -> {
            setKilometerAllowance();
            calcTransportCosts();
        });
        tf_pkwDistance.textProperty().addListener((observable, oldValue, newValue) -> {
            setDistance();
            calcTransportCosts();
        });
        tf_pkwDays.textProperty().addListener((observable, oldValue, newValue) -> {
            setDays();
            calcTransportCosts();
        });
        tf_lkwDuration.textProperty().addListener((observable, oldValue, newValue) -> {
            setDuration();
            calcTransportCosts();
        });
        tf_lkwPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            setPrice();
            calcTransportCosts();
        });

        if (ProjectViewController.getOpenedProject() != null) {
            loadTransportCosts();
        }

    }

    public static Project_TransportController getInstance() {
        return instance;
    }

    public static void setValuesChanged(boolean valuesChanged) {
        ModifyController.getInstance().setProject_transport(valuesChanged);
    }

    public void setPrice() {
        pricePerHour.setWorth(tf_lkwPrice.getText().isEmpty() || !tf_lkwPrice.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_lkwPrice.getText().replace(',', '.')));
    }

    public void setKilometerAllowance() {
        kilometerAllowance.setWorth(tf_pkwMoney.getText().isEmpty() || !tf_pkwMoney.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_pkwMoney.getText().replace(',', '.')));
    }

    public void setDistance() {
        distance.setWorth(tf_pkwDistance.getText().isEmpty() || !tf_pkwDistance.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_pkwDistance.getText().replace(',', '.')));
    }

    public void setDuration() {
        duration.setWorth(tf_lkwDuration.getText().isEmpty() || !tf_lkwDuration.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_lkwDuration.getText().replace(',', '.')));
    }

    public void setDays() {
        days.setWorth(tf_pkwDays.getText().isEmpty() || !tf_pkwDays.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_pkwDays.getText().replace(',', '.')));
    }
    
    public Worth getTotalCosts(){
        return totalCosts;
    }

    public void loadTransportCosts() {
        WorthController worthController = new WorthController();
        Project openedProject = ProjectViewController.getOpenedProject();

        kilometerAllowance = (worthController.findWorthByShortTermAndProjectId("KMG", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("KMG", openedProject.getId()) : kilometerAllowance;
        distance = (worthController.findWorthByShortTermAndProjectId("ET", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("ET", openedProject.getId()) : distance;
        days = (worthController.findWorthByShortTermAndProjectId("TA", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("TA", openedProject.getId()) : days;
        pricePerHour = (worthController.findWorthByShortTermAndProjectId("PLS", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("PLS", openedProject.getId()) : pricePerHour;
        duration = (worthController.findWorthByShortTermAndProjectId("DT", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("DT", openedProject.getId()) : duration;
        transportCosts = (worthController.findWorthByShortTermAndProjectId("KT", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("KT", openedProject.getId()) : transportCosts;
        abidanceCosts = (worthController.findWorthByShortTermAndProjectId("KA", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("KA", openedProject.getId()) : abidanceCosts;
        totalCosts = (worthController.findWorthByShortTermAndProjectId("GPT", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("GPT", openedProject.getId()) : totalCosts;

        tf_pkwMoney.setText(UtilityFormat.getStringForTextField(kilometerAllowance));
        tf_pkwDistance.setText(UtilityFormat.getStringForTextField(distance));
        tf_pkwDays.setText(UtilityFormat.getStringForTextField(days));
        tf_lkwPrice.setText(UtilityFormat.getStringForTextField(pricePerHour));
        tf_lkwDuration.setText(UtilityFormat.getStringForTextField(duration));
        lb_PriceTransport.setText(UtilityFormat.getStringForLabel(transportCosts));
        lb_PriceStay.setText(UtilityFormat.getStringForLabel(abidanceCosts));
        lb_PriceComplete.setText(UtilityFormat.getStringForLabel(totalCosts));

        setValuesChanged(false);
    }

    public void persist() {
        WorthController worthController = new WorthController();

        if (!ProjectViewController.isProjectOpened()) {
            kilometerAllowance.setProject(ProjectViewController.getOpenedProject());
            distance.setProject(ProjectViewController.getOpenedProject());
            days.setProject(ProjectViewController.getOpenedProject());
            pricePerHour.setProject(ProjectViewController.getOpenedProject());
            duration.setProject(ProjectViewController.getOpenedProject());
            transportCosts.setProject(ProjectViewController.getOpenedProject());
            abidanceCosts.setProject(ProjectViewController.getOpenedProject());
            totalCosts.setProject(ProjectViewController.getOpenedProject());

            worthController.create(kilometerAllowance);
            worthController.create(distance);
            worthController.create(days);
            worthController.create(pricePerHour);
            worthController.create(duration);
            worthController.create(transportCosts);
            worthController.create(abidanceCosts);
            worthController.create(totalCosts);
        } else {
            try {
                worthController.edit(kilometerAllowance);
                worthController.edit(distance);
                worthController.edit(days);
                worthController.edit(pricePerHour);
                worthController.edit(duration);
                worthController.edit(transportCosts);
                worthController.edit(abidanceCosts);
                worthController.edit(totalCosts);
            } catch (Exception e) {
            }
        }

        setValuesChanged(false);
    }

    private void calcTransportCosts() {

        transportCosts.setWorth(days.getWorth() * kilometerAllowance.getWorth() * distance.getWorth() * 2);
        abidanceCosts.setWorth(pricePerHour.getWorth() * duration.getWorth());
        totalCosts.setWorth(transportCosts.getWorth() + abidanceCosts.getWorth());

        try {
            lb_PriceTransport.setText(decimalFormat.format(transportCosts.getWorth()) + " €");
            lb_PriceStay.setText(decimalFormat.format(abidanceCosts.getWorth()) + " €");
            lb_PriceComplete.setText(decimalFormat.format(totalCosts.getWorth()) + " €");
            setValuesChanged(true);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
        }
    }
}