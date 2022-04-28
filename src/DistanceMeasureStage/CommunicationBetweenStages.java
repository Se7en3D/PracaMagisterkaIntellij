package DistanceMeasureStage;

public class CommunicationBetweenStages {
    public Boolean sendMeasureDistanceFromDistanceMeasureStage;

    public CommunicationBetweenStages(){
        this.sendMeasureDistanceFromDistanceMeasureStage=false;
    }

    public Boolean getSendMeasureDistanceFromDistanceMeasureStage() {
        return sendMeasureDistanceFromDistanceMeasureStage;
    }

    public void setSendMeasureDistanceFromDistanceMeasureStage(Boolean sendMeasureDistanceFromDistanceMeasureStage) {
        this.sendMeasureDistanceFromDistanceMeasureStage = sendMeasureDistanceFromDistanceMeasureStage;
    }
}
