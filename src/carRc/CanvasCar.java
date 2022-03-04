package carRc;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;

public class CanvasCar {
    private Canvas CVController;
    private Canvas CVcarWithSensors;
    private Canvas CVBattery;
    private DrivingMeasurmentSamples drivingMeasurmentSample;
    public void drawCarWithSensors(){
        double height=CVcarWithSensors.getHeight();
        double width=CVcarWithSensors.getWidth();
        double centerx=width/2;
        double centery=height/2;
        GraphicsContext context = CVcarWithSensors.getGraphicsContext2D();
        context.clearRect(0, 0, CVcarWithSensors.getWidth(), CVcarWithSensors.getHeight());
        drawCar(centerx+50,centery,100,150);
        drawSensor(centerx,centery,width-20,height-20);
    }

    public CanvasCar(Canvas CVController, Canvas CVcarWithSensors, Canvas CVBattery,DrivingMeasurmentSamples drivingMeasurmentSample) {
        this.CVController = CVController;
        this.CVcarWithSensors = CVcarWithSensors;
        this.CVBattery = CVBattery;
        this.drivingMeasurmentSample=drivingMeasurmentSample;
    }

    public void drawCar(double x, double y, double w, double h){
        double centerx=x;
        double centery=y;
        double heightElement=h/5; // 1/8
        double widthOval=w/4;
        double topArcx=centerx-w/2;
        double topArcy=centery-heightElement*5/2-h/4;
        double firstLinex=topArcx;
        double firstLiney=topArcy+h/4;
        double seconfLinex=x+w/2;
        double addWidthOval=10;

        GraphicsContext context = CVcarWithSensors.getGraphicsContext2D();
        Paint strokePreview=context.getStroke();
        double lineWidthPreview=context.getLineWidth();
        context.setLineWidth(4.0);
        if(drivingMeasurmentSample.getIrSensor(DrivingMeasurmentSamples.IR_NR1_NUMBER)){
            context.setStroke(Color.RED);
        }
        context.strokeArc(topArcx, topArcy, w, h/2, 0, 60, ArcType.OPEN);
        context.setStroke(strokePreview);

        if(drivingMeasurmentSample.getIrSensor(DrivingMeasurmentSamples.IR_NR2_NUMBER)){
            context.setStroke(Color.RED);
        }
        context.strokeArc(topArcx, topArcy, w, h/2, 60, 60, ArcType.OPEN);
        context.setStroke(strokePreview);


        if(drivingMeasurmentSample.getIrSensor(DrivingMeasurmentSamples.IR_NR3_NUMBER)){
            context.setStroke(Color.RED);
        }
        context.strokeArc(topArcx, topArcy, w, h/2, 120, 60, ArcType.OPEN);
        context.setStroke(strokePreview);


        context.strokeLine(firstLinex,firstLiney,topArcx,firstLiney+heightElement-addWidthOval/2);
        /*for(int i=0;i<10;i++) { //rysowanie koła lewego górnego
            context.strokeOval(firstLinex - widthOval / 2+i, firstLiney + heightElement - addWidthOval / 2, widthOval, heightElement + addWidthOval);
        }*/
        context.strokeLine(firstLinex,firstLiney+heightElement*2+addWidthOval/2,firstLinex,firstLiney+heightElement*3-addWidthOval/2);
        /*for(int i=0;i<10;i++) { //rysowanie koła lewego dolnego
            context.strokeOval(firstLinex-widthOval/2+i,firstLiney+heightElement*3-addWidthOval/2,widthOval,heightElement+addWidthOval);
        }*/

        if(drivingMeasurmentSample.getIrSensor(DrivingMeasurmentSamples.IR_NR4_NUMBER)){
            context.setStroke(Color.RED);
        }
        context.strokeLine(firstLinex,firstLiney+heightElement*4+addWidthOval/2,firstLinex,firstLiney+heightElement*5);
        context.setStroke(strokePreview);


        //context.strokeArc(firstLinex, firstLiney+heightElement*5-h/4, w, h/2, 0, -180, ArcType.OPEN);
        if(drivingMeasurmentSample.getIrSensor(DrivingMeasurmentSamples.IR_NR5_NUMBER)){
            context.setStroke(Color.RED);
        }
        context.strokeArc(firstLinex, firstLiney+heightElement*5-h/4, w, h/2, 0, -60, ArcType.OPEN);
        context.setStroke(strokePreview);

        if(drivingMeasurmentSample.getIrSensor(DrivingMeasurmentSamples.IR_NR6_NUMBER)){
            context.setStroke(Color.RED);
        }
        context.strokeArc(firstLinex, firstLiney+heightElement*5-h/4, w, h/2, -60, -60, ArcType.OPEN);
        context.setStroke(strokePreview);
        if(drivingMeasurmentSample.getIrSensor(DrivingMeasurmentSamples.IR_NR7_NUMBER)){
            context.setStroke(Color.RED);
        }
        context.strokeArc(firstLinex, firstLiney+heightElement*5-h/4, w, h/2, -120, -60, ArcType.OPEN);
        context.setStroke(strokePreview);
        context.strokeLine(seconfLinex,firstLiney,seconfLinex,firstLiney+heightElement-addWidthOval/2);
        /*for(int i=0;i<10;i++) { //rysowanie koła prawego dolnego
            context.strokeOval(seconfLinex-widthOval/2-i,firstLiney+heightElement-addWidthOval/2,widthOval,heightElement+addWidthOval);
        }*/
        if(drivingMeasurmentSample.getIrSensor(DrivingMeasurmentSamples.IR_NR8_NUMBER)){
            context.setStroke(Color.RED);
        }
        context.strokeLine(seconfLinex,firstLiney+heightElement*2+addWidthOval/2,seconfLinex,firstLiney+heightElement*3-addWidthOval/2);
        context.setStroke(strokePreview);
        /*for(int i=0;i<10;i++) { //rysowanie koła prawego górnego
            context.strokeOval(seconfLinex-widthOval/2-i,firstLiney+heightElement*3-addWidthOval/2,widthOval,heightElement+addWidthOval);
        }*/
        context.strokeLine(seconfLinex,firstLiney+heightElement*4+addWidthOval/2,seconfLinex,firstLiney+heightElement*5);
        context.setLineWidth(lineWidthPreview);
    }

