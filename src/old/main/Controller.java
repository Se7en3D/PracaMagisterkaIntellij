package old.main;

import old.Bluetooth.Bluetooth;
import old.carRc.DrivingMeasurmentSamples;
import old.carRc.FrameInput;
import old.errorDevice.TableViewError;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import old.measurmentSensor.measurmentTableView;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javax.bluetooth.*;

public class Controller implements Initializable {
    private Bluetooth bluetooth=new Bluetooth();
    private typicalFunction typicalFunction=new typicalFunction();
    private DrivingMeasurmentSamples drivingMeasurmentSample= new DrivingMeasurmentSamples();
    private Thread trReadFromBluetooth; //Watek odpowiedzialny za odczytywanie informacji
    private Thread trControlMainScene=new Thread();
    private Boolean varBTToFront=false;
    private Boolean varBTToBack=false;
    private Boolean varBTToLeft=false;
    private Boolean varBTToRight=false;
    private Stage stageMeasurment=new Stage();
    private Stage stageError=new Stage();
    private Stage stageCalib=new Stage();
    private FrameInput frameInput =new FrameInput();
    private measurmentTableView measurmenttableview=new measurmentTableView();
    private TableViewError tableViewError=new TableViewError();
    private int beforeDirection=0;
    private int mousePositionX=0;
    private int mousePositionY=0;
    private float fun1a=0;
    private float fun1b=0;
    private float fun2a=0;
    private float fun2b=0;
    private int carMoveVector=0;
    private boolean sendData=false;
    private int time500ms=0;

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
        try{
            LBConnectionStatus.minWidth(120);
            BTConnectToDevice.minWidth(100);
            BTConnectToDevice.setStyle("-fx-background-color:green; -fx-text-fill:black");
            bluetooth.searchDevices();
            setCBBluetoothDevices(bluetooth.getRemoteDevice());
            return;
        }catch(BluetoothStateException ex){
            typicalFunction.showError("Bluetooth jest wyłączony");
            ex.printStackTrace();
        }catch(IOException ex){
            ex.printStackTrace();
            typicalFunction.showError("Błąd modułu IO");
        }
    }
    @FXML
    public void searchDevice(){
        drawBattery();drawDefaultJoyStick();drawCarWithSensors();
        CBBluetoothDevices.getItems().clear();
        try{
            bluetooth.searchDevices();
            setCBBluetoothDevices(bluetooth.getRemoteDevice());
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }


    @FXML
    public void connectToDevice(){
        calibrateFunction();
        drawDefaultJoyStick();
        drawCarWithSensors();
        if(trControlMainScene!=null && !trControlMainScene.isAlive()){
            try {
                trControlMainScene = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(true){
                            try {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        funRTControlSystem();
                                    }
                                });
                                Thread.sleep(250);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                trControlMainScene.start();
            }catch(Exception ex){
                ex.printStackTrace();
        }}

        /*
         Wyłączenie pola związane z informacjami diagnostycznymi samochodu
         */
        VBControlCar.disableProperty().set(true);
        /*
        Sprawdzenie czy wybrano jakieś urzadzenie bluetooth
         */
        if(CBBluetoothDevices.getValue()==null){
            typicalFunction.showError("Nie wybrano urządzenia bluetooth");
            return;
        }
        if(disconnectDevice()){
            return;
        }
        try{
            bluetooth.connectToDevice(CBBluetoothDevices.getValue());
            trReadFromBluetooth=new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        try {
                            byte[] bytes = bluetooth.getData();
                            if(bytes==null)
                                continue;
                            for(byte i:bytes){
                                if(frameInput.addFrameInput((int)(i&0xFF))){
                                    switch(frameInput.getFunction()){
                                        case FrameInput.MEASURE_DISTANCE_FUN_RECEIVED:
                                            //frameInput.printFrame();
                                            measurmenttableview.addData(frameInput.getFrameInput());
                                            break;
                                        case FrameInput.ERROR_CODE_FUN:
                                            tableViewError.addData(frameInput.getFrameInput());
                                            setInfoAlert();
                                                break;
                                        case FrameInput.SEND_MEASUREMENT_FUN:
                                            //frameInput.getFrameInput();
                                            drivingMeasurmentSample.addMeasurment(frameInput.getFrameInput());
                                            Platform.runLater(new Runnable() {
                                                @Override
                                               public void run() {
                                                    drawCarWithSensors();
                                                }
                                            });
                                            break;
                                        case FrameInput.SEND_IR_SENSOR_STATUS:
                                            drivingMeasurmentSample.addIrSensor(frameInput.getFrameInput());
                                            break;
                                        case FrameInput.SEND_BATTERY_MEASURMENT_VALUE:
                                            drivingMeasurmentSample.addBatteryVoltage(frameInput.getFrameInput());
                                            break;
                                        default:
                                            frameInput.printInvalidFrame();
                                    }
                                }
                            }

                            Thread.sleep(2);
                        }catch (Exception ex){
                            ex.printStackTrace();

                        }
                    }
                }
            });
            //trGetSensorInfo.start();
            trReadFromBluetooth.start();
            LBConnectionStatus.setText("Połączony");
            BTConnectToDevice.setText("Rozłącz");
            BTConnectToDevice.setStyle("-fx-background-color:red; -fx-text-fill:white");
            VBControlCar.disableProperty().set(false);
        }catch(BluetoothConnectionException ex){
            typicalFunction.showError("Wybrane urządzenie jest niedostępne");
            ex.printStackTrace();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @FXML
    public void showSceneMeasurment(){
        try{
            if(!stageMeasurment.isShowing()) {
                URL fxmlURL = this.getClass().getClassLoader().getResource("\\measurmentSensor\\DistanceMeasureScene.fxml");
                Parent root = FXMLLoader.load(fxmlURL);
                Scene scene = new Scene(root);
                root.getStylesheets().add("style.css");
                stageMeasurment.setScene(scene);
                stageMeasurment.setTitle("Praca magisterska panel pomiaru odległości");
                stageMeasurment.show();
            }
        }catch(Exception ex){
            ex.printStackTrace();
            typicalFunction.showError("Nie udało się znaleśc pliku DistanceMeasureScene.fxml. \r\n Bład: "+ex.getMessage());
        }
    }
    @FXML
    public void showError(){
        clearInfoAlert();
        try{
            if(!stageError.isShowing()) {
                URL fxmlURL = this.getClass().getClassLoader().getResource("\\errorDevice\\ErrorStageScene.fxml");
                Parent root = FXMLLoader.load(fxmlURL);
                Scene scene = new Scene(root);
                root.getStylesheets().add("style.css");
                stageError.setScene(scene);
                stageError.setTitle("Praca magisterska panel błędów");
                stageError.show();
            }
        }catch(Exception ex){
            ex.printStackTrace();
            typicalFunction.showError("Nie udało się znaleśc pliku errorDevice\\ErrorStageScene.fxml. \r\n Bład: "+ex.getMessage());
        }
    }

    @FXML
    public void showColibration(){
        clearInfoAlert();
        try{
            if(!stageCalib.isShowing()) {
                URL fxmlURL = this.getClass().getClassLoader().getResource("\\Calibration\\ErrorStageScene.fxml");
                Parent root = FXMLLoader.load(fxmlURL);
                Scene scene = new Scene(root);
                root.getStylesheets().add("style.css");
                stageCalib.setScene(scene);
                stageCalib.setTitle("Praca magisterska panel kalibracji");
                stageCalib.show();
            }
        }catch(Exception ex){
            ex.printStackTrace();
            typicalFunction.showError("Nie udało się znaleśc pliku \\Calibration\\ErrorStageScene.fxml. \r\n Bład: "+ex.getMessage());
        }
    }
    public boolean disconnectDevice(){
        if(LBConnectionStatus.getText().equals("Połączony")){
            try {
                close();
                LBConnectionStatus.setText("Niepołączony");
                BTConnectToDevice.setText("Połącz");
                BTConnectToDevice.setStyle("-fx-background-color:green;");
                return true;
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return false;
        }

    private void setInfoAlert(){
        if(IVbellImage.getImage()==null) {
            BTShowError.setStyle("-fx-background-color:red;" +
                    "-fx-text-fill:white");
            try {
                FileInputStream input = new FileInputStream("src\\image\\bell.png");
                Image image = new Image(input);
                IVbellImage.setImage(image);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    private void clearInfoAlert(){
        if(IVbellImage.getImage()!=null) {
            if (IVbellImage.getImage() != null) {
                BTShowError.setStyle("");
                IVbellImage.setImage(null);
            }
        }
    }
    public void sendCommandGetStatusAllStructure(){
        try {
            bluetooth.sendData((byte) FrameInput.GET_STATUS_ALL_STRUCTURE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void funRTControlSystem(){
            int send=calcfunction();
            if(send!=0){
                try {
                    //System.out.println("Wysłano funkcja funRTControlSystem()");
                    bluetooth.sendData((byte) send);
                    time500ms=0;
                } catch (Exception exception) {
                    exception.printStackTrace();
                    disconnectDevice();
                }
            }else{
                time500ms++;
                if(time500ms>1){
                    time500ms=0;
                    drivingMeasurmentSample.timeoutNextMeasurment();
                    drawCarWithSensors();
                    drawBattery();
                }
            }
    }

    private void setCBBluetoothDevices(RemoteDevice[] remoteDevices)throws IOException {
        ArrayList<String> sRemoteDevicesName=new ArrayList<>();
        for(RemoteDevice i: remoteDevices){
            sRemoteDevicesName.add(i.getFriendlyName(false));
        }
        CBBluetoothDevices.getItems().addAll(sRemoteDevicesName);
    }

    @FXML
    public void startDrawing(){
        sendData=true;
        Bounds bounds = CVController.getBoundsInLocal();
        Bounds screenBounds = CVController.localToScreen(bounds);
        int x = (int) screenBounds.getMinX();
        int y = (int) screenBounds.getMinY();
        Point p = MouseInfo.getPointerInfo().getLocation();
        this.mousePositionX = p.x-x-10;
        this.mousePositionY = p.y-y-10;
        GraphicsContext context = CVController.getGraphicsContext2D();
        drawDefaultJoyStick();
        context.setFill(Color.RED);
        context.strokeOval(this.mousePositionX, this.mousePositionY, 20, 20);

        int send=calcfunction();
        if(send!=beforeDirection){
            try {
                //System.out.println("Wysłano funkcja startDrawing()");
                bluetooth.sendData((byte) send);
            } catch (Exception exception) {
                exception.printStackTrace();
                disconnectDevice();
            }
            beforeDirection=send;
        }


    }

    private int calcfunction(){
        if(sendData){
            int x=this.mousePositionX;
            int y=this.mousePositionY;

            double newX=(this.mousePositionX-CVController.getWidth()/2);
            double newY=(this.mousePositionY-CVController.getHeight()/2)*-1;
            double tangensAlfa=Math.toDegrees(Math.atan(newY/newX));
            if(newX<0 && newY>0 ){
                tangensAlfa+=180;
                if(tangensAlfa<90)
                    tangensAlfa=180;
            }
            if(newY<0){
                tangensAlfa=tangensAlfa*-1;
                if(tangensAlfa==-0.0){
                    tangensAlfa=0.0;
                }
                if(newX<0) {
                    tangensAlfa += 180;
                    if(tangensAlfa<90)
                        tangensAlfa=180;
                }
            }

            //System.out.println("kąt="+tangensAlfa+" x="+newX+"  y="+newY );

            if(tangensAlfa<=25){
                //System.out.println("ROTATE_RIGHT");
                return FrameInput.ROTATE_RIGHT;
            }
            if(tangensAlfa<=70){
                //System.out.println("RIDE_RIGHT_FUN");
                if(newY>0)
                    return FrameInput.RIDE_RIGHT_FUN;
                else
                    return FrameInput.RIDE_BACKWARD_RIGHT;
            }
            if (tangensAlfa>155){
                //System.out.println("ROTATE_LEFT");
                return  FrameInput.ROTATE_LEFT;
            }
            if(tangensAlfa>110){
                //System.out.println("RIDE_LEFT_FUN");
                if(newY>0)
                    return  FrameInput.RIDE_LEFT_FUN;
                else
                    //System.out.println("RIDE_BACKWARD_LEFT");
                    return FrameInput.RIDE_BACKWARD_LEFT;
            }
            if(newY>0){
                //System.out.println("RIDE_FORWARD_FUN");
                return  FrameInput.RIDE_FORWARD_FUN;
            }else{
                //System.out.println("RIDE_BACKWARD_FUN");
                return FrameInput.RIDE_BACKWARD_FUN;
            }
        }
        return 0;
    }
    @FXML
    public void stopDrawing(){
        //System.out.println("STOP");
        sendData=false;
        drawDefaultJoyStick();
        beforeDirection=0;
    }

    private void drawDefaultJoyStick(){
        GraphicsContext context = CVController.getGraphicsContext2D();
        context.clearRect(0, 0, CVController.getWidth(), CVController.getHeight());
        double centerWidth= CVController.getWidth();
        double centerHeight= CVController.getHeight();
        //System.out.println("centerWidth="+centerWidth+" centerHeight="+centerHeight);
        context.setFill(Color.BLACK);
        context.strokeOval(2, 2, centerWidth-4, centerHeight-4);
        //Strzałka do przodu
        double x=centerWidth/2;
        double y=10;
        context.strokeLine(x,y,x-18,y+18);
        context.strokeLine(x-18,y+18,x-6,y+18);
        context.strokeLine(x-6,y+18,x-6,y+36);
        context.strokeLine(x-6,y+36,x+6,y+36);
        context.strokeLine(x+6,y+36,x+6,y+18);
        context.strokeLine(x+6,y+18,x+18,y+18);
        context.strokeLine(x+18,y+18,x,y);
        //Strzałka w lewo
        x=10;
        y=centerHeight/2;
        context.strokeLine(x,y,x+18,y-18);
        context.strokeLine(x+18,y-18,x+18,y-6);
        context.strokeLine(x+18,y-6,x+36,y-6);
        context.strokeLine(x+36,y-6,x+36,y+6);
        context.strokeLine(x+36,y+6,x+18,y+6);
        context.strokeLine(x+18,y+6,x+18,y+18);
        context.strokeLine(x+18,y+18,x,y);

        //Strzałka do tyłu
        x=centerWidth/2;
        y=centerHeight-10;
        context.strokeLine(x,y,x+18,y-18);
        context.strokeLine(x+18,y-18,x+6,y-18);
        context.strokeLine(x+6,y-18,x+6,y-36);
        context.strokeLine(x+6,y-36,x-6,y-36);
        context.strokeLine(x-6,y-36,x-6,y-18);
        context.strokeLine(x-6,y-18,x-18,y-18);
        context.strokeLine(x-18,y-18,x,y);

        //Strzałka w prawo
        x=centerWidth-10;
        y=centerHeight/2;
        context.strokeLine(x,y,x-18,y+18);
        context.strokeLine(x-18,y+18,x-18,y+6);
        context.strokeLine(x-18,y+6,x-36,y+6);
        context.strokeLine(x-36,y+6,x-36,y-6);
        context.strokeLine(x-36,y-6,x-18,y-6);
        context.strokeLine(x-18,y-6,x-18,y-18);
        context.strokeLine(x-18,y-18,x,y);
    }

    private void calibrateFunction(){
        double height=CVController.getHeight();
        double width=CVController.getWidth();
        // obliczanie funkcji od jednej przekątnej do drugiej
        fun1b=0;
        fun1a= (float) (height/width);
        //Funkcja dlugiejprzekontnej
        fun2b=(float) height;
        fun2a=-(fun2b/((float) width));
        //System.out.println("fun1a="+fun1a+" fun1b="+fun1b+" fun2a="+fun2a+" fun2b="+fun2b);

    }

    private void drawCarWithSensors(){
        double height=CVcarWithSensors.getHeight();
        double width=CVcarWithSensors.getWidth();
        double centerx=width/2;
        double centery=height/2;
        GraphicsContext context = CVcarWithSensors.getGraphicsContext2D();
        context.clearRect(0, 0, CVcarWithSensors.getWidth(), CVcarWithSensors.getHeight());
       double defaultWidth=100;
        double defaultHeight=150;
        if(sendData==false){
            drawCar(centerx,centery,defaultWidth,defaultHeight);
            carMoveVector=0;
        }else{
            carMoveVector+=10;
            drawCar(centerx,centery-carMoveVector,defaultWidth,defaultHeight);
            if(carMoveVector>40){
                carMoveVector=0;
            }
        }

        drawSensor(centerx,centery,width-20,height-20);
    }
    private void drawCar(double x, double y, double w,double h){
        double centerx=x;
        double centery=y;
        double heightElement=h/5; // 1/8
        double widthOval=w/4;
        double topArcx=centerx-w/2;
        double topArcy=centery-heightElement*5/2-h/4;
        double firstLinex=topArcx;
        double firstLiney=topArcy+h/4;
        double seconfLinex=x+w/2;
        double addWidthOval=10;

        GraphicsContext context = CVcarWithSensors.getGraphicsContext2D();
        Paint strokePreview=context.getStroke();
        double lineWidthPreview=context.getLineWidth();
        context.setLineWidth(4.0);
        if(drivingMeasurmentSample.getIrSensor(DrivingMeasurmentSamples.IR_NR2_NUMBER)){
            context.setStroke(Color.RED);
        }
        context.strokeArc(topArcx, topArcy, w, h/2, 0, 90, ArcType.OPEN);
        context.setStroke(strokePreview);


        if(drivingMeasurmentSample.getIrSensor(DrivingMeasurmentSamples.IR_NR3_NUMBER)){
            context.setStroke(Color.RED);
        }
        context.strokeArc(topArcx, topArcy, w, h/2, 90, 90, ArcType.OPEN);
        context.setStroke(strokePreview);

        if(drivingMeasurmentSample.getIrSensor(DrivingMeasurmentSamples.IR_NR4_NUMBER)){
            context.setStroke(Color.RED);
        }
        context.strokeLine(firstLinex,firstLiney,topArcx,firstLiney+heightElement-addWidthOval/2);
        context.setStroke(strokePreview);
        //rysowanie koła lewego górnego
        context.fillRoundRect(firstLinex-w/4/2, (firstLiney+heightElement-addWidthOval/2)+5, w/4, h/5, w/10, h/10);

        context.strokeLine(firstLinex,firstLiney+heightElement*2+addWidthOval/2,firstLinex,firstLiney+heightElement*3-addWidthOval/2);
        //rysowanie koła lewego dolnego
        context.fillRoundRect(firstLinex-w/4/2, (firstLiney+heightElement*3-addWidthOval/2)+5, w/4, h/5, w/10, h/10);

        if(drivingMeasurmentSample.getIrSensor(DrivingMeasurmentSamples.IR_NR5_NUMBER)){
            context.setStroke(Color.RED);
        }
        context.setStroke(strokePreview);

        context.strokeLine(firstLinex,firstLiney+heightElement*4+addWidthOval/2,firstLinex,firstLiney+heightElement*5);

        if(drivingMeasurmentSample.getIrSensor(DrivingMeasurmentSamples.IR_NR6_NUMBER)){
            context.setStroke(Color.RED);
        }
        context.strokeArc(firstLinex, firstLiney+heightElement*5-h/4, w, h/2, 0, -90, ArcType.OPEN);
        context.setStroke(strokePreview);

        if(drivingMeasurmentSample.getIrSensor(DrivingMeasurmentSamples.IR_NR7_NUMBER)){
            context.setStroke(Color.RED);
        }
        context.strokeArc(firstLinex, firstLiney+heightElement*5-h/4, w, h/2, -90, -90, ArcType.OPEN);
        context.setStroke(strokePreview);

        if(drivingMeasurmentSample.getIrSensor(DrivingMeasurmentSamples.IR_NR8_NUMBER)){
            context.setStroke(Color.RED);
        }
            context.strokeLine(seconfLinex,firstLiney+heightElement*4+addWidthOval/2,seconfLinex,firstLiney+heightElement*5);
        context.setStroke(strokePreview);


        //rysowanie koła prawego dolnego
        context.fillRoundRect(seconfLinex-w/4/2, (firstLiney+heightElement-addWidthOval/2)+5, w/4, h/5, w/10, h/10);

        context.strokeLine(seconfLinex,firstLiney+heightElement*2+addWidthOval/2,seconfLinex,firstLiney+heightElement*3-addWidthOval/2);

        //rysowanie koła prawego górnego
        context.fillRoundRect(seconfLinex-w/4/2, firstLiney+heightElement*3-addWidthOval/2+5, w/4, h/5, w/10, h/10);


        if(drivingMeasurmentSample.getIrSensor(DrivingMeasurmentSamples.IR_NR1_NUMBER)){
            context.setStroke(Color.RED);
        }
        context.strokeLine(seconfLinex,firstLiney,seconfLinex,firstLiney+heightElement-addWidthOval/2);
        context.setStroke(strokePreview);

        context.setLineWidth(lineWidthPreview);
    }

    private void drawSensor(double x,double y,double w,double h){
        GraphicsContext context = CVcarWithSensors.getGraphicsContext2D();
        //double  ovalX=(CVcarWithSensors.getWidth()-w)/2;
        //double ovalY=(CVcarWithSensors.getHeight()-h)/2;
        //context.strokeOval(ovalX,ovalY,w,h);
        //int WITHLETTER=30;
        //int HEIGHTLETTER=20;
        //int maxMeasurment=(int)(w/WITHLETTER);
        //double addRest=(w-maxMeasurment*WITHLETTER)/maxMeasurment;
        Paint strokePreview=context.getStroke();
        int[] arrayOrdinalNumberShowed=new int[DrivingMeasurmentSamples.MAX_ARRAY_OF_UPDATED_ELEMENTS];
        for(int i=0;i<DrivingMeasurmentSamples.MAX_ARRAY_OF_UPDATED_ELEMENTS;i++){
            int ordinalNumber=drivingMeasurmentSample.getAnArrayofUpdatedElements(i);
            if(ordinalNumber==0){ //Brak wartości do wyświetlenia
                continue;
            }
            int j;

            for(j=0;j<DrivingMeasurmentSamples.MAX_ARRAY_OF_UPDATED_ELEMENTS;j++){ //funkcja sprawdzająca czy pomiar o tym numerze został już wyświetlony
                if(arrayOrdinalNumberShowed[j]==ordinalNumber){
                    break;
                }
            }

            if(j!=DrivingMeasurmentSamples.MAX_ARRAY_OF_UPDATED_ELEMENTS){ //Sprawdzenie czy wcześniejsza funkcja została wykonana do konca
                continue;
            }
            boolean strokeReset=false;
            ordinalNumber--;
            float laser=drivingMeasurmentSample.getLaserSensorMeasurement(ordinalNumber);
            double ultra=drivingMeasurmentSample.getUltrasonicSensorMeasurement(ordinalNumber);
            if ((laser < 10.0 && laser > 0.0) || ( ultra<10.0 && ultra>0.0)){
                context.setStroke(Color.RED);
                strokeReset=true;
            }
            String text=String.format("%03.2f\n%03.2f",laser,ultra);
            context.strokeText(text,DrivingMeasurmentSamples.positionX[ordinalNumber],DrivingMeasurmentSamples.positionY[ordinalNumber]);

            if(strokeReset){
                context.setStroke(strokePreview);
            }
        }

        /*
        for(int i=0;i< DrivingMeasurmentSamples.positionX.length;i++){
            boolean strokeReset=false;
            float laser=drivingMeasurmentSample.getLaserSensorMeasurement(i);
            double ultra=drivingMeasurmentSample.getUltrasonicSensorMeasurement(i);
            if ((laser < 10.0 && laser > 0.0) || ( ultra<10.0 && ultra>0.0)){
                context.setStroke(Color.RED);
                strokeReset=true;
            }
            String text=String.format("%03.2f\n%03.2f",laser,ultra);
            context.strokeText(text,DrivingMeasurmentSamples.positionX[i],DrivingMeasurmentSamples.positionY[i]);

            if(strokeReset){
                context.setStroke(strokePreview);
            }
        }*/

    }
    private void drawBattery(){
        GraphicsContext context = CVBattery.getGraphicsContext2D();
        context.clearRect(0, 0, CVBattery.getWidth(), CVBattery.getHeight());
        Double width=CVBattery.getWidth();
        Double height=CVBattery.getHeight();
        double battery=drivingMeasurmentSample.getVoltage();
        //System.out.println(battery);
        String text = "NaN%";
        if(battery>0) {
           text=String.format("%.1f",battery)+"%";
        }
        double widthBattery=width/2-20;
        context.strokeRect(width/2, 0,widthBattery , height);
        context.fillRect((width-20),7.5,10,height/2);
        context.strokeText(text,width/2-45,height/2+5);
        //Wypełnienie baterii
        Paint strokeFill=context.getFill();
        Double lineWidth=context.getLineWidth();
        if(!text.contains("NaN")){
            if(battery<15){
                context.setFill(Color.RED);
            }else if(battery<30){
                context.setFill(Color.ORANGERED);
            }else{
                context.setFill(Color.GREEN);
            }
            context.fillRect(width/2, 0,widthBattery*battery/100 , height);
        }

        context.setFill(strokeFill);
        context.setLineWidth(lineWidth);
    }
    public void close(){
        try {

            if(stageMeasurment.isShowing()) {
               stageMeasurment.close();
            }
            if(stageError.isShowing()) {
                stageError.close();
            }
            if(trControlMainScene!=null){
                trControlMainScene.stop();
            }
            if(trReadFromBluetooth!=null) {
                trReadFromBluetooth.stop();
            }
            bluetooth.closeConnection();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
