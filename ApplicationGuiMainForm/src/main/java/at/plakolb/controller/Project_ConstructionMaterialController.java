/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.AssemblyController;
import at.plakolb.calculationlogic.db.controller.ParameterController;
import at.plakolb.calculationlogic.db.controller.ProductController;
import at.plakolb.calculationlogic.db.controller.WorthController;
import at.plakolb.calculationlogic.db.entity.Assembly;
import at.plakolb.calculationlogic.db.entity.Component;
import at.plakolb.calculationlogic.db.entity.Product;
import at.plakolb.calculationlogic.db.entity.Worth;
import at.plakolb.calculationlogic.eunmeration.ProductType;
import at.plakolb.calculationlogic.util.Logging;
import at.plakolb.calculationlogic.util.UtilityFormat;
import at.plakolb.edit.AssemblyValueCell;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * FXML Controller class
 *
 * @author User
 */
public class Project_ConstructionMaterialController implements Initializable {

    private static Project_ConstructionMaterialController instance;
    @FXML
    private ComboBox<Product> cb_Product;
    @FXML
    private TextField tf_Amount;
    @FXML
    private TextField tf_Price;
    @FXML
    private ComboBox<Component> cb_Component;
    @FXML
    private TableView<Assembly> tv_Assembly;
    @FXML
    private TableColumn<Assembly, String> tc_Product;
    @FXML
    private TableColumn<Assembly, String> tc_Amount;
    @FXML
    private TableColumn<Assembly, String> tc_Price;
    @FXML
    private TableColumn<Assembly, String> tc_Component;
    @FXML
    private TableColumn<Assembly, String> tc_TotalPrice;
    @FXML
    private TableColumn tc_Button;
    @FXML
    private Label lb_TotalCosts;


    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;

        tv_Assembly.setEditable(true);

