/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.util.Logging;
import at.plakolb.calculationlogic.db.controller.ParameterController;
import at.plakolb.calculationlogic.db.controller.WorthController;
import at.plakolb.calculationlogic.db.entity.Worth;
import at.plakolb.calculationlogic.util.UtilityFormat;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Level;
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

    private int id;

    public int getID() {
        return id;
    }

    public void setID(int i) {
        id = i;
    }

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

        tf_Length.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            calculate(tf_Length, length);
        });

        tf_Width.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            calculate(tf_Width, width);
        });

        tf_Angle.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            calculate(tf_Angle, angle);
        });

        tf_Ridge.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            calculate(tf_Ridge, ridge);
        });

        tf_Eaves.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            calculate(tf_Eaves, eaves);
        });

        tf_GableRight.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            calculate(tf_GableRight, gableRight);
        });

        tf_GableLeft.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            calculate(tf_GableLeft, gableLeft);
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
     * Loads all existing worth objects from the database into the view.
     */
    public void loadValuesFromDataBase() {
        long projectId = ProjectViewController.getOpenedProject().getId();
        WorthController worthController = new WorthController();

        int index = Project_ResultAreaController.getInstance().getIndex(getID());
        length = (worthController.findWorthByShortTermAndProjectId("l" + index, projectId) != null) ? worthController.findWorthByShortTermAndProjectId("l" + index, projectId) : length;
        width = (worthController.findWorthByShortTermAndProjectId("b" + index, projectId) != null) ? worthController.findWorthByShortTermAndProjectId("b" + index, projectId) : width;
        baseArea = (worthController.findWorthByShortTermAndProjectId("A" + index, projectId) != null) ? worthController.findWorthByShortTermAndProjectId("A" + index, projectId) : baseArea;
        angle = (worthController.findWorthByShortTermAndProjectId("N" + index, projectId) != null) ? worthController.findWorthByShortTermAndProjectId("N" + index, projectId) : angle;
        roofArea = (worthController.findWorthByShortTermAndProjectId("D" + index, projectId) != null) ? worthController.findWorthByShortTermAndProjectId("D" + index, projectId) : roofArea;
        eaves = (worthController.findWorthByShortTermAndProjectId("dv" + index, projectId) != null) ? worthController.findWorthByShortTermAndProjectId("dv" + index, projectId) : eaves;
        ridge = (worthController.findWorthByShortTermAndProjectId("dh" + index, projectId) != null) ? worthController.findWorthByShortTermAndProjectId("dh" + index, projectId) : ridge;
        gableRight = (worthController.findWorthByShortTermAndProjectId("dr" + index, projectId) != null) ? worthController.findWorthByShortTermAndProjectId("dr" + index, projectId) : gableRight;
        gableLeft = (worthController.findWorthByShortTermAndProjectId("dl" + index, projectId) != null) ? worthController.findWorthByShortTermAndProjectId("dl" + index, projectId) : gableLeft;
        ledge = (worthController.findWorthByShortTermAndProjectId("DV" + index, projectId) != null) ? worthController.findWorthByShortTermAndProjectId("DV" + index, projectId) : ledge;
        ledgeAndRoofArea = (worthController.findWorthByShortTermAndProjectId("DF" + index, projectId) != null) ? worthController.findWorthByShortTermAndProjectId("DF" + index, projectId) : ledgeAndRoofArea;

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
    }

    /**
     * Transforms the textfield String into a calculable number and saves it
     * into the worth Object. After that, all areas gets calclutated.
     *
     * @param event
     */
    private void calculate(TextField textField, Worth worth) {
        if (!isCalculating) {
            try {
                isCalculating = true;
                textField.setText(textField.getText().replaceAll(",", ".").replaceAll("[^\\d.]", ""));
                textField.setText(UtilityFormat.removeUnnecessaryCommas(textField.getText()));

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
                new Alert(Alert.AlertType.ERROR, "Fehlerhafte Eingabe").showAndWait();
                textField.setText("");
            } catch (Exception ex) {
                Logging.getLogger().log(Level.SEVERE, "Project_BaseAndRoofAreaController: calculate method didn't work.", ex);
            } finally {
                ModifyController.getInstance().setProject_resultArea(Boolean.TRUE);
                isCalculating = false;
            }
        }
    }

    /**
     * Returns the total areas from the ResultAreaController.
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

    /**
     * Persists the calculated Values to the database.
     */
    public void persist() {
        ParameterController parameterController = new ParameterController();
        WorthController worthController = new WorthController();

        int index = Project_ResultAreaController.getInstance().getIndex(getID());

        length.setShortTerm(length.getParameter().getShortTerm() + index);
        width.setShortTerm(width.getParameter().getShortTerm() + index);
        angle.setShortTerm(angle.getParameter().getShortTerm() + index);
        eaves.setShortTerm(eaves.getParameter().getShortTerm() + index);
        ridge.setShortTerm(ridge.getParameter().getShortTerm() + index);
        gableRight.setShortTerm(gableRight.getParameter().getShortTerm() + index);
        gableLeft.setShortTerm(gableLeft.getParameter().getShortTerm() + index);

        try {
            if (!ProjectViewController.isProjectOpened()) {
                length.setProject(ProjectViewController.getOpenedProject());
                width.setProject(ProjectViewController.getOpenedProject());
                angle.setProject(ProjectViewController.getOpenedProject());
                eaves.setProject(ProjectViewController.getOpenedProject());
                ridge.setProject(ProjectViewController.getOpenedProject());
                gableRight.setProject(ProjectViewController.getOpenedProject());
                gableLeft.setProject(ProjectViewController.getOpenedProject());

                worthController.create(length);
                worthController.create(width);
                worthController.create(angle);
                worthController.create(eaves);
                worthController.create(ridge);
                worthController.create(gableRight);
                worthController.create(gableLeft);
            } else {
                worthController.edit(length);
                worthController.edit(width);
                worthController.edit(angle);
                worthController.edit(eaves);
                worthController.edit(ridge);
                worthController.edit(gableRight);
                worthController.edit(gableLeft);

            }
        } catch (Exception ex) {
            Logging.getLogger().log(Level.SEVERE, "Project_BaseAndRoofAreaController: persist method didn't work.", ex);
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
