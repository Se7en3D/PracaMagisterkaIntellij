package CommunicationPackage;

public class MeasuringSample {
    private float ultrasonicSensorMeasures;
    private float laserSensorMeasures;
    private int position;
    private int timeToResetRefresed;
    private Boolean refreshed;
    private static final int timeToReset=10;

    public MeasuringSample(int position){
        this.ultrasonicSensorMeasures=0;
        this.laserSensorMeasures=0;
        this.timeToResetRefresed=0;
        this.position=position;
        this.refreshed=false;
    }

    public void setSample(int position,float ultrasonicSensorMeasures,float laserSensorMeasures){
        this.ultrasonicSensorMeasures=ultrasonicSensorMeasures;
        this.laserSensorMeasures=laserSensorMeasures;
        this.position=position;
        this.refreshed=true;
        this.timeToResetRefresed=0;
    }
    public float getDistance(){
        if(ultrasonicSensorMeasures<50 && laserSensorMeasures<7961){
            return laserSensorMeasures;
        }else{
            return ultrasonicSensorMeasures;
        }

    }

    public Boolean isRefreshed(){
        return  this.refreshed;
    }
    public int getPosition(){
        return this.position;
    }
    public void addTime(){
        if(this.refreshed) {
            this.timeToResetRefresed++;
            if (this.timeToResetRefresed >= timeToReset) {
                this.refreshed=false;
            }
        }
    }
}
