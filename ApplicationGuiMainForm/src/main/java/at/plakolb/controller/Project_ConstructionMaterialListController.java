/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.*;
import at.plakolb.calculationlogic.db.entity.*;
import at.plakolb.calculationlogic.eunmeration.ProductType;
import at.plakolb.calculationlogic.util.Logging;
import at.plakolb.calculationlogic.util.UtilityFormat;
import at.plakolb.edit.ComponentValueCell;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.util.Callback;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;

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
    private TableColumn<Component, String> tc_Amount;
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

        if (ProjectViewController.getOpenedProject() != null && ProjectViewController.getOpenedProject().getId() != null) {
            components = new ComponentController().findComponentsByProjectIdAndComponentType(ProjectViewController.getOpenedProject().getId(), "Kubikmeter");
        }

        tv_Materials.setEditable(true);

        tf_Amount.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            tf_Amount.setText(tf_Amount.getText().replace(".", ",").replaceAll("[^\\d,]", ""));
            tf_Amount.setText(UtilityFormat.removeUnnecessaryCommas(tf_Amount.getText()));
        });

        tc_Category.setCellValueFactory(new PropertyValueFactory<>("category"));

        tc_ProductName.setCellValueFactory(new PropertyValueFactory<>("product"));

        tc_Length.setCellValueFactory((TableColumn.CellDataFeatures<Component, String> param) -> {
            if (param.getValue().getLengthComponent() != null) {
                return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(param.getValue().getLengthComponent()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });
        tc_Length.setCellFactory((TableColumn<Component, String> param) -> new ComponentValueCell());
        tc_Length.setOnEditCommit((TableColumn.CellEditEvent<Component, String> event) -> {
            Component component = event.getTableView().getItems().get(event.getTablePosition().getRow());
            if (!event.getNewValue().equals("")) {
                component.setLengthComponent(Double.parseDouble(event.getNewValue().replace(',', '.')));
            } else {
                component.setLengthComponent(null);
            }
            refreshTable();
        });

        tc_Amount.setCellValueFactory((TableColumn.CellDataFeatures<Component, String> param) -> {
            if (param.getValue().getNumberOfProducts() != null) {
                return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(param.getValue().getNumberOfProducts()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });
        tc_Amount.setCellFactory((TableColumn<Component, String> param) -> new ComponentValueCell());
        tc_Amount.setOnEditCommit((TableColumn.CellEditEvent<Component, String> event) -> {
            Component component = (event.getTableView().getItems().get(event.getTablePosition().getRow()));
            if (!event.getNewValue().equals("")) {
                component.setNumberOfProducts(Double.parseDouble(event.getNewValue().replace(',', '.')));
            } else {
                component.setNumberOfProducts(null);
            }
            refreshTable();
        });

        tc_Volume.setCellValueFactory((CellDataFeatures<Component, String> param) -> {
            Component component = param.getValue();
            return new ReadOnlyObjectWrapper<>(UtilityFormat.worthWithThreeDecimalPlaces(component.getWidthComponent() / 100 * component.getLengthComponent() * component.getHeightComponent() / 100 * component.getNumberOfProducts()));
        });

        tc_PricePerCubic.setCellValueFactory((CellDataFeatures<Component, String> param) -> {
            Component component = param.getValue();
            return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(component.getPriceComponent() / ((component.getWidthComponent() / 100.0) * component.getLengthComponent() * (component.getHeightComponent() / 100.0) * component.getNumberOfProducts())));
        });
        tc_PricePerCubic.setCellFactory((TableColumn<Component, String> param) -> new ComponentValueCell());
        tc_PricePerCubic.setOnEditCommit((TableColumn.CellEditEvent<Component, String> event) -> {
            Component component = (event.getTableView().getItems().get(event.getTablePosition().getRow()));
            component.setPriceComponent(((component.getWidthComponent() / 100.0) * component.getLengthComponent() * (component.getHeightComponent() / 100.0) * component.getNumberOfProducts()) * (Double.parseDouble(event.getNewValue().replace(',', '.'))));
            refreshTable();
        });

        tc_Price.setCellValueFactory((TableColumn.CellDataFeatures<Component, String> param) -> {
            if (param.getValue().getPriceComponent() != null) {
                return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(param.getValue().getPriceComponent()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });
        tc_Price.setCellFactory((TableColumn<Component, String> param) -> new ComponentValueCell());
        tc_Price.setOnEditCommit((TableColumn.CellEditEvent<Component, String> event) -> {
            Component component = (event.getTableView().getItems().get(event.getTablePosition().getRow()));
            if (!event.getNewValue().equals("")) {
                component.setPriceComponent(Double.parseDouble(event.getNewValue().replace(',', '.')));
            } else {
                component.setPriceComponent(null);
            }
            refreshTable();
        });

        tc_CuttingHours.setCellValueFactory((TableColumn.CellDataFeatures<Component, String> param) -> {
            if (param.getValue().getTailoringHours() != null) {
                return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(param.getValue().getTailoringHours()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });
        tc_CuttingHours.setCellFactory((TableColumn<Component, String> param) -> new ComponentValueCell());
        tc_CuttingHours.setOnEditCommit((TableColumn.CellEditEvent<Component, String> event) -> {
            Component component = (event.getTableView().getItems().get(event.getTablePosition().getRow()));
            if (!event.getNewValue().equals("")) {
                component.setTailoringHours(Double.parseDouble(event.getNewValue().replace(',', '.')));
            } else {
                component.setTailoringHours(null);
            }
            refreshTable();
        });

        tc_CuttingPricePerHours.setCellValueFactory((TableColumn.CellDataFeatures<Component, String> param) -> {
            if (param.getValue().getTailoringPricePerHour() != null) {
                return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(param.getValue().getTailoringPricePerHour()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });
        tc_CuttingPricePerHours.setCellFactory((TableColumn<Component, String> param) -> new ComponentValueCell());
        tc_CuttingPricePerHours.setOnEditCommit((TableColumn.CellEditEvent<Component, String> event) -> {
            Component component = (event.getTableView().getItems().get(event.getTablePosition().getRow()));
            if (!event.getNewValue().equals("")) {
                component.setTailoringPricePerHour(Double.parseDouble(event.getNewValue().replace(',', '.')));
            } else {
                component.setTailoringPricePerHour(null);
            }
            refreshTable();
        });

        tc_CuttingPrice.setCellValueFactory((CellDataFeatures<Component, String> param) -> {
            Component component = param.getValue();
            return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(component.getTailoringHours() * component.getTailoringPricePerHour()));
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

                                Alert alert = null;

                                if (Project_ConstructionMaterialController.getInstance().getAssemblyCount() > 0) {
                                    alert = new Alert(Alert.AlertType.CONFIRMATION, "Wenn Sie dieses Material löschen, werden auch alle dazugehörigen Produkte gelöscht:\n" + getBelongings(tv_Materials.getSelectionModel().getSelectedItem()) + ".\nMöchten Sie troztdem fortfahren?",
                                            ButtonType.YES, ButtonType.CANCEL);
                                    alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
                                    alert.showAndWait();
                                    if (alert.getResult() == ButtonType.YES) {
                                        Project_ConstructionMaterialController.getInstance().deleteRelativeAssemblies(tv_Materials.getSelectionModel().getSelectedItem());

                                        try {
                                            components.remove(tv_Materials.getSelectionModel().getSelectedItem());
                                            if (tv_Materials.getSelectionModel().getSelectedItem().getId() != null) {
                                                new ComponentController().destroy(tv_Materials.getSelectionModel().getSelectedItem().getId());
                                            }
                                            ModifyController.getInstance().setProject_constructionmaterialList(Boolean.TRUE);
                                        } catch (Exception ex) {
                                            Logging.getLogger().log(Level.SEVERE, "Couldn't delete material.", ex);
                                        } finally {
                                            refreshTable();
                                        }
                                    }
                                } else {
                                    alert = new Alert(Alert.AlertType.CONFIRMATION, "Sind Sie sicher, dass sie dieses Material entgültig löschen möchten?",
                                            ButtonType.YES, ButtonType.CANCEL);
                                    alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
                                    alert.showAndWait();

                                    if (alert.getResult() == ButtonType.YES) {
                                        try {
                                            components.remove(tv_Materials.getSelectionModel().getSelectedItem());
                                            if (tv_Materials.getSelectionModel().getSelectedItem().getId() != null) {
                                                new ComponentController().destroy(tv_Materials.getSelectionModel().getSelectedItem().getId());
                                            }
                                            ModifyController.getInstance().setProject_constructionmaterialList(Boolean.TRUE);
                                        } catch (Exception ex) {
                                            Logging.getLogger().log(Level.SEVERE, "Couldn't delete material.", ex);
                                        } finally {
                                            refreshTable();
                                        }
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

        ModifyController.getInstance().setProject_constructionmaterialList(Boolean.FALSE);
    }

    public List<Component> getComponents() {
        return components;
    }

    public static Project_ConstructionMaterialListController getInstance() {
        return instance;
    }

    public double getWage() {
        return Double.parseDouble(lb_CuttingCostSum.getText().substring(0, lb_CuttingCostSum.getText().length() - 2).replace(',', '.'));
    }

    public double getMaterial() {
        return Double.parseDouble(lb_MaterialCostSum.getText().substring(0, lb_MaterialCostSum.getText().length() - 2).replace(',', '.'));
    }

    public double getTotalCosts() {
        return Double.parseDouble(lb_TotalCosts.getText().substring(0, lb_TotalCosts.getText().length() - 2).replace(',', '.'));
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
            if (tf_Amount.getText().isEmpty() || Double.parseDouble(tf_Amount.getText().replace(',', '.')) <= 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Bitte geben Sie zum Erstellen eine gültige Zahl ein, die größer als 0 ist.");
                alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
                alert.showAndWait();
            } else {
                Component component = new Component(product.getFullName(),
                        product.getWidthProduct(),
                        product.getHeightProduct(),
                        product.getLengthProduct(),
                        product.getPriceUnit() * Double.parseDouble(tf_Amount.getText().replace(',', '.')),
                        Double.parseDouble(tf_Amount.getText().replace(',', '.')),
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
                ModifyController.getInstance().setProject_constructionmaterialList(Boolean.TRUE);
            }
        } catch (NumberFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Die Anzahl darf nur Zahlen enthalten");
            alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
            alert.showAndWait();
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

        for(Component component : components){
            System.out.println(component.getFullNameProduct() + ": ");
            for (Assembly assembly : component.getAssemblys()){
                System.out.println(assembly.toString());
            }
            System.out.println("\n");
        }

    }

    /**
     * Calulates the sums of certain TableColumns
     */
    private void calculateCosts() {
        lb_CubicSum.setText(UtilityFormat.getStringForLabel(getColmunSum(tc_Volume)) + " €");
        lb_MaterialCostSum.setText(UtilityFormat.getStringForLabel(getColmunSum(tc_Price)) + " €");
        lb_CuttingTimeSum.setText(UtilityFormat.getStringForLabel(getColmunSum(tc_CuttingHours)) + " €");
        lb_CuttingCostSum.setText(UtilityFormat.getStringForLabel(getColmunSum(tc_CuttingPrice)) + " €");
        lb_TotalCosts.setText(UtilityFormat.getStringForLabel(getColmunSum(tc_CuttingPrice) + getColmunSum(tc_Price)) + " €");

        ModifyController.getInstance().setProject_constructionmaterialList(Boolean.TRUE);
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
            sum += Double.parseDouble(column.getCellData(component).replace(',', '.'));
        }
        return sum;
    }

    /**
     * Persist all new components to the database.
     */
    public void persist() {
        try {
            ComponentController componentController = new ComponentController();
            WorthController worthController = new WorthController();
            ParameterController parameterController = new ParameterController();
            for (Component component : components) {
                if (component.getId() == null) {
                    componentController.create(component);
                } else {

                    componentController.edit(component);

                }
            }
            if (ProjectViewController.isProjectOpened() == false) {
                worthController.create(new Worth(ProjectViewController.getOpenedProject(), parameterController.findParameterPByShortTerm("GP"), getColmunSum(tc_Price)));
                worthController.create(new Worth(ProjectViewController.getOpenedProject(), parameterController.findParameterPByShortTerm("KZPG"), getColmunSum(tc_CuttingPrice)));
                worthController.create(new Worth(ProjectViewController.getOpenedProject(), parameterController.findParameterPByShortTerm("GKV"), getColmunSum(tc_CuttingPrice) + getColmunSum(tc_Price)));
            } else {
                Worth materialCost = worthController.findWorthByParameterIdAndProjectId(parameterController.findParameterPByShortTerm("GP").getId(), ProjectViewController.getOpenedProject().getId());
                materialCost.setWorth(getColmunSum(tc_Price));
                Worth wageCost = worthController.findWorthByParameterIdAndProjectId(parameterController.findParameterPByShortTerm("KZPG").getId(), ProjectViewController.getOpenedProject().getId());
                wageCost.setWorth(getColmunSum(tc_CuttingPrice));
                Worth totalCost = worthController.findWorthByParameterIdAndProjectId(parameterController.findParameterPByShortTerm("GKV").getId(), ProjectViewController.getOpenedProject().getId());
                totalCost.setWorth(getColmunSum(tc_CuttingPrice)+getColmunSum(tc_Price));
                worthController.edit(materialCost);
                worthController.edit(wageCost);
                worthController.edit(totalCost);
            }
        } catch (Exception e) {
            Logging.getLogger().log(Level.SEVERE, "Project_ConstructionMaterialListController: persist method didn't work.", e);
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

    private String getBelongings(Component component) {

        String belongings = "";

        if (component != null) {

            for (Assembly assembly : Project_ConstructionMaterialController.getInstance().getAssemblys()) {
                if (assembly.getComponent().equals(component)) {
                    belongings += ("- " + assembly.getProduct().toString() + "\n");
                }
            }
        }
        return belongings;
    }
}
