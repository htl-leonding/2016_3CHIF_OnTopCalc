/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.util.Logging;
import at.plakolb.calculationlogic.db.controller.ProjectController;
import at.plakolb.calculationlogic.entity.Project;
import at.plakolb.calculationlogic.util.Print;
import at.plakolb.calculationlogic.util.UtilityFormat;
import at.plakolb.settings.SettingsController;
import com.itextpdf.text.DocumentException;
import java.awt.Desktop;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */ 
public class PrintProjectController implements Initializable {
    static Stage stage;
    @FXML
    private ComboBox<Project> cb_projects;

    private static PrintProjectController instance;
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

    java.io.File path;
    String lastPath;
    PrintService printService;

    @FXML
    private CheckBox cb_openAfterCreation;
    @FXML
    private Button bt_showLastPDF;
    @FXML
    private CheckBox cb_Area;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        cb_projects.setItems(FXCollections.observableArrayList(new ProjectController().findProjectsByDeletion(false)));
        if (cb_projects.getItems().isEmpty()) {
            cb_projects.setPromptText("Es sind keine Projekte zum Drucken vorhanden.");
        } else {
            cb_projects.getSelectionModel().select(0);
        }

        tf_dateAndPosition.setText("Rohrbach, am " + UtilityFormat.getDateString(new Date()));
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

    public void SetProject(Project project) {
        for (Project p : cb_projects.getItems()) {
            if (p.getId().equals(project.getId())) {
                cb_projects.getSelectionModel().select(p);
                return;
            }
        }
        System.out.println(project.getProjectNameWithId() + " steht nicht zur Druckauswahl!");
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
            UtilityFormat.setCutTextForTextField(tf_path, path.getAbsolutePath());
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
            showPDF(print.createPDF(), cb_openAfterCreation.isSelected());
            bt_showLastPDF.setDisable(false);           
            if (p) {              
              try {  
        if(PrintServiceLookup.lookupPrintServices(null, null).length<1){
        new Alert(Alert.AlertType.ERROR,"Bitte fügen Sie einen Drucker hinzu!").showAndWait();
        }
        else{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PrinterSelection.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Print Assistent");
            stage.setScene(new Scene(root1));  
            stage.showAndWait();
            PrinterSelectionController controller = (PrinterSelectionController)fxmlLoader.getController();
                   print.print(controller.getPrintService(),Integer.parseInt(controller.getCopyAmount()));
        }
              } catch (IOException ex) {
                   Logging.getLogger().log(Level.SEVERE, "Print method didn't work.", ex);
               }
            }
        } catch (DocumentException | FileNotFoundException ex) {
            Logging.getLogger().log(Level.SEVERE, "CreatePDF method didn't work.", ex);
        }
    }

    public void showPDF(String path, boolean allowed) {
        lastPath = path;
        try {
            System.out.println(System.getProperty("os.name").toLowerCase());
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                if (Desktop.isDesktopSupported() && allowed) {
                    Desktop.getDesktop().open(new File(path));
                }
            }
            else{
                executeOnOtherSystems(path, allowed);
            }
        } catch (IOException ex) {
            Logging.getLogger().log(Level.SEVERE, "ShowPDF method didn't work.", ex);
        }

    }
    private boolean executeOnOtherSystems(String path,boolean isallowed){
    if (!System.getProperty("os.name").toLowerCase().contains("mac")) {
                if (runCommand("kde-open", "%s", path)&&isallowed)return true;
                if (runCommand("gnome-open", "%s", path)&&isallowed)return true;
                if (runCommand("xdg-open", "%s", path)&&isallowed)return true;
            }

        else{
            if(runCommand("open", "%s", path)&&isallowed) return true;
        }
    return false;
    }
        private boolean runCommand(String command, String args, String file) {
        String[] parts = prepareCommand(command, args, file);

        try {
            Process p = Runtime.getRuntime().exec(parts);
            if (p == null) return false;

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
        showPDF(lastPath, true);
    }
}
