package measurmentSensor;

import java.util.ArrayList;

import static java.lang.Math.log10;
import static java.lang.Math.pow;

public class measurmentTableViewMeasurClass {
    private int referenceMeasurment;
    private double ultrasonicSensor;
    private int laserSensor;

    public measurmentTableViewMeasurClass() {
    }

    public measurmentTableViewMeasurClass(int referenceMeasurment, int ultrasonicSensor, int laserSensor) {
        this.referenceMeasurment = referenceMeasurment;
        this.ultrasonicSensor=ultrasonicSensor*0.034/2*10;//(float)1.0138*ultrasonicSensor-(float)10.0853;
        //korekcja
        this.laserSensor =laserSensor;
    }

    public measurmentTableViewMeasurClass (int referenceMeasurment, ArrayList<Integer> frame){
        this.referenceMeasurment = referenceMeasurment;
        int tempInt=(frame.get(5)<<24) | (frame.get(4)<<16)|(frame.get(3)<<8)|(frame.get(2));
        this.ultrasonicSensor=Float.intBitsToFloat(tempInt);
        this.laserSensor=(frame.get(7)<<8)|(frame.get(6));
        //this.laserSensor =(float ) 0.00011899*this.laserSensor*this.laserSensor+(float) 0.90281*this.laserSensor-(float)64.5932;

    }

    public measurmentTableViewMeasurClass (ArrayList<Integer> frame){
        this.ultrasonicSensor=(frame.get(5)<<24) | (frame.get(4)<<16)|(frame.get(3)<<8)|(frame.get(2));
        this.laserSensor=(frame.get(7)<<8)|(frame.get(6));
    }

    @Override
    public String toString() {
        return  referenceMeasurment +" "+ultrasonicSensor+" " + laserSensor +"\n";
    }

    public int getReferenceMeasurment() {
        return referenceMeasurment;
    }

    public void setReferenceMeasurment(int referenceMeasurment) {
        this.referenceMeasurment = referenceMeasurment;
    }

    public double getUltrasonicSensor() {
        return ultrasonicSensor;
    }

    public void setUltrasonicSensor(float ultrasonicSensor) {
        this.ultrasonicSensor = ultrasonicSensor;
    }

    public double getLaserSensor() {
        return laserSensor;
    }

    public void setLaserSensor(int laserSensor) {
        this.laserSensor = laserSensor;
    }


}
