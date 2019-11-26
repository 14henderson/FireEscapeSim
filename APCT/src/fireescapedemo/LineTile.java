package fireescapedemo;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class LineTile{
    private double[] actualCords = new double[2];
    private int[] gridCords = new int[2];
    private int size =  2;
    private Building mainBuilding;
    private Circle lineTileCircle;

    public LineTile(int XSetter, int YSetter, int xGrid, int yGrid, Building mainBuilding){
        this.actualCords[0] = XSetter;
        this.actualCords[1] = YSetter;
        this.gridCords[0] = xGrid;
        this.gridCords[1] = yGrid;
        this.mainBuilding = mainBuilding;
    //    System.out.println(this.getX()+", "+this.getY()+", "+mainBuilding.getSize());
    }
    public void bringToFront(){
        this.lineTileCircle.toFront();
    }
    public void render(){
        try{
            this.mainBuilding.windowContainer.getChildren().remove(this.lineTileCircle);
        } catch(Exception e){}
        this.lineTileCircle = new Circle(this.size);
        this.refreshLocations();

        this.lineTileCircle.setLayoutX(this.getX());
        this.lineTileCircle.setLayoutY(this.getY());

        this.lineTileCircle.setOpacity(1);
        this.lineTileCircle.setStroke(Color.BLACK);
        this.lineTileCircle.setStrokeWidth(4);
        this.mainBuilding.windowContainer.getChildren().add(this.lineTileCircle);
        this.lineTileCircle.toFront();
    }

    public double getX(){
        return this.actualCords[0];
    }
    public double getY(){
        return this.actualCords[1];
    }
    public void setX(double newX){
        this.actualCords[0] = newX;
    }
    public void setY(double newY){
        this.actualCords[1] = newY;
    }
    public int getSize(){
        return this.size;
    }

    public Circle getLineTileRect(){return this.lineTileCircle;}

    public void refreshLocations(){
        this.setX((this.gridCords[0]*this.mainBuilding.getSize())+this.mainBuilding.getXPanOffset());
        this.setY((this.gridCords[1]*this.mainBuilding.getSize())+this.mainBuilding.getYPanOffset());
    }

    public void zoom(){
        this.refreshLocations();
        this.lineTileCircle.setLayoutX(this.getX());
        this.lineTileCircle.setLayoutY(this.getY());
        this.lineTileCircle.toFront();
    }
    public void pan(int xinc, int yinc){
        this.actualCords[0] += xinc;
        this.actualCords[1] += yinc;
        this.lineTileCircle.setLayoutX((this.gridCords[0]*this.mainBuilding.getSize())+this.mainBuilding.getXPanOffset());
        this.lineTileCircle.setLayoutY((this.gridCords[1]*this.mainBuilding.getSize())+this.mainBuilding.getYPanOffset());
        this.lineTileCircle.toFront();
    }
}
