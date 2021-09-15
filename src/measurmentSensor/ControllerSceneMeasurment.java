package measurmentSensor;

import Bluetooth.Bluetooth;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import main.typicalFunction;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class ControllerSceneMeasurment implements Initializable {
    private Bluetooth bluetooth=new Bluetooth();
    private typicalFunction typicalfunction=new typicalFunction();
    @FXML
    Button btSendStartMeasurment;
    @FXML
    Button btClearMeasurment;
    @FXML
    Button btSaveMeasurmentToFile;
    @FXML
    TextField tfDistance;
    @FXML
    TableView tvMeasurments;
    private measurmentTableView measurmenttableview;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UnaryOperator<ListChangeListener.Change> filter = change -> {
            String text = tfDistance.getText();

            if (text.matches("[0-9]*")) {
                return change;
            }

            return null;
        };
        measurmenttableview=new measurmentTableView(tvMeasurments);
        measurmenttableview.showTable();
    }

    public void sendStartMeasurment(){
        try {
            if(!tfDistance.getText().equals("")) {
                measurmenttableview.setReferenceMeasurment(Integer.parseInt(tfDistance.getText()));
            }else{
                measurmenttableview.setReferenceMeasurment(0);
            }
            bluetooth.sendData((byte) 0xF0);
        }catch (Exception ex){
            typicalfunction.showError("Nie można wysłąc polecenia. Błąd: "+ex.getMessage());
            ex.printStackTrace();
            typicalfunction.showError("Błąd połączenie z urządzeniem bluetooth \n należy włączyć je ponownie");
        }
    }

    public void clearMeasurments(){
        measurmenttableview.clearData();
    }

    public void saveMeasurmentToFile(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        //directoryChooser.setInitialDirectory(new File("src"));
        try {
            File file=directoryChooser.showDialog(new Stage());
            FileWriter myWriter = new FileWriter(file.getAbsolutePath()+"\\pomiary.txt");
            ArrayList<measurmentTableViewMeasurClass> arrayTemp=measurmentTableView.getMeasurList();
            for(measurmentTableViewMeasurClass i:arrayTemp){
                myWriter.write(i.toString());
            }
            myWriter.close();
        }catch (Exception ex){
            typicalfunction.showError("Bład podczas wybierania folderów. Error "+ex.getMessage());
            ex.printStackTrace();
        }
    }
}
