package CommunicationPackage;

import CanvasPackage.CanvasBattery;
import CanvasPackage.CanvasCar;
import CanvasPackage.CanvasCarController;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;

import java.awt.*;
import java.util.ArrayList;

public class Communication {
    private Battery battery=new Battery();
    private DistanceMeasuringSamples distanceMeasuringSamples=new DistanceMeasuringSamples();
    private IrSensor irSensor=new IrSensor();
    private ClassInfoDecoder classInfoDecoder=new ClassInfoDecoder();
    private CanvasBattery canvasBattery=new CanvasBattery();
    private CanvasCarController canvasCarController=new CanvasCarController();
    private CanvasCar canvasCar=new CanvasCar();
    private ArrayList<Integer> arrayByteInput=new ArrayList<>();
    private ArrayList<ClassInfo> classInfoArrayList=new ArrayList<>();
    private static final int communiactionFirstByte=0xFF;
    public void addFrameInput(int data){
        int bufferSize=arrayByteInput.size();
        if(bufferSize>0){
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
                    System.out.print(data);
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
                distanceMeasuringSamples.addMeasure(arrayByteInput);
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
                break;
            default:
                System.out.println("Brak funkcji obsługującej "+String.format("%X",funId));
                break;

        }
    }
    synchronized public void refreshBatteryCanvas(Canvas canvas){
        double percentBattery=battery.getPercentVoltage();
        canvasBattery.drawBattery(canvas,percentBattery);
    }
    public int calcSendFunctionFromCanvasController(Canvas canvas){
        Point pointMouse = MouseInfo.getPointerInfo().getLocation();
        Point2D pointNote = canvas.localToScreen(0, 0);
        int positionXMouseTowardNote = pointMouse.x - (int) pointNote.getX();
        int positionYMouseTowardNote = pointMouse.y - (int) pointNote.getY();
        int widthNode=(int)canvas.getWidth();
        int heightNode=(int)canvas.getHeight();
        if(((positionXMouseTowardNote>=0)&& (positionXMouseTowardNote<=widthNode))&&
                ((positionYMouseTowardNote>=0)&&(positionYMouseTowardNote<=heightNode))){
            System.out.println("Pozycja myszy względem Canvasa x="+positionXMouseTowardNote+ " y="+positionYMouseTowardNote);
            canvasCarController.drawControllerWithPosition(canvas,positionXMouseTowardNote-10,positionYMouseTowardNote-10);
        }else{
            canvasCarController.drawController(canvas);
        }
        return 0x00;
    }
    public void resetCanvasController(Canvas canvas){
        canvasCarController.drawController(canvas);
    }
    public void canvasCarWithSensor(Canvas canvas){
        double height=canvas.getHeight();
        double width=canvas.getWidth();
        double centerx=width/2;
        double centery=height/2;
        canvasCar.drawCar(canvas,centerx,centery,100,150,irSensor.getSensorStatus());
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
}
