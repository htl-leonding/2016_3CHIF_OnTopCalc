/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.util.Logging;
import at.plakolb.calculationlogic.db.controller.ProjectController;
import at.plakolb.calculationlogic.db.exceptions.NonexistentEntityException;
import at.plakolb.calculationlogic.db.entity.Project;
import at.plakolb.calculationlogic.util.BackUpDatabase;
import at.plakolb.calculationlogic.util.UtilityFormat;
import at.plakolb.settings.SettingsController;
import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Andreas
 */
public class OptionsController implements Initializable, Observer {

    private static OptionsController controller;

    @FXML
    private TableColumn<Project, Integer> cl_id;
    @FXML
    private TableColumn<Project, String> cl_pname;
    @FXML
    private TableColumn<Project, String> cl_type;
    @FXML
    private TableColumn<Project, String> cl_roofType;
    @FXML
    private TableColumn<Project, Integer> cl_client;
    @FXML
    private TableView<Project> tv_paperbin;
    @FXML
    private TableColumn cl_options;
    @FXML
    private Button bt_createBackup;
    @FXML
    private ProgressIndicator pgic_backupProgress;
    @FXML
    private TextField tf_defaultBackupDirectory;
    @FXML
    private Button bt_chooseFileBackup;
    @FXML
    private CheckBox cb_backUpStartCheck;
    @FXML
    private ComboBox<String> cb_weeks;
    @FXML
    private Label lb_weekTXT;
    @FXML
    private TextField tf_defaultPDFDirectory;
    @FXML
    private Button bt_chooseFilePDF;
    @FXML
    private Label lb_percentage;
    @FXML
    private Label lb_percentageRE;
    @FXML
    private ProgressIndicator pgic_backupProgressRE;
    @FXML
    private Button bt_readBackup;

    private int backup = 0; //-1 Backup erstellen | 1 Backup wiederherstellen

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        controller = this;

        VBox placeholder = new VBox(new ImageView(new Image("/images/cloud.png")), new Label("Keine Daten vorhanden"));
        placeholder.setAlignment(Pos.CENTER);
        tv_paperbin.setPlaceholder(placeholder);

        cl_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        cl_pname.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        cl_client.setCellValueFactory(new PropertyValueFactory<>("client"));
        cl_type.setCellValueFactory(new PropertyValueFactory<>("constructionType"));
        cl_roofType.setCellValueFactory(new PropertyValueFactory<>("roofForm"));

        updateData();

        cl_options.setCellValueFactory(new PropertyValueFactory<>("Buttons"));

