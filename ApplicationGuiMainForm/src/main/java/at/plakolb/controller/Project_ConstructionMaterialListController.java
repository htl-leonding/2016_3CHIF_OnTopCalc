/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.CategoryController;
import at.plakolb.calculationlogic.db.controller.ComponentController;
import at.plakolb.calculationlogic.db.controller.ParameterController;
import at.plakolb.calculationlogic.db.controller.ProductController;
import at.plakolb.calculationlogic.entity.Category;
import at.plakolb.calculationlogic.entity.Component;
import at.plakolb.calculationlogic.entity.Product;
import at.plakolb.calculationlogic.eunmeration.ProductType;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class Project_ConstructionMaterialListController extends java.util.Observable implements Initializable {

    private static Project_ConstructionMaterialListController instance;
    @FXML
    private TableView<Component> tv_Materials;
    @FXML
    private TableColumn<Component, Category> tc_Category;
    @FXML
    private TableColumn<Component, String> tc_ProductName;
    @FXML
    private TableColumn<Component, String> tc_Length;
    @FXML
    private TableColumn<Component, Integer> tc_Amount;
    @FXML
    private TableColumn<Component, String> tc_Volume;
    @FXML
    private TableColumn<Component, String> tc_PricePerCubic;
    @FXML
    private TableColumn<Component, String> tc_Price;
    @FXML
    private TableColumn<Component, String> tc_CuttingHours;
    @FXML
    private TableColumn<Component, String> tc_CuttingPricePerHours;
    @FXML
    private TableColumn<Component, String> tc_CuttingPrice;
    @FXML
    private TableColumn tc_Deletion;
    @FXML
    private ComboBox<Category> cb_Category;
    @FXML
    private ComboBox<Product> cb_Product;
    @FXML
    private TextField tf_Amount;
    @FXML
    private Label lb_CubicSum;
    @FXML
    private Label lb_MaterialCostSum;
    @FXML
    private Label lb_CuttingTimeSum;
    @FXML
    private Label lb_CuttingCostSum;
    @FXML
    private Label lb_TotalCosts;

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
        DecimalFormat decimalFormatTwo = new DecimalFormat("#.##");
        decimalFormatTwo.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
        DecimalFormat decimalFormatFour = new DecimalFormat("#.####");
        decimalFormatFour.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
        if (ProjectViewController.getOpenedProject() != null && ProjectViewController.getOpenedProject().getId() != null) {
            components = new ComponentController().findComponentsByProjectIdAndComponentType(ProjectViewController.getOpenedProject().getId(), "Kubikmeter");
        }

        tc_Category.setCellValueFactory(new PropertyValueFactory<>("category"));
        tc_ProductName.setCellValueFactory(new PropertyValueFactory<>("product"));

        tc_Length.setCellValueFactory((TableColumn.CellDataFeatures<Component, String> param) -> {
            if (param.getValue().getLengthComponent() != null) {
                return new ReadOnlyObjectWrapper<>(decimalFormatTwo.format(param.getValue().getLengthComponent()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        tc_Amount.setCellValueFactory(new PropertyValueFactory<>("numberOfProducts"));

        tc_Volume.setCellValueFactory((CellDataFeatures<Component, String> param) -> {
            Component component = param.getValue();
            return new ReadOnlyObjectWrapper<>(decimalFormatFour.format(component.getWidthComponent() / 100 * component.getLengthComponent() * component.getHeightComponent() / 100 * component.getNumberOfProducts()));
        });

        tc_PricePerCubic.setCellValueFactory((CellDataFeatures<Component, String> param) -> {
            Component component = param.getValue();
            return new ReadOnlyObjectWrapper<>(decimalFormatTwo.format(component.getPriceComponent() / ((component.getWidthComponent() / 100.0) * component.getLengthComponent() * (component.getHeightComponent() / 100.0) * component.getNumberOfProducts())));
        });

        tc_Price.setCellValueFactory((TableColumn.CellDataFeatures<Component, String> param) -> {
            if (param.getValue().getPriceComponent() != null) {
                return new ReadOnlyObjectWrapper<>(decimalFormatTwo.format(param.getValue().getPriceComponent()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        tc_CuttingHours.setCellValueFactory((TableColumn.CellDataFeatures<Component, String> param) -> {
            if (param.getValue().getTailoringHours() != null) {
                return new ReadOnlyObjectWrapper<>(decimalFormatTwo.format(param.getValue().getTailoringHours()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        tc_CuttingPricePerHours.setCellValueFactory((TableColumn.CellDataFeatures<Component, String> param) -> {
            if (param.getValue().getTailoringPricePerHour() != null) {
                return new ReadOnlyObjectWrapper<>(decimalFormatTwo.format(param.getValue().getTailoringPricePerHour()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        tc_CuttingPrice.setCellValueFactory((CellDataFeatures<Component, String> param) -> {
            Component component = param.getValue();
            return new ReadOnlyObjectWrapper<>(decimalFormatTwo.format(component.getTailoringHours() * component.getTailoringPricePerHour()));
        });

        /**
         * Delete Button
         */
        tc_Deletion.setCellFactory(new Callback<TableColumn<Component, String>, TableCell<Component, String>>() {
            @Override
            public TableCell<Component, String> call(TableColumn<Component, String> param) {
                TableCell<Component, String> cell = new TableCell<Component, String>() {

                    Label deletionLabel = new Label();

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(null);

                        if (empty) {
                            setGraphic(null);
                        } else {
                            deletionLabel.setId("delete");
                            deletionLabel.setTooltip(new Tooltip("Material löschen"));

                            deletionLabel.setOnMouseClicked(event -> {
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Sind Sie sicher, dass sie dieses Material entgültig löschen möchten? Vorsicht, der Löschvorgang kann nicht mehr rückgängig gemacht werden.",
                                        ButtonType.YES, ButtonType.CANCEL);
                                alert.showAndWait();
                                if (alert.getResult() == ButtonType.YES) {
                                    try {
                                        components.remove(tv_Materials.getSelectionModel().getSelectedItem());
                                        if (tv_Materials.getSelectionModel().getSelectedItem().getId() != null) {
                                            new ComponentController().destroy(tv_Materials.getSelectionModel().getSelectedItem().getId());
                                        }
                                    } catch (Exception e) {
                                    } finally {
                                        refreshTable();
                                    }
                                }
                            });
                            setGraphic(deletionLabel);
                        }
                    }
                };
                return cell;
            }
        });

        List<Category> categoryList = new LinkedList<>();
        categoryList.add(new CategoryController().findCategoryByShortTerm("K"));
        categoryList.add(new CategoryController().findCategoryByShortTerm("KD"));

        cb_Category.setItems(FXCollections.observableArrayList(categoryList));
        cb_Product.setItems(FXCollections.observableArrayList(new ProductController().findByProductTypeOrderByName(ProductType.WOOD)));
        cb_Category.getSelectionModel().select(0);
        cb_Product.getSelectionModel().select(0);

        refreshTable();

        tv_Materials.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tv_Materials.getSelectionModel().getSelectedItem() != null) {
                Parent root;
                try {
                    root = FXMLLoader.load(getClass().getResource("/fxml/MaterialModifier.fxml"));
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setTitle("Material");
                    stage.setScene(scene);
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(((Node) event.getSource()).getScene().getWindow());
                    stage.show();
                } catch (IOException ex) {
                }
                MaterialModifierController.getInstance().loadProductIntoModifier(tv_Materials.getSelectionModel().getSelectedItem());
            }
        });
    }

    public List<Component> getComponents() {
        return components;
    }

    public static Project_ConstructionMaterialListController getInstance() {
        return instance;
    }
    
    public double getWage(){
        return Double.parseDouble(lb_CuttingCostSum.getText().substring(0, lb_CuttingCostSum.getText().length() - 2));
    }
    
    public double getMaterial(){
        return Double.parseDouble(lb_MaterialCostSum.getText().substring(0, lb_MaterialCostSum.getText().length() - 2));
    }
    
    public double getTotalCosts(){
        return Double.parseDouble(lb_TotalCosts.getText().substring(0, lb_TotalCosts.getText().length() - 2));
    }

    /**
     * Adds a new component from the UI elements.
     *
     * @param event
     */
    @FXML
    private void addProduct(ActionEvent event) {
        ParameterController parameterController = new ParameterController();
        Product product = cb_Product.getValue();
        Category category = cb_Category.getValue();

        try {
            if (tf_Amount.getText().isEmpty() || tf_Amount.getText().equals("0") || tf_Amount.getText().contains("-")) {
                new Alert(Alert.AlertType.ERROR, "Bitte geben Sie zum Erstellen eine Anzahl an die größer als null ist.").showAndWait();
            } else {
                Component component = new Component(product.getFullName(),
                        product.getWidthProduct(),
                        product.getHeightProduct(),
                        product.getLengthProduct(),
                        product.getPriceUnit() * Double.parseDouble(tf_Amount.getText()),
                        Double.parseDouble(tf_Amount.getText()),
                        category,
                        product.getUnit(),
                        product,
                        ProjectViewController.getOpenedProject());

                component.setTailoringHours(parameterController.findParameterPByShortTerm("KZG").getDefaultValue());
                component.setTailoringPricePerHour(parameterController.findParameterPByShortTerm("KPSZ").getDefaultValue());
                component.setComponentType("Kubikmeter");

                components.add(component);
                if (Long.compare(category.getId(), new CategoryController().findCategoryByShortTerm("KD").getId()) == 0) {
                    setChanged();
                    notifyObservers();
                }
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Die Anzahl darf nur Zahlen enthalten").showAndWait();
        }

        refreshTable();
    }

    /**
     * Refreshes the TableView
     */
    public void refreshTable() {
        tv_Materials.setItems(FXCollections.observableArrayList(components));
        tv_Materials.getColumns().get(0).setVisible(false);
        tv_Materials.getColumns().get(0).setVisible(true);
        calculateCosts();
        setChanged();
        notifyObservers();
    }

    /**
     * Calulates the sums of certain TableColumns
     */
    private void calculateCosts() {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
        lb_CubicSum.setText(decimalFormat.format(getColmunSum(tc_Volume)) + " m³");
        lb_MaterialCostSum.setText(decimalFormat.format(getColmunSum(tc_Price)) + " €");
        lb_CuttingTimeSum.setText(decimalFormat.format(getColmunSum(tc_CuttingHours)) + " h");
        lb_CuttingCostSum.setText(decimalFormat.format(getColmunSum(tc_CuttingPrice)) + " €");
        lb_TotalCosts.setText(decimalFormat.format(getColmunSum(tc_CuttingPrice) + getColmunSum(tc_Price)) + " €");
    }

    /**
     * Calulates the sum of one TableColumn.
     *
     * @param column
     * @return
     */
    private double getColmunSum(TableColumn<Component, String> column) {
        double sum = 0;
        for (Component component : tv_Materials.getItems()) {
            sum += Double.parseDouble(column.getCellData(component));
        }
        return sum;
    }

    /**
     * Persist all new components to the database.
     */
    public void persist() {
        ComponentController componentController = new ComponentController();
        for (Component component : components) {
            if (component.getId() == null) {
                componentController.create(component);
            }
        }
    }

    public List<Component> getRafterList() {
        List<Component> list = new LinkedList<>();
        Category cat = new CategoryController().findCategoryByShortTerm("KD");

        for (Component c : components) {
            if (Long.compare(c.getCategory().getId(), cat.getId()) == 0) {
                list.add(c);
            }
        }
        return list;
    }

    public double getTotalRafterLength() {
        double res = 0;
        for (Component c : getRafterList()) {
            res += c.getLengthComponent() * c.getNumberOfProducts();
        }
        return res;
    }
}
