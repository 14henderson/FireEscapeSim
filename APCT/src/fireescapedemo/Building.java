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
    //private int height;
    //private int width;
    //private int tileSize;
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

    public Building(int floorHeight, int floorWidth, int tileSize, Pane windowContainerParent){
        this.mainBuilding = this;
        this.windowContainer = windowContainerParent;
        if ( floors == null){
             floors = new ArrayList();
             floors.add(new Floor(floorHeight,floorWidth,tileSize));
             currentFloor = 0;
             this.initialEmployeeCount = 0;
        }
    }

    @Override
    public void updateView(){this.floors.get(this.currentFloor).updateView();}

    @Override
    public void initialiseView(){
        this.mainBuilding = this;
        this.floors.get(this.currentFloor).initialiseView();
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

    public void zoom(int zoomValue){
        this.getCurrentFloor().zoom(zoomValue);
    }
    public void pan(int xinc, int yinc){
        this.getCurrentFloor().pan(xinc, yinc);
    }


    public int getHeight(){return this.getCurrentFloor().getMapHeight();}
    public int getWidth(){return this.getCurrentFloor().getMapWidth();}
    public int getSize(){return this.getCurrentFloor().getTileSize();}
    public void setSize(int newSize){this.getCurrentFloor().setTileSize(newSize);}
    public final ArrayList<Floor> getFloors(){return this.floors;}
    public final Floor getCurrentFloor(){ return  floors.get( currentFloor); }
    public final int getCurrentFloorIndex() {return this.currentFloor;}
    public int getTotalFloors(){return this.floors.size();}
    public int getInitialEmployeeCount(){return this.initialEmployeeCount; }
    public boolean hasNextFloor(){return ( this.currentFloor + 1) <  this.floors.size(); }
    public boolean hasPrevFloor(){return ( this.currentFloor - 1) > -1; }
    public void setWindowContainer(Pane paneRef){this.windowContainer = paneRef;}
    public void setCurrentFloor(int floor){this.currentFloor = floor;}
    public int getXPanOffset(){
        return this.getCurrentFloor().floorBlocks[0][0].getActualX();
    }
    public int getYPanOffset(){
        return this.getCurrentFloor().floorBlocks[0][0].getActualY();
    }

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
        Floor newFloor = new Floor(20, 20, 50);
        this.floors.add(newFloor);
    }




}

