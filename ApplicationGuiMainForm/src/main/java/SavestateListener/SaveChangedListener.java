/*	HTL Leonding	*/
package SavestateListener;

import at.plakolb.controller.ProjectViewController;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author User
 */
public class SaveChangedListener implements EventHandler<MouseEvent>{

    public SaveChangedListener() {
        super();
    }

    @Override
    public void handle(MouseEvent event) {
       String t = event.getTarget().toString().toLowerCase();
       if(t.contains("tabpaneskin")
           || t.contains("text"))
           return;
       // Tab actTab=AssemblingController.getInstance().getTb_AssemblingPane().getSelectionModel().getSelectedItem();
       Tab actTab=ProjectViewController.getInstance().getTb_MainPane().getSelectionModel().getSelectedItem();
       if (!actTab.getText().contains("*")) {
            actTab.setText(actTab.getText()+"*");
        }
       
    }

    
}
