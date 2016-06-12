/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.entity.Component;
import at.plakolb.calculationlogic.db.entity.Unit;
import at.plakolb.calculationlogic.util.UtilityFormat;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class Project_MaterialAndCostController implements Initializable {

    private static Project_MaterialAndCostController instance;
    @FXML
    private TableView<Component> tv_Materials;
    @FXML
    private TableColumn<Component, String> tc_Category;
    @FXML
    private TableColumn<Component, String> tc_Component;
    @FXML
    private TableColumn<Component, String> tc_Width;
    @FXML
    private TableColumn<Component, String> tc_Height;
    @FXML
    private TableColumn<Component, String> tc_Length;
    @FXML
    private TableColumn<Component, String> tc_Amount;
    @FXML
    private TableColumn<Component, Unit> tc_Unit;
    @FXML
    private TableColumn<Component, String> tc_PricePerUnit;
    @FXML
    private TableColumn<Component, String> tc_TotalCosts;

    private List<Component> components;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        components = new LinkedList<>();

        tc_Category.setCellValueFactory(new PropertyValueFactory<>("category"));

        tc_Component.setCellValueFactory((TableColumn.CellDataFeatures<Component, String> param) -> {
            return new ReadOnlyObjectWrapper<>(param.getValue().getProduct().getFullName());
        });

        tc_Width.setCellValueFactory((TableColumn.CellDataFeatures<Component, String> param) -> {
            if (param.getValue().getProduct().getWidthProduct() != null) {
                return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(param.getValue().getProduct().getWidthProduct()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        tc_Height.setCellValueFactory((TableColumn.CellDataFeatures<Component, String> param) -> {
            if (param.getValue().getProduct().getHeightProduct() != null) {
                return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(param.getValue().getProduct().getHeightProduct()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        tc_Length.setCellValueFactory((TableColumn.CellDataFeatures<Component, String> param) -> {
            if (param.getValue().getProduct().getLengthProduct() != null) {
                return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(param.getValue().getProduct().getLengthProduct()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        tc_Amount.setCellValueFactory((TableColumn.CellDataFeatures<Component, String> param) -> {
            if (param.getValue().getNumberOfProducts() != null) {
                return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(param.getValue().getNumberOfProducts()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        tc_Unit.setCellValueFactory(new PropertyValueFactory<>("unit"));

        tc_PricePerUnit.setCellValueFactory((TableColumn.CellDataFeatures<Component, String> param) -> {
            if (param.getValue().getPriceComponent() != null) {
                return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(param.getValue().getPriceComponent()) + " €");
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        tc_TotalCosts.setCellValueFactory((TableColumn.CellDataFeatures<Component, String> param) -> {
            if (param.getValue().getProduct() != null && param.getValue().getNumberOfProducts() != null) {
                return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(param.getValue().getNumberOfProducts() * param.getValue().getPriceComponent()) + " €");
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        refreshTableView();
    }

    public static Project_MaterialAndCostController getInstance() {
        return instance;
    }

    public void refreshTableView() {
        components.clear();
        components.addAll(Project_ConstructionMaterialListController.getInstance().getComponents());

        if (Assembling_FormworkController.getInstance().getComponent().getProduct() != null) {
            components.add(Assembling_FormworkController.getInstance().getComponent());
        }

        if (Assembling_VisibleFormworkController.getInstance().getComponent().getProduct() != null) {
            components.add(Assembling_VisibleFormworkController.getInstance().getComponent());
        }

        if (Assembling_FoilController.getInstance().getComponent().getProduct() != null) {
            components.add(Assembling_FoilController.getInstance().getComponent());
        }

        if (Assembling_SealingBandController.getInstance().getComponent().getProduct() != null) {
            components.add(Assembling_SealingBandController.getInstance().getComponent());
        }

        if (Assembling_CounterBattenController.getInstance().getComponent().getProduct() != null) {
            components.add(Assembling_CounterBattenController.getInstance().getComponent());
        }

        if (Project_ColourController.getInstance().getComponent().getProduct() != null) {
            components.add(Project_ColourController.getInstance().getComponent());
        }

        if (Assembling_BattensOrFullFormworkController.getInstance().getComponent().getProduct() != null) {
            components.add(Assembling_BattensOrFullFormworkController.getInstance().getComponent());
        }

        tv_Materials.setItems(FXCollections.observableArrayList(components));
        tv_Materials.getColumns().get(0).setVisible(false);
        tv_Materials.getColumns().get(0).setVisible(true);
    }
}
