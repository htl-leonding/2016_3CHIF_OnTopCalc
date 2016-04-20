/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.CategoryController;
import at.plakolb.calculationlogic.db.controller.ComponentController;
import at.plakolb.calculationlogic.db.controller.ParameterController;
import at.plakolb.calculationlogic.db.controller.ProductController;
import at.plakolb.calculationlogic.db.controller.WorthController;
import at.plakolb.calculationlogic.entity.Category;
import at.plakolb.calculationlogic.entity.Component;
import at.plakolb.calculationlogic.entity.Product;
import at.plakolb.calculationlogic.entity.Project;
import at.plakolb.calculationlogic.entity.Worth;
import at.plakolb.calculationlogic.eunmeration.ProductType;
import at.plakolb.calculationlogic.util.UtilityFormat;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author User
 */
public class Project_ColourController implements Initializable {

    private static Project_ColourController instance;
    @FXML
    private Label lb_VisibleFormwork;
    @FXML
    private ComboBox<Product> cb_Product;
    @FXML
    private TextField tf_ProfiHour;
    @FXML
    private TextField tf_AdittionalColourFactor;
    @FXML
    private TextField tf_TimeOfPainting;
    @FXML
    private TextField tf_PricePerLiter;
    @FXML
    private Label lb_PaintArea;
    @FXML
    private Label lb_ProductCost;
    @FXML
    private Label lb_MontageCost;
    @FXML
    private Label lb_PaintLiter;
    @FXML
    private Label lb_TotalCosts;

    private DecimalFormat decimalFormat;

    private Component component;
    
    private Worth additionalColourFactor;
    private Worth timeofPainting;
    private double pricePerLiter;
    private Worth paintArea;
    private Worth productCost;
    private Worth montageCost;
    private Worth paintLiter;
    private Worth totalCost;
    private Worth profiHour;

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

        cb_Product.setItems(FXCollections.observableArrayList(new ProductController().findByProductTypeOrderByName(ProductType.COLOR)));
        ParameterController parameterController = new ParameterController();
        additionalColourFactor = new Worth(parameterController.findParameterPByShortTerm("FK"));
         profiHour = new Worth(parameterController.findParameterPByShortTerm("PMFP"));
        timeofPainting = new Worth(parameterController.findParameterPByShortTerm("ZPFA"));
        paintArea = new Worth(parameterController.findParameterPByShortTerm("FMM"));
        productCost = new Worth(parameterController.findParameterPByShortTerm("KPFarbe"));
        montageCost = new Worth(parameterController.findParameterPByShortTerm("KMFarbe"));
        paintLiter = new Worth(parameterController.findParameterPByShortTerm("FML"));
        totalCost = new Worth(parameterController.findParameterPByShortTerm("GKFarbe"));
        
        tf_ProfiHour.textProperty().addListener((observable, oldValue, newValue) -> {
            setHour();
            calculateValues();
        });
        tf_PricePerLiter.textProperty().addListener((observable, oldValue, newValue) -> {
            setPricePerLiter();
            calculateValues();
        });
        tf_AdittionalColourFactor.textProperty().addListener((observable, oldValue, newValue) -> {
            setColourFactor();
            calculateValues();
        });
        tf_TimeOfPainting.textProperty().addListener((observable, oldValue, newValue) -> {
            setTime();
            calculateValues();
        });
        cb_Product.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Product>() {
            @Override
            public void changed(ObservableValue<? extends Product> observable, Product oldValue, Product newValue) {
                if (newValue!=null) {
                   pricePerLiter=newValue.getPriceUnit();
                   tf_PricePerLiter.setText(UtilityFormat.getStringForTextField(pricePerLiter));
                }
            }
        });
        
        if (ProjectViewController.getOpenedProject() != null) {
            loadColourValues();
        }
    }

    public void setHour() {
        profiHour.setWorth(tf_ProfiHour.getText().isEmpty() || !tf_ProfiHour.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_ProfiHour.getText().replace(',', '.')));
    }

    public void setPricePerLiter() {
        pricePerLiter = (tf_PricePerLiter.getText().isEmpty() || !tf_PricePerLiter.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_PricePerLiter.getText().replace(',', '.')));
    }

    public void setColourFactor() {
        additionalColourFactor.setWorth(tf_AdittionalColourFactor.getText().isEmpty() || !tf_AdittionalColourFactor.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_AdittionalColourFactor.getText().replace(',', '.')));
    }

    public void setTime() {
        timeofPainting.setWorth(tf_TimeOfPainting.getText().isEmpty() || !tf_TimeOfPainting.getText().matches("[0-9]*.[0-9]*")
                ? 0 : Double.valueOf(tf_TimeOfPainting.getText().replace(',', '.')));
    }
