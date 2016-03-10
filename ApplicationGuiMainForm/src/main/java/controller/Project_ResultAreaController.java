/*	HTL Leonding	*/
package controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class Project_ResultAreaController implements Initializable {

    @FXML
    private TabPane tb_Roofarea;
    private ObservableList<Tab> tabs;
    private List<Project_BaseAndRoofAreaController> areaList;
    private static Project_ResultAreaController instance;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tabs = tb_Roofarea.getTabs();
        addTab(null);
        tabs.get(1).setClosable(false);
        instance = this;
    }

    public static Project_ResultAreaController getInstance() {
        return instance;
    }
    
    /**
     * Adds a new tab to the tab pane
     *
     * @param event
     */
    @FXML
    private void addTab(Event event) {
        if (tb_Roofarea.getSelectionModel().isSelected(0)) {
            try {
                Node node = (Node) FXMLLoader.load(getClass().getResource("/fxml/Project_BaseAndRoofArea.fxml"));
                tabs.add(new Tab("Grund- und Dachfläche", node));
                tb_Roofarea.getSelectionModel().selectLast();
            } catch (IOException exception) {
            } catch (Exception exception) {
            }

        }
    }

}
