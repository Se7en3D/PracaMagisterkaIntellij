package CanvasPackage;

import CommunicationPackage.IrSensor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;
import old.carRc.DrivingMeasurmentSamples;

public class CanvasCar {

    public void drawMeasurments(Canvas canvas){

    }
    public void drawCar(Canvas canvas, double x, double y, double w, double h, boolean[] sensorStatus){
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
    }

    private void canvasClear(Canvas canvas){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        if(gc==null){
            return;
        }
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}
