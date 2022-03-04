import CanvasPackage.CanvasBattery;
import CanvasPackage.CanvasCarController;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import BluetoothPackage.Bluetooth;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private Bluetooth bluetooth=new Bluetooth();
    private CanvasBattery canvasBattery=new CanvasBattery();
    private CanvasCarController canvasCarController=new CanvasCarController();
    private Thread threadRefreshContent;
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
        if(threadRefreshContent!=null){
            threadRefreshContent.stop();
        }
        threadRefreshContent=new Thread(new Runnable() {
            @Override
            public void run() {
                refreshContentFunction();
            }
        });
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
        threadRefreshContent.start();
    }
    private void mouseClickEventConnectToDevice(){
        String LBString=LBConnectionStatus.getText();
        switch(LBString){
            case "Niepołączony":
                setStyleConnect();
                break;
            case "Połączony":
            default:
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

    private void refreshContentFunction(){
        while(true) {
            try {
                /*if(mousePressedOnCVController) {
                    Point pointMouse = MouseInfo.getPointerInfo().getLocation();
                    Point2D pointNote = CVController.localToScreen(0, 0);
                    int positionXMouseTowardNote = pointMouse.x - (int) pointNote.getX();
                    int positionYMouseTowardNote = pointMouse.y - (int) pointNote.getY();
                    int widthNode=(int)CVController.getWidth();
                    int heightNode=(int)CVController.getHeight();
                    if(((positionXMouseTowardNote>=0)&& (positionXMouseTowardNote<=widthNode))&&
                            ((positionYMouseTowardNote>=0)&&(positionYMouseTowardNote<=heightNode))){
                        System.out.println("Pozycja myszy względem Canvasa x="+positionXMouseTowardNote+ " y="+positionYMouseTowardNote);
                        canvasCarController.drawControllerWithPosition(CVController,positionXMouseTowardNote-10,positionYMouseTowardNote-10);
                    }else{
                        canvasCarController.drawController(CVController);
                    }
                }else{
                    canvasCarController.drawController(CVController);
                }*/
                Thread.sleep(16);
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
        canvasBattery.drawBattery(CVBattery,70);
        canvasCarController.drawController(CVController);
    }

    private void canvasClear(Canvas canvas){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

}
