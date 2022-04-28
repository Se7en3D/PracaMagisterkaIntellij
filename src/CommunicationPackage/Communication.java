package CommunicationPackage;

import CanvasPackage.CanvasBattery;
import CanvasPackage.CanvasCar;
import CanvasPackage.CanvasCarController;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import old.carRc.FrameInput;

import java.awt.*;
import java.util.ArrayList;

public class Communication {
    private Battery battery=new Battery();
    private DistanceMeasuring distanceMeasuring =new DistanceMeasuring();
    private IrSensor irSensor=new IrSensor();
    private ClassInfoDecoder classInfoDecoder=new ClassInfoDecoder();
    private CanvasBattery canvasBattery=new CanvasBattery();
    private CanvasCarController canvasCarController=new CanvasCarController();
    private CanvasCar canvasCar=new CanvasCar();
    private ArrayList<Integer> arrayByteInput=new ArrayList<>();
    private ArrayList<ClassInfo> classInfoArrayList=new ArrayList<>();
    private ArrayList<DistanceMeasuringWithoutPosition> distanceMeasuringWithoutPositions=new ArrayList<>();
    private int driveStatus=1;
    private int timeToSendControlCommand;
    private static final int communiactionFirstByte=0xFF;

    public Communication(){
        timeToSendControlCommand=0;
    }
    public void addFrameInput(int data){
        int bufferSize=arrayByteInput.size();
        if(bufferSize>0){
            if(bufferSize==1 && (data==0xFF || data==0x00)){
                arrayByteInput.clear();
            }
            arrayByteInput.add(data);
            if(bufferSize>2){
                //int lastByte=arrayByteInput.get(bufferSize)<<8+arrayByteInput.get(bufferSize-1);
                if(arrayByteInput.get(bufferSize)==0xFE){
                    if(arrayByteInput.get(bufferSize-1)==0xFE){
                        //showArrayByteInput();
                        updateInformationByFrameInput();
                        arrayByteInput.clear();
                    }
                }
            }
        }else{
            if(data==communiactionFirstByte){
                arrayByteInput.add(data);
            }else{
                if(data!=0) {
                    char sign=(char) data;
                    System.out.print(sign);
                }
            }
        }
    }

    private void showArrayByteInput(){
        System.out.print("Odebrana ramka komunikacyjna: ");
        for(int i: arrayByteInput){
            System.out.print(String.format(" %X ",i));
        }
        System.out.println("");
    }

