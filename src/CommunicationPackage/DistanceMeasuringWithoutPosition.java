package CommunicationPackage;

import java.util.ArrayList;

public class DistanceMeasuringWithoutPosition {
    private int referenceMeasurment;
    private double ultrasonicSensor;
    private double laserSensor;

    public DistanceMeasuringWithoutPosition (int referenceMeasurment,ArrayList<Integer> frame){
        this.referenceMeasurment = referenceMeasurment;
        int tempInt=(frame.get(5)<<24) | (frame.get(4)<<16)|(frame.get(3)<<8)|(frame.get(2));
        this.ultrasonicSensor=(0.0343*tempInt)/2;
        this.ultrasonicSensor=this.ultrasonicSensor*1.0744-6.3270-this.ultrasonicSensor*this.ultrasonicSensor*0.00033439;
        this.laserSensor=((double )((frame.get(7)<<8)|(frame.get(6))))/10;
        /*System.out.println(this.laserSensor);
        System.out.println((this.laserSensor*this.laserSensor*this.laserSensor*0.000041402));
        System.out.println((this.laserSensor*this.laserSensor*0.0045));
        System.out.println((this.laserSensor*1.1052));*/
        this.laserSensor=(this.laserSensor*1.1052)-5.3326-(this.laserSensor*this.laserSensor*0.0045)+(this.laserSensor*this.laserSensor*this.laserSensor*0.000041402);
        //this.laserSensor =(float ) 0.00011899*this.laserSensor*this.laserSensor+(float) 0.90281*this.laserSensor-(float)64.5932;
        this.laserSensor=Math.round(this.laserSensor * 100.0) / 100.0;
        this.ultrasonicSensor=Math.round(this.ultrasonicSensor * 100.0) / 100.0;
    }

    public int getReferenceMeasurment() {
        return referenceMeasurment;
    }

    public double getUltrasonicSensor() {
        return ultrasonicSensor;
    }

    public double getLaserSensor() {
        return laserSensor;
    }

    @Override
    public String toString() {
        return referenceMeasurment +" ; " + ultrasonicSensor + " ; " + laserSensor + '\n';
    }
}