//listener adden worths initialize

    public static Project_ColourController getInstance() {
        return instance;
    }

    public void loadColourValues() {
        
        ParameterController parameterController = new ParameterController();
        WorthController worthController = new WorthController();
        Project openedProject = ProjectViewController.getOpenedProject();
        
        lb_VisibleFormwork.setText(UtilityFormat.getStringForLabel(Assembling_VisibleFormworkController.getInstance().getVisibleFormwork()));

        timeofPainting = (worthController.findWorthByShortTermAndProjectId("ZPFA", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("ZPFA", openedProject.getId()) : timeofPainting;
        /*combobox*/
        
        profiHour = (worthController.findWorthByShortTermAndProjectId("PMFP", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("PMFP", openedProject.getId()) : profiHour;
        additionalColourFactor = (worthController.findWorthByShortTermAndProjectId("FK", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("FK", openedProject.getId()) : additionalColourFactor;
        paintArea = (worthController.findWorthByShortTermAndProjectId("FMM", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("FMM", openedProject.getId()) : paintArea;
        totalCost = (worthController.findWorthByShortTermAndProjectId("GKFarbe", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("GKFarbe", openedProject.getId()) : totalCost;
        paintLiter = (worthController.findWorthByShortTermAndProjectId("FML", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("FML", openedProject.getId()) : paintLiter;
        productCost = (worthController.findWorthByShortTermAndProjectId("KPFarbe", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("KPFarbe", openedProject.getId()) : productCost;
        montageCost = (worthController.findWorthByShortTermAndProjectId("KMFarbe", openedProject.getId()) != null) ? worthController.findWorthByShortTermAndProjectId("KMFarbe", openedProject.getId()) : montageCost;
        Category category = new CategoryController().findCategoryByShortTerm("X");
       
        component = new ComponentController().findColorByProjectId(ProjectViewController.getOpenedProject().getId());
        if (component != null) {
            System.out.println(component.getComponentType());
            cb_Product.getSelectionModel().select(component.getProduct());            
        }
        lb_VisibleFormwork.setText(UtilityFormat.getStringForLabel(Assembling_VisibleFormworkController.getInstance().getVisibleFormwork()));
        tf_ProfiHour.setText(UtilityFormat.getStringForTextField(profiHour));
        tf_AdittionalColourFactor.setText(UtilityFormat.getStringForTextField(additionalColourFactor));
        tf_PricePerLiter.setText(UtilityFormat.getStringForTextField(pricePerLiter));
        tf_TimeOfPainting.setText(UtilityFormat.getStringForTextField(timeofPainting));
        lb_MontageCost.setText(UtilityFormat.getStringForLabel(montageCost));
        lb_PaintArea.setText(UtilityFormat.getStringForLabel(paintArea));
        lb_PaintLiter.setText(UtilityFormat.getStringForLabel(paintLiter));
        lb_ProductCost.setText(UtilityFormat.getStringForLabel(productCost));
        lb_TotalCosts.setText(UtilityFormat.getStringForLabel(totalCost));
    }

    public void persist() {
        WorthController worthController = new WorthController();

        Category category = new CategoryController().findCategoryByShortTerm("X");
        component = new ComponentController().findColorByProjectId(ProjectViewController.getOpenedProject().getId());

        if (component == null) {
            component = new Component();
        }
        try {
            Product product = new ProductController().findProduct(cb_Product.getSelectionModel().getSelectedItem().getId());

            component.setDescription(product.getName());
            component.setCategory(category);
            component.setComponentType("Produkt");
            component.setPriceComponent(tf_PricePerLiter.getText().isEmpty() || !tf_PricePerLiter.getText().matches("[0-9]*.[0-9]*")
                    ? null : Double.valueOf(tf_PricePerLiter.getText()));
            component.setNumberOfProducts((int) paintLiter.getWorth());
            component.setUnit(product.getUnit());
            component.setProduct(product);
            component.setComponentType(ProductType.COLOR.toString());
            component.setProject(ProjectViewController.getOpenedProject());
            new ComponentController().edit(component);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (!ProjectViewController.isProjectOpened()) {
            component.setProject(ProjectViewController.getOpenedProject());
            profiHour.setProject(ProjectViewController.getOpenedProject());
            additionalColourFactor.setProject(ProjectViewController.getOpenedProject());
            timeofPainting.setProject(ProjectViewController.getOpenedProject());
            paintArea.setProject(ProjectViewController.getOpenedProject());
            productCost.setProject(ProjectViewController.getOpenedProject());
            montageCost.setProject(ProjectViewController.getOpenedProject());
            paintLiter.setProject(ProjectViewController.getOpenedProject());
            totalCost.setProject(ProjectViewController.getOpenedProject());

            worthController.create(profiHour);
            worthController.create(additionalColourFactor);
            worthController.create(timeofPainting);
            worthController.create(paintArea);
            worthController.create(productCost);
            worthController.create(montageCost);
            worthController.create(paintLiter);
            worthController.create(totalCost);
        } else {
            try {
                worthController.edit(profiHour);
                worthController.edit(additionalColourFactor);
                worthController.edit(timeofPainting);
                worthController.edit(paintArea);
                worthController.edit(productCost);
                worthController.edit(montageCost);
                worthController.edit(paintLiter);
                worthController.edit(totalCost);
            } catch (Exception e) {
            }
        }
    }

    public void calculateValues() {
        //Benötigte Farbe in m²
        //Alte Formel-ID: FMM
        paintArea.setWorth(Assembling_VisibleFormworkController.getInstance().getVisibleFormwork() + additionalColourFactor.getWorth());
        lb_PaintArea.setText(UtilityFormat.getStringForLabel(paintArea));

        //Benötigte Farbe in l
        //Alte Formel-ID:FML
        paintLiter.setWorth(paintArea.getWorth() / 10);
        lb_PaintLiter.setText(UtilityFormat.getStringForLabel(paintLiter));

        //Montage Kosten
        //Alte Formel-ID: KMFarbe
        montageCost.setWorth(profiHour.getWorth() * timeofPainting.getWorth());
        lb_MontageCost.setText(UtilityFormat.getStringForLabel(montageCost));
        
        //product Kosten
        //Alte Formel KPFarbe
        productCost.setWorth(pricePerLiter*paintLiter.getWorth());
        lb_ProductCost.setText(UtilityFormat.getStringForLabel(productCost));
        //Gesamtkosten
        //Alte Formel: GK
        //INSERT INTO FORMULA (ID,LONGTERM,SELECTSTATEMENT,SHORTTERM) values (NEXT VALUE FOR FORMULA_SEQ,'Gesamtkosten Farbe',
        //'select NULLIF((select NULLIF(w.worth, 0) from Worth w join ParameterP p on w.parameter_id = p.id where w.project_id = ? and p.shortterm = ''KPFarbe''), 0) + 
        // NULLIF((select NULLIF(w.worth, 0) from Worth w join ParameterP p on w.parameter_id = p.id where w.project_id = ? and p.shortterm = ''KMFarbe''), 0) from sysibm.sysdummy1','GKFarbe');
        totalCost.setWorth(productCost.getWorth()+montageCost.getWorth());
        lb_TotalCosts.setText(UtilityFormat.getStringForLabel(totalCost));
    }
}
