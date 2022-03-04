package old.carRc;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;

public class DrivingMeasurmentSamples {
    private final static int SERVO_MAX_POSITION=13;
    private final static int IR_MAX_SENSOR=8;
    public final static int MAX_ARRAY_OF_UPDATED_ELEMENTS=20;
    public final static double[] positionX={
            470,
            440,
            410,
            370,
            340,
            310, //Połowa

            280,

            250,
            220,
            190,
            160,
            130,
            100
    };
    public final static double[] positionY={
            260,
            220,
            180,
            140,
            100,
            60, //koniec

            20,//połowa

            60,
            100,
            140,
            180,
            220,
            260,
    };
    public final static int IR_NR1_NUMBER=0;
    public final static int IR_NR2_NUMBER=1;
    public final static int IR_NR3_NUMBER=2;
    public final static int IR_NR4_NUMBER=3;
    public final static int IR_NR5_NUMBER=4;
    public final static int IR_NR6_NUMBER=5;
    public final static int IR_NR7_NUMBER=6;
    public final static int IR_NR8_NUMBER=7;
    public final static int VOLTAGE_MAX_SAMPLE=5;
    private int[] anArrayofUpdatedElements=new int[MAX_ARRAY_OF_UPDATED_ELEMENTS];
    private int anArrayofUpdatedElementsPosition=0;
    private double[] ultrasonicSensor=new double[SERVO_MAX_POSITION];
    private float[] laserSensor=new float[SERVO_MAX_POSITION];
    private boolean[] IrSensor=new boolean[IR_MAX_SENSOR];
    private static int maxValueADCMeasurment=65536;
    private static float maxVoltageBattery= 8.5F;
    private static float minVoltageBattery=6.0F;
    private int voltage=0;
    private double voltageSample[]=new double[VOLTAGE_MAX_SAMPLE];
    private static final double incorrectValueVoltage=3.2;
    private int voltageSampleNumber=0;

    public DrivingMeasurmentSamples() {
    }
    public void addMeasurment(ArrayList<Integer> frame){
        int ordinalNumber=frame.get(2);
        System.out.print("Funkcja addMeasurment ordinalNumber="+ordinalNumber+"   ");
        if(ordinalNumber>=SERVO_MAX_POSITION){
            try {
                throw new Exception("ordinalNumber powyżej zmiennej SERVO_MAX_POSITION");
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
        this.ultrasonicSensor[ordinalNumber]=(float)tempInt*0.034/2;
        this.laserSensor[ordinalNumber]=(frame.get(8)<<8)|(frame.get(7));
        System.out.println("Zmierzone wartości to ultrasonicSensor="+this.ultrasonicSensor[ordinalNumber]+" laserSensor="+laserSensor[ordinalNumber]);

        if(this.laserSensor[ordinalNumber]>8180){
            this.laserSensor[ordinalNumber]=0;
        }else {
            this.laserSensor[ordinalNumber]=this.laserSensor[ordinalNumber]/10;
            //this.laserSensor[ordinalNumber] = (float) (((float) 0.00011899 * this.laserSensor[ordinalNumber] * this.laserSensor[ordinalNumber] + (float) 0.90281 * this.laserSensor[ordinalNumber] - (float) 64.5932) / 10.0);
        }
            /* Dodanie kolejnego elementu do tablicy która jest wyswietlana w wizualizacji odległości */
        anArrayofUpdatedElements[anArrayofUpdatedElementsPosition++]=ordinalNumber+1;
        if(anArrayofUpdatedElementsPosition>=MAX_ARRAY_OF_UPDATED_ELEMENTS){
            anArrayofUpdatedElementsPosition=0;
        }
    }

    public void addIrSensor(ArrayList<Integer> frame){
        int data=frame.get(2);
        //System.out.println("odebrane dane= "+String.format("%X",data));
        if(data>=0xFF){
            data=frame.get(4);
        }
        for(int i=0;i<IR_MAX_SENSOR;i++){
            //System.out.println("i="+i+" wynik="+(data&(1<<i)));
            if((data&(1<<i))>0){
                IrSensor[i]=true;
                //System.out.println(" sensor nr."+(i+1)+" wykrył kolizje");
            }else{
                IrSensor[i]=false;
            }
        }
    }
    public void addBatteryVoltage(ArrayList<Integer> frame){
        int value=0;
        value|=frame.get(2)<<24;
        value|=frame.get(3)<<16;
        value|=frame.get(4)<<8;
        value|=frame.get(5);
        //System.out.println(value);
        float measurmentVoltege= (float) (value*3.3/maxValueADCMeasurment);
        float batteryVoltage=(float)(measurmentVoltege*measurmentVoltege*measurmentVoltege*(-0.1155)+measurmentVoltege*measurmentVoltege*(0.4562)+measurmentVoltege*(-1.0853)+8.6136); //(float) (-1.75*measurmentVoltege+11.28);
        //System.out.println("measurmentVoltege="+measurmentVoltege+" batteryVoltage="+batteryVoltage);
        if(batteryVoltage<minVoltageBattery || batteryVoltage>maxVoltageBattery){ //zmierzone napięcie musi sie mieścić w zakresie pomiarowym
            return;
        }
        voltageSample[voltageSampleNumber]=batteryVoltage;
        voltageSampleNumber++;
        if(voltageSampleNumber>=VOLTAGE_MAX_SAMPLE){
            voltageSampleNumber=0;
        }
        double voltageMean=0.0;
        for(double x:voltageSample){
            voltageMean+=x;
            if(x==0.0){
                return;
            }
        }
        voltageMean/=VOLTAGE_MAX_SAMPLE;
        this.voltage= (int) (100*(voltageMean-minVoltageBattery)/(maxVoltageBattery-minVoltageBattery));

        if(this.voltage<0){
            this.voltage=0;
        }
    };
    public boolean getIrSensor(int number){
        return IrSensor[number];
    }
    public double getUltrasonicSensorMeasurement(int ordinalNumber){
        if(ordinalNumber>=SERVO_MAX_POSITION){
            return 0;
        }
        return ultrasonicSensor[ordinalNumber];
    }

    public float getLaserSensorMeasurement(int ordinalNumber){
        if(ordinalNumber>=SERVO_MAX_POSITION){
            return 0;
        }
        return laserSensor[ordinalNumber];
    }

    public int getAnArrayofUpdatedElements(int anArrayofUpdatedElementsPosition){
        return  anArrayofUpdatedElements[anArrayofUpdatedElementsPosition];
    }
    public void timeoutNextMeasurment(){
        anArrayofUpdatedElements[anArrayofUpdatedElementsPosition++]=0;
        if(anArrayofUpdatedElementsPosition>=MAX_ARRAY_OF_UPDATED_ELEMENTS){
            anArrayofUpdatedElementsPosition=0;
        }
    }

    public double getVoltage() {
        return voltage;
    }
}
