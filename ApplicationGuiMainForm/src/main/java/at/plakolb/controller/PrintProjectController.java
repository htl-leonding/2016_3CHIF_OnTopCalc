/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ProjectController;
import at.plakolb.calculationlogic.entity.Project;
import at.plakolb.calculationlogic.util.Print;
import at.plakolb.calculationlogic.util.UtilityFormat;
import com.itextpdf.text.DocumentException;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class PrintProjectController implements Initializable {
    
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
    private ImageView iv_pdfView;
    @FXML
    private Button bt_next;
    @FXML
    private Button bt_back;
    @FXML
    private TextField tf_path;
    
    java.io.File path;
    @FXML
    private Button bt_print;

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
    private void deleteSelection(ActionEvent event) {
        cb_battens.setSelected(false);
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
    private void print(ActionEvent event) {
        Print print = new Print(path.getAbsolutePath(), cb_projects.getSelectionModel().getSelectedItem(), tf_dateAndPosition.getText());
        try {
            List<Boolean> listPrint = new ArrayList<>();
            listPrint.add(cb_mainInformations.isSelected());            
            listPrint.add(cb_materialAndCostList.isSelected());
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
            Desktop.getDesktop().open(new File(print.createPDF()));
        } catch (DocumentException ex) {
            Logger.getLogger(PrintProjectController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PrintProjectController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PrintProjectController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            setCutTextForTextField(tf_path, path.getAbsolutePath());
        } catch (Exception e) {
        } finally {
            refreshPrintAbility();
        }
    }
    
    private void refreshPrintAbility() {
        if (!tf_dateAndPosition.getText().isEmpty() && cb_projects.getSelectionModel().getSelectedItem() != null && path != null && path.isDirectory()) {
            bt_print.setDisable(false);
        } else {
            bt_print.setDisable(true);
        }
    }
    
    private void setCutTextForTextField(TextField textField, String original) {
        original = original.replace("\\", "/");
        textField.setTooltip(new Tooltip(original));
        Text text = new Text(original);
        text.setFont(textField.getFont());
        String[] splittedPath = original.split("/");
        
        if (text.getLayoutBounds().getWidth() + textField.getPadding().getLeft() + textField.getPadding().getRight() + 2d > textField.getWidth()) {
            text.setText(splittedPath.length > 3 ? String.format("%s/%s/.../%s",
                    splittedPath[0],
                    splittedPath[1],
                    splittedPath[splittedPath.length - 1]) : "../" + splittedPath[splittedPath.length - 1]);
            if (text.getLayoutBounds().getWidth() + textField.getPadding().getLeft() + textField.getPadding().getRight() + 2d > textField.getWidth()) {
                text.setText("../" + splittedPath[splittedPath.length - 1]);
            }
        }
        textField.setText(text.getText());
    }
}
