import CommunicationPackage.Communication;
import DistanceMeasureStage.CommunicationBetweenStages;
import DistanceMeasureStage.DistanceMeasureController;
import ErrorStage.ErrorStageController;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import BluetoothPackage.Bluetooth;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.xml.soap.Text;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.SynchronousQueue;

public class MainController implements Initializable {
    private Bluetooth bluetooth=new Bluetooth();
    private static Communication communication=new Communication();
    private Stage errorStage=new Stage();
    private Stage measureStage=new Stage();
    private Thread threadControlJoyStick;
    private Thread threadRefreshContents;
    private Thread threadRecivedData;
    private Boolean mousePressedOnCVController=false;
    private Boolean resetCVcarWithSensors=true;
    private CommunicationBetweenStages communicationBetweenStages=new CommunicationBetweenStages();
    @FXML
    Button BTSearchDevice;
    @FXML
    Button BTConnectToDevice;
    @FXML
    Button BTShowError;
    @FXML
    Label LBConnectionStatus;
    @FXML
    ComboBox<String> CBBluetoothDevices;
    @FXML
    VBox VBControlCar;
    @FXML
    Button BTMeasurment;
    @FXML
    ImageView IVbellImage;
    @FXML
    Canvas CVController;
    @FXML
    Canvas CVcarWithSensors;
    @FXML
    Canvas CVBattery;
    @FXML
    Label LBTextController;
    @FXML
    Label LBDriveStatusDescription;
    @FXML
    Label LBDriveStatus;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshBluetoothDevice();
        BTSearchDevice.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                refreshBluetoothDevice();
            }
        });
        BTConnectToDevice.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    mouseClickEventConnectToDevice();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        setStyleNoConnect();
        if(threadControlJoyStick !=null){
            threadControlJoyStick.stop();
        }
        CVController.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mousePressedOnCVController=true;
            }
        });
        CVController.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mousePressedOnCVController=false;
            }
        });
        BTShowError.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                showErrorStage();
            }
        });
        BTMeasurment.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                showMeasureStage();
            }
        });
        threadRecivedData=new Thread(new Runnable() {
            @Override
            public void run() {
                recivedDataFunction();
            }
        });
        threadRecivedData.start();
        IVbellImage.setVisible(false);
    }

    private void mouseClickEventConnectToDevice() throws Exception{
        String LBString=LBConnectionStatus.getText();
        switch(LBString){
            case "Niepołączony":
                String selectedBluetooth=CBBluetoothDevices.getValue();
                if(selectedBluetooth==null){
                    System.out.println("Nie wybrano urządzenia");
                    return;
                }
                try {
                   bluetooth.connectToDevice(selectedBluetooth);
                }catch (Exception ex){
                    ex.printStackTrace();
                    System.out.println("Nie można się połączyć z urządzeniem");
                    return;
                }
                setStyleConnect();
                        /* Watek od kontroli joysticku do kierunku jazdy*/
                if(threadControlJoyStick !=null && threadControlJoyStick.isAlive()){
                    threadControlJoyStick.stop();
                }
                threadControlJoyStick =new Thread(new Runnable() {
                    @Override
                    public void run() {
                        controlJoyStick();
                    }
                });
                threadControlJoyStick.start();
                        /*Wątek od kontroli odswierzania contentu w scenie*/
                if(threadRefreshContents !=null && threadRefreshContents.isAlive()){
                    threadRefreshContents.stop();
                }
                threadRefreshContents =new Thread(new Runnable() {
                    @Override
                    public void run() {
                        refreshContent();
                    }
                });
                threadRefreshContents.start();

                threadRecivedData=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        recivedDataFunction();
                    }
                });
                threadRecivedData.start();
                break;
            case "Połączony":
            default:
                try {
                    threadControlJoyStick.stop();
                    threadRefreshContents.stop();
                    bluetooth.closeConnection();
                }catch (Exception ex){
                    System.out.println("Nie można zamknąć połaczenia z urządzeniem bluetooth");
                }

                setStyleNoConnect();
                break;

        }
    }


    private void refreshBluetoothDevice(){
        this.CBBluetoothDevices.getItems().clear();
        try {
            ArrayList<String> stringComboBoxBluetoothFrendlyName=bluetooth.getFrendlyName();
            CBBluetoothDevices.getItems().addAll(stringComboBoxBluetoothFrendlyName);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void controlJoyStick(){
        while(true) {
            try {
                if(mousePressedOnCVController) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                            canvasClear(CVController);
                            int function=communication.calcSendFunctionFromCanvasController(CVController,CVController.getHeight(),CVController.getWidth());
                            if(function!=0){
                                if(communication.isReadyToSendControlCommand()) {
                                    resetCVcarWithSensors=true;
                                    bluetooth.sendRideFunction((byte) function);
                                }
                            }}catch(Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    });
                }else{
                    if(resetCVcarWithSensors) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                resetCVcarWithSensors = false;
                                canvasClear(CVController);
                                communication.resetCanvasController(CVController.getGraphicsContext2D(), CVController.getHeight(), CVController.getWidth());
                            }
                        });
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try{
                Thread.sleep(16);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
    private void refreshContent(){
        while(true){
            try{
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        canvasClear(CVBattery);
                        canvasClear(CVcarWithSensors);
                        communication.refreshBatteryCanvas(CVBattery.getGraphicsContext2D(),CVBattery.getHeight(),CVBattery.getWidth());
                        communication.canvasCarWithSensor(CVcarWithSensors.getGraphicsContext2D(),CVcarWithSensors.getHeight(),CVcarWithSensors.getWidth());
                        communication.timeRefresh();
                        final int driveStatus=communication.getDriveStatus();
                        switch(driveStatus){
                            case 1:
                                LBDriveStatus.setText("Oczekiwanie");
                                LBDriveStatus.setStyle("-fx-text-fill:green;");
                                break;
                            case 2:
                                LBDriveStatus.setText("Omijanie\nprzeszkody");
                                LBDriveStatus.setStyle("-fx-text-fill:orange;");
                                break;
                            case 3:
                                LBDriveStatus.setText("Wykryto\nprzeszkodę");
                                LBDriveStatus.setStyle("-fx-text-fill:red;");
                                break;
                            default:
                                LBDriveStatus.setText("Kod "+driveStatus);
                                LBDriveStatus.setStyle("-fx-text-fill:black;");
                                break;
                        }
                        Boolean isDataToSend=communicationBetweenStages.getSendMeasureDistanceFromDistanceMeasureStage();
                        if(isDataToSend){
                            communicationBetweenStages.setSendMeasureDistanceFromDistanceMeasureStage(false);
                            try {
                                bluetooth.sendOnlyFunction((byte) 0xF0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                //communication.addErrorTestValue();
            }catch (Exception ex){

            }
            try{
                Thread.sleep(100);
            }catch (Exception ex){

            }
        }
    }

    private void recivedDataFunction(){
        while(true) {
            try {
                byte[] buffer = bluetooth.getInputData();
                if(buffer!=null){
                    for(byte x: buffer){
                        communication.addFrameInput((int)x&0xFF);
                    }
                }
                Thread.sleep(5);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    private void setStyleNoConnect(){
        LBConnectionStatus.setText("Niepołączony");
        BTConnectToDevice.setText("Połącz");
        BTConnectToDevice.setStyle("-fx-background-color: #03fc8c;");
        VBControlCar.setDisable(true);
        LBTextController.setVisible(false);
        LBDriveStatus.setVisible(false);
        LBDriveStatusDescription.setVisible(false);
        canvasClear(CVBattery);
        canvasClear(CVController);
        canvasClear(CVcarWithSensors);
    }

    private void setStyleConnect() throws Exception{
        LBConnectionStatus.setText("Połączony");
        BTConnectToDevice.setText("Rozłącz");
        BTConnectToDevice.setStyle("-fx-background-color: #d93204;-fx-text-fill:white");
        VBControlCar.setDisable(false);
        LBTextController.setVisible(true);
        LBDriveStatus.setVisible(true);
        LBDriveStatusDescription.setVisible(true);
        canvasClear(CVBattery);
        canvasClear(CVController);
        communication.refreshBatteryCanvas(CVBattery.getGraphicsContext2D(),CVBattery.getHeight(),CVBattery.getWidth());
        communication.resetCanvasController(CVController.getGraphicsContext2D(),CVController.getHeight(),CVController.getWidth());
    }

    synchronized private void canvasClear(Canvas canvas){
        if(canvas!=null) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        }else{
            System.out.println("CanvasClear Error");
        }

    }
    private void showErrorStage(){
        try{
            if(!errorStage.isShowing()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("\\ErrorStage\\ErrorStageScene.fxml"));
                if(loader==null){
                    System.out.println("loader errorStage jest równy null");
                }
                //URL fxmlURL = this.getClass().getClassLoader().getResource("\\ErrorStage\\ErrorStageScene.fxml");
                Parent root = loader.load();
                if(root!=null) {
                    Scene scene = new Scene(root);
                    ErrorStageController controller = loader.<ErrorStageController>getController();
                    controller.showTableView(communication);
                    //root.getStylesheets().add("../style.css");
                    errorStage.setScene(scene);
                    errorStage.setOnHidden(e -> {
                        controller.close();
                    });
                    errorStage.setTitle("Praca magisterska panel informacyjny");
                    errorStage.show();
                }else{
                    System.out.println("root errorStage jest równy null");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    private void showMeasureStage(){
        try{
            if(!measureStage.isShowing()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("\\DistanceMeasureStage\\DistanceMeasureScene.fxml"));
                if(loader==null){
                    System.out.println("loader measureStage jest równy null");
                }
                URL fxmlURL = this.getClass().getClassLoader().getResource("\\DistanceMeasureStage\\DistanceMeasureScene.fxml");
                Parent root = loader.load();
                if(root!=null) {
                    Scene scene = new Scene(root);
                    DistanceMeasureController controller = loader.<DistanceMeasureController>getController();
                    controller.showTableView(communication,communicationBetweenStages);
                    //root.getStylesheets().add("../style.css");
                    measureStage.setScene(scene);
                    measureStage.setOnHidden(e -> {
                        controller.close();
                    });
                    measureStage.setTitle("Praca magisterska panel badania czujników");
                    measureStage.show();
                }else{
                    System.out.println("root measureStage jest równy null");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    public void close(){
        if(threadControlJoyStick!=null && threadControlJoyStick.isAlive()){
            threadControlJoyStick.stop();
        }
        if(threadRefreshContents!=null && threadRefreshContents.isAlive()){
            threadRefreshContents.stop();
        }
        if(threadRecivedData !=null && threadRecivedData.isAlive()){
            threadRecivedData.stop();
        }
        if(errorStage!=null && errorStage.isShowing()){
            errorStage.close();
        }
        try {
            bluetooth.closeConnection();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
