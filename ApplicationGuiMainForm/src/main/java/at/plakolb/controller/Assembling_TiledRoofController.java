package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ParameterController;
import at.plakolb.calculationlogic.db.controller.WorthController;
import at.plakolb.calculationlogic.entity.Project;
import at.plakolb.calculationlogic.entity.Worth;
import at.plakolb.calculationlogic.util.UtilityFormat;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
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
public class Assembling_TiledRoofController extends Observable implements Initializable, Observer {

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

    Worth slatSpacing;
    Worth waste;
    Worth lengthNoWaste;
    Worth length;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        Project_ResultAreaController.getInstance().addObserver(this);

        ParameterController parameterController = new ParameterController();
        slatSpacing = new Worth(parameterController.findParameterPByShortTerm("LA"));
        waste = new Worth(parameterController.findParameterPByShortTerm("VL"));
        length = new Worth(parameterController.findParameterPByShortTerm("LL"));
        lengthNoWaste = new Worth(parameterController.findParameterPByShortTerm("LDOV"));

        tf_slatSpacing.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            UtilityFormat.setWorthFromTextField(tf_slatSpacing, slatSpacing);
            calculate();
            ModifyController.getInstance().setAssembling_battensOrFullFormwork(Boolean.TRUE);
        });
        tf_waste.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            UtilityFormat.setWorthFromTextField(tf_waste, waste);
            calculate();
            ModifyController.getInstance().setAssembling_battensOrFullFormwork(Boolean.TRUE);
        });

        load();
    }

    public void persist() {
        WorthController wc = new WorthController();
        Assembling_BattensOrFullFormworkController.getInstance().getComponent().setNumberOfProducts(length.getWorth());

        if (!ProjectViewController.isProjectOpened() || slatSpacing.getProject() == null) {
            slatSpacing.setProject(ProjectViewController.getOpenedProject());
            waste.setProject(ProjectViewController.getOpenedProject());
            lengthNoWaste.setProject(ProjectViewController.getOpenedProject());
            length.setProject(ProjectViewController.getOpenedProject());

            wc.create(slatSpacing);
            wc.create(waste);
            wc.create(lengthNoWaste);
            wc.create(length);
        } else {
            try {
                wc.edit(slatSpacing);
                wc.edit(waste);
                wc.edit(lengthNoWaste);
                wc.edit(length);
            } catch (Exception ex) {
                Logger.getLogger(Assembling_TiledRoofController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

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

            tf_slatSpacing.setText(UtilityFormat.getStringForTextField(slatSpacing));
            System.out.println(Assembling_BattensOrFullFormworkController.getInstance().getWastePercent());
            tf_waste.setText(UtilityFormat.getStringForTextField(Assembling_BattensOrFullFormworkController.getInstance().getWastePercent()));

            lb_length.setText(UtilityFormat.getStringForLabel(length));
            lb_lengthNoWaste.setText(UtilityFormat.getStringForLabel(lengthNoWaste));
            lb_waste.setText(UtilityFormat.getStringForLabel(waste));

            ModifyController.getInstance().setAssembling_battensOrFullFormwork(Boolean.FALSE);
        }
    }

    public void calculate() {
        try {
            //Länge der Dachlatten ohne Verschnitt
            //Alte Formel-ID: LDOV
            lengthNoWaste.setWorth(Project_ResultAreaController.getInstance().getLedgeAndRoofArea() / slatSpacing.getWorth() / 100);
            lb_lengthNoWaste.setText(UtilityFormat.getStringForLabel(lengthNoWaste));

            //Verschnitt
            //Alte Formel-ID:VL
            waste.setWorth(lengthNoWaste.getWorth() * Assembling_BattensOrFullFormworkController.getInstance().getWastePercent() / 100);
            lb_waste.setText(UtilityFormat.getStringForLabel(waste));

            //Länge + Verschnitt
            //Alte Formel-ID:LL
            length.setWorth(lengthNoWaste.getWorth() + waste.getWorth());
            lb_length.setText(UtilityFormat.getStringForLabel(length));

        } catch (Exception ex) {
            if (ProjectViewController.isProjectOpened()) {
                new Alert(Alert.AlertType.ERROR, "Werte können nicht berechnet werden!\nFehlerinformation: " + ex.getLocalizedMessage(), ButtonType.OK).showAndWait();
            }
        }

        setChanged();
        notifyObservers();
    }

    public static Assembling_TiledRoofController getInstance() {
        return instance;
    }

    @Override
    public void update(Observable o, Object arg) {
        calculate();
    }

    public Worth getLength() {
        return length;
    }
}