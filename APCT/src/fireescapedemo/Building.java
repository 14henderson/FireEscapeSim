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
/*
    public Building(Building loadedBuilding){
        this.floors = loadedBuilding.floors;
        this.currentFloor = 0;
        this.height = loadedBuilding.height;
        this.width = loadedBuilding.width;
        this.size = loadedBuilding.size;

        for(Floor aFloor : this.floors){
            aFloor.floor = new Pane();

            for(Tile[] aTileRow : aFloor.getCurrentFloorBlock()){
                for(Tile aTile : aTileRow){
                   // aTile.
                }
            }
        }
    }
*/



    public final ArrayList<Floor> getFloors(){return this.floors;}
    public final Floor getCurrentFloor(){ return  floors.get( currentFloor); }
    //public final Tile[][] getCurrentFloorBlock(){return  floors.get( currentFloor).getCurrentFloorBlock();}
    public final int getCurrentFloorIndex() {return this.currentFloor;}
    public int getTotalFloors(){return this.floors.size();}
    public boolean hasNextFloor(){return ( this.currentFloor + 1) <  this.floors.size(); }
    public boolean hasPrevFloor(){return ( this.currentFloor - 1) > -1; }
    
    public Floor increaseFloor() {
        if(hasNextFloor()){currentFloor += 1;}
        return  floors.get( currentFloor);
    }
    public Floor decreaseFloor() {
        if(hasPrevFloor()){currentFloor -= 1;}
        return  floors.get( currentFloor);
    }

    @Override
    public void render(){
        this.floors.get(this.currentFloor).render();
    }


    @Override
    public String toString(){
        String output = "";
        output += ("Num Floors:"+this.getTotalFloors()+"\n");
        //output += ("")
        return output;
    }




    public final void addFloor() {
        Floor newFloor = new Floor(this.height, this.width, this.size);
        this.floors.add(newFloor);

    }

/*

        Tile b;
        Rectangle r;
        this.floorBlocks = new Tile[ size* width / size][ size *  width / size];
        this.wallBlocks = new Rectangle[(this.floorBlocks.length + 1)][(this.floorBlocks[0].length+1)];

        for(i = 0; i < this.floorBlocks.length; i ++){
            for(j = 0; j< this.floorBlocks[i].length; j++){
                this.floorBlocks[i][j] = new Tile(i,j,size);
                this.floor.getChildren().add(this.floorBlocks[i][j].block);
            }
        }

        for(i = 0; i < wallBlocks.length; i ++){
            for(j = 0; j< this.wallBlocks[i].length; j++){

                r = new Rectangle(((i*size) - (newSize / 2)) ,((j*size) - (newSize / 2)), newSize, newSize);
                Rectangle finalR = r;
                r.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if(Tile.isBuildEnabled()){setLineClicked(mouseEvent, finalR);}
                    }
                });
                r.setOpacity(0.2);
                r.setFill(Color.RED);
                this.floor.getChildren().add(r);
                wallBlocks[i][j] = r;
            }
        }*/
    }

