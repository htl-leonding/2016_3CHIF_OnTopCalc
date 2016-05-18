package at.plakolb.controller;

/*	HTL Leonding	*/

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Observable;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import at.plakolb.controller.PrintProjectController;
import java.util.ArrayList;
import java.util.List;

/**
 * FXML Controller class
 *
 * @author Andreas
 */
public class PrinterSelectionController implements Initializable {
    PrintService pr;
    Boolean PrinterSelected,CopiesSelected;
    @FXML
    private Button bt_ok;
    @FXML
    private Button bt_cancel;
    @FXML
    private ComboBox<PrintService> cb_choosPrinter;
    @FXML
    private ComboBox<String> cb_copieCount;
private ObservableList<PrintService> ol;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PrinterSelected=false;
        // TODO
        ol=FXCollections.observableArrayList(Arrays.asList(PrintServiceLookup.lookupPrintServices(null, null)));
        cb_choosPrinter.setItems(ol);
        cb_copieCount.setValue("1");
        cb_copieCount.setItems(FXCollections.observableArrayList("1", "2", "3", "4", "5"));
    }    

    @FXML
    private void printerserviceChanged(ActionEvent event){
        PrinterSelected=true;
    }

    @FXML
    private void submit(ActionEvent event) {
        if (PrinterSelected) {
          PrintProjectController.stage.hide();
        }
        else{
                new Alert(Alert.AlertType.ERROR,"Bitte wählen Sie einen Drucker aus!").showAndWait();
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        PrintProjectController.stage.hide();
    }

    @FXML
    private void cntChanged(ActionEvent event) {
        try{
            Integer.parseInt(cb_copieCount.getValue());      
        }
        catch(NumberFormatException e){
        new Alert(Alert.AlertType.ERROR,"Bitte Übergeben Sie einen numerischen Wert!").showAndWait();

        }
        CopiesSelected=true;
    }
    public PrintService getPrintService(){
        return cb_choosPrinter.getValue();
    }
    public String getCopyAmount(){
        return cb_copieCount.getValue();
    }
}
