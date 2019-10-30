package fireescapedemo;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;




public class Building extends MapObject implements Serializable {
    private ArrayList<Floor> floors;
    private int currentFloor;
    private int height;
    private int width;
    private int size;
    private static final long serialVersionUID = 12345;
    public static transient Pane windowContainer;

    public Building(){
        this.mainBuilding = this;
        if(floors == null){
            throw new RuntimeException();
        }
    }

    public Building(int floorHeight, int floorWidth, int floorSize, Pane windowContainerParent){
        this.mainBuilding = this;
        this.windowContainer = windowContainerParent;
        if ( floors == null){
             floors = new ArrayList();
             height = floorHeight;
             width = floorWidth;
             size = floorSize;
             floors.add(new Floor(floorHeight,floorWidth,floorSize));
             currentFloor = 0;
        }
    }

    @Override
    public void render(){
        this.floors.get(this.currentFloor).render();
    }

    @Override
    public String toString(){
        String output = "";
        output += ("Num Floors:"+this.getTotalFloors()+"\n");
        return output;
    }

    public void zoom(int zoomValue){
        this.size += zoomValue;
        this.getCurrentFloor().zoom(zoomValue);
    }


    public int getHeight(){return this.height;}
    public int getWidth(){return this.width;}
    public int getSize(){return this.size;}
    public void setSize(int newSize){this.size = newSize;}
    public final ArrayList<Floor> getFloors(){return this.floors;}
    public final Floor getCurrentFloor(){ return  floors.get( currentFloor); }
    public final int getCurrentFloorIndex() {return this.currentFloor;}
    public int getTotalFloors(){return this.floors.size();}
    public boolean hasNextFloor(){return ( this.currentFloor + 1) <  this.floors.size(); }
    public boolean hasPrevFloor(){return ( this.currentFloor - 1) > -1; }
    public void setWindowContainer(Pane paneRef){this.windowContainer = paneRef;}
    public void setCurrentFloor(int floor){this.currentFloor = floor;}

    public Floor increaseFloor() {
        if(hasNextFloor()){currentFloor += 1;}
        return  floors.get( currentFloor);
    }
    public Floor decreaseFloor() {
        if(hasPrevFloor()){currentFloor -= 1;}
        return  floors.get( currentFloor);
    }

    public final void addFloor() {
        Floor newFloor = new Floor(this.height, this.width, this.size);
        this.floors.add(newFloor);
    }




}

