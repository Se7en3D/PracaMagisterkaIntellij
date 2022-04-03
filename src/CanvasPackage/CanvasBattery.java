package CanvasPackage;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class CanvasBattery {


    public void drawBattery(GraphicsContext context,double batteryPercent,Double height,Double width){
        String text = "NaN%";
        Double percent=batteryPercent;
        if(percent>100.0){
            percent=100.0;
        }
        if(batteryPercent>0) {
            text=String.format("%.1f",batteryPercent)+"%";
        }
        double widthBattery=width/2-20;
        context.strokeRect(width/2, 0,widthBattery , height);
        context.fillRect((width-20),7.5,10,height/2);
        context.strokeText(text,width/2-45,height/2+5);
        //Wypełnienie baterii
        Paint strokeFill=context.getFill();
        Double lineWidth=context.getLineWidth();
        if(!text.contains("NaN")){
            if(percent<15){
                context.setFill(Color.RED);
            }else if(percent<30){
                context.setFill(Color.ORANGERED);
            }else{
                context.setFill(Color.GREEN);
            }
            context.fillRect(width/2, 0,widthBattery*percent/100 , height);
        }

        context.setFill(strokeFill);
        context.setLineWidth(lineWidth);
    }

}