    public void drawSensor(double x,double y,double w,double h){
        GraphicsContext context = CVcarWithSensors.getGraphicsContext2D();
        //double  ovalX=(CVcarWithSensors.getWidth()-w)/2;
        //double ovalY=(CVcarWithSensors.getHeight()-h)/2;
        //context.strokeOval(ovalX,ovalY,w,h);
        //int WITHLETTER=30;
        //int HEIGHTLETTER=20;
        //int maxMeasurment=(int)(w/WITHLETTER);
        //double addRest=(w-maxMeasurment*WITHLETTER)/maxMeasurment;
        Paint strokePreview=context.getStroke();
        int[] arrayOrdinalNumberShowed=new int[DrivingMeasurmentSamples.MAX_ARRAY_OF_UPDATED_ELEMENTS];
        for(int i=0;i<DrivingMeasurmentSamples.MAX_ARRAY_OF_UPDATED_ELEMENTS;i++){
            int ordinalNumber=drivingMeasurmentSample.getAnArrayofUpdatedElements(i);
            if(ordinalNumber==0){ //Brak wartości do wyświetlenia
                continue;
            }
            int j;

            for(j=0;j<DrivingMeasurmentSamples.MAX_ARRAY_OF_UPDATED_ELEMENTS;j++){ //funkcja sprawdzająca czy pomiar o tym numerze został już wyświetlony
                if(arrayOrdinalNumberShowed[j]==ordinalNumber){
                    break;
                }
            }

            if(j!=DrivingMeasurmentSamples.MAX_ARRAY_OF_UPDATED_ELEMENTS){ //Sprawdzenie czy wcześniejsza funkcja została wykonana do konca
                continue;
            }
            boolean strokeReset=false;
            ordinalNumber--;
            float laser=drivingMeasurmentSample.getLaserSensorMeasurement(ordinalNumber);
            double ultra=drivingMeasurmentSample.getUltrasonicSensorMeasurement(ordinalNumber);
            if ((laser < 10.0 && laser > 0.0) || ( ultra<10.0 && ultra>0.0)){
                context.setStroke(Color.RED);
                strokeReset=true;
            }
            String text=String.format("%03.2f\n%03.2f",laser,ultra);
            context.strokeText(text,DrivingMeasurmentSamples.positionX[ordinalNumber],DrivingMeasurmentSamples.positionY[ordinalNumber]);

            if(strokeReset){
                context.setStroke(strokePreview);
            }
        }

        /*
        for(int i=0;i< DrivingMeasurmentSamples.positionX.length;i++){
            boolean strokeReset=false;
            float laser=drivingMeasurmentSample.getLaserSensorMeasurement(i);
            double ultra=drivingMeasurmentSample.getUltrasonicSensorMeasurement(i);
            if ((laser < 10.0 && laser > 0.0) || ( ultra<10.0 && ultra>0.0)){
                context.setStroke(Color.RED);
                strokeReset=true;
            }
            String text=String.format("%03.2f\n%03.2f",laser,ultra);
            context.strokeText(text,DrivingMeasurmentSamples.positionX[i],DrivingMeasurmentSamples.positionY[i]);

            if(strokeReset){
                context.setStroke(strokePreview);
            }
        }*/

    }
}
