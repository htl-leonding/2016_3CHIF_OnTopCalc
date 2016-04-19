/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.AssemblyController;
import at.plakolb.calculationlogic.db.controller.ComponentController;
import at.plakolb.calculationlogic.db.controller.ProductController;
import at.plakolb.calculationlogic.entity.Assembly;
import at.plakolb.calculationlogic.entity.Component;
import at.plakolb.calculationlogic.entity.Product;
import at.plakolb.calculationlogic.eunmeration.ProductType;
import at.plakolb.calculationlogic.util.UtilityFormat;
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
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

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
    private TableColumn<Assembly, Integer> tc_Amount;
    @FXML
    private TableColumn<Assembly, String> tc_Price;
    @FXML
    private TableColumn<Assembly, String> tc_Component;
    @FXML
    private TableColumn<Assembly, String> tc_TotalPrice;
    @FXML
    private TableColumn tc_Button;

    private List<Assembly> assemblyList;
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
        assemblyList = new LinkedList<>();

        if (ProjectViewController.getOpenedProject() != null && ProjectViewController.getOpenedProject().getId() != null) {
            assemblyList = new AssemblyController().findAssembliesByProjectId(ProjectViewController.getOpenedProject().getId());
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));

        tc_Product.setCellValueFactory(new PropertyValueFactory<>("product"));
        tc_Amount.setCellValueFactory(new PropertyValueFactory<>("numberOfComponents"));
        tc_Component.setCellValueFactory(new PropertyValueFactory<>("component"));

        tc_Price.setCellValueFactory((TableColumn.CellDataFeatures<Assembly, String> param)
                -> new ReadOnlyObjectWrapper<>(decimalFormat.format(param.getValue().getPrice()))
        );

        tc_TotalPrice.setCellValueFactory((TableColumn.CellDataFeatures<Assembly, String> param)
                -> new ReadOnlyObjectWrapper<>(decimalFormat.format(param.getValue().getPrice() * param.getValue().getNumberOfComponents()))
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
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Sind Sie sicher, dass sie dieses Material entgültig löschen möchten? Vorsicht, der Löschvorgang kann nicht mehr rückgängig gemacht werden.",
                                        ButtonType.YES, ButtonType.CANCEL);
                                alert.showAndWait();
                                if (alert.getResult() == ButtonType.YES) {
                                    try {
                                        assemblyList.remove(tv_Assembly.getSelectionModel().getSelectedItem());
                                        if (tv_Assembly.getSelectionModel().getSelectedItem().getId() != null) {
                                            new ComponentController().destroy(tv_Assembly.getSelectionModel().getSelectedItem().getId());
                                        }
                                    } catch (Exception e) {
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

        refreshListView();
    }

    public static Project_ConstructionMaterialController getInstance() {
        return instance;
    }

    public void refreshListView() {
        tv_Assembly.setItems(FXCollections.observableArrayList(assemblyList));
        tv_Assembly.getColumns().get(0).setVisible(false);
        tv_Assembly.getColumns().get(0).setVisible(true);
        lb_TotalCosts.setText(UtilityFormat.getStringForLabel(getTotalCosts(assemblyList)) + " €");
    }

    public void refreshComponents() {
        cb_Component.setItems(FXCollections.observableArrayList(Project_ConstructionMaterialListController.getInstance().getComponents()));

        if (cb_Component.getItems().isEmpty()) {
            cb_Component.setPromptText("Kein Bauteil vorhanden");
        } else {
            cb_Component.setPromptText("Bauteil auswählen");
        }
    }

    @FXML
    private void addMaterial(ActionEvent event) {

        int amount = 0;
        double price = 0;
        String errorMessage = "";

        if (cb_Product.getSelectionModel().getSelectedItem() == null) {
            errorMessage += "Es wurde kein Produkt ausgewählt.\n";
        }
        if (cb_Component.getSelectionModel().getSelectedItem() == null) {
            errorMessage += "Es wurde kein Bauteil ausgewählt.\n";
        }

        try {
            amount = Integer.parseInt(tf_Amount.getText());
            price = Double.parseDouble(tf_Price.getText());

            if (amount == 0) {
                errorMessage += "Die Anzahl muss größer als 0 sein.\n";
            }
            if (amount < 0 || price < 0) {
                errorMessage += "Negative Zahlen sind nicht erlaubt.\n";
            }
        } catch (NumberFormatException e) {
            errorMessage += "Die eingegebenen Zahlen sind nicht gültig.\n";
        }

        if (errorMessage.equals("")) {
            assemblyList.add(new Assembly(cb_Product.getSelectionModel().getSelectedItem(),
                    cb_Component.getSelectionModel().getSelectedItem(),
                    ProjectViewController.getOpenedProject(),
                    amount,
                    price));
            refreshListView();
        }
        else{
            new Alert(Alert.AlertType.ERROR,errorMessage).showAndWait();
        }
    }

    public void persist() {

        AssemblyController assemblyController = new AssemblyController();

        for (Assembly assembly : assemblyList) {
            if (assembly.getId() == null) {
                assemblyController.create(assembly);
            }
        }
    }

    private double getTotalCosts(List<Assembly> assemblys) {

        double totalCosts = 0;

        for (Assembly assembly : assemblys) {
            totalCosts += assembly.getPrice() * assembly.getNumberOfComponents();
        }

        return totalCosts;
    }
}
