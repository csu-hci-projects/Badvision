package badvision.dataanalysis;

import badvision.dataanalysis.model.RecordedResponse;
import badvision.dataanalysis.model.Subject;
import badvision.dataanalysis.util.ExportUtil;
import badvision.dataanalysis.util.JsonUtil;
import static badvision.dataanalysis.util.StringUtil.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.DirectoryChooser;

/**
 * Ties the JavaFX UI (described in the FXML file) to the rest of the application logic
 * @author brobert
 */
public class PrimaryController {
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;
    
    @FXML
    private TableView<Subject> subjectTable;

    @FXML
    private TableView<RecordedResponse> experimentTable;

    @FXML // fx:id="experimentEndColumn"
    private TableColumn<Subject, String> experimentEndColumn; // Value injected by FXMLLoader

    @FXML // fx:id="experimentStartColumn"
    private TableColumn<Subject, String> experimentStartColumn; // Value injected by FXMLLoader

    @FXML // fx:id="factorOrderColumn"
    private TableColumn<Subject, String> factorOrderColumn; // Value injected by FXMLLoader

    @FXML // fx:id="fileColumn"
    private TableColumn<Subject, String> fileColumn; // Value injected by FXMLLoader

    @FXML // fx:id="ipColumn"
    private TableColumn<Subject, String> ipColumn; // Value injected by FXMLLoader

    @FXML // fx:id="locationColumn"
    private TableColumn<Subject, String> locationColumn; // Value injected by FXMLLoader

    @FXML // fx:id="numRecordsColumn"
    private TableColumn<Subject, Integer> numRecordsColumn; // Value injected by FXMLLoader

    @FXML // fx:id="recordTypeColumn"
    private TableColumn<RecordedResponse, String> recordTypeColumn; // Value injected by FXMLLoader

    @FXML // fx:id="trialDateColumn"
    private TableColumn<RecordedResponse, String> trialDateColumn; // Value injected by FXMLLoader
    
    @FXML // fx:id="exportButton"
    private Button exportButton; // Value injected by FXMLLoader

    File currentDirectory;
    
    @FXML
    void loadDataFiles(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Pick folder with data");
        currentDirectory = chooser.showDialog(null);
        if (currentDirectory == null) {
            return;
        }
        subjectTable.getItems().clear();
        Arrays.stream(currentDirectory.listFiles((f)->f.getName().toLowerCase().endsWith(".json")))
                .flatMap(JsonUtil::loadDataFile).forEach(subjectTable.getItems()::add);
        System.out.println("Loaded "+subjectTable.getItems().size()+" subjects");
        exportButton.setDisable(subjectTable.getItems().size()==0);
    }
    
    @FXML
    void saveReport(ActionEvent event) throws IOException {
        ExportUtil.saveReport(currentDirectory, subjectTable.getItems());
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert experimentEndColumn != null : "fx:id=\"experimentEndColumn\" was not injected: check your FXML file 'primary.fxml'.";
        assert experimentStartColumn != null : "fx:id=\"experimentStartColumn\" was not injected: check your FXML file 'primary.fxml'.";
        assert factorOrderColumn != null : "fx:id=\"factorOrderColumn\" was not injected: check your FXML file 'primary.fxml'.";
        assert fileColumn != null : "fx:id=\"fileColumn\" was not injected: check your FXML file 'primary.fxml'.";
        assert ipColumn != null : "fx:id=\"ipColumn\" was not injected: check your FXML file 'primary.fxml'.";
        assert locationColumn != null : "fx:id=\"locationColumn\" was not injected: check your FXML file 'primary.fxml'.";
        assert numRecordsColumn != null : "fx:id=\"numRecordsColumn\" was not injected: check your FXML file 'primary.fxml'.";
        assert recordTypeColumn != null : "fx:id=\"recordTypeColumn\" was not injected: check your FXML file 'primary.fxml'.";
        assert trialDateColumn != null : "fx:id=\"trialDateColumn\" was not injected: check your FXML file 'primary.fxml'.";
        assert exportButton != null : "fx:id=\"exportButton\" was not injected: check your FXML file 'primary.fxml'.";
        experimentStartColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(formatDate(p.getValue().getStartTime())));
        experimentEndColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(formatDate(p.getValue().getEndTime())));
        fileColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getFilename()));
        ipColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getIp()));
        locationColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(formatLocation(p.getValue().getLocation())));
        numRecordsColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getRecordCount()));        
        factorOrderColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getStartRecord().get().getRequest().getTrialOrder()));
        subjectTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> experimentTable.getItems().setAll(newVal.getRecords()));
        trialDateColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(formatDate(p.getValue().getRequest().getTime())));
        recordTypeColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getRecordType()));
        
    }    
}
