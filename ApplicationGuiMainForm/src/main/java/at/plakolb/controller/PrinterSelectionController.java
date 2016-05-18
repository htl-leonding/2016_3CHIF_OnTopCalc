/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.util.ComboBoxPrintService;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

/**
 * FXML Controller class
 *
 * @author Andreas
 */
public class PrinterSelectionController implements Initializable {

    PrintService pr;
    Boolean PrinterSelected, CopiesSelected;
    @FXML
    private ComboBox<ComboBoxPrintService> cb_choosPrinter;
    @FXML
    private ComboBox<String> cb_copieCount;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PrinterSelected = false;
        cb_choosPrinter.setItems(FXCollections.observableList(new LinkedList<ComboBoxPrintService>()));

        FXCollections.observableArrayList(Arrays.asList(PrintServiceLookup.lookupPrintServices(null, null))).stream().forEach((printService) -> {
            cb_choosPrinter.getItems().add(new ComboBoxPrintService(printService));
        });

        cb_copieCount.setValue("1");
        cb_copieCount.setItems(FXCollections.observableArrayList("1", "2", "3", "4", "5"));
    }

    @FXML
    private void printerserviceChanged(ActionEvent event) {
        PrinterSelected = true;
    }

    @FXML
    private void submit(ActionEvent event) {
        if (PrinterSelected) {
            PrintProjectController.getStage().hide();
        } else {
            new Alert(Alert.AlertType.ERROR, "Bitte wählen Sie einen Drucker aus!").showAndWait();
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        PrintProjectController.getStage().hide();
    }

    @FXML
    private void cntChanged(ActionEvent event) {
        try {
            Integer.parseInt(cb_copieCount.getValue());
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Bitte Übergeben Sie einen numerischen Wert!").showAndWait();

        }
        CopiesSelected = true;
    }

    public PrintService getPrintService() {
        return cb_choosPrinter.getValue().getPrintService();
    }

    public String getCopyAmount() {
        return cb_copieCount.getValue();
    }
}
