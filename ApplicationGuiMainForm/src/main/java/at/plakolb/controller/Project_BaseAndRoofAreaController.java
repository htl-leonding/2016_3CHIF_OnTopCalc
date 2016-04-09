/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ParameterController;
import at.plakolb.calculationlogic.db.controller.WorthController;
import at.plakolb.calculationlogic.entity.Worth;
import at.plakolb.calculationlogic.util.UtilityFormat;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
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

    private boolean isCalculating;
    private DecimalFormat decimalFormat;

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

        decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));

        tf_Length.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            calcArea(tf_Length, length);
        });

        tf_Width.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            calcArea(tf_Width, width);
        });

        tf_Angle.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            calcArea(tf_Angle, angle);
        });

        tf_Ridge.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            calcArea(tf_Ridge, ridge);
        });

        tf_Eaves.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            calcArea(tf_Eaves, eaves);
        });

        tf_GableRight.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            calcArea(tf_GableRight, gableRight);
        });

        tf_GableLeft.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            calcArea(tf_GableLeft, gableLeft);
        });
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

    /**
     * Calcuates all roof areas.
     *
     * @param event
     */
    private void calcArea(TextField textField, Worth worth) {
        if (!isCalculating) {
            try {
                isCalculating = true;
                textField.setText(textField.getText().replaceAll(",", "."));

                if (textField.equals(tf_Angle)) {
                    if (parseDouble(textField.getText()) >= 90) {
                        textField.setText(textField.getText().substring(0, textField.getText().length() - 1));
                        new Alert(Alert.AlertType.ERROR, "Der Winkel darf nicht größer wie 90° sein.").showAndWait();
                    }
                }

                worth.setWorth(parseDouble(textField.getText()));

                baseArea.setWorth(length.getWorth() * width.getWorth());
                roofArea.setWorth(baseArea.getWorth() / Math.cos(angle.getWorth() * Math.PI / 180));
                ledgeAndRoofArea.setWorth(((length.getWorth() + gableLeft.getWorth() + gableRight.getWorth()) * (width.getWorth() + ridge.getWorth() + eaves.getWorth()))
                        / Math.cos(angle.getWorth() * Math.PI / 180));
                ledge.setWorth(ledgeAndRoofArea.getWorth() - roofArea.getWorth());

                lb_RoofArea.setText(decimalFormat.format(roofArea.getWorth()) + " m²");
                lb_BaseArea.setText(decimalFormat.format(baseArea.getWorth()) + " m²");
                lb_Ledge.setText(decimalFormat.format(ledge.getWorth()) + " m²");
                lb_LedgeAndRoofArea.setText(decimalFormat.format(ledgeAndRoofArea.getWorth()) + " m²");

                Project_ResultAreaController.getInstance().calcArea();

            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Geben Sie bitte eine Zahl ein.\n(Eingegebens Zeichen: " + textField.getText().charAt(textField.getText().length() - 1) + ")").showAndWait();
                textField.setText(textField.getText().substring(0, textField.getText().length() - 1));
            } finally{
                isCalculating = false;
            }
        }
    }

    /**
     * Gets the total areas from the ResultAreaController.
     *
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        lb_TotalBaseArea.setText(decimalFormat.format(Project_ResultAreaController.getInstance().getBaseArea()) + " m²");
        lb_TotalRoofArea.setText(decimalFormat.format(Project_ResultAreaController.getInstance().getRoofArea()) + " m²");
        lb_TotalLegde.setText(decimalFormat.format(Project_ResultAreaController.getInstance().getLedge()) + " m²");
        lb_TotalLedgeAndRoofArea.setText(decimalFormat.format(Project_ResultAreaController.getInstance().getLedgeAndRoofArea()) + " m²");
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

        tf_Length.setText(UtilityFormat.getStringForTextField(length));
        tf_Width.setText(UtilityFormat.getStringForTextField(width));
        lb_BaseArea.setText(UtilityFormat.getStringForLabel(baseArea));
        tf_Angle.setText(UtilityFormat.getStringForTextField(angle));
        lb_RoofArea.setText(UtilityFormat.getStringForLabel(roofArea));
        tf_Eaves.setText(UtilityFormat.getStringForTextField(eaves));
        tf_Ridge.setText(UtilityFormat.getStringForTextField(ridge));
        tf_GableRight.setText(UtilityFormat.getStringForTextField(gableRight));
        tf_GableLeft.setText(UtilityFormat.getStringForTextField(gableLeft));
        lb_Ledge.setText(UtilityFormat.getStringForLabel(ledge));
        lb_LedgeAndRoofArea.setText(UtilityFormat.getStringForLabel(ledgeAndRoofArea));
        Project_ResultAreaController.getInstance().calcArea();
    }

    /**
     * Persists the calculated Values to the database.
     */
    public void persistArea() {

        WorthController worthController = new WorthController();
        if (!ProjectViewController.isProjectOpened()) {
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
     * Parses string to double.
     *
     * @param text
     * @return
     */
    private double parseDouble(String text) {
        if (text.isEmpty()) {
            return 0;
        } else {
            return Double.parseDouble(text);
        }
    }
}
