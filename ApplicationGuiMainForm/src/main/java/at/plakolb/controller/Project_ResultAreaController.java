/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ParameterController;
import at.plakolb.calculationlogic.db.controller.WorthController;
import at.plakolb.calculationlogic.entity.Project;
import at.plakolb.calculationlogic.entity.Worth;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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

    Worth worthArea;
    Worth worthRoofArea;
    Worth worthRoofOverhang;
    Worth worthRoofAreaWithRoofOverhang;

    private int next_ID;

    private int getNextID() {
        next_ID++;

        return next_ID - 1;
    }

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
        instance = this;

        if (ProjectViewController.isProjectOpened() && !ProjectViewController.getOpenedProject().getWorths().isEmpty()) {
            WorthController worthController = new WorthController();
            worthArea = worthController.findWorthByShortTermAndProjectId("A", ProjectViewController.getOpenedProject().getId());
            worthRoofArea = worthController.findWorthByShortTermAndProjectId("D", ProjectViewController.getOpenedProject().getId());
            worthRoofOverhang = worthController.findWorthByShortTermAndProjectId("DV", ProjectViewController.getOpenedProject().getId());
            worthRoofAreaWithRoofOverhang = worthController.findWorthByShortTermAndProjectId("DF", ProjectViewController.getOpenedProject().getId());
        } else {
            ParameterController parameterController = new ParameterController();
            worthArea = new Worth(parameterController.findParameterPByShortTerm("A"));
            worthRoofArea = new Worth(parameterController.findParameterPByShortTerm("D"));
            worthRoofOverhang = new Worth(parameterController.findParameterPByShortTerm("DV"));
            worthRoofAreaWithRoofOverhang = new Worth(parameterController.findParameterPByShortTerm("DF"));
        }

        tabs.get(0).setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/addTab.png"))));

        if (ProjectViewController.isProjectOpened()) {
            WorthController worthController = new WorthController();
            int max_index = worthController.BaseAndRoofAreaCountTabs(ProjectViewController.getOpenedProject().getId());
            if (max_index > 0) {
                for (int i = 0; i < max_index; i++) {
                    addTab(null);
                }
                for (Project_BaseAndRoofAreaController ctrl : areaController) {
                    ctrl.loadValuesFromDataBase();
                }
                calcArea();
                System.out.println("Loaded Tabs");
            } else {
                addTab(null);

            }
        } else {
            addTab(null);
        }

    }

    public static Project_ResultAreaController getInstance() {
        return instance;
    }

    public double getBaseArea() {
        return worthArea.getWorth();
    }

    public double getRoofArea() {
        return worthRoofArea.getWorth();
    }

    public double getLedge() {
        return worthRoofOverhang.getWorth();
    }

    public double getLedgeAndRoofArea() {
        return worthRoofAreaWithRoofOverhang.getWorth();
    }

    public Worth getLedgeAndRoofAreaWorth() {
        return worthRoofAreaWithRoofOverhang;
    }

    /**
     * Calculates the total area of the roof.
     */
    public void calcArea() {

        double baseArea = 0;
        double roofArea = 0;
        double ledge = 0;
        double ledgeAndRoofArea = 0;

        double oldBA = worthArea.getWorth();
        double oldRA = worthRoofArea.getWorth();
        double oldL = worthRoofOverhang.getWorth();
        double oldLARA = worthRoofAreaWithRoofOverhang.getWorth();

        for (Project_BaseAndRoofAreaController controller : areaController) {
            baseArea += controller.getBaseAreaValue();
            roofArea += controller.getRoofAreaValue();
            ledge += controller.getLedgeValue();
            ledgeAndRoofArea += controller.getLedgeAndRoofAreaValue();
        }

        worthArea.setWorth(baseArea);
        worthRoofArea.setWorth(roofArea);
        worthRoofOverhang.setWorth(ledge);
        worthRoofAreaWithRoofOverhang.setWorth(ledgeAndRoofArea);

        setChanged();
        notifyObservers();
        if (oldBA != baseArea || oldRA != roofArea || oldL != ledge || oldLARA != ledgeAndRoofArea) {
            ModifyController.getInstance().setProject_resultArea(Boolean.TRUE);
        }
    }

    /**
     * Adds a new tab to the tab pane
     *
     * @param event
     */
    @FXML
    private void addTab(Event event) {
        try {
            URL location = getClass().getResource("/fxml/Project_BaseAndRoofArea.fxml");

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
            Parent fxmlNode = (Parent) fxmlLoader.load(location.openStream());
            Project_BaseAndRoofAreaController controller = (Project_BaseAndRoofAreaController) fxmlLoader.getController();
            controller.setID(getNextID());

            addObserver(controller);
            fxmlNode.setUserData(controller);
            areaController.add(controller);
            Tab newTab = new Tab("Grund- und Dachfläche", fxmlNode);

            newTab.setOnCloseRequest(value -> {

                Tab actTab = (Tab) value.getSource();
                Object o = actTab.getContent().getUserData();
                if (o != null) {
                    Project_BaseAndRoofAreaController ctrl = (Project_BaseAndRoofAreaController) o;

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Grund- und Dachflächenberechnung");
                    alert.setHeaderText("Wollen Sie diesen Reiter wirklich löschen?");
                    alert.getButtonTypes().clear();
                    alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.YES) {
                        Project project = ProjectViewController.getOpenedProject();
                        if (project != null) {
                            areaController.remove(o);
                            calcArea();
                        }
                        //set closable for tabs
                        if (tabs.size() > 3) {
                            for (Tab tab : tabs) {
                                tab.setClosable(true);
                            }
                            tabs.get(0).setClosable(false);
                        } else {
                            for (Tab tab : tabs) {
                                tab.setClosable(false);
                            }
                        }
                    } else {
                        value.consume();
                    }
                }
            });
            tabs.add(newTab);
            tb_Roofarea.getSelectionModel().selectLast();

            //set closable for tabs
            if (tabs.size() >= 3) {
                for (Tab tab : tabs) {
                    tab.setClosable(true);
                }
                tabs.get(0).setClosable(false);
            } else if (tabs.size() == 2) {
                tabs.get(0).setClosable(false);
                tabs.get(1).setClosable(false);
            }

            setChanged();
            notifyObservers();
            if (event != null) {
                ModifyController.getInstance().setProject_resultArea(Boolean.TRUE);
            }

        } catch (IOException exception) {
            System.out.println("Exception Message: " + exception.getMessage());
        } catch (Exception exception) {
            System.out.println("Exception Message: " + exception.getMessage());
        }

    }

    /**
     * Persists the calculated Values to the database.
     */
    public void persist() {
        Project project = ProjectViewController.getOpenedProject();
        if (project != null) {
            WorthController worthController = new WorthController();

            if (!ProjectViewController.isProjectOpened()) {
                worthArea.setProject(project);
                worthRoofArea.setProject(project);
                worthRoofOverhang.setProject(project);
                worthRoofAreaWithRoofOverhang.setProject(project);

                worthController.create(worthArea);
                worthController.create(worthRoofArea);
                worthController.create(worthRoofOverhang);
                worthController.create(worthRoofAreaWithRoofOverhang);
            } else {
                try {
                    worthController.edit(worthArea);
                    worthController.edit(worthRoofArea);
                    worthController.edit(worthRoofOverhang);
                    worthController.edit(worthRoofAreaWithRoofOverhang);
                } catch (Exception ex) {
                    Logger.getLogger(Project_ResultAreaController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        for (Project_BaseAndRoofAreaController c : areaController) {
            c.persistArea();
        }
    }

    /**
     * Index des Tabs in der Liste
     *
     * @param id ID des Tabs
     * @return Aktueller Index in der Liste
     */
    public int getIndex(int id) {
        for (int i = 0; i < areaController.size(); i++) {
            if (areaController.get(i).getID() == id) {
                return i;
            }
        }
        return -1;
    }
}
