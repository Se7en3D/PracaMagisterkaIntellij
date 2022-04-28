package CommunicationPackage;

import java.util.ArrayList;

public class DistanceMeasuringWithoutPosition {
    private int referenceMeasurment;
    private double ultrasonicSensor;
    private float laserSensor;

    public DistanceMeasuringWithoutPosition (ArrayList<Integer> frame){
        this.referenceMeasurment = 0;
        int tempInt=(frame.get(5)<<24) | (frame.get(4)<<16)|(frame.get(3)<<8)|(frame.get(2));
        this.ultrasonicSensor=Float.intBitsToFloat(tempInt);
        this.laserSensor=(frame.get(7)<<8)|(frame.get(6));
        this.laserSensor =(float ) 0.00011899*this.laserSensor*this.laserSensor+(float) 0.90281*this.laserSensor-(float)64.5932;
    }

    public int getReferenceMeasurment() {
        return referenceMeasurment;
    }

    public double getUltrasonicSensor() {
        return ultrasonicSensor;
    }

    public float getLaserSensor() {
        return laserSensor;
    }
}
