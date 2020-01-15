package fireescapedemo;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
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
    private boolean calledFromSim = false;
    private int width;
    private int height;
    //private static final long serialVersionUID = 12345;
    public static transient Pane windowContainer;
    public static transient TabPane paneContainer;


    public Building(){
        this.stairs = new HashMap<>();
        System.out.println("New Stairs being creates");
        this.mainBuilding = this;
        if(floors == null){
            throw new RuntimeException();
        }
        //this.pan(10, 10);
    }

    public Building(int floorWidth, int floorHeight, TabPane container){
        this.mainBuilding = this;
        //this.windowContainer = windowContainerParent;
        this.paneContainer = container;
        this.stairs = new HashMap<>();
        System.out.println("New Stairs being creates");
        this.width = floorWidth;
        this.height = floorHeight;

        if ( floors == null){
             floors = new ArrayList();
             //Floor tmpRef = new Floor(firstFloorPane, "Floor 0", floorHeight,floorWidth,tileSize, 0);
            // tmpRef.setFloorNum(0);
            // floors.add(tmpRef);
           //  currentFloor = 0;
             this.initialEmployeeCount = 0;
        }


    }

    public Building(int floorWidth, int floorHeight, int tileSize){
        this.mainBuilding = this;
        //this.windowContainer = windowContainerParent;
        this.stairs = new HashMap<>();
        this.width = floorWidth;
        this.height = floorHeight;
        System.out.println("New Stairs being creates");

        if ( floors == null){
            floors = new ArrayList();
            //Floor tmpRef = new Floor(firstFloorPane, "Floor 0", floorHeight,floorWidth,tileSize, 0);
            //tmpRef.setFloorNum(0);
            //floors.add(tmpRef);
            //currentFloor = 0;
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
        if(!this.floors.isEmpty()){this.floors.get(this.currentFloor).initialiseView();}
    }

    public void initialiseAll(){
        if(this.stairs == null){
            System.out.println("No stairs found");
            this.stairs = new HashMap<>();
        }
        this.mainBuilding = this;
        for(Floor f : this.floors){
            f.initialiseView();
        }
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


    public int getHeight(){
        if(this.getCurrentFloor() == null){
            return this.height;
        }else{return this.getCurrentFloor().getMapHeight();}
    }
    public int getWidth(){
        if(this.getCurrentFloor() == null){
            return this.width;
        }else{return this.getCurrentFloor().getMapWidth();}
    }
    public int getSize(){
        if(this.getCurrentFloor() == null){
            return 50;
        }else {
            //System.out.println("RETURNING A TILE SIZE OF: "+this.getCurrentFloor().getTileSize());
            return this.getCurrentFloor().getTileSize();
        }
    }
    public void setSize(int newSize){
        if(this.getCurrentFloor() != null) {
            this.getCurrentFloor().setTileSize(newSize);
        }else{
            this.floors.forEach(n -> n.setTileSize(newSize));
        }
    }
    public HashMap<Integer, Staircase> getStairs(){return this.stairs;}
    public final ArrayList<Floor> getFloors(){return this.floors;}
    public int getTotalFloors(){return this.floors.size();}
    public int getInitialEmployeeCount(){return this.initialEmployeeCount; }
    public boolean hasNextFloor(){return ( this.currentFloor + 1) <  this.floors.size(); }
    public boolean hasPrevFloor(){return ( this.currentFloor - 1) > -1; }
    public void setWindowContainer(Pane paneRef){this.windowContainer = paneRef;}
    public void setCurrentFloor(int floor){this.currentFloor = floor;}
    public double getXPanOffset(){
        if(this.getCurrentFloor() == null){return 0;}
        return this.getCurrentFloor().getPanXOffset();
    }
    public double getYPanOffset(){
        if(this.getCurrentFloor() == null){return 0;}
        return this.getCurrentFloor().getPanYOffset();
    }
    public void enableSim(){this.runningSim = true;}
    public boolean getSimState(){return this.runningSim;}
    public double getActorSize(){return 40;}
    public void setTabPane(TabPane newTabPane){this.paneContainer = newTabPane;}
    public void setCalledBySim(boolean b){this.calledFromSim = b;}
    public boolean getCalledBySim(){return this.calledFromSim;}

    public final Floor getCurrentFloor(){
        if(this.paneContainer.getTabs().size() == 0){
            return null;
        }
        String currTabName = this.paneContainer.getSelectionModel().getSelectedItem().getText();
        for(Floor f: this.floors){
            if(f.getId().equals(currTabName)){
                return f;
            }
        }
        //System.console().printf("CANNOT FIND FLOOR WITH THE NAME: "+currTabName);
        return null;
    }


    public Floor getFloor(String id){
        for(Floor f : this.floors){
            if(f.getId().equals(id)){
                return f;
            }
        }
        return null;
    }
    public final int getCurrentFloorIndex() {
        System.out.println("Calling unsafe method! (getCurrentFloorIndex)");
        return this.currentFloor;
    }


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

    public final void addFloor(Pane floorPane, String id) {
        Floor newFloor = new Floor(floorPane, id, this, this.getHeight(), this.getWidth(), 50, this.floors.size());
        this.floors.add(newFloor);
    }
    public final void removeFloor(String id){
        this.floors.removeIf(n -> (n.getId() == id));

    }
    public final void refreshFloorLabels(){
        String newId;
        for(int n=0; n<this.floors.size(); n++){
            newId = ("Floor "+n);
            this.floors.get(n).setId(newId);
            this.paneContainer.getTabs().get(n).setText(newId);
        }
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

