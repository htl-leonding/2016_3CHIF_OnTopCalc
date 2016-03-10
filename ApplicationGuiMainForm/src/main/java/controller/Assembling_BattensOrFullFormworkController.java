package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class Assembling_BattensOrFullFormworkController implements Initializable {

    @FXML
    private ChoiceBox<String> cb_roofType;
    @FXML
    private Pane contentPane;
    @FXML
    private Label title;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        cb_roofType.getItems().addAll("Ziegeldach","Blechdach");
        
        //Add change listener to change the fxml file of the content pane if the selection has changed.
        cb_roofType.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            try {
                if (newValue.intValue() == 0){
                    contentPane.getChildren().clear();
                    contentPane.getChildren().add((Node) FXMLLoader.load(getClass().getResource("/fxml/Assembling_TiledRoof.fxml")));
                    title.setText("Lattung");
                }
                else if (newValue.intValue() == 1){
                    contentPane.getChildren().clear();
                    contentPane.getChildren().add((Node) FXMLLoader.load(getClass().getResource("/fxml/Assembling_SheetRoof.fxml")));
                    title.setText("Vollschalung");
                }
            } catch (IOException exception) {
            }
        });
    }    
    
}
