/*	HTL Leonding	*/
package at.plakolb.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

/**
 * This view contains the Assembling-TabPane and is also responsible for a
 * correct persistance of the montage values.
 *
 * @author Kepplinger
 */
public class AssemblingController implements Initializable, Observer {

    private static AssemblingController instance;

    @FXML
    private TabPane tb_AssemblingPane;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        ModifyController.getInstance().addObserver(this);
    }

    public static AssemblingController getInstance() {
        return instance;
    }

    /**
     * Returns the TabPane for the ProjectViewController
     *
     * @return
     */
    public TabPane getTb_AssemblingPane() {
        return tb_AssemblingPane;
    }

    /**
     * Persists the values of all montage views.
     */
    public void persist() {
        Assembling_FormworkController.getInstance().persist();
        Assembling_VisibleFormworkController.getInstance().persist();
        Assembling_FoilController.getInstance().persist();
        Assembling_SealingBandController.getInstance().persist();
        Assembling_CounterBattenController.getInstance().persist();
        Assembling_BattensOrFullFormworkController.getInstance().persist();
    }

    /**
     * Handles the save visulation.
     *
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        List<Boolean> modified = ModifyController.getInstance().getAssemblingModifiedList();
        int idx = 0;
        for (Tab tab : tb_AssemblingPane.getTabs()) {
            String text = tab.getText();
            tab.getStyleClass().remove("changed");
            if (idx < modified.size() && modified.get(idx) == true) {
                if (!text.contains("*")) {
                    tab.setText("*" + text);
                }
                tab.getStyleClass().add("changed");
            } else if (text.contains("*")) {
                tab.setText(text.substring(1));
            }
            idx++;
        }
    }
}
