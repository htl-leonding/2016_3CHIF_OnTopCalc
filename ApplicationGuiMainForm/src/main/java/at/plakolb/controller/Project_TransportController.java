package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ParameterController;
import at.plakolb.calculationlogic.db.controller.WorthController;
import at.plakolb.calculationlogic.entity.ParameterP;
import at.plakolb.calculationlogic.entity.Project;
import at.plakolb.calculationlogic.entity.Worth;
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

    private double days;
    private double duration;
    private double distance;
    private double price;
    private double kilometerAllowance;

    /**
     * Get the value of price
     *
     * @return the value of price
     */
    private double getPrice() {
        return price;
    }

    /**
     * Set the value of price
     */
    public void setPrice() {
        this.price = tf_lkwPrice.getText().isEmpty() || !tf_lkwPrice.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_lkwPrice.getText().replace(',', '.'));
    }

    /**
     * Get the value of kilometerAllowance
     *
     * @return the value of kilometerAllowance
     */
    private double getKilometerAllowance() {
        return kilometerAllowance;
    }

    /**
     * Set the value of kilometerAllowance
     */
    public void setKilometerAllowance() {
        this.kilometerAllowance = tf_pkwMoney.getText().isEmpty() || !tf_pkwMoney.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_pkwMoney.getText().replace(',', '.'));
    }

    /**
     * Get the value of distance
     *
     * @return the value of distance
     */
    private double getDistance() {
        return distance;
    }

    /**
     * Set the value of distance
     */
    public void setDistance() {
        this.distance = tf_pkwDistance.getText().isEmpty() || !tf_pkwDistance.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_pkwDistance.getText().replace(',', '.'));
    }

    /**
     * Get the value of duration
     *
     * @return the value of duration
     */
    private double getDuration() {
        return duration;
    }

    /**
     * Set the value of duration
     */
    public void setDuration() {
        this.duration = tf_lkwDuration.getText().isEmpty() || !tf_lkwDuration.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_lkwDuration.getText().replace(',', '.'));
    }

    /**
     * Get the value of days
     *
     * @return the value of days
     */
    private double getDays() {
        return days;
    }

    /**
     * Set the value of days
     */
    public void setDays() {
        this.days = tf_pkwDays.getText().isEmpty() || !tf_pkwDays.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_pkwDays.getText().replace(',', '.'));
    }

    /**
     * Get the value of costsAbidance
     *
     * @return the value of costsAbidance
     */
    public double getCostsAbidance() {
        return getPrice() * getDuration();
    }

    /**
     * Returns the complete transport costs
     *
     * @return
     */
    public double getCompleteCosts() {
        return getCostsTransport() + getCostsAbidance();
    }

    /**
     * Get the value of costsTransport
     *
     * @return the value of costsTransport
     */
    public double getCostsTransport() {
        return getDays() * getKilometerAllowance() * getDistance() * 2;
    }

    private static boolean valuesChanged;

    public static boolean isValuesChanged() {
        return valuesChanged;
    }

    public static void setValuesChanged(boolean valuesChanged) {
        Project_TransportController.valuesChanged = valuesChanged;
    }

    private DecimalFormat decimalFormat;

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

        tf_pkwMoney.textProperty().addListener((observable, oldValue, newValue) -> {
            setKilometerAllowance();
            valueChanged();
        });
        tf_pkwDistance.textProperty().addListener((observable, oldValue, newValue) -> {
            setDistance();
            valueChanged();
        });
        tf_pkwDays.textProperty().addListener((observable, oldValue, newValue) -> {
            setDays();
            valueChanged();
        });
        tf_lkwDuration.textProperty().addListener((observable, oldValue, newValue) -> {
            setDuration();
            valueChanged();
        });
        tf_lkwPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            setPrice();
            valueChanged();
        });

        loadTransportCosts();
    }

    public static Project_TransportController getInstance() {
        return instance;
    }

    public void loadTransportCosts() {
        ParameterController parameterController = new ParameterController();
        WorthController worthController = new WorthController();

        Project op = ProjectViewController.getOpenedProject();

        System.out.println(op);
        if (op == null) {
            return;
        }
        ParameterP parameter
                = parameterController.findParameterPByShortTerm("KMG");
        Worth worthKilometerAllowance
                = worthController.findWorthByParameterIdAndProjectId(parameter.getId(), op.getId());

        parameter = parameterController.findParameterPByShortTerm("ET");
        Worth distance = worthController.findWorthByParameterIdAndProjectId(parameter.getId(), op.getId());

        parameter = parameterController.findParameterPByShortTerm("TA");
        Worth days = worthController.findWorthByParameterIdAndProjectId(parameter.getId(), op.getId());

        if (worthKilometerAllowance != null) {
            tf_pkwMoney.setText(decimalFormat.format(worthKilometerAllowance.getWorth()));
        }
        if (distance != null) {
            tf_pkwDistance.setText(decimalFormat.format(distance.getWorth()));
        }
        if (days != null) {
            tf_pkwDays.setText(decimalFormat.format(days.getWorth()));
        }

        parameter = parameterController.findParameterPByShortTerm("PLS");
        Worth pickupPrice = worthController.findWorthByParameterIdAndProjectId(parameter.getId(), op.getId());

        parameter = parameterController.findParameterPByShortTerm("DT");
        Worth pickupDuration = worthController.findWorthByParameterIdAndProjectId(parameter.getId(), op.getId());

        if (pickupPrice != null) {
            tf_lkwPrice.setText(decimalFormat.format(pickupPrice.getWorth()));
        }
        if (pickupDuration != null) {
            tf_lkwDuration.setText(decimalFormat.format(pickupDuration.getWorth()));
        }

        //Transportkosten
        parameter = parameterController.findParameterPByShortTerm("KT");
        Worth kt = worthController.findWorthByParameterIdAndProjectId(parameter.getId(), op.getId());
        //Aufenthaltskosten
        parameter = parameterController.findParameterPByShortTerm("KA");
        Worth ka = worthController.findWorthByParameterIdAndProjectId(parameter.getId(), op.getId());

        if (kt != null && ka != null) {
            lb_PriceTransport.setText(decimalFormat.format(kt.getWorth()) + " €");
            lb_PriceStay.setText(decimalFormat.format(ka.getWorth()) + " €");
            lb_PriceComplete.setText(decimalFormat.format(ka.getWorth() + kt.getWorth()) + " €");
        }
        setValuesChanged(false);
    }

    public void persist() throws Exception {
        Project project = ProjectViewController.getOpenedProject();

        ParameterController parameterController = new ParameterController();
        WorthController worthController = new WorthController();

        ParameterP parameterKilometerAllowance
                = parameterController.findParameterPByShortTerm("KMG");
        Worth worthKilometerAllowance
                = worthController.findWorthByParameterIdAndProjectId(parameterKilometerAllowance.getId(), project.getId());
        if (worthKilometerAllowance == null) {
            worthKilometerAllowance = new Worth(project, parameterKilometerAllowance, getKilometerAllowance());
            worthController.create(worthKilometerAllowance);
        } else {
            worthKilometerAllowance.setWorth(getKilometerAllowance());
            worthController.edit(worthKilometerAllowance);
        }

        ParameterP parameterDistance = parameterController.findParameterPByShortTerm("ET");
        Worth worthDistance = worthController.findWorthByParameterIdAndProjectId(parameterDistance.getId(), project.getId());
        if (worthDistance == null) {
            worthDistance = new Worth(project, parameterDistance, getDistance());
            worthController.create(worthDistance);
        } else {
            worthDistance.setWorth(getDistance());
            worthController.edit(worthDistance);
        }

        ParameterP parameterDays = parameterController.findParameterPByShortTerm("TA");
        Worth worthDays = worthController.findWorthByParameterIdAndProjectId(parameterDays.getId(), project.getId());
        if (worthDays == null) {
            worthDays = new Worth(project, parameterDays, getDays());
            worthController.create(worthDays);
        } else {
            worthDays.setWorth(getDays());
            worthController.edit(worthDays);
        }

        ParameterP parameterPricePickup = parameterController.findParameterPByShortTerm("PLS");
        Worth worthPricePickup = worthController.findWorthByParameterIdAndProjectId(parameterPricePickup.getId(), project.getId());
        if (worthPricePickup == null) {
            worthPricePickup = new Worth(project, parameterPricePickup, getPrice());
            worthController.create(worthPricePickup);
        } else {
            worthPricePickup.setWorth(getPrice());
            worthController.edit(worthPricePickup);
        }

        ParameterP parameterDuration = parameterController.findParameterPByShortTerm("DT");
        Worth worthDuration = worthController.findWorthByParameterIdAndProjectId(parameterDuration.getId(), project.getId());
        if (worthDuration == null) {
            worthDuration = new Worth(project, parameterDuration, getDuration());
            worthController.create(worthDuration);
        } else {
            worthDuration.setWorth(getDuration());
            worthController.edit(worthDuration);
        }

        //Transportkosten
        ParameterP parameter = parameterController.findParameterPByShortTerm("KT");
        Worth worthCosts = worthController.findWorthByParameterIdAndProjectId(parameter.getId(), project.getId());
        if (worthCosts == null) {
            worthCosts = new Worth(project, parameter, getCostsTransport());
            worthController.create(worthCosts);
        } else {
            worthCosts.setWorth(getCostsTransport());
            worthController.edit(worthCosts);
        }
        //Aufenthaltskosten
        parameter = parameterController.findParameterPByShortTerm("KA");
        worthCosts = worthController.findWorthByParameterIdAndProjectId(parameter.getId(), project.getId());
        if (worthCosts == null) {
            worthCosts = new Worth(project, parameter, getCostsAbidance());
            worthController.create(worthCosts);
        } else {
            worthCosts.setWorth(getCostsAbidance());
            worthController.edit(worthCosts);
        }
        //Gesamtkosten
        parameter = parameterController.findParameterPByShortTerm("GPT");
        worthCosts = worthController.findWorthByParameterIdAndProjectId(parameter.getId(), project.getId());
        if (worthCosts == null) {
            worthCosts = new Worth(project, parameter, getCompleteCosts());
            worthController.create(worthCosts);
        } else {
            worthCosts.setWorth(getCompleteCosts());
            worthController.edit(worthCosts);
        }
        setValuesChanged(false);
    }

    private void valueChanged() {
        try {
            lb_PriceTransport.setText(decimalFormat.format(getCostsTransport()) + " €");

            lb_PriceStay.setText(decimalFormat.format(getCostsAbidance()) + " €");

            lb_PriceComplete.setText(decimalFormat.format(getCompleteCosts()) + " €");
            setValuesChanged(true);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
        }
    }
}
