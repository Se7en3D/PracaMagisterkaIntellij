package CommunicationPackage;

import java.util.ArrayList;

public class DistanceMeasuringSamples {
    private static final int servoPosition=12;
    private float ultrasonicSensorMeasures[]=new float[servoPosition];
    private float laserSensorMeasures[]=new float[servoPosition];

    public void addMeasure(ArrayList<Integer> frame){
        int ordinalNumber=frame.get(2);
        System.out.print("Funkcja addMeasurment ordinalNumber="+ordinalNumber+"   ");
        if(ordinalNumber>=servoPosition){
            try {
                throw new Exception("ordinalNumber powyżej zmiennej servoPosition");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return;
        }

        if(ordinalNumber<0){
            try {
                throw new Exception("ordinalNumber mniejszy od 0");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return;
        }
        int tempInt=(frame.get(6)<<24) | (frame.get(5)<<16)|(frame.get(4)<<8)|(frame.get(3));
        this.ultrasonicSensorMeasures[ordinalNumber]= (float) ((float)tempInt*0.034/2);
        this.laserSensorMeasures[ordinalNumber]=(frame.get(8)<<8)|(frame.get(7));
        System.out.println("Zmierzone wartości to ultrasonicSensor="+this.ultrasonicSensorMeasures[ordinalNumber]+" laserSensor="+laserSensorMeasures[ordinalNumber]);
        this.laserSensorMeasures[ordinalNumber]=this.laserSensorMeasures[ordinalNumber]/10;
    }
}
