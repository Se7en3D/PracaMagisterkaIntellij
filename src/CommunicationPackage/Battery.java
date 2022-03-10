package CommunicationPackage;

import java.util.ArrayList;

public class Battery {
    private float adcVoltage;
    private float batteryVoltage;
    private float percentVoltage;
    private final static double maxBatteryVoltage=8.5;
    private final static double minBatteryVoltage=6.0;
    private static int maxValueADCMeasurment=65536;

    public Battery(){
        this.adcVoltage=0;
        this.batteryVoltage=0;
        this.percentVoltage=0;
    }

    synchronized public void calcVoltage(ArrayList<Integer> frame){
        int value=0;
        value|=frame.get(2)<<24;
        value|=frame.get(3)<<16;
        value|=frame.get(4)<<8;
        value|=frame.get(5);

        adcVoltage= (float) (value*3.3/maxValueADCMeasurment);
        batteryVoltage=(float)(adcVoltage*adcVoltage*adcVoltage*(-0.1858)+adcVoltage*adcVoltage*(0.6476)+adcVoltage*(-1.2192)+8.6897); //(float) (-1.75*measurmentVoltege+11.28);

        if(batteryVoltage<minBatteryVoltage || batteryVoltage>maxBatteryVoltage){ //zmierzone napięcie musi sie mieścić w zakresie pomiarowym
            return;
        }

        this.percentVoltage= (float)(100*(batteryVoltage-minBatteryVoltage)/(maxBatteryVoltage-minBatteryVoltage));

        if(this.percentVoltage<0){
            this.percentVoltage=0;
        }
        System.out.println("Pomiar adc="+adcVoltage+" napięcie na baterii="+batteryVoltage+" procent zasilania="+percentVoltage);
    }

    synchronized public float getPercentVoltage() {
        return percentVoltage;
    }
}
