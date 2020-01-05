package fireescapedemo;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;




public class Building extends MapObject implements Serializable {
    private ArrayList<Floor> floors;
    private static HashMap<Integer, Staircase> stairs;
    private int currentFloor;
    private int initialEmployeeCount;
    private boolean runningSim = false;
    //private static final long serialVersionUID = 12345;
    public static transient Pane windowContainer;


    public Building(){
        this.stairs = new HashMap<>();
        System.out.println("New Stairs being creates");
        this.mainBuilding = this;
        if(floors == null){
            throw new RuntimeException();
        }
    }

    public Building(int floorWidth, int floorHeight, int tileSize, Pane windowContainerParent){
        this.mainBuilding = this;
        this.windowContainer = windowContainerParent;
        this.stairs = new HashMap<>();
        System.out.println("New Stairs being creates");
        if ( floors == null){
             floors = new ArrayList();
             Floor tmpRef = new Floor(floorHeight,floorWidth,tileSize, 0);
             tmpRef.setFloorNum(0);
             floors.add(tmpRef);
             currentFloor = 0;
             this.initialEmployeeCount = 0;
        }
    }

    public Building(int floorWidth, int floorHeight, int tileSize){
        this.mainBuilding = this;
        //this.windowContainer = windowContainerParent;
        this.stairs = new HashMap<>();
        System.out.println("New Stairs being creates");
        if ( floors == null){
            floors = new ArrayList();
            Floor tmpRef = new Floor(floorHeight,floorWidth,tileSize, 0);
            tmpRef.setFloorNum(0);
            floors.add(tmpRef);
            currentFloor = 0;
            this.initialEmployeeCount = 0;
        }
    }

    @Override
    public void updateView(){
        if(this.mainBuilding == null){
            this.mainBuilding = this;
        }
        this.floors.get(this.currentFloor).updateView();
    }

    @Override
    public void initialiseView(){
        if(this.stairs == null){
            System.out.println("No stairs found");
            this.stairs = new HashMap<>();
        }
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
    public HashMap<Integer, Staircase> getStairs(){return this.stairs;}
    public final ArrayList<Floor> getFloors(){return this.floors;}
    public final Floor getCurrentFloor(){ return  floors.get( currentFloor); }
    public final int getCurrentFloorIndex() {return this.currentFloor;}
    public int getTotalFloors(){return this.floors.size();}
    public int getInitialEmployeeCount(){return this.initialEmployeeCount; }
    public boolean hasNextFloor(){return ( this.currentFloor + 1) <  this.floors.size(); }
    public boolean hasPrevFloor(){return ( this.currentFloor - 1) > -1; }
    public void setWindowContainer(Pane paneRef){this.windowContainer = paneRef;}
    public void setCurrentFloor(int floor){this.currentFloor = floor;}
    public double getXPanOffset(){
        return this.getCurrentFloor().getPanXOffset();
    }
    public double getYPanOffset(){
        return this.getCurrentFloor().getPanYOffset();
    }
    public void enableSim(){this.runningSim = true;}
    public boolean getSimState(){return this.runningSim;}

    public Floor increaseFloor() {
        if(hasNextFloor()){currentFloor += 1;}
        return  floors.get( currentFloor);
    }
    public Floor decreaseFloor() {
        if(hasPrevFloor()){
            currentFloor -= 1;
        }
        return  floors.get( currentFloor);
    }

    /*
    public void renderBlocks(){
        int index = 0;
        for(Floor floor : floors){ floor.renderBlocks(index); index++;}

    }*/

    public final void addFloor() {
        Floor newFloor = new Floor(this.getHeight(), this.getWidth(), 50, this.floors.size());
        this.floors.add(newFloor);
    }

    public static int normaliseXCoord(double x, Building b){
        double tmp = ((x-b.getXPanOffset())/b.getCurrentFloor().getTileSize());
        return (int)tmp;
    }

    public static int normaliseYCoord(double y, Building b){
        double tmp = ((y-b.getYPanOffset())/b.getCurrentFloor().getTileSize());
        return (int)tmp;
    }


}

