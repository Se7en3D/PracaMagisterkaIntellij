import CommunicationPackage.Communication;
import ErrorStage.ErrorStageController;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private Bluetooth bluetooth=new Bluetooth();
    private static Communication communication=new Communication();
    private Stage errorStage=new Stage();
    private Thread threadControlJoyStick;
    private Thread threadRefreshContents;
    private Thread threadRecivedData;
    private Boolean mousePressedOnCVController=false;
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
                mouseClickEventConnectToDevice();
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
        threadRecivedData=new Thread(new Runnable() {
            @Override
            public void run() {
                recivedDataFunction();
            }
        });
        threadRecivedData.start();
    }

    private void mouseClickEventConnectToDevice(){
        String LBString=LBConnectionStatus.getText();
        switch(LBString){
            case "Niepołączony":
                String selectedBluetooth=CBBluetoothDevices.getValue();
                if(selectedBluetooth==null){
                    System.out.println("Nie wybrano urządzenia");
                    return;
                }
                try {
                   // bluetooth.connectToDevice(selectedBluetooth);
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
                    //bluetooth.closeConnection();
                }catch (Exception ex){
                    System.out.println("Nie można zamknąć połaczenia z urządzeniem bluetooth");
                }

                setStyleNoConnect();
                break;

        }
    }


    private void refreshBluetoothDevice(){
        this.CBBluetoothDevices.getItems().removeAll();
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
                    communication.calcSendFunctionFromCanvasController(CVController);
                }else{
                    communication.resetCanvasController(CVController);
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
                communication.refreshBatteryCanvas(CVBattery);
                communication.canvasCarWithSensor(CVcarWithSensors);
                communication.addErrorTestValue();
                if(errorStage.isShowing()){
                    try {
                        Window x=errorStage.get();
                        x.getC
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    System.out.println("ClassInfoArrayList="+communication.getClassInfoArrayList().size());
                }else{
                    System.out.println("No showing");
                }
            }catch (Exception ex){

            }
            try{
                Thread.sleep(1000);
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
        canvasClear(CVBattery);
        canvasClear(CVController);
        canvasClear(CVcarWithSensors);
    }

    private void setStyleConnect(){
        LBConnectionStatus.setText("Połączony");
        BTConnectToDevice.setText("Rozłącz");
        BTConnectToDevice.setStyle("-fx-background-color: #d93204;-fx-text-fill:white");
        VBControlCar.setDisable(false);
        communication.refreshBatteryCanvas(CVBattery);
        communication.resetCanvasController(CVController);
    }

    private void canvasClear(Canvas canvas){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
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
                    controller.showTableView(communication.getClassInfoArrayList());
                    //root.getStylesheets().add("../style.css");
                    errorStage.setScene(scene);
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
}