        Callback<TableColumn<Project, String>, TableCell<Project, String>> cellFactory
                = new Callback<TableColumn<Project, String>, TableCell<Project, String>>() {
            @Override
            public TableCell call(final TableColumn<Project, String> param) {
                final TableCell<Project, String> cell = new TableCell<Project, String>() {

                    final Label l_restore = new Label();
                    final Label l_delFinal = new Label();
                    final HBox box = new HBox(l_restore, l_delFinal);

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            l_restore.setId("restore");
                            l_delFinal.setId("deleteFinal");
                            box.setId("box");
                            box.setAlignment(Pos.CENTER);

                            l_restore.setTooltip(new Tooltip("Projekt wiederherstellen"));
                            l_delFinal.setTooltip(new Tooltip("Projekt entgültig löschen"));

                            l_restore.setOnMouseClicked(event -> {
                                Project project = getTableView().getItems().get(getIndex());
                                project.setDeletion(false);
                                try {
                                    new ProjectController().edit(project);
                                    updateData();
                                } catch (NonexistentEntityException ex) {
                                    Logging.getLogger().log(Level.SEVERE, "Project couldn't be restored.", ex);
                                }
                                updateData();
                            });
                            l_delFinal.setOnMouseClicked(event -> {
                                Project p = getTableView().getItems().get(getIndex());
                                ProjectController c = new ProjectController();
                                c.delete(p.getId());
                                updateData();
                            });
                            setGraphic(box);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };

        cl_options.setCellFactory(cellFactory);

        tf_defaultBackupDirectory.setTooltip(new Tooltip("Kein Standardordner ausgewählt"));
        tf_defaultPDFDirectory.setTooltip(new Tooltip("Kein Standardordner ausgewählt"));
        UtilityFormat.setCutTextForTextField(tf_defaultBackupDirectory, SettingsController.getProperty("backupPath"));
        UtilityFormat.setCutTextForTextField(tf_defaultPDFDirectory, SettingsController.getProperty("pdfPath"));

        cb_weeks.setValue(SettingsController.getProperty("remindBackupWeeks"));
        cb_backUpStartCheck.setSelected(SettingsController.getBooleanProperty("remindBackup"));

        lb_weekTXT.setOnMouseClicked((event) -> {
            cb_backUpStartCheck.setSelected(!cb_backUpStartCheck.isSelected());
        });
        cb_weeks.setItems(FXCollections.observableArrayList("1", "2", "3", "4", "5", "6"));
        cb_weeks.setOnAction((event) -> {
            int value;
            try {
                value = Integer.parseInt(cb_weeks.getValue());
            } catch (NumberFormatException e) {
                value = Integer.parseInt(SettingsController.getProperty("remindBackupWeeks"));
            }
            Alert alert = null;
            if (value < 0) {
                alert = new Alert(Alert.AlertType.INFORMATION, "Wenn Sie keine Überprüfung beim Start durchführen wollen können Sie dieses Feature auch deaktivieren.\nSoll dieses Feature jetzt deaktiviert werden?");
                alert.getButtonTypes().clear();
                alert.getButtonTypes().addAll(ButtonType.NO, ButtonType.YES);
                value = Integer.parseInt(SettingsController.getProperty("remindBackupWeeks"));
                alert.showAndWait();

            }
            if (alert != null && alert.getResult() == ButtonType.YES) {
                cb_backUpStartCheck.setSelected(false);
            } else {
                cb_backUpStartCheck.setSelected(true);
            }
            SettingsController.setProperty("remindBackupWeeks", Integer.toString(value));
            EventHandler<ActionEvent> onAction = cb_weeks.getOnAction();
            cb_weeks.setOnAction(null);
            cb_weeks.setValue(Integer.toString(value));
            cb_weeks.setOnAction(onAction);
        });
        cb_backUpStartCheck.selectedProperty().addListener((event) -> {
            SettingsController.setProperty("remindBackup", cb_backUpStartCheck.isSelected() ? "true" : "false");
        });

        bt_chooseFileBackup.setOnAction((event) -> {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setTitle("OnTopCalc - Pfad auswählen");
            try {

                Stage stage = new Stage();
                stage.setResizable(false);
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(((Node) event.getSource()).getScene().getWindow());
                File p = dc.showDialog(stage);

                if (p != null) {
                    UtilityFormat.setCutTextForTextField(tf_defaultBackupDirectory, p.getAbsolutePath());
                } else {
                    tf_defaultBackupDirectory.setText("");
                    tf_defaultBackupDirectory.setTooltip(new Tooltip("Kein Standardordner ausgewählt"));
                }
            } catch (Exception ex) {
                Logging.getLogger().log(Level.SEVERE, null, ex);
            }
        });

        bt_chooseFilePDF.setOnAction((event) -> {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setTitle("OnTopCalc - Pfad auswählen");
            try {

                Stage stage = new Stage();
                stage.setResizable(false);
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(((Node) event.getSource()).getScene().getWindow());
                File p = dc.showDialog(stage);
                if (p != null) {
                    UtilityFormat.setCutTextForTextField(tf_defaultPDFDirectory, p.getAbsolutePath());
                } else {
                    tf_defaultPDFDirectory.setText("");
                    tf_defaultPDFDirectory.setTooltip(new Tooltip("Kein Standardordner ausgewählt"));
                }
            } catch (Exception ex) {
                Logging.getLogger().log(Level.SEVERE, null, ex);
            }
        });
        tf_defaultBackupDirectory.setOnMouseClicked((event) -> {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setTitle("OnTopCalc - Pfad auswählen");
            try {

                Stage stage = new Stage();
                stage.setResizable(false);
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(((Node) event.getSource()).getScene().getWindow());
                File p = dc.showDialog(stage);

                if (p != null) {
                    UtilityFormat.setCutTextForTextField(tf_defaultBackupDirectory, p.getAbsolutePath());
                } else {
                    tf_defaultBackupDirectory.setText("");
                    tf_defaultBackupDirectory.setTooltip(new Tooltip("Kein Standardordner ausgewählt"));
                }
            } catch (Exception ex) {
                Logging.getLogger().log(Level.SEVERE, null, ex);
            }
        });

        tf_defaultPDFDirectory.setOnMouseClicked((event) -> {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setTitle("OnTopCalc - Pfad auswählen");
            try {

                Stage stage = new Stage();
                stage.setResizable(false);
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(((Node) event.getSource()).getScene().getWindow());
                File p = dc.showDialog(stage);
                if (p != null) {
                    UtilityFormat.setCutTextForTextField(tf_defaultPDFDirectory, p.getAbsolutePath());
                } else {
                    tf_defaultPDFDirectory.setText("");
                    tf_defaultPDFDirectory.setTooltip(new Tooltip("Kein Standardordner ausgewählt"));
                }
            } catch (Exception ex) {
                Logging.getLogger().log(Level.SEVERE, null, ex);
            }
        });

        tf_defaultBackupDirectory.textProperty().addListener((event) -> {
            if (!tf_defaultBackupDirectory.getText().isEmpty()) {
                SettingsController.setProperty("backupPath", tf_defaultBackupDirectory.getTooltip().getText());
            } else {
                SettingsController.setProperty("backupPath", "");
            }
        });
        tf_defaultPDFDirectory.textProperty().addListener((event) -> {
            if (!tf_defaultPDFDirectory.getText().isEmpty()) {
                SettingsController.setProperty("pdfPath", tf_defaultPDFDirectory.getTooltip().getText());
            } else {
                SettingsController.setProperty("pdfPath", "");
            }
        });
        tf_defaultBackupDirectory.widthProperty().addListener((event) -> {
            if (tf_defaultBackupDirectory.getTooltip() != null && !SettingsController.getProperty("backupPath").isEmpty()) {
                UtilityFormat.setCutTextForTextField(tf_defaultBackupDirectory, tf_defaultBackupDirectory.getTooltip().getText());
            }
        });
        tf_defaultPDFDirectory.widthProperty().addListener((event) -> {
            if (tf_defaultPDFDirectory.getTooltip() != null && !SettingsController.getProperty("pdfPath").isEmpty()) {
                UtilityFormat.setCutTextForTextField(tf_defaultPDFDirectory, tf_defaultPDFDirectory.getTooltip().getText());
            }
        });
    }

    public void updateData() {
        tv_paperbin.setItems(FXCollections.observableArrayList(new ProjectController().findProjectsByDeletion(true)));
    }

    @FXML
    private void createBackup(ActionEvent event) {

        if (SettingsController.getProperty("backupPath").isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Sie haben keine Sicherungs Speicherort angegeben. Die Sicherung wird in " + System.getProperty("user.home")
                    + " gespeichert. Möchten Sie fortfahren.", ButtonType.CANCEL, ButtonType.OK);
            alert.showAndWait();

            if (alert.getResult().equals(ButtonType.CANCEL)) {
                return;
            }
        }
        pgic_backupProgress.setVisible(true);
        bt_createBackup.setDisable(true);
        bt_readBackup.setDisable(true);
        backup = -1;

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                BackUpDatabase backUpDatabase = new BackUpDatabase(SettingsController.getProperty("backupPath"));
                backUpDatabase.addObserver(controller);
                int returnValue = backUpDatabase.exp();

                Platform.runLater(() -> {
                    if (returnValue == 3) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Die Sicherung wurde erfolgreich erstellt!");
                        alert.setHeaderText("Sicherung erstellt");
                        alert.setContentText("Die Sicherung wurde erfolgreich erstellt!");
                        alert.showAndWait();
                        SettingsController.setDateProperty("lastBackup", new Date());

                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Es konnte keine Sicherung erstellt werden!");
                        alert.setHeaderText("Fehler");
                        alert.setContentText("Es konnte keine Sicherung erstellt werden!");
                        alert.showAndWait();
                    }
                    pgic_backupProgress.setVisible(false);
                    bt_createBackup.setDisable(false);
                    lb_percentage.setVisible(false);
                    bt_readBackup.setDisable(false);
                    backup = 0;
                });
            }
        }, "BackupCreatorThread");
        t.start();
    }

    @Override
    public void update(Observable o, Object arg) {
        Platform.runLater(() -> {
            if (backup == -1) {
                if (!lb_percentage.isVisible()) {
                    lb_percentage.setVisible(true);
                }
                lb_percentage.setText(String.format("%s%%", arg));
                pgic_backupProgress.setVisible(true);
            } else if (backup == 1) {
                if (!lb_percentageRE.isVisible()) {
                    lb_percentageRE.setVisible(true);
                }
                lb_percentageRE.setText(String.format("%s%%", arg));
                pgic_backupProgressRE.setVisible(true);
            }
        });
    }

    public void createBackup() {
        createBackup(null);
    }

    public static OptionsController getInstance() {
        return controller;
    }

    String recoverPath = "";

    @FXML
    private void readBackup(ActionEvent event) {
        Alert deleteDBAlert = new Alert(Alert.AlertType.WARNING, "Die derzeitige Datenbank wird gelöscht!");
        deleteDBAlert.getButtonTypes().clear();
        deleteDBAlert.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        deleteDBAlert.showAndWait();
        if (deleteDBAlert.getResult() == ButtonType.OK) {
            pgic_backupProgressRE.setVisible(true);
            bt_createBackup.setDisable(true);
            bt_readBackup.setDisable(true);
            backup = 1;

            BackUpDatabase backUpDatabase = new BackUpDatabase(SettingsController.getProperty("backupPath"));
            backUpDatabase.addObserver(controller);

            DirectoryChooser dc = new DirectoryChooser();
            if (new File(SettingsController.getProperty("backupPath")).exists()) {
                dc.setInitialDirectory(new File(SettingsController.getProperty("backupPath")));
            }
            dc.setTitle("OnTopCalc - Auswahl der Sicherung");
            try {
                Stage stage = new Stage();
                stage.setResizable(false);
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(((Node) event.getSource()).getScene().getWindow());
                File p = dc.showDialog(stage);
                if (p != null) {
                    recoverPath = p.getAbsolutePath();
                } else {
                    pgic_backupProgressRE.setVisible(false);
                    bt_createBackup.setDisable(false);
                    bt_readBackup.setDisable(false);
                    return;
                }
            } catch (Exception ex) {
                Logging.getLogger().log(Level.SEVERE, null, ex);
            }
            if (new File(recoverPath).exists()) {

                Thread t = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        int returnValue = backUpDatabase.imp(recoverPath);

                        Platform.runLater(() -> {
                            if (returnValue == 0) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Die Sicherung wurde erfolgreich wiederhergestellt!");
                                alert.setHeaderText("Sicherung wiederhergestellt!");
                                alert.setContentText("Die Sicherung wurde erfolgreich wiederhergestellt!");
                                alert.showAndWait();
                                SettingsController.setDateProperty("lastBackup", new Date());

                            } else {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Die Sicherung konnte nicht wiederhergestellt werden!");
                                alert.setHeaderText("Fehler");
                                alert.setContentText("Die Sicherung konnte nicht wiederhergestellt werden!");
                                alert.showAndWait();
                            }
                            updateData();
                            pgic_backupProgressRE.setVisible(false);
                            lb_percentageRE.setVisible(false);
                            bt_createBackup.setDisable(false);
                            bt_readBackup.setDisable(false);
                            backup = 0;
                        });
                    }
                }, "BackupReaderThread");
                t.start();
            }
        }
    }
}
