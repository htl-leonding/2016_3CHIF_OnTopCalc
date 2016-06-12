/*	HTL Leonding	*/
package at.plakolb.controller;

import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.w3c.dom.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Andreas
 */
public class HelpController implements Initializable {

    @FXML
    private WebView wv_page;
    @FXML
    private AnchorPane ap_loadingPage;

    WebEngine webEngine;

    boolean controlled;
    @FXML
    private AnchorPane ap_main;
    @FXML
    private AnchorPane ap_NoInternet;
    @FXML
    private ProgressIndicator pi_progress;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        webEngine = wv_page.getEngine();
        wv_page.setContextMenuEnabled(false);
        webEngine.documentProperty().addListener((event) -> {
            if (!controlled) {
                controlDocument(webEngine.getDocument());
            } else {
                controlled = false;
            }
        });
        pi_progress.progressProperty().bind(webEngine.getLoadWorker().progressProperty());
        
        webEngine.getLoadWorker().stateProperty().addListener((event) -> {
            ap_main.getChildren().remove(ap_NoInternet);
            ap_main.getChildren().remove(ap_loadingPage);
            if (webEngine.getLoadWorker().getState() == Worker.State.SCHEDULED || webEngine.getLoadWorker().getState() == Worker.State.RUNNING) {
                ap_main.getChildren().add(ap_loadingPage);
            } else if (webEngine.getLoadWorker().getState() == Worker.State.FAILED) {
                ap_main.getChildren().add(ap_NoInternet);
            }
        });
        webEngine.load("https://github.com/htl-leonding/2016_3CHIF_OnTopCalc/wiki");
    }

    private void controlDocument(Document document) {
        try {
            Element node = document.getElementById("wiki-content");
            NodeList childNodes = document.getElementsByTagName("body").item(0).getChildNodes();

            while (childNodes.getLength() > 0) {
                document.getElementsByTagName("body").item(0).removeChild(childNodes.item(0));
            }
            document.getElementsByTagName("body").item(0).appendChild(node);

            NodeList nodes = document.getElementById("wiki-rightbar").getChildNodes();

            Node cloneURL = null;

            for (int i = 0; i < nodes.getLength(); i++) {
                NamedNodeMap attributes = nodes.item(i).getAttributes();
                if (attributes != null) {
                    for (int a = 0; a < attributes.getLength(); a++) {
                        if (attributes.item(a).getNodeValue().equals("clone-url")) {
                            cloneURL = nodes.item(i);
                            break;
                        }
                    }
                }
            }
            if (cloneURL != null) {
                document.getElementById("wiki-rightbar").removeChild(cloneURL);
            }

            AnchorPane p = (AnchorPane) ap_loadingPage.getParent();
            p.getChildren().remove(ap_loadingPage);
        } catch (Exception e) {

        }
        controlled = true;
    }

    @FXML
    private void refresh(ActionEvent event) {
        if (webEngine != null) {
            webEngine.reload();
        }
    }
}
