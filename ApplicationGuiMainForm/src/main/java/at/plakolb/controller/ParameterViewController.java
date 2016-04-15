package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ParameterController;
import at.plakolb.calculationlogic.entity.ParameterP;
import at.plakolb.calculationlogic.entity.Unit;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Andreas
 */
public class ParameterViewController implements Initializable {

    private static ParameterViewController instance;

    @FXML
    private TableView<ParameterP> tv_Prameter;
    @FXML
    private TableColumn<ParameterP, String> tc_LongTerm;
    @FXML
    private TableColumn<ParameterP, String> tc_DefaultValue;
    @FXML
    private TableColumn<ParameterP, Unit> tc_Unit;
    @FXML
    private TableColumn tc_Button;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        DecimalFormat decimalFormat = new DecimalFormat("#.####");
        decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));

        tc_LongTerm.setCellValueFactory(new PropertyValueFactory<>("longTerm"));

        tc_DefaultValue.setCellValueFactory((TableColumn.CellDataFeatures<ParameterP, String> param) -> {
            if (param.getValue().getDefaultValue() != null) {
                return new ReadOnlyObjectWrapper<>(decimalFormat.format(param.getValue().getDefaultValue()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        tc_Unit.setCellValueFactory(new PropertyValueFactory<>("unit"));

        tc_Button.setCellFactory((new Callback<TableColumn<ParameterP, String>, TableCell<ParameterP, String>>() {
            @Override
            public TableCell call(final TableColumn<ParameterP, String> param) {
                final TableCell<ParameterP, String> cell = new TableCell<ParameterP, String>() {

                    final Label edit = new Label();

                    @Override
                    public void updateItem(String item, boolean empty) {

                        super.updateItem(item, empty);
                        if (empty || !((ParameterP) getTableRow().getItem()).isEditable()) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            edit.setId("edit");
                            edit.setTooltip(new Tooltip("Standartwert verändern"));
                            edit.setOnMouseClicked((MouseEvent event) -> {
                                Parent root;
                                try {
                                    root = FXMLLoader.load(getClass().getResource("/fxml/ParameterModifier.fxml"));
                                    Scene scene = new Scene(root);
                                    Stage stage = new Stage();
                                    stage.setTitle("Parameter editieren");
                                    stage.setScene(scene);
                                    stage.initModality(Modality.WINDOW_MODAL);
                                    stage.initOwner(((Node) event.getSource()).getScene().getWindow());
                                    stage.show();
                                } catch (IOException ex) {
                                }

                                ParameterModifierController.getInstance().loadParameterIntoModifier(tv_Prameter.getSelectionModel().getSelectedItem());
                            });

                            setGraphic(edit);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        }));

        tv_Prameter.setItems(FXCollections.observableArrayList(new ParameterController().findAll()));

        tv_Prameter.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tv_Prameter.getSelectionModel().getSelectedItem() != null && tv_Prameter.getSelectionModel().getSelectedItem().isEditable()) {
                Parent root;
                try {
                    root = FXMLLoader.load(getClass().getResource("/fxml/ParameterModifier.fxml"));
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setTitle("Parameter editieren");
                    stage.setScene(scene);
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(((Node) event.getSource()).getScene().getWindow());
                    stage.show();
                } catch (IOException ex) {
                }

                ParameterModifierController.getInstance().loadParameterIntoModifier(tv_Prameter.getSelectionModel().getSelectedItem());
            }
        });
    }

    public static ParameterViewController getInstance() {
        return instance;
    }

    /**
     * Refreshes the TableView.
     */
    public void refreshTable() {
        tv_Prameter.getColumns().get(0).setVisible(false);
        tv_Prameter.getColumns().get(0).setVisible(true);
    }
}
