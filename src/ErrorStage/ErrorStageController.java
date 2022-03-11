package ErrorStage;

import CommunicationPackage.ClassInfo;
import CommunicationPackage.Communication;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import old.errorDevice.TableViewError;
import old.errorDevice.TableViewErrorClass;
import old.main.typicalFunction;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class ErrorStageController implements Initializable {
    Communication communication=null;
    Thread refreshTable;
    @FXML
    TableView tableView;
    @FXML
    Button BTClear;
    @FXML
    Button BTSaveInFile;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    public void saveInFile(){
        /*DirectoryChooser directoryChooser = new DirectoryChooser();
        //directoryChooser.setInitialDirectory(new File("src"));
        try {
            File file=directoryChooser.showDialog(new Stage());
            FileWriter myWriter = new FileWriter(file.getAbsolutePath()+"\\pomiary.txt");
            ArrayList<TableViewErrorClass> arrayTemp= tableViewError.getErrorList();
            for(TableViewErrorClass i:arrayTemp){
                myWriter.write(i.toString());
            }
            myWriter.close();
        }catch (Exception ex){
            typicalfunction.showError("Bład podczas wybierania folderów. Error "+ex.getMessage());
            ex.printStackTrace();
        }*/
    }

    @FXML
    public void clearFrame(){
        //tableViewError.clearData();
    }
    public void showTableView(Communication communication){
        this.communication=communication;

        if(refreshTable==null){
            refreshTable=new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true) {
                        try {
                            refreshTableView();
                            Thread.sleep(2000);
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }
            });
            refreshTable.start();
        }

        ArrayList<ClassInfo> classInfoArrayList= communication.getClassInfoArrayList();
        TableColumn<ClassInfo, Integer> tcCodeNumber =new TableColumn<>("Numer kodu");
        tcCodeNumber.setCellValueFactory(new PropertyValueFactory<>("codeNumber"));

        TableColumn<ClassInfo, String> tcVariableInProgram =new TableColumn<>("Zmienna w programie");
        tcVariableInProgram.setCellValueFactory(new PropertyValueFactory<>("variableInProgram"));

        TableColumn<ClassInfo, String> tcSource = new TableColumn<>("Zródło/plik");
        tcSource.setCellValueFactory(new PropertyValueFactory<>("source"));
        TableColumn<ClassInfo, String> tcComment = new TableColumn<>("Komentarz");
        tcComment.setCellValueFactory(new PropertyValueFactory<>("comment"));

        tcCodeNumber.setMinWidth(200);
        tcVariableInProgram.setMinWidth(200);
        tcSource.setMinWidth(200);
        tcComment.setMinWidth(200);
        tableView.getColumns().addAll(tcCodeNumber,tcVariableInProgram,tcSource,tcComment);
        tableView.getItems().addAll(classInfoArrayList);
    }
    public synchronized void refreshTableView() throws  Exception{
        if(communication!=null) {
            try {
            ArrayList<ClassInfo> classInfoArrayList = communication.getClassInfoArrayList();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        tableView.getItems().clear();
                        tableView.getItems().addAll(classInfoArrayList);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            });
            }catch(Exception ex){
                ex.printStackTrace();
            }
            }
        }

    public void close(){
        if(refreshTable.isAlive()){
            refreshTable.stop();
        }
    }
}
