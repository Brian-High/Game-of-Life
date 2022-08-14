package gameoflife;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;


public class Tile {
    boolean isAlive;
    boolean needsCheck;
    double height;
    double width;
    double x;
    double y;
    Paint fill;

    public Tile(double width, double height, Paint fill, double x, double y){
        this.isAlive = false;
        this.height = height;
        this.width = width;
        this.fill = fill;
        this.x = x;
        this.y = y;
        needsCheck = false;
    }
    public void setAlive(Main main){
        main.gc.setFill(Color.BLACK);
        main.gc.fillRect(x,y,width,height);
        isAlive = true;
    }
    public void setDead(Main main){
        main.gc.setFill(Color.WHITE);
        main.gc.fillRect(x,y,width,height);
        isAlive = false;

    }
    public void flipAlive(Main main){
        isAlive = !isAlive;
        if(isAlive){
            setAlive(main);
        } else setDead(main);
    }
    public boolean getAlive(){
        return isAlive;
    }

    public void checkClicked(double x, double y, Main main){
        double tTop = this.y;
        double tBot = this.y + height;
        double tLeft = this.x;
        double tRight = this.x+ width;
        boolean xOverlap = false;
        boolean yOverlap = false;

        if(x > tLeft && x < tRight) xOverlap = true;
        if(y > tTop && y < tBot) yOverlap = true;

        if(xOverlap && yOverlap){
            flipAlive(main);
        }
    }
    public void needsCheck(){
        needsCheck = true;
    }
}
