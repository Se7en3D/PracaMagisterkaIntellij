package errorDevice;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import main.typicalFunction;
import measurmentSensor.measurmentTableView;
import measurmentSensor.measurmentTableViewMeasurClass;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;


public class Controller implements Initializable {
    private typicalFunction typicalfunction;
    private TableViewError tableViewError;

    @FXML
    TableView tableView;
    @FXML
    Button BTClear;
    @FXML
    Button BTSaveInFile;


    @Override
    public void initialize(URL location, ResourceBundle resources) {


        tableViewError=new TableViewError(tableView);
        tableViewError.showTable();
    }

    @FXML
    public void saveInFile(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
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
        }
    }

    @FXML
    public void clearFrame(){
        tableViewError.clearData();
    }
}
