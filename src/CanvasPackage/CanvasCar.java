package CanvasPackage;

import CommunicationPackage.DistanceMeasuring;
import CommunicationPackage.IrSensor;
import CommunicationPackage.MeasuringSample;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;
import old.carRc.DrivingMeasurmentSamples;

import java.util.ArrayList;

public class CanvasCar {
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
    private int offsetYcarPosition;
    public CanvasCar(){
        this.offsetYcarPosition=0;
    }

    public void nextOffset(){
        offsetYcarPosition+=5;
        if (offsetYcarPosition>50){
            offsetYcarPosition=0;
        }
    }

    public void drawCar(Canvas canvas, double x, double y, double w, double h, boolean[] sensorStatus, MeasuringSample[] distanceMeasuring){
        double centerx=x;
        double centery=y-this.offsetYcarPosition;
        double heightElement=h/5; // 1/8
        double widthOval=w/4;
        double topArcx=centerx-w/2;
        double topArcy=centery-heightElement*5/2-h/4;
        double firstLinex=topArcx;
        double firstLiney=topArcy+h/4;
        double seconfLinex=x+w/2;
        double addWidthOval=10;
        canvasClear(canvas);
        GraphicsContext context = canvas.getGraphicsContext2D();
        Paint strokePreview=context.getStroke();
        double lineWidthPreview=context.getLineWidth();
        context.setLineWidth(4.0);
        if(sensorStatus[IrSensor.IR_NR1_NUMBER]){
            context.setStroke(Color.RED);
        }
        context.strokeArc(topArcx, topArcy, w, h/2, 0, 60, ArcType.OPEN);
        context.setStroke(strokePreview);


        if(sensorStatus[IrSensor.IR_NR2_NUMBER]){
            context.setStroke(Color.RED);
        }
        context.strokeArc(topArcx, topArcy, w, h/2, 60, 60, ArcType.OPEN);
        context.setStroke(strokePreview);

        if(sensorStatus[IrSensor.IR_NR3_NUMBER]){
            context.setStroke(Color.RED);
        }
        context.strokeArc(topArcx, topArcy, w, h/2, 120, 60, ArcType.OPEN);
        context.setStroke(strokePreview);

        if(sensorStatus[IrSensor.IR_NR4_NUMBER]){
            context.setStroke(Color.RED);
        }
        context.strokeLine(firstLinex,firstLiney,topArcx,firstLiney+heightElement-addWidthOval/2);
        context.strokeLine(firstLinex,firstLiney+heightElement*2+addWidthOval/2,firstLinex,firstLiney+heightElement*3-addWidthOval/2);
        context.strokeLine(firstLinex,firstLiney+heightElement*4+addWidthOval/2,firstLinex,firstLiney+heightElement*5);
        context.strokeLine(firstLinex,firstLiney+heightElement*4+addWidthOval/2,firstLinex,firstLiney+heightElement*5);
        context.setStroke(strokePreview);

        //rysowanie koła lewego dolnego
        context.fillRoundRect(firstLinex-w/4/2, (firstLiney+heightElement*3-addWidthOval/2)+5, w/4, h/5, w/10, h/10);
        //rysowanie koła lewego górnego
        context.fillRoundRect(firstLinex-w/4/2, (firstLiney+heightElement-addWidthOval/2)+5, w/4, h/5, w/10, h/10);


        if(sensorStatus[IrSensor.IR_NR8_NUMBER]){
            context.setStroke(Color.RED);
        }
        context.strokeArc(firstLinex, firstLiney+heightElement*5-h/4, w, h/2, 0, -60, ArcType.OPEN);
        context.setStroke(strokePreview);

        if(sensorStatus[IrSensor.IR_NR7_NUMBER]){
            context.setStroke(Color.RED);
        }
        context.strokeArc(firstLinex, firstLiney+heightElement*5-h/4, w, h/2, -60, -60, ArcType.OPEN);
        context.setStroke(strokePreview);

        if(sensorStatus[IrSensor.IR_NR6_NUMBER]){
            context.setStroke(Color.RED);
        }
        context.strokeArc(firstLinex, firstLiney+heightElement*5-h/4, w, h/2, -120, -60, ArcType.OPEN);
        context.setStroke(strokePreview);


        if(sensorStatus[IrSensor.IR_NR5_NUMBER]){
            context.setStroke(Color.RED);
        }
        context.strokeLine(seconfLinex,firstLiney+heightElement*4+addWidthOval/2,seconfLinex,firstLiney+heightElement*5);
        context.strokeLine(seconfLinex,firstLiney+heightElement*2+addWidthOval/2,seconfLinex,firstLiney+heightElement*3-addWidthOval/2);
        context.strokeLine(seconfLinex,firstLiney,seconfLinex,firstLiney+heightElement-addWidthOval/2);
        context.setStroke(strokePreview);


        //rysowanie koła prawego dolnego
        context.fillRoundRect(seconfLinex-w/4/2, (firstLiney+heightElement-addWidthOval/2)+5, w/4, h/5, w/10, h/10);

        //rysowanie koła prawego górnego
        context.fillRoundRect(seconfLinex-w/4/2, firstLiney+heightElement*3-addWidthOval/2+5, w/4, h/5, w/10, h/10);

        context.setLineWidth(lineWidthPreview);

        drawCarMeasurments(canvas,distanceMeasuring);
    }

    public void drawCarMeasurments(Canvas canvas, MeasuringSample[] distanceMeasuring){
        GraphicsContext context = canvas.getGraphicsContext2D();
        Paint strokePreview=context.getStroke();
        boolean strokeReset=false;
        int distanceMeasuringLength=distanceMeasuring.length;
        for(int i=0;i<distanceMeasuringLength;i++){
            int position=distanceMeasuring[i].getPosition();
            if(position>positionX.length){
                System.out.println("CanvasCar drawCarMeasurments() position="+position+" a maksymalna wielkość to "+positionX.length);
                continue;
            }
            float distance=distanceMeasuring[i].getDistance();
            if(distance<10.0 && distance>0.0){
                context.setStroke(Color.RED);
                strokeReset=true;
            }
            String text=String.format("%03.2f",distance);
            context.strokeText(text,positionX[position],positionY[position]);

            context.setStroke(strokePreview);

        }
        context.setStroke(strokePreview);
    }


    private void canvasClear(Canvas canvas){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}
