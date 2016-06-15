/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ProjectController;
import at.plakolb.calculationlogic.db.entity.Project;
import at.plakolb.calculationlogic.util.Logging;
import at.plakolb.calculationlogic.util.Print;
import at.plakolb.calculationlogic.util.PrintInformationException;
import at.plakolb.calculationlogic.util.UtilityFormat;
import at.plakolb.settings.SettingsController;
import com.itextpdf.text.DocumentException;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.print.PrintServiceLookup;
import java.awt.*;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class PrintProjectController implements Initializable {

    private static PrintProjectController instance;
    private static Stage stage;
    public VBox selectedViews;
    public VBox selectedViewsContainer;
    public Button bt_toggleSelectedViews;
    public VBox printButtons;
    public VBox container;

    @FXML
    private ComboBox<Project> cb_projects;
    @FXML
    private CheckBox cb_Area;
    @FXML
    private CheckBox cb_mainInformations;
    @FXML
    private CheckBox cb_materialAndCostList;
    @FXML
    private CheckBox cb_woodmaterialAndConstruction;
    @FXML
    private CheckBox cb_formwork;
    @FXML
    private CheckBox cb_visibleFormwork;
    @FXML
    private CheckBox cb_foil;
    @FXML
    private CheckBox cb_nailSealingTape;
    @FXML
    private CheckBox cb_battens;
    @FXML
    private CheckBox cb_lathing;
    @FXML
    private CheckBox cb_materialMontage;
    @FXML
    private CheckBox cb_color;
    @FXML
    private CheckBox cb_transport;
    @FXML
    private CheckBox cb_costView;
    @FXML
    private TextField tf_dateAndPosition;
    @FXML
    private TextField tf_path;
    @FXML
    private Button bt_createPDF;
    @FXML
    private Button bt_createPDFAndPrint;
    @FXML
    private CheckBox cb_openAfterCreation;
    @FXML
    private Button bt_showLastPDF;


    private java.io.File path;
    private String lastPath;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        instance = this;
        selectedViewsContainer.getChildren().remove(selectedViews);

        cb_projects.setItems(FXCollections.observableArrayList(new ProjectController().findProjectsByDeletion(false)));
        if (cb_projects.getItems().isEmpty()) {
            cb_projects.setPromptText("Es sind keine Projekte zum Drucken vorhanden.");
        } else {
            cb_projects.getSelectionModel().select(0);
        }

        tf_dateAndPosition.setText("Rohrbach, am " + UtilityFormat.getDate(LocalDateTime.now()));
        tf_dateAndPosition.textProperty().addListener((observable, oldvalue, newValue) -> {
            refreshPrintAbility();
        });

        bt_createPDF.setTooltip(new Tooltip("PDF erstellen"));
        bt_createPDFAndPrint.setTooltip(new Tooltip("PDF erstellen und drucken"));
        bt_showLastPDF.setTooltip(new Tooltip("Letzte PDF anzeigen"));

        File f = new File(SettingsController.getProperty("pdfPath"));
        if (f.isDirectory()) {
            path = f;
            UtilityFormat.setCutTextForTextField(tf_path, path.getAbsolutePath());
        }

        refreshPrintAbility();
    }

    public static PrintProjectController getInstance() {
        return instance;
    }

    public static Stage getStage() {
        return stage;
    }

    public void SetProject(Project project) {
        for (Project p : cb_projects.getItems()) {
            if (p.getId().equals(project.getId())) {
                cb_projects.getSelectionModel().select(p);
                return;
            }
        }
        refreshPrintAbility();
    }

    @FXML
    private void print(ActionEvent event) {
        createPDF(true);
    }

    @FXML
    private void choosePath(Event event) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("OnTopCalc - Pfad auswählen");
        try {

            Stage stage = new Stage();
            stage.setResizable(false);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            File p = dc.showDialog(stage);

            if (p != null) {
                path = p;
            }

            if (path != null) {
                UtilityFormat.setCutTextForTextField(tf_path, path.getAbsolutePath());
            }

        } catch (Exception ex) {
            Logging.getLogger().log(Level.SEVERE, null, ex);
        } finally {
            refreshPrintAbility();
        }
    }

    private void refreshPrintAbility() {
        if (!tf_dateAndPosition.getText().isEmpty() && cb_projects.getSelectionModel().getSelectedItem() != null && path != null && path.isDirectory()) {
            bt_createPDFAndPrint.setDisable(false);
            bt_createPDF.setDisable(false);
        } else {
            bt_createPDFAndPrint.setDisable(true);
            bt_createPDF.setDisable(true);
        }
    }

    @FXML
    private void createPDF(ActionEvent event) {
        createPDF(false);
    }

    public void createPDF(boolean p) {
        Print print = new Print(path.getAbsolutePath(), cb_projects.getSelectionModel().getSelectedItem(), tf_dateAndPosition.getText());
        try {
            List<Boolean> listPrint = new ArrayList<>();
            listPrint.add(cb_mainInformations.isSelected());
            listPrint.add(cb_Area.isSelected());
            listPrint.add(cb_woodmaterialAndConstruction.isSelected());
            listPrint.add(cb_formwork.isSelected());
            listPrint.add(cb_visibleFormwork.isSelected());
            listPrint.add(cb_foil.isSelected());
            listPrint.add(cb_nailSealingTape.isSelected());
            listPrint.add(cb_battens.isSelected());
            listPrint.add(cb_lathing.isSelected());
            listPrint.add(cb_materialMontage.isSelected());
            listPrint.add(cb_color.isSelected());
            listPrint.add(cb_transport.isSelected());
            listPrint.add(cb_materialAndCostList.isSelected());
            listPrint.add(cb_costView.isSelected());

            print.setListPrint(listPrint);

            if (cb_openAfterCreation.isSelected() && !p) {
                showPDF(print.createPDF());
            }

            bt_showLastPDF.setDisable(false);
            if (p) {
                try {

                    if (PrintServiceLookup.lookupPrintServices(null, null).length < 1) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Bitte fügen Sie einen Drucker hinzu!");
                        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
                        alert.showAndWait();
                    } else {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PrinterSelection.fxml"));
                        Parent root1 = (Parent) fxmlLoader.load();
                        stage = new Stage();
                        stage.initModality(Modality.WINDOW_MODAL);
                        stage.setTitle("Print Assistent");
                        stage.setScene(new Scene(root1));
                        stage.initOwner(((Node) bt_showLastPDF).getScene().getWindow());
                        stage.setResizable(false);
                        stage.showAndWait();
                        PrinterSelectionController controller = (PrinterSelectionController) fxmlLoader.getController();
                        if (!controller.isCancled()) {
                            if (controller.getPrintService() != null) {
                                print.createPDF();
                                print.print(controller.getPrintService(), Integer.parseInt(controller.getCopyAmount()));
                                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Druckauftrag erfolgreich gesendet!", ButtonType.OK);
                                alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
                                alert.showAndWait();
                            }
                        }
                    }
                } catch (IOException | PrinterException ex) {
                    Logging.getLogger().log(Level.SEVERE, "Print method didn't work.", ex);
                }
            }
        } catch (DocumentException | PrintInformationException ex) {
            Logging.getLogger().log(Level.SEVERE, "Couldn't create PDF file.", ex);
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
            alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
            alert.showAndWait();
        } catch (FileNotFoundException ex) {
            Logging.getLogger().log(Level.INFO, "File not found.", ex);
            Alert alert = new Alert(Alert.AlertType.ERROR, "PDF konnte nicht erstellt werden. Stellen Sie sicher, dass Sie Zugriffsrechte für den angegebenen Pfad besitzen.", ButtonType.OK);
            alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
            alert.showAndWait();
        } catch (Exception ex) {
            Logging.getLogger().log(Level.SEVERE, "CreatePDF method didn't work.", ex);
            Alert alert = new Alert(Alert.AlertType.ERROR, "PDF konnte nicht erstellt werden", ButtonType.OK);
            alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
            alert.showAndWait();
        }
    }

    public void showPDF(String path) {
        lastPath = path;
        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(new File(path));
                }
            } else {
                executeOnOtherSystems(path);
            }
        } catch (IOException ex) {
            Logging.getLogger().log(Level.SEVERE, "ShowPDF method didn't work.", ex);
        }

    }

    private boolean executeOnOtherSystems(String path) {
        if (!System.getProperty("os.name").toLowerCase().contains("mac")) {
            if (runCommand("kde-open", "%s", path)) {
                return true;
            }
            if (runCommand("gnome-open", "%s", path)) {
                return true;
            }
            if (runCommand("xdg-open", "%s", path)) {
                return true;
            }
        } else if (runCommand("open", "%s", path)) {
            return true;
        }
        return false;
    }

    private boolean runCommand(String command, String args, String file) {
        String[] parts = prepareCommand(command, args, file);

        try {
            Process p = Runtime.getRuntime().exec(parts);
            if (p == null) {
                return false;
            }

            try {
                int retval = p.exitValue();
                if (retval == 0) {
                    return false;
                } else {
                    return false;
                }
            } catch (IllegalThreadStateException itse) {
                return true;
            }
        } catch (IOException e) {
            return false;
        }
    }

    private static String[] prepareCommand(String command, String args, String file) {

        List<String> parts = new ArrayList<String>();
        parts.add(command);

        if (args != null) {
            for (String s : args.split(" ")) {
                s = String.format(s, file); // put in the filename thing

                parts.add(s.trim());
            }
        }

        return parts.toArray(new String[parts.size()]);
    }

    @FXML
    private void selectAll(ActionEvent event) {
        cb_battens.setSelected(true);
        cb_Area.setSelected(true);
        cb_color.setSelected(true);
        cb_costView.setSelected(true);
        cb_foil.setSelected(true);
        cb_formwork.setSelected(true);
        cb_lathing.setSelected(true);
        cb_mainInformations.setSelected(true);
        cb_materialAndCostList.setSelected(true);
        cb_materialMontage.setSelected(true);
        cb_nailSealingTape.setSelected(true);
        cb_transport.setSelected(true);
        cb_visibleFormwork.setSelected(true);
        cb_woodmaterialAndConstruction.setSelected(true);
    }

    @FXML
    private void deleteSelection(ActionEvent event) {
        cb_battens.setSelected(false);
        cb_Area.setSelected(false);
        cb_color.setSelected(false);
        cb_costView.setSelected(false);
        cb_foil.setSelected(false);
        cb_formwork.setSelected(false);
        cb_lathing.setSelected(false);
        cb_mainInformations.setSelected(false);
        cb_materialAndCostList.setSelected(false);
        cb_materialMontage.setSelected(false);
        cb_nailSealingTape.setSelected(false);
        cb_transport.setSelected(false);
        cb_visibleFormwork.setSelected(false);
        cb_woodmaterialAndConstruction.setSelected(false);
    }

    @FXML
    private void showLastPDF(ActionEvent event) {
        showPDF(lastPath);
    }

    public void toggleSelectedViews(ActionEvent actionEvent) {
        if (selectedViewsContainer.getChildren().contains(selectedViews)) {
            selectedViewsContainer.getChildren().remove(selectedViews);
            bt_toggleSelectedViews.setTooltip(new Tooltip("Anzeigen"));
            bt_toggleSelectedViews.getStyleClass().remove("viewsEye2");
            bt_toggleSelectedViews.getStyleClass().add("viewsEye");
            ((Node) actionEvent.getSource()).getScene().getWindow().sizeToScene();
        } else {
            selectedViewsContainer.getChildren().add(selectedViews);
            bt_toggleSelectedViews.setTooltip(new Tooltip("Verbergen"));
            bt_toggleSelectedViews.getStyleClass().add("viewsEye2");
            bt_toggleSelectedViews.getStyleClass().remove("viewsEye");
            ((Node) actionEvent.getSource()).getScene().getWindow().sizeToScene();
        }
    }
}