    private void updateInformationByFrameInput(){
        int funId=arrayByteInput.get(1);
        switch(funId){
            case FunctionID.MEASURE_DISTANCE_FOR_PC:
                distanceMeasuring.addMeasure(arrayByteInput);
                //System.out.println("Rozpoznano funkcje MEASURE_DISTANCE_FOR_PC");
                break;
            case FunctionID.ERROR_CODE_FUN:
                ClassInfo tempClassInfo= classInfoDecoder.getDecodedClassInfo(arrayByteInput);
                if(tempClassInfo!=null){
                    classInfoArrayList.add(tempClassInfo);
                }
                //System.out.println("Rozpoznano funkcje ERROR_CODE_FUN");
                break;
            case FunctionID.SEND_IR_SENSOR_STATUS:
                //System.out.println("Rozpoznano funkcje SEND_IR_SENSOR_STATUS");
                irSensor.updateSensorStatus(arrayByteInput);
                break;
            case FunctionID.SEND_BATTERY_MEASURMENT_VALUE:
                //System.out.println("Rozpoznano funkcje SEND_BATTERY_MEASURMENT_VALUE");
                battery.calcVoltage(arrayByteInput);
                break;
            case FunctionID.MEASURE_DISTANCE_FUN_RECEIVED:
                System.out.println("Rozpoznano funkcje MEASURE_DISTANCE_FUN_RECEIVED");
                distanceMeasuringWithoutPositions.add(new DistanceMeasuringWithoutPosition(arrayByteInput));
                break;
            case FunctionID.SEND_DRIVE_STATUS:
                driveStatus=arrayByteInput.get(2);
                //System.out.println("Status jazdy "+arrayByteInput.get(2));
                break;
            default:
                System.out.print("Brak funkcji obsługującej ramkę");
                for(int i: arrayByteInput){
                    System.out.print(String.format(" %X ",i));
                }
                System.out.println();
                break;

        }
    }
    synchronized public void refreshBatteryCanvas(GraphicsContext context,Double height,Double width){
        double percentBattery=battery.getPercentVoltage();
        canvasBattery.drawBattery(context,percentBattery,height,width);
    }
    public int calcSendFunctionFromCanvasController(Canvas canvas,Double height,Double width) throws Exception{
        Point pointMouse = MouseInfo.getPointerInfo().getLocation();
        Point2D pointNote = canvas.localToScreen(0, 0);
        double positionXMouseTowardNote = pointMouse.x - pointNote.getX();
        double positionYMouseTowardNote = pointMouse.y - pointNote.getY();
        int widthNode=(int)canvas.getWidth();
        int heightNode=(int)canvas.getHeight();
        if(((positionXMouseTowardNote>=0)&& (positionXMouseTowardNote<=widthNode))&&
                ((positionYMouseTowardNote>=0)&&(positionYMouseTowardNote<=heightNode))){
            //System.out.println("Pozycja myszy względem Canvasa x="+positionXMouseTowardNote+ " y="+positionYMouseTowardNote);
            canvasCarController.drawControllerWithPosition(canvas.getGraphicsContext2D(),height,width,positionXMouseTowardNote-10,positionYMouseTowardNote-10);
        }else{
            //canvasCarController.drawController(canvas);
        }
        positionXMouseTowardNote=(positionXMouseTowardNote-width/2);
        positionYMouseTowardNote=(positionYMouseTowardNote-height/2)*-1;
        double tangensAlfa=Math.toDegrees(Math.atan(positionYMouseTowardNote/positionXMouseTowardNote));
        if(positionXMouseTowardNote<0 && positionYMouseTowardNote>0 ){
            tangensAlfa+=180;
            if(tangensAlfa<90)
                tangensAlfa=180;
        }
        if(positionYMouseTowardNote<0){
            tangensAlfa=tangensAlfa*-1;
            if(tangensAlfa==-0.0){
                tangensAlfa=0.0;
            }
            if(positionXMouseTowardNote<0) {
                tangensAlfa += 180;
                if(tangensAlfa<90)
                    tangensAlfa=180;
            }
        }

        //System.out.println("kąt="+tangensAlfa+" x="+positionXMouseTowardNote+"  y="+positionYMouseTowardNote );

        if(tangensAlfa<=25){
            //System.out.println("ROTATE_RIGHT");
            return FrameInput.ROTATE_RIGHT;
        }
        if(tangensAlfa<=70){
            //System.out.println("RIDE_RIGHT_FUN");
            if(positionYMouseTowardNote>0)
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
            if(positionYMouseTowardNote>0)
                return  FrameInput.RIDE_LEFT_FUN;
            else
                //System.out.println("RIDE_BACKWARD_LEFT");
                return FrameInput.RIDE_BACKWARD_LEFT;
        }
        if(positionYMouseTowardNote>0){
            //System.out.println("RIDE_FORWARD_FUN");
            return  FrameInput.RIDE_FORWARD_FUN;
        }else{
            //System.out.println("RIDE_BACKWARD_FUN");
            return FrameInput.RIDE_BACKWARD_FUN;
        }
    }
    public void resetCanvasController(GraphicsContext context,double centerHeight,double centerWidth){
        canvasCarController.drawController(context,centerHeight,centerWidth);
    }
    public void canvasCarWithSensor(GraphicsContext context, double height, double width){
        double centerx=width/2;
        double centery=height/2;
        MeasuringSample[] samples=distanceMeasuring.getMeasure();
        try {
            canvasCar.drawCar(context, centerx, centery, 100, 150, irSensor.getSensorStatus(), samples);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public ArrayList<ClassInfo> getClassInfoArrayList(){
        return this.classInfoArrayList;
    }
    public void addErrorTestValue(){
        ClassInfo tempClassInfo= classInfoDecoder.getDecodeClassInfoStandard();
        if(tempClassInfo!=null){
            classInfoArrayList.add(tempClassInfo);
        }
    }
    public void timeRefresh(){
        this.timeToSendControlCommand++;
        distanceMeasuring.time();
    }
    public Boolean isReadyToSendControlCommand(){
        if(this.timeToSendControlCommand>0){
            canvasCar.nextOffset();
            this.timeToSendControlCommand=0;
            return true;
        }
        return false;
    };
    public void clearClassInfoDecoder(){
        classInfoArrayList.clear();
    }
    public int getDriveStatus(){
        return this.driveStatus;
    }

    public ArrayList<DistanceMeasuringWithoutPosition> getDistanceMeasuringWithoutPositions() {
        return distanceMeasuringWithoutPositions;
    }
}
