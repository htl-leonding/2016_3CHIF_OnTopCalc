/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ParameterController;
import at.plakolb.calculationlogic.db.controller.WorthController;
import at.plakolb.calculationlogic.db.entity.Project;
import at.plakolb.calculationlogic.db.entity.Worth;
import at.plakolb.calculationlogic.util.Logging;
import at.plakolb.calculationlogic.util.UtilityFormat;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * Over this view it's possible to calculate the sheet roof. It's contained by
 * the Assembling_BattensOrFullFormwork.fxml.
 *
 * @author Kepplinger
 */
public class Assembling_SheetRoofController extends Observable implements Initializable, Observer {

    private static Assembling_SheetRoofController instance;
    @FXML
    private TextField tf_wastePercent;
    @FXML
    private Label lb_waste;
    @FXML
    private Label lb_roofArea;
    @FXML
    private Label lb_formwork;

    private Worth waste;
    private Worth formwork;
    private Worth wastePercent;

    /**
     * Initializes the controller class and all worth objects. Also adds a
     * change listener to verify the user input.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        Project_ResultAreaController.getInstance().addObserver(this);

        ParameterController parameterController = new ParameterController();
        waste = new Worth(parameterController.findParameterPByShortTerm("VVS"));
        formwork = new Worth(parameterController.findParameterPByShortTerm("VollS"));
        wastePercent = new Worth(parameterController.findParameterPByShortTerm("VVP"));

        lb_waste.setText(UtilityFormat.getStringForLabel(waste));
        lb_formwork.setText(UtilityFormat.getStringForLabel(formwork));
        tf_wastePercent.setText(UtilityFormat.getStringForTextField(wastePercent));

        tf_wastePercent.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            UtilityFormat.setWorthFromTextField(tf_wastePercent, wastePercent);
            Assembling_BattensOrFullFormworkController.getInstance().calculate();
            ModifyController.getInstance().setAssembling_battensOrFullFormwork(Boolean.TRUE);
        });

        load();

        if (Assembling_BattensOrFullFormworkController.getInstance() != null)
            Assembling_BattensOrFullFormworkController.getInstance().calculate();
    }

    public static Assembling_SheetRoofController getInstance() {
        return instance;
    }

    public Worth getFormwork() {
        return formwork;
    }

    /**
     * Loads all required values from the database into the view.
     */
    public void load() {
        lb_roofArea.setText(UtilityFormat.getStringForLabel(Project_ResultAreaController.getInstance().getLedgeAndRoofAreaWorth()));

        WorthController worthController = new WorthController();
        Project project = ProjectViewController.getOpenedProject();
        if (project != null) {
            waste = (worthController.findWorthByShortTermAndProjectId("VVS", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("VVS", project.getId()) : waste;
            formwork = (worthController.findWorthByShortTermAndProjectId("VollS", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("VollS", project.getId()) : formwork;
            wastePercent = (worthController.findWorthByShortTermAndProjectId("VVP", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("VVP", project.getId()) : wastePercent;

            tf_wastePercent.setText(UtilityFormat.getStringForTextField(wastePercent));
            lb_waste.setText(UtilityFormat.getStringForLabel(waste));
            lb_formwork.setText(UtilityFormat.getStringForLabel(formwork));
        }
        ModifyController.getInstance().setAssembling_battensOrFullFormwork(Boolean.FALSE);
    }

    /**
     * Calculates all required values.
     */
    public void calculate() {
        try {

            //Verschnitt Vollschalung
            waste.setWorth(Project_ResultAreaController.getInstance().getLedgeAndRoofArea() * (wastePercent.getWorth() / 100));
            lb_waste.setText(UtilityFormat.getStringForLabel(waste));

            //Vollschalung
            formwork.setWorth(Project_ResultAreaController.getInstance().getLedgeAndRoofArea() + waste.getWorth());
            lb_formwork.setText(UtilityFormat.getStringForLabel(formwork));

        } catch (Exception ex) {
            if (ProjectViewController.isProjectOpened()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Werte können nicht berechnet werden!\nFehlerinformation: " + ex.getLocalizedMessage(), ButtonType.OK);
                alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
                alert.showAndWait();
                Logging.getLogger().log(Level.SEVERE, "Assembling_SheetRoofController: calculate method didn't work", ex);
            }
        }

        setChanged();
        notifyObservers();
    }

    /**
     * Persists all values from the view to the database.
     */
    public void persist() {
        WorthController wc = new WorthController();
        Assembling_BattensOrFullFormworkController.getInstance().getComponent().setNumberOfProducts(formwork.getWorth());

        try {
            if (!ProjectViewController.isProjectOpened() || waste.getProject() == null) {
                waste.setProject(ProjectViewController.getOpenedProject());
                formwork.setProject(ProjectViewController.getOpenedProject());
                wastePercent.setProject(ProjectViewController.getOpenedProject());

                wc.create(waste);
                wc.create(formwork);
                wc.create(wastePercent);
            } else {

                wc.edit(waste);
                wc.edit(formwork);
                wc.edit(wastePercent);
            }
        } catch (Exception ex) {
            Logging.getLogger().log(Level.SEVERE, "Assembling_SheetRoofController: persist method didn't work.", ex);
        }
    }

    /**
     * Refreshes the roof area when values have been changed.
     *
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        lb_roofArea.setText(UtilityFormat.getStringForLabel(Project_ResultAreaController.getInstance().getLedgeAndRoofAreaWorth()));
        Assembling_BattensOrFullFormworkController.getInstance().calculate();
    }
}
