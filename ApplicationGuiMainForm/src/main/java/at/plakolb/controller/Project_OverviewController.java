/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.util.UtilityFormat;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class Project_OverviewController implements Initializable {

    private static Project_OverviewController instance;
    @FXML
    private Label lb_WoodMaterial;
    @FXML
    private Label lb_FormworkMaterial;
    @FXML
    private Label lb_TransportMaterial;
    @FXML
    private Label lb_ColourMaterial;
    @FXML
    private Label lb_AssemblingMaterial;
    @FXML
    private Label lb_BattenFormworkMaterial;
    @FXML
    private Label lb_CounterBattenMaterial;
    @FXML
    private Label lb_SealingBandMaterial;
    @FXML
    private Label lb_FoilMaterial;
    @FXML
    private Label lb_VisibleFormworkMaterial;
    @FXML
    private Label lb_FormworkTotal;
    @FXML
    private Label lb_WoodTotal;
    @FXML
    private Label lb_ColourWage;
    @FXML
    private Label lb_BattenFormworkWage;
    @FXML
    private Label lb_CounterBattenWage;
    @FXML
    private Label lb_SealingBandWage;
    @FXML
    private Label lb_FoilWage;
    @FXML
    private Label lb_VisibleFormworkWage;
    @FXML
    private Label lb_FormworkWage;
    @FXML
    private Label lb_WoodWage;
    @FXML
    private Label lb_ColourTotal;
    @FXML
    private Label lb_AssemblingTotal;
    @FXML
    private Label lb_BattenFormworkTotal;
    @FXML
    private Label lb_CounterBattenTotal;
    @FXML
    private Label lb_SealingBandTotal;
    @FXML
    private Label lb_FoilTotal;
    @FXML
    private Label lb_VisibleFormworkTotal;
    @FXML
    private Label lb_TransportTotal;
    @FXML
    private Label lb_Brutto;
    @FXML
    private Label lb_Tax;
    @FXML
    private Label lb_Netto;
    
    private double nettoCosts;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        nettoCosts = 0;
        refreshValues();
    }    

    public static Project_OverviewController getInstance() {
        return instance;
    }
    
    public void refreshValues(){
        
        nettoCosts = 0;
        
        nettoCosts += Project_ConstructionMaterialListController.getInstance().getTotalCosts();
        lb_WoodMaterial.setText(UtilityFormat.getStringForLabel(Project_ConstructionMaterialListController.getInstance().getMaterial()) + " €");
        lb_WoodWage.setText(UtilityFormat.getStringForLabel(Project_ConstructionMaterialListController.getInstance().getWage()) + " €");
        lb_WoodTotal.setText(UtilityFormat.getStringForLabel(Project_ConstructionMaterialListController.getInstance().getTotalCosts()) + " €");
        
        nettoCosts += Assembling_FormworkController.getInstance().getTotalCosts().getWorth();
        lb_FormworkMaterial.setText(UtilityFormat.getStringForLabel(Assembling_FormworkController.getInstance().getMaterial()));
        lb_FormworkWage.setText(UtilityFormat.getStringForLabel(Assembling_FormworkController.getInstance().getWage()));
        lb_FormworkTotal.setText(UtilityFormat.getStringForLabel(Assembling_FormworkController.getInstance().getTotalCosts()));
        
        nettoCosts += Assembling_VisibleFormworkController.getInstance().getTotalCosts().getWorth();
        lb_VisibleFormworkMaterial.setText(UtilityFormat.getStringForLabel(Assembling_VisibleFormworkController.getInstance().getMaterial()));
        lb_VisibleFormworkWage.setText(UtilityFormat.getStringForLabel(Assembling_VisibleFormworkController.getInstance().getWage()));
        lb_VisibleFormworkTotal.setText(UtilityFormat.getStringForLabel(Assembling_VisibleFormworkController.getInstance().getTotalCosts()));
        
        nettoCosts += Assembling_FoilController.getInstance().getTotalCosts().getWorth();
        lb_FoilMaterial.setText(UtilityFormat.getStringForLabel(Assembling_FoilController.getInstance().getMaterial()));
        lb_FoilWage.setText(UtilityFormat.getStringForLabel(Assembling_FoilController.getInstance().getWage()));
        lb_FoilTotal.setText(UtilityFormat.getStringForLabel(Assembling_FoilController.getInstance().getTotalCosts()));
        
        nettoCosts += Assembling_SealingBandController.getInstance().getTotalCosts().getWorth();
        lb_SealingBandMaterial.setText(UtilityFormat.getStringForLabel(Assembling_SealingBandController.getInstance().getMaterial()));
        lb_SealingBandWage.setText(UtilityFormat.getStringForLabel(Assembling_SealingBandController.getInstance().getWage()));
        lb_SealingBandTotal.setText(UtilityFormat.getStringForLabel(Assembling_SealingBandController.getInstance().getTotalCosts()));
        
        nettoCosts += Assembling_CounterBattenController.getInstance().getTotalCosts().getWorth();
        lb_CounterBattenMaterial.setText(UtilityFormat.getStringForLabel(Assembling_CounterBattenController.getInstance().getMaterial()));
        lb_CounterBattenWage.setText(UtilityFormat.getStringForLabel(Assembling_CounterBattenController.getInstance().getWage()));
        lb_CounterBattenTotal.setText(UtilityFormat.getStringForLabel(Assembling_CounterBattenController.getInstance().getTotalCosts()));
        
        nettoCosts += Assembling_BattensOrFullFormworkController.getInstance().getTotalCosts().getWorth();
        lb_BattenFormworkMaterial.setText(UtilityFormat.getStringForLabel(Assembling_BattensOrFullFormworkController.getInstance().getMaterial()));
        lb_BattenFormworkWage.setText(UtilityFormat.getStringForLabel(Assembling_BattensOrFullFormworkController.getInstance().getWage()));
        lb_BattenFormworkTotal.setText(UtilityFormat.getStringForLabel(Assembling_BattensOrFullFormworkController.getInstance().getTotalCosts()));
        
        nettoCosts += Project_ConstructionMaterialController.getInstance().getMaterial();
        lb_AssemblingMaterial.setText(UtilityFormat.getStringForLabel(Project_ConstructionMaterialController.getInstance().getMaterial()) + " €");
        lb_AssemblingTotal.setText(UtilityFormat.getStringForLabel(Project_ConstructionMaterialController.getInstance().getMaterial()) + " €");
        
        nettoCosts += Project_ColourController.getInstance().getTotalCosts().getWorth();
        lb_ColourMaterial.setText(UtilityFormat.getStringForLabel(Project_ColourController.getInstance().getMaterial()));
        lb_ColourWage.setText(UtilityFormat.getStringForLabel(Project_ColourController.getInstance().getWage()));
        lb_ColourTotal.setText(UtilityFormat.getStringForLabel(Project_ColourController.getInstance().getTotalCosts()));
        
        nettoCosts += Project_TransportController.getInstance().getTotalCosts().getWorth();
        lb_TransportMaterial.setText(UtilityFormat.getStringForLabel(Project_TransportController.getInstance().getTotalCosts()));
        lb_TransportTotal.setText(UtilityFormat.getStringForLabel(Project_TransportController.getInstance().getTotalCosts()));
        
        lb_Netto.setText(UtilityFormat.getStringForLabel(nettoCosts) + " €");
        lb_Tax.setText(UtilityFormat.getStringForLabel(nettoCosts * 0.2) + " €");
        lb_Brutto.setText(UtilityFormat.getStringForLabel(nettoCosts * 1.2) + " €");
    }
}
