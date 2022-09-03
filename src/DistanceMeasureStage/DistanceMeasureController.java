package DistanceMeasureStage;

import CommunicationPackage.ClassInfo;
import CommunicationPackage.Communication;
import CommunicationPackage.DistanceMeasuringWithoutPosition;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;


public class DistanceMeasureController implements Initializable {
    private Communication communication;
    private CommunicationBetweenStages communicationBetweenStages=new CommunicationBetweenStages();
    private Thread refreshContent;
    //private Bluetooth bluetooth=new Bluetooth();
   // private typicalFunction typicalfunction=new typicalFunction();
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
    //private measurmentTableView measurmenttableview;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
       /* UnaryOperator<ListChangeListener.Change> filter = change -> {
            String text = tfDistance.getText();

            if (text.matches("[0-9]*")) {
                return change;
            }

            return null;
        };
        measurmenttableview=new measurmentTableView(tvMeasurments);
        measurmenttableview.showTable();*/
    }

    public void sendStartMeasurment(){
        communicationBetweenStages.setSendMeasureDistanceFromDistanceMeasureStage(true);
        String distanceString=tfDistance.getText();
        int distanceInt=0;
        if(distanceString!=null){
            try {
                distanceInt = Integer.parseInt(distanceString);
            }catch (Exception ex){
                //ex.printStackTrace();
                distanceInt=0;
            }
        }
        communication.setReferenceMeasurment(distanceInt);
    }
    public void clearMeasurments(){
        ArrayList<DistanceMeasuringWithoutPosition> distanceMeasuringWithoutPositions=communication.getDistanceMeasuringWithoutPositions();
        distanceMeasuringWithoutPositions.clear();
        tvMeasurments.getItems().clear();
    }
    public void saveMeasurmentToFile(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        //directoryChooser.setInitialDirectory(new File("src"));
        try {
            File file=directoryChooser.showDialog(new Stage());
            FileWriter myWriter = new FileWriter(file.getAbsolutePath()+"\\pomiary.txt");
            ArrayList<DistanceMeasuringWithoutPosition> arrayTemp= communication.getDistanceMeasuringWithoutPositions();
            for(DistanceMeasuringWithoutPosition i:arrayTemp){
                myWriter.write(i.toString());
            }
            myWriter.close();
        }catch (Exception ex){
            //typicalfunction.showError("Bład podczas wybierania folderów. Error "+ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void showTableView( Communication communication,CommunicationBetweenStages communicationBetweenStages){
        this.communication=communication;
        this.communicationBetweenStages=communicationBetweenStages;
        if(refreshContent==null){
            refreshContent=new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        try{
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    refreshTableView();
                                }
                            });
                            Thread.sleep(2000);
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }
            });
            refreshContent.start();
        }
        TableColumn<DistanceMeasuringWithoutPosition,Integer> tcReferenceMeasurment=new TableColumn<>("Wartośc referencyjna");
        tcReferenceMeasurment.setCellValueFactory(new PropertyValueFactory<>("referenceMeasurment"));
        TableColumn<DistanceMeasuringWithoutPosition,Double> tcUltrasonicSensor=new TableColumn<>("Czujnik ultradzwiękowy");
        tcUltrasonicSensor.setCellValueFactory(new PropertyValueFactory<>("ultrasonicSensor"));
        TableColumn<DistanceMeasuringWithoutPosition,Double> tcLaserSensor=new TableColumn<>("Czujnik laserowy");
        tcLaserSensor.setCellValueFactory(new PropertyValueFactory<>("laserSensor"));

        tcReferenceMeasurment.setMinWidth(200);
        tcUltrasonicSensor.setMinWidth(200);
        tcLaserSensor.setMinWidth(200);

        tvMeasurments.getColumns().addAll(tcReferenceMeasurment,tcUltrasonicSensor,tcLaserSensor);
        refreshTableView();
     }

    public void refreshTableView(){
        ArrayList<DistanceMeasuringWithoutPosition> distanceMeasuringWithoutPositions=communication.getDistanceMeasuringWithoutPositions();
        tvMeasurments.getItems().clear();
        tvMeasurments.getItems().addAll(distanceMeasuringWithoutPositions);

    }
    public void close(){
        if(refreshContent.isAlive()){
            refreshContent.stop();
        }
    }
}