        tf_Amount.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            tf_Amount.setText(tf_Amount.getText().replace('.', ',').replaceAll("[^\\d,]", ""));
            tf_Amount.setText(UtilityFormat.removeUnnecessaryCommas(tf_Amount.getText()));
        });
        tc_Amount.setCellFactory((TableColumn<Assembly, String> param) -> new AssemblyValueCell());
        tc_Amount.setOnEditCommit((TableColumn.CellEditEvent<Assembly, String> event) -> {
            Assembly assembly = (event.getTableView().getItems().get(event.getTablePosition().getRow()));
            if (!event.getNewValue().equals("")) {
                assembly.setNumberOfComponents(Double.parseDouble(event.getNewValue().replace(',', '.')));
            } else {
                assembly.setNumberOfComponents(null);
            }
            ModifyController.getInstance().setProject_constructionMaterial(Boolean.TRUE);
            refreshListView();
        });

        tf_Price.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            tf_Price.setText(tf_Price.getText().replace('.', ',').replaceAll("[^\\d,]", ""));
            tf_Price.setText(UtilityFormat.removeUnnecessaryCommas(tf_Price.getText()));
        });

        tc_Product.setCellValueFactory(new PropertyValueFactory<>("product"));

        tc_Amount.setCellValueFactory((TableColumn.CellDataFeatures<Assembly, String> param) -> {
            if (param.getValue().getNumberOfComponents() != null) {
                return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(param.getValue().getNumberOfComponents()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        tc_Component.setCellValueFactory(new PropertyValueFactory<>("component"));

        tc_Price.setCellValueFactory((TableColumn.CellDataFeatures<Assembly, String> param) -> {
            if (param.getValue().getPrice() != null) {
                return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(param.getValue().getPrice()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });
        tc_Price.setCellFactory((TableColumn<Assembly, String> param) -> new AssemblyValueCell());
        tc_Price.setOnEditCommit((TableColumn.CellEditEvent<Assembly, String> event) -> {
            Assembly assembly = (event.getTableView().getItems().get(event.getTablePosition().getRow()));
            if (!event.getNewValue().equals("")) {
                assembly.setPrice(Double.parseDouble(event.getNewValue().replace(',', '.')));
            } else {
                assembly.setPrice(null);
            }
            ModifyController.getInstance().setProject_constructionMaterial(Boolean.TRUE);
            refreshListView();
        });

        tc_TotalPrice.setCellValueFactory((TableColumn.CellDataFeatures<Assembly, String> param)
                -> new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(param.getValue().getPrice() * param.getValue().getNumberOfComponents()) + " €")
        );

        tc_Button.setCellFactory(new Callback<TableColumn<Component, String>, TableCell<Component, String>>() {
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
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Sind Sie sicher, dass sie dieses Material entgültig löschen möchten?\nVorsicht, der Löschvorgang kann nicht mehr rückgängig gemacht werden.",
                                        ButtonType.YES, ButtonType.CANCEL);
                                alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
                                alert.showAndWait();
                                if (alert.getResult() == ButtonType.YES) {
                                    try {
                                        Assembly assembly = tv_Assembly.getSelectionModel().getSelectedItem();
                                        for (Component c : getComponentList()) {
                                            if (c.getAssemblys().contains(assembly)) {
                                                c.getAssemblys().remove(assembly);
                                                System.out.println("Assembly gelöscht");
                                            }
                                        }
                                        refreshListView();
                                        ModifyController.getInstance().setProject_constructionMaterial(Boolean.TRUE);
                                    } catch (Exception ex) {
                                        Logging.getLogger().log(Level.SEVERE, "Couldn't delete material.", ex);
                                    } finally {
                                        refreshListView();
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

        cb_Product.setItems(FXCollections.observableArrayList(new ProductController().findByProductTypeOrderByName(ProductType.MISCELLANEOUS)));
        cb_Component.setItems(FXCollections.observableArrayList(Project_ConstructionMaterialListController.getInstance().getComponents()));

        cb_Product.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Product> observable, Product oldValue, Product newValue) -> {
            if (cb_Product.getSelectionModel().getSelectedItem() != null) {
                tf_Price.setText(UtilityFormat.getStringForTextField(cb_Product.getSelectionModel().getSelectedItem().getPriceUnit()));
            }
        });

        refreshListView();
        ModifyController.getInstance().setProject_constructionMaterial(Boolean.FALSE);
    }

    public static Project_ConstructionMaterialController getInstance() {
        return instance;
    }

    public Double getMaterial() {
        return getTotalCosts(getAssemblies());
    }

    public List<Assembly> getAssemblys() {
        return getAssemblies();
    }

    public void refreshListView() {
        tv_Assembly.setItems(FXCollections.observableArrayList(getAssemblies()));
        tv_Assembly.refresh();
        lb_TotalCosts.setText(UtilityFormat.getStringForLabel(getTotalCosts(getAssemblies())) + " €");
    }


    @FXML
    private void addMaterial(ActionEvent event) {

        double amount = 0;
        double price = 0;
        String errorMessage = "";

        if (cb_Product.getSelectionModel().getSelectedItem() == null) {
            errorMessage += "Es wurde kein Produkt ausgewählt.\n";
        }
        if (cb_Component.getSelectionModel().getSelectedItem() == null) {
            errorMessage += "Es wurde kein Bauteil ausgewählt.\n";
        }

        try {
            amount = Double.parseDouble(tf_Amount.getText().replace(',', '.'));
            price = Double.parseDouble(tf_Price.getText().replace(',', '.'));

            if (amount == 0) {
                errorMessage += "Die Anzahl muss größer als 0 sein.\n";
            }
            if (amount < 0 || price < 0) {
                errorMessage += "Negative Zahlen sind nicht erlaubt.\n";
            }
        } catch (NumberFormatException ex) {
            errorMessage += "Die eingegebenen Zahlen sind nicht gültig.\n";
        }

        if (errorMessage.equals("")) {

            Component component = cb_Component.getSelectionModel().getSelectedItem();
            component.setProject(ProjectViewController.getOpenedProject());

            new Assembly(cb_Product.getSelectionModel().getSelectedItem(),
                    component,
                    ProjectViewController.getOpenedProject(),
                    amount,
                    price);
            ModifyController.getInstance().setProject_constructionMaterial(Boolean.TRUE);
            refreshListView();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, errorMessage);
            alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
            alert.showAndWait();
        }
    }

    public void persist() {
        try {
            AssemblyController assemblyController = new AssemblyController();
            WorthController worthController = new WorthController();
            ParameterController parameterController = new ParameterController();

            for (Assembly assembly : getAssemblies()) {
                if (assembly.getId() == null) {
                    assemblyController.create(assembly);
                } else {
                    assemblyController.edit(assembly);
                }
            }
            if (ProjectViewController.isProjectOpened() == false) {
                worthController.create(new Worth(ProjectViewController.getOpenedProject(), parameterController.findParameterPByShortTerm("GMFM"), getMaterial()));
            } else {
                Worth totalCost = worthController.findWorthByParameterIdAndProjectId(parameterController.findParameterPByShortTerm("GMFM").getId(), ProjectViewController.getOpenedProject().getId());
                totalCost.setWorth(getMaterial());
                worthController.edit(totalCost);
            }
        } catch (Exception e) {
            Logging.getLogger().log(Level.SEVERE, "Project_ConstructionMaterialController: persist method didn't work.", e);
        }
    }

    public void refreshComponents() {
        cb_Component.setItems(FXCollections.observableArrayList(getComponentList()));

        if (cb_Component.getItems().isEmpty()) {
            cb_Component.setPromptText("Kein Bauteil vorhanden");
        } else {
            cb_Component.setPromptText("Bauteil auswählen");
        }
    }

    private double getTotalCosts(List<Assembly> assemblys) {

        double totalCosts = 0;

        for (Assembly assembly : assemblys) {
            totalCosts += assembly.getPrice() * assembly.getNumberOfComponents();
        }

        return totalCosts;
    }

    public int getAssemblyCount() {
        return getAssemblies().size();
    }

    public void deleteRelativeAssemblies(Component c) {
        try {
            if (getComponentList().contains(c)) {
                c.getAssemblys().clear();
                ModifyController.getInstance().setProject_constructionMaterial(Boolean.TRUE);
            }
        } catch (Exception ex) {
            Logging.getLogger().log(Level.SEVERE, "Couldn't delete relative assemblies.", ex);
        } finally {
            refreshListView();
        }
    }

    private List<Assembly> getAssemblies() {
        List<Assembly> resultList = new LinkedList<>();
        for (Component c : getComponentList()) {
            resultList.addAll(c.getAssemblys());
        }
        return resultList;
    }
    private List<Component> getComponentList(){
        List<Component> componentList = new ArrayList<>();
        componentList.addAll(Project_ConstructionMaterialListController.getInstance().getComponents());

        if (Assembling_FormworkController.getInstance().getComponent().getProduct() != null) {
            componentList.add(Assembling_FormworkController.getInstance().getComponent());
        }

        if (Assembling_VisibleFormworkController.getInstance().getComponent().getProduct() != null) {
            componentList.add(Assembling_VisibleFormworkController.getInstance().getComponent());
        }

        if (Assembling_FoilController.getInstance().getComponent().getProduct() != null) {
            componentList.add(Assembling_FoilController.getInstance().getComponent());
        }

        if (Assembling_SealingBandController.getInstance().getComponent().getProduct() != null) {
            componentList.add(Assembling_SealingBandController.getInstance().getComponent());
        }

        if (Assembling_CounterBattenController.getInstance().getComponent().getProduct() != null) {
            componentList.add(Assembling_CounterBattenController.getInstance().getComponent());
        }

        if (Assembling_BattensOrFullFormworkController.getInstance().getComponent().getProduct() != null) {
            componentList.add(Assembling_BattensOrFullFormworkController.getInstance().getComponent());
        }

        cb_Component.setItems(FXCollections.observableArrayList(componentList));

        if (cb_Component.getItems().isEmpty()) {
            cb_Component.setPromptText("Kein Bauteil vorhanden");
        } else {
            cb_Component.setPromptText("Bauteil auswählen");
        }
        return componentList;
    }
}
