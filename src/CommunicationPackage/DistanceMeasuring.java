package CommunicationPackage;

import java.util.ArrayList;

public class DistanceMeasuring {
    private static final int servoPosition=13;
    private MeasuringSample[] sample=new MeasuringSample[13];

    public DistanceMeasuring(){
        for(int i=0;i<servoPosition;i++){
            sample[i]=new MeasuringSample(i);
        }
    }

    public void addMeasure(ArrayList<Integer> frame){
        if(frame.size()<=8){
            System.out.println("DistanceMeasuring addMeasure frame size mniejsze bądz równe 8");
            return;
        }
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
        float ultrasonicSensorMeasures= (float) ((float)tempInt*0.034/2);
        float laserSensorMeasures=(frame.get(8)<<8)|(frame.get(7))/10;
        sample[ordinalNumber].setSample(ordinalNumber,ultrasonicSensorMeasures,laserSensorMeasures);
        System.out.println("Zmierzone wartości to ultrasonicSensor="+ultrasonicSensorMeasures+" laserSensor="+laserSensorMeasures);
        //this.laserSensorMeasures[ordinalNumber]=this.laserSensorMeasures[ordinalNumber]/10;
    }
    public MeasuringSample[] getMeasure(){
        ArrayList<MeasuringSample> samples=new ArrayList<>();
        for(int i=0;i<servoPosition;i++){
            if(sample[i].isRefreshed()){
                samples.add(sample[i]);
            }

        }
        return samples.toArray(new MeasuringSample[0]);
    }
    public void time(){
        for(MeasuringSample i: sample){
            i.addTime();
        }
    }
}
