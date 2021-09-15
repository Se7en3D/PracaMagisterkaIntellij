package carRc;

import java.util.ArrayList;

public class FrameInput {
    public static final int RIDE_FORWARD_FUN = 1;
    public static final int RIDE_BACKWARD_FUN = 2;
    public static final int RIDE_RIGHT_FUN = 3;
    public static final int RIDE_LEFT_FUN = 4;
    public static final int ROTATE_LEFT = 5;
    public static final int ROTATE_RIGHT = 6;
    public static final int STOP_FUN = 7;
    public static final int SEND_MEASUREMENT_FUN = 125;
    public static final int ERROR_CODE_FUN = 126;
    public static final int SEND_IR_SENSOR_STATUS = 127;
    public static final int SEND_BATTERY_MEASURMENT_VALUE = 128;
    public static final int CALIBRATION_PWM_DATA = 129;

    public static final int MEASURE_DISTANCE_FUN = 240;
    public static final int MEASURE_DISTANCE_FUN_RECEIVED=241;
    public static final int NO_DEFINED_FUN = 250;


    private ArrayList<Integer> frameInput=new ArrayList<>();
    private ArrayList<Integer> frameOutput=new ArrayList<>();
    private boolean completeFrame=false;



    public void printInvalidFrame(){
        for(int i:frameInput){
            System.out.print(" "+String.format("%x",i)+" ");
        }
        completeFrame=false;
        System.out.println("");
        frameInput.clear();
    }

    public boolean addFrameInput(int data){
         if(frameInput.size()>0){ //Sprawdzenie czy w buforze znajują się jakieś dane
             if(completeFrame){ //Sprawdzenie czy odebrano poprzednią ramkę z bufora
                 System.out.println("Odebrano nową ramkę ale nie wykonano jeszcze poprzedniej");
                 return false;
             }
             /*if(data==0xFF){ //Sprawdzenie czy odebrano 0xFF( sygnalizując nową ramkę)
                System.out.print("Odebrano niekompletną ramkę: ");
                printInvalidFrame();
                frameInput.clear();
                frameInput.add(data);
                return false;
             }*/
            if(frameInput.get(frameInput.size()-1)==0xFE && data==0xFE){ //Sprawdzenie czy odebrano koniec transmisji
                frameInput.add(data);
                completeFrame=true;
                return true;
            }
            frameInput.add(data);
         }else{
            if(data==0xFF){ //Sprawdzenie czy jest to początek ramki
                frameInput.add(data);
                return false;
            }else{
                System.out.print((char) data);
                return false;
            }
         }
        return false;
    }
    public int getFunction(){
        if(completeFrame){
            return frameInput.get(1);
        }
        return 0;
    }
    public ArrayList<Integer> getFrameInput(){
        if(completeFrame){
            ArrayList<Integer> tempArray=new ArrayList<>(frameInput);
            completeFrame=false;
            frameInput.clear();
            return  tempArray;
        }
        return null;
    }
    public void printFrame(){
                if(completeFrame){
                    for(int i:frameInput){
                        System.out.print(" "+String.format("%x",i)+" ");
                    }
                    System.out.println("");
                    completeFrame=false;
                    frameInput.clear();
            }
    }

}
