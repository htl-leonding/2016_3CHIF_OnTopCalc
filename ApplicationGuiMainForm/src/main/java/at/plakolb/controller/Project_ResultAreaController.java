/*	HTL Leonding	*/
package at.plakolb.controller;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class Project_ResultAreaController extends Observable implements Initializable {

    private static Project_ResultAreaController instance;

    @FXML
    private TabPane tb_Roofarea;
    private ObservableList<Tab> tabs;
    private List<Project_BaseAndRoofAreaController> areaController;

    private double baseArea;
    private double roofArea;
    private double ledge;
    private double ledgeAndRoofArea;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        areaController = new LinkedList<>();
        tabs = tb_Roofarea.getTabs();
        addTab(null);
        tabs.get(1).setClosable(false);
        instance = this;
        
        if (ProjectViewController.getOpenedProject() != null && ProjectViewController.getOpenedProject().getId() != null) {
            areaController.get(0).loadValuesFromDataBase();
        }
    }

    public static Project_ResultAreaController getInstance() {
        return instance;
    }

    public double getBaseArea() {
        return baseArea;
    }

    public double getRoofArea() {
        return roofArea;
    }

    public double getLedge() {
        return ledge;
    }

    public double getLedgeAndRoofArea() {
        return ledgeAndRoofArea;
    }

    /**
     * Calculates the total area of the roof.
     */
    public void calcArea() {

        baseArea = 0;
        roofArea = 0;
        ledge = 0;
        ledgeAndRoofArea = 0;

        for (Project_BaseAndRoofAreaController controller : areaController) {
            baseArea += controller.getBaseAreaValue();
            roofArea += controller.getRoofAreaValue();
            ledge += controller.getLedgeValue();
            ledgeAndRoofArea += controller.getLedgeAndRoofAreaValue();
        }
        setChanged();
        notifyObservers();
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
                URL location = getClass().getResource("/fxml/Project_BaseAndRoofArea.fxml");

                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(location);
                fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
                Parent fxmlNode = (Parent) fxmlLoader.load(location.openStream());
                Project_BaseAndRoofAreaController controller = (Project_BaseAndRoofAreaController) fxmlLoader.getController();

                addObserver(controller);
                fxmlNode.setUserData(controller);
                areaController.add(controller);
                tabs.add(new Tab("Grund- und Dachfläche", fxmlNode));
                tb_Roofarea.getSelectionModel().selectLast();

                setChanged();
                notifyObservers();

            } catch (IOException exception) {
            } catch (Exception exception) {
            }
        }
    }
    
    /**
     * Persists the calculated Values to the database.
     */
    public void persistArea(){
        areaController.get(0).persistArea();
    }
}