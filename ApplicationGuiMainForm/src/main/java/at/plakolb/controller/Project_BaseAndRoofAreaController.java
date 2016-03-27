/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ParameterController;
import at.plakolb.calculationlogic.db.controller.WorthController;
import at.plakolb.calculationlogic.entity.Worth;
import at.plakolb.calculationlogic.math.utils.MathUtils;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class Project_BaseAndRoofAreaController implements Initializable, Observer {

    @FXML
    private TextField tf_Width;
    @FXML
    private TextField tf_Length;
    @FXML
    private TextField tf_Angle;
    @FXML
    private Label lb_BaseArea;
    @FXML
    private Label lb_RoofArea;
    @FXML
    private TextField tf_Ridge;
    @FXML
    private TextField tf_Eaves;
    @FXML
    private Label lb_Ledge;
    @FXML
    private TextField tf_GableRight;
    @FXML
    private TextField tf_GableLeft;
    @FXML
    private Label lb_LedgeAndRoofArea;
    @FXML
    private Label lb_TotalBaseArea;
    @FXML
    private Label lb_TotalRoofArea;
    @FXML
    private Label lb_TotalLedgeAndRoofArea;
    @FXML
    private Label lb_TotalLegde;

    private Worth length;
    private Worth width;
    private Worth baseArea;
    private Worth angle;
    private Worth roofArea;
    private Worth eaves;
    private Worth ridge;
    private Worth gableRight;
    private Worth gableLeft;
    private Worth ledge;
    private Worth ledgeAndRoofArea;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ParameterController parameterController = new ParameterController();
        length = new Worth(parameterController.findParameterPByShortTerm("l"));
        width = new Worth(parameterController.findParameterPByShortTerm("b"));
        baseArea = new Worth(parameterController.findParameterPByShortTerm("A"));
        angle = new Worth(parameterController.findParameterPByShortTerm("N"));
        roofArea = new Worth(parameterController.findParameterPByShortTerm("D"));
        eaves = new Worth(parameterController.findParameterPByShortTerm("dv"));
        ridge = new Worth(parameterController.findParameterPByShortTerm("dh"));
        gableRight = new Worth(parameterController.findParameterPByShortTerm("dr"));
        gableLeft = new Worth(parameterController.findParameterPByShortTerm("dl"));
        ledge = new Worth(parameterController.findParameterPByShortTerm("DV"));
        ledgeAndRoofArea = new Worth(parameterController.findParameterPByShortTerm("DF"));
    }

    public double getBaseAreaValue() {
        if (baseArea != null) {
            return baseArea.getWorth();
        } else {
            return 0;
        }
    }

    public double getRoofAreaValue() {
        if (roofArea != null) {
            return roofArea.getWorth();
        } else {
            return 0;
        }
    }

    public double getLedgeValue() {
        if (ledge != null) {
            return ledge.getWorth();
        } else {
            return 0;
        }
    }

    public double getLedgeAndRoofAreaValue() {
        if (ledgeAndRoofArea != null) {
            return ledgeAndRoofArea.getWorth();
        } else {
            return 0;
        }
    }

    public Worth getLength() {
        return length;
    }

    public Worth getWidth() {
        return width;
    }

    public Worth getBaseArea() {
        return baseArea;
    }

    public Worth getAngle() {
        return angle;
    }

    public Worth getEaves() {
        return eaves;
    }

    public Worth getRidge() {
        return ridge;
    }

    public Worth getGableRight() {
        return gableRight;
    }

    public Worth getGableLeft() {
        return gableLeft;
    }

    public Worth getLedge() {
        return ledge;
    }

    public Worth getLedgeAndRoofArea() {
        return ledgeAndRoofArea;
    }

    /**
     * Calcuates all roof areas.
     *
     * @param event
     */
    @FXML
    private void calcArea(ActionEvent event) {
        length.setWorth(parseDouble(tf_Length.getText()));
        width.setWorth(parseDouble(tf_Width.getText()));
        angle.setWorth(parseDouble(tf_Angle.getText()));
        ridge.setWorth(parseDouble(tf_Ridge.getText()));
        eaves.setWorth(parseDouble(tf_Eaves.getText()));
        gableRight.setWorth(parseDouble(tf_GableRight.getText()));
        gableLeft.setWorth(parseDouble(tf_GableLeft.getText()));

        baseArea.setWorth(length.getWorth() * width.getWorth());
        roofArea.setWorth(baseArea.getWorth() / Math.cos(angle.getWorth() * Math.PI / 180));
        ledgeAndRoofArea.setWorth(((length.getWorth() + gableLeft.getWorth() + gableRight.getWorth()) * (width.getWorth() + ridge.getWorth() + eaves.getWorth()))
                / Math.cos(angle.getWorth() * Math.PI / 180));
        ledge.setWorth(ledgeAndRoofArea.getWorth() - roofArea.getWorth());

        lb_RoofArea.setText(String.valueOf(MathUtils.round(roofArea.getWorth(), 2)) + " m²");
        lb_BaseArea.setText(String.valueOf(MathUtils.round(baseArea.getWorth(), 2)) + " m²");
        lb_Ledge.setText(String.valueOf(MathUtils.round(ledge.getWorth(), 2)) + " m²");
        lb_LedgeAndRoofArea.setText(String.valueOf(MathUtils.round(ledgeAndRoofArea.getWorth(), 2)) + " m²");

        Project_ResultAreaController.getInstance().calcArea();
    }

    /**
     * Gets the total areas from the ResultAreaController.
     *
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        lb_TotalBaseArea.setText(String.valueOf(MathUtils.round(Project_ResultAreaController.getInstance().getBaseArea(), 2)) + " m²");
        lb_TotalRoofArea.setText(String.valueOf(MathUtils.round(Project_ResultAreaController.getInstance().getRoofArea(), 2)) + " m²");
        lb_TotalLegde.setText(String.valueOf(MathUtils.round(Project_ResultAreaController.getInstance().getLedge(), 2)) + " m²");
        lb_TotalLedgeAndRoofArea.setText(String.valueOf(MathUtils.round(Project_ResultAreaController.getInstance().getLedgeAndRoofArea(), 2)) + " m²");
    }

    public void loadValuesFromDataBase() {
        long projectId = ProjectViewController.getOpenedProject().getId();
        WorthController worthController = new WorthController();

        length = (worthController.findWorthByShortTermAndProjectId("l", projectId) != null) ? worthController.findWorthByShortTermAndProjectId("l", projectId) : length;
        width = (worthController.findWorthByShortTermAndProjectId("b", projectId) != null) ? worthController.findWorthByShortTermAndProjectId("b", projectId) : width;
        baseArea = (worthController.findWorthByShortTermAndProjectId("A", projectId) != null) ? worthController.findWorthByShortTermAndProjectId("A", projectId) : baseArea;
        angle = (worthController.findWorthByShortTermAndProjectId("N", projectId) != null) ? worthController.findWorthByShortTermAndProjectId("N", projectId) : angle;
        roofArea = (worthController.findWorthByShortTermAndProjectId("D", projectId) != null) ? worthController.findWorthByShortTermAndProjectId("D", projectId) : roofArea;
        eaves = (worthController.findWorthByShortTermAndProjectId("dv", projectId) != null) ? worthController.findWorthByShortTermAndProjectId("dv", projectId) : eaves;
        ridge = (worthController.findWorthByShortTermAndProjectId("dh", projectId) != null) ? worthController.findWorthByShortTermAndProjectId("dh", projectId) : ridge;
        gableRight = (worthController.findWorthByShortTermAndProjectId("dr", projectId) != null) ? worthController.findWorthByShortTermAndProjectId("dr", projectId) : gableRight;
        gableLeft = (worthController.findWorthByShortTermAndProjectId("dl", projectId) != null) ? worthController.findWorthByShortTermAndProjectId("dl", projectId) : gableLeft;
        ledge = (worthController.findWorthByShortTermAndProjectId("DV", projectId) != null) ? worthController.findWorthByShortTermAndProjectId("DV", projectId) : ledge;
        ledgeAndRoofArea = (worthController.findWorthByShortTermAndProjectId("DF", projectId) != null) ? worthController.findWorthByShortTermAndProjectId("DF", projectId) : ledgeAndRoofArea;

        tf_Length.setText(getStringForTextField(length));
        tf_Width.setText(getStringForTextField(width));
        lb_BaseArea.setText(getStringForLabel(baseArea) + " m²");
        tf_Angle.setText(getStringForTextField(angle));
        lb_RoofArea.setText(getStringForLabel(roofArea) + " m²");
        tf_Eaves.setText(getStringForTextField(eaves));
        tf_Ridge.setText(getStringForTextField(ridge));
        tf_GableRight.setText(getStringForTextField(gableRight));
        tf_GableLeft.setText(getStringForTextField(gableLeft));
        lb_Ledge.setText(getStringForLabel(ledge) + " m²");
        lb_LedgeAndRoofArea.setText(getStringForLabel(ledgeAndRoofArea) + " m²");
        Project_ResultAreaController.getInstance().calcArea();
    }

    /**
     * Persists the calculated Values to the database.
     */
    public void persistArea() {

        calcArea(null);

        WorthController worthController = new WorthController();
        if (ProjectViewController.getOpenedProject().getWorths().isEmpty()) {
            length.setProject(ProjectViewController.getOpenedProject());
            width.setProject(ProjectViewController.getOpenedProject());
            baseArea.setProject(ProjectViewController.getOpenedProject());
            angle.setProject(ProjectViewController.getOpenedProject());
            roofArea.setProject(ProjectViewController.getOpenedProject());
            eaves.setProject(ProjectViewController.getOpenedProject());
            ridge.setProject(ProjectViewController.getOpenedProject());
            gableRight.setProject(ProjectViewController.getOpenedProject());
            gableLeft.setProject(ProjectViewController.getOpenedProject());
            ledge.setProject(ProjectViewController.getOpenedProject());
            ledgeAndRoofArea.setProject(ProjectViewController.getOpenedProject());

            worthController.create(length);
            worthController.create(width);
            worthController.create(baseArea);
            worthController.create(angle);
            worthController.create(roofArea);
            worthController.create(eaves);
            worthController.create(ridge);
            worthController.create(gableRight);
            worthController.create(gableLeft);
            worthController.create(ledge);
            worthController.create(ledgeAndRoofArea);
        } else {
            try {
                worthController.edit(length);
                worthController.edit(width);
                worthController.edit(baseArea);
                worthController.edit(angle);
                worthController.edit(roofArea);
                worthController.edit(eaves);
                worthController.edit(ridge);
                worthController.edit(gableRight);
                worthController.edit(gableLeft);
                worthController.edit(ledge);
                worthController.edit(ledgeAndRoofArea);
            } catch (Exception e) {
            }
        }
    }

    /**
     * Parses the double of a worth obejct.
     *
     * @param worth
     * @return
     */
    private String getStringForTextField(Worth worth) {

        if (worth == null) {
            return "";
        } else if (worth.getWorth() == 0) {
            return "";
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("#.#######");
            decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
            return String.valueOf(decimalFormat.format(worth.getWorth()));
        }
    }

    /**
     * Parses the double of a worth obejct.
     *
     * @param worth
     * @return
     */
    private String getStringForLabel(Worth worth) {
        if (worth == null) {
            return "0.00";
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
            return String.valueOf(decimalFormat.format(worth.getWorth()));
        }
    }

    /**
     * Parses string to double.
     *
     * @param text
     * @return
     */
    private double parseDouble(String text) {
        if (text.isEmpty()) {
            return 0;
        } else {
            try {
                return Double.parseDouble(text);
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR,"Das Format der eingegebene Zahlen ist nicht korrekt. Bitte überprüfen Sie ihre Eingabe noch einmal.").showAndWait();
                return 0;
            }
        }
    }
}
