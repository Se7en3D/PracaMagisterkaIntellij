package CommunicationPackage;

import java.util.ArrayList;

public class IrSensor {
    public static final int MAX_SENSOR=8;
    public final static int IR_NR1_NUMBER=0;
    public final static int IR_NR2_NUMBER=1;
    public final static int IR_NR3_NUMBER=2;
    public final static int IR_NR4_NUMBER=3;
    public final static int IR_NR5_NUMBER=4;
    public final static int IR_NR6_NUMBER=5;
    public final static int IR_NR7_NUMBER=6;
    public final static int IR_NR8_NUMBER=7;
    private boolean []sensorStatus=new boolean[MAX_SENSOR];

    public IrSensor(){
        sensorStatus[0]=false;
        sensorStatus[1]=false;
        sensorStatus[2]=false;
        sensorStatus[3]=false;
        sensorStatus[4]=false;
        sensorStatus[5]=false;
        sensorStatus[6]=false;
        sensorStatus[7]=false;
    }
    public void updateSensorStatus(ArrayList<Integer> frame){
        int data=frame.get(2);
        //System.out.println("odebrane dane= "+String.format("%X",data));
        if(data>=0xFF){
            data=frame.get(4);
        }
        for(int i=0;i<MAX_SENSOR;i++){
            //System.out.println("i="+i+" wynik="+(data&(1<<i)));
            if((data&(1<<i))>0){
                sensorStatus[i]=true;
                //System.out.println(" sensor nr."+(i+1)+" wykrył kolizje");
            }else{
                sensorStatus[i]=false;
            }
        }
    }
    public boolean[] getSensorStatus(){
        return this.sensorStatus;
    }
}
