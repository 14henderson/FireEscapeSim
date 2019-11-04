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
    private int initialEmployeeCount;
    private static final long serialVersionUID = 12345;
    public static transient Pane windowContainer;

    public Building(){
        System.out.println("In constructor ");
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
             this.initialEmployeeCount = 0;
        }
    }

    @Override
    public void render(){this.floors.get(this.currentFloor).render();}

    @Override
    public void rerender(){
        this.floors.get(this.currentFloor).rerender();
    }

    public void calculateInitialEmployeeCount(){
        this.initialEmployeeCount = 0;
        for(Floor floor : this.floors){
            for(Employee employee : floor.employees){
                if(!employee.hasExited()){
                    this.initialEmployeeCount++;
                }
            }
        }
    }

            /*
    public final ArrayList<Floor> getFloors(){return floors;}
    public final Pane getCurrentFloor(){ return  floors.get( currentFloor).getFloor(); }
    public final Pane getFloorPane(int index){return floors.get(index).getFloor(); }
    public final Floor getFloor(int index) {return floors.get(index);}
    public final Tile[][] getCurrentFloorBlock(){return  floors.get( currentFloor).getCurrentFloorBlock();}
    public final int getFloorNum() {return currentFloor;}
    
    public boolean hasNextFloor(){return ( currentFloor + 1) <  floors.size(); }
    public boolean hasPrevFloor(){return ( currentFloor - 1) > -1; }
    
    public Pane nextFloor() {
        if(hasNextFloor()){
             currentFloor += 1;
        }
        return  floors.get( currentFloor).getFloor();

             */
    @Override
    public String toString(){
        String output = "";
        output += ("Num Floors:"+this.getTotalFloors()+"\n");
        return output;
    }

    public int getHeight(){return this.height;}
    public int getWidth(){return this.width;}
    public int getSize(){return this.size;}
    public final ArrayList<Floor> getFloors(){return this.floors;}
    public final Floor getCurrentFloor(){ return  floors.get( currentFloor); }
    public final int getCurrentFloorIndex() {return this.currentFloor;}
    public int getTotalFloors(){return this.floors.size();}
    public int getInitialEmployeeCount(){return this.initialEmployeeCount; }
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

    /*
    public void renderBlocks(){
        int index = 0;
        for(Floor floor : floors){ floor.renderBlocks(index); index++;}

    }*/

    public final void addFloor() {
        Floor newFloor = new Floor(this.height, this.width, this.size);
        this.floors.add(newFloor);
    }




}

