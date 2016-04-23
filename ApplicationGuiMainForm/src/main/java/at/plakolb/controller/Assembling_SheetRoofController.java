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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
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

    Worth waste;
    Worth formwork;

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
        waste = new Worth(parameterController.findParameterPByShortTerm("VVS"));
        formwork = new Worth(parameterController.findParameterPByShortTerm("VollS"));

        tf_wastePercent.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            setWastePercent();
            calculate();
        });

        load();
    }

    public void persist() {
        WorthController wc = new WorthController();

        if (!ProjectViewController.isProjectOpened() || waste.getProject() == null) {
            waste.setProject(ProjectViewController.getOpenedProject());
            formwork.setProject(ProjectViewController.getOpenedProject());

            wc.create(waste);
            wc.create(formwork);
        } else {
            try {
                wc.edit(waste);
                wc.edit(formwork);
            } catch (Exception ex) {
                Logger.getLogger(Assembling_SheetRoofController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void load() {
        lb_roofArea.setText(UtilityFormat.getStringForLabel(Project_ResultAreaController.getInstance().getLedgeAndRoofAreaWorth()));

        WorthController worthController = new WorthController();
        Project project = ProjectViewController.getOpenedProject();
        if (project != null) {
            waste = (worthController.findWorthByShortTermAndProjectId("VVS", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("VVS", project.getId()) : waste;
            formwork = (worthController.findWorthByShortTermAndProjectId("VollS", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("VollS", project.getId()) : formwork;
            tf_wastePercent.setText(UtilityFormat.getStringForTextField(Assembling_BattensOrFullFormworkController.getInstance().getWastePercent()));
            lb_waste.setText(UtilityFormat.getStringForLabel(waste));
            lb_formwork.setText(UtilityFormat.getStringForLabel(formwork));
        }
    }

    public void calculate() {
        //Verschnitt Vollschalung
        //Alte Formel-ID: VSS
        waste.setWorth(Project_ResultAreaController.getInstance().getLedgeAndRoofArea() * Assembling_BattensOrFullFormworkController.getInstance().getWastePercent() / 100);
        lb_waste.setText(UtilityFormat.getStringForLabel(waste));

        //Vollschalung
        //Alte Formel-ID: VollS
        formwork.setWorth(Project_ResultAreaController.getInstance().getLedgeAndRoofArea() + waste.getWorth());
        lb_formwork.setText(UtilityFormat.getStringForLabel(formwork));

        setChanged();
        notifyObservers();
    }

    public static Assembling_SheetRoofController getInstance() {
        return instance;
    }

    @Override
    public void update(Observable o, Object arg) {
        lb_roofArea.setText(UtilityFormat.getStringForLabel(Project_ResultAreaController.getInstance().getLedgeAndRoofAreaWorth()));
        calculate();
    }

    private void setWastePercent() {
        Assembling_BattensOrFullFormworkController.getInstance().setWastePercent(tf_wastePercent.getText().isEmpty() || !tf_wastePercent.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_wastePercent.getText().replace(',', '.')));
    }

    public Worth getFormwork(){
        return formwork;
    }
}
