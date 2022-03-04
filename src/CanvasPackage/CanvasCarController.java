package CanvasPackage;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CanvasCarController {
    public void drawControllerWithPosition(Canvas canvas,int x,int y){
        drawController(canvas);
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.setFill(Color.RED);
        context.strokeOval(x, y, 20, 20);
        context.setFill(Color.BLACK);

    }
    public void drawController(Canvas canvas){
        canvasClear(canvas);
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        double centerWidth= canvas.getWidth();
        double centerHeight= canvas.getHeight();
        //System.out.println("centerWidth="+centerWidth+" centerHeight="+centerHeight);
        context.setFill(Color.BLACK);
        context.strokeOval(2, 2, centerWidth-4, centerHeight-4);
        //Strzałka do przodu
        double x=centerWidth/2;
        double y=10;
        context.strokeLine(x,y,x-18,y+18);
        context.strokeLine(x-18,y+18,x-6,y+18);
        context.strokeLine(x-6,y+18,x-6,y+36);
        context.strokeLine(x-6,y+36,x+6,y+36);
        context.strokeLine(x+6,y+36,x+6,y+18);
        context.strokeLine(x+6,y+18,x+18,y+18);
        context.strokeLine(x+18,y+18,x,y);
        //Strzałka w lewo
        x=10;
        y=centerHeight/2;
        context.strokeLine(x,y,x+18,y-18);
        context.strokeLine(x+18,y-18,x+18,y-6);
        context.strokeLine(x+18,y-6,x+36,y-6);
        context.strokeLine(x+36,y-6,x+36,y+6);
        context.strokeLine(x+36,y+6,x+18,y+6);
        context.strokeLine(x+18,y+6,x+18,y+18);
        context.strokeLine(x+18,y+18,x,y);

        //Strzałka do tyłu
        x=centerWidth/2;
        y=centerHeight-10;
        context.strokeLine(x,y,x+18,y-18);
        context.strokeLine(x+18,y-18,x+6,y-18);
        context.strokeLine(x+6,y-18,x+6,y-36);
        context.strokeLine(x+6,y-36,x-6,y-36);
        context.strokeLine(x-6,y-36,x-6,y-18);
        context.strokeLine(x-6,y-18,x-18,y-18);
        context.strokeLine(x-18,y-18,x,y);

        //Strzałka w prawo
        x=centerWidth-10;
        y=centerHeight/2;
        context.strokeLine(x,y,x-18,y+18);
        context.strokeLine(x-18,y+18,x-18,y+6);
        context.strokeLine(x-18,y+6,x-36,y+6);
        context.strokeLine(x-36,y+6,x-36,y-6);
        context.strokeLine(x-36,y-6,x-18,y-6);
        context.strokeLine(x-18,y-6,x-18,y-18);
        context.strokeLine(x-18,y-18,x,y);
    }

    private void canvasClear(Canvas canvas){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}
