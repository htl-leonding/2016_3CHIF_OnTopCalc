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
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * Over this view it's possible to calculate the tiled roof. It's contained by
 * the Assembling_BattensOrFullFormwork.fxml.
 *
 * @author Kepplinger
 */
public class Assembling_TiledRoofController implements Initializable {

    private static Assembling_TiledRoofController instance;

    @FXML
    private TextField tf_slatSpacing;
    @FXML
    private TextField tf_waste;
    @FXML
    private Label lb_waste;
    @FXML
    private Label lb_lengthNoWaste;
    @FXML
    private Label lb_length;

    private Worth wastePercent;
    private Worth slatSpacing;
    private Worth waste;
    private Worth lengthNoWaste;
    private Worth length;

    /**
     * Initializes the controller class and all worth objects. Also adds two
     * change listeners to verify the user input.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;

        ParameterController parameterController = new ParameterController();
        wastePercent = new Worth(parameterController.findParameterPByShortTerm("VLP"));
        slatSpacing = new Worth(parameterController.findParameterPByShortTerm("LA"));
        waste = new Worth(parameterController.findParameterPByShortTerm("VL"));
        length = new Worth(parameterController.findParameterPByShortTerm("LL"));
        lengthNoWaste = new Worth(parameterController.findParameterPByShortTerm("LDOV"));

        tf_slatSpacing.setText(UtilityFormat.getStringForTextField(slatSpacing));
        tf_waste.setText(UtilityFormat.getStringForTextField(wastePercent));
        lb_length.setText(UtilityFormat.getStringForLabel(length));
        lb_lengthNoWaste.setText(UtilityFormat.getStringForLabel(lengthNoWaste));
        lb_waste.setText(UtilityFormat.getStringForLabel(waste));

        tf_slatSpacing.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            UtilityFormat.setWorthFromTextField(tf_slatSpacing, slatSpacing);
            Assembling_BattensOrFullFormworkController.getInstance().calculate();
            ModifyController.getInstance().setAssembling_battensOrFullFormwork(Boolean.TRUE);
        });
        tf_waste.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            UtilityFormat.setWorthFromTextField(tf_waste, wastePercent);
            Assembling_BattensOrFullFormworkController.getInstance().calculate();
            ModifyController.getInstance().setAssembling_battensOrFullFormwork(Boolean.TRUE);
        });

        load();

        if (Assembling_BattensOrFullFormworkController.getInstance() != null)
            Assembling_BattensOrFullFormworkController.getInstance().calculate();
    }

    public static Assembling_TiledRoofController getInstance() {
        return instance;
    }

    public Worth getLength() {
        return length;
    }

    /**
     * Loads all required values from the database into the view.
     */
    public void load() {
        WorthController worthController = new WorthController();
        Project project = ProjectViewController.getOpenedProject();

        if (project != null) {
            slatSpacing = (worthController.findWorthByShortTermAndProjectId("LA", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("LA", project.getId()) : slatSpacing;
            waste = (worthController.findWorthByShortTermAndProjectId("VL", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("VL", project.getId()) : waste;
            length = (worthController.findWorthByShortTermAndProjectId("LL", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("LL", project.getId()) : length;
            lengthNoWaste = (worthController.findWorthByShortTermAndProjectId("LDOV", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("LDOV", project.getId()) : lengthNoWaste;
            wastePercent = (worthController.findWorthByShortTermAndProjectId("VLP", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("VLP", project.getId()) : wastePercent;

            tf_slatSpacing.setText(UtilityFormat.getStringForTextField(slatSpacing));
            tf_waste.setText(UtilityFormat.getStringForTextField(wastePercent));

            lb_length.setText(UtilityFormat.getStringForLabel(length));
            lb_lengthNoWaste.setText(UtilityFormat.getStringForLabel(lengthNoWaste));
            lb_waste.setText(UtilityFormat.getStringForLabel(waste));

            ModifyController.getInstance().setAssembling_battensOrFullFormwork(Boolean.FALSE);
        }
    }

    /**
     * Calculates all required values.
     */
    public void calculate() {
        try {

            //Länge der Dachlatten ohne Verschnitt
            lengthNoWaste.setWorth(Project_ResultAreaController.getInstance().getLedgeAndRoofArea() / (slatSpacing.getWorth() / 100));
            lb_lengthNoWaste.setText(UtilityFormat.getStringForLabel(lengthNoWaste));

            //Verschnitt Lattung
            waste.setWorth(lengthNoWaste.getWorth() * (wastePercent.getWorth() / 100));
            lb_waste.setText(UtilityFormat.getStringForLabel(waste));

            //Länge der Dachlatten - Lattung
            length.setWorth(lengthNoWaste.getWorth() + waste.getWorth());
            lb_length.setText(UtilityFormat.getStringForLabel(length));

        } catch (Exception ex) {
            if (ProjectViewController.isProjectOpened()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Werte können nicht berechnet werden!\nFehlerinformation: " + ex.getLocalizedMessage(), ButtonType.OK);
                alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
                alert.showAndWait();
                Logging.getLogger().log(Level.SEVERE, "Assembling_TiledRoofController: calculate method didn't work.", ex);
            }
        }
    }

    /**
     * Persists all values from the view to the database.
     */
    public void persist() {
        WorthController wc = new WorthController();
        Assembling_BattensOrFullFormworkController.getInstance().getComponent().setNumberOfProducts(length.getWorth());
        try {
            if (!ProjectViewController.isProjectOpened() || slatSpacing.getProject() == null) {
                slatSpacing.setProject(ProjectViewController.getOpenedProject());
                waste.setProject(ProjectViewController.getOpenedProject());
                lengthNoWaste.setProject(ProjectViewController.getOpenedProject());
                length.setProject(ProjectViewController.getOpenedProject());
                wastePercent.setProject(ProjectViewController.getOpenedProject());

                wc.create(slatSpacing);
                wc.create(waste);
                wc.create(lengthNoWaste);
                wc.create(length);
                wc.create(wastePercent);
            } else {

                wc.edit(slatSpacing);
                wc.edit(waste);
                wc.edit(lengthNoWaste);
                wc.edit(length);
                wc.edit(wastePercent);
            }
        } catch (Exception ex) {
            Logging.getLogger().log(Level.SEVERE, "Assembling_TiledRoofController: persist method didn't work.", ex);
        }
    }
}
