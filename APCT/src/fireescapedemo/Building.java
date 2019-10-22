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




public class Building extends Component implements Serializable {
    private static ArrayList<Floor> floors;
    private static int currentFloor;
    private static int height;
    private static int width;
    private static int size;

    public Building(){
        if(floors == null){
            throw new RuntimeException();
        }
    }

    public Building(int heigh, int widt, int siz){
        if ( floors == null){
             floors = new ArrayList();
             height = heigh;
             width = widt;
             size = siz;
             floors.add(new Floor(heigh,widt,siz));
             currentFloor = 0;
        }
    }

    public final void addFloor(){
        floors.add(new Floor(height,width,size));
    }

    public final ArrayList<Floor> getFloors(){return floors;}
    public final Pane getCurrentFloor(){ return  floors.get( currentFloor).getFloor(); }
    public final Tile[][] getCurrentFloorBlock(){return  floors.get( currentFloor).getCurrentFloorBlock();}
    public final int getFloorNum() {return currentFloor;}
    
    public boolean hasNextFloor(){return ( currentFloor + 1) <  floors.size(); }
    public boolean hasPrevFloor(){return ( currentFloor - 1) > -1; }
    
    public Pane nextFloor() {
        if(hasNextFloor()){
             currentFloor += 1;
        }
        return  floors.get( currentFloor).getFloor();
    }
    public Pane prevFloor() {
        if(hasPrevFloor()){
             currentFloor -= 1;
        }
        return  floors.get( currentFloor).getFloor();
    }
    
    public void renderBlocks(){
        for(Floor floor : floors){ floor.renderBlocks(); }

    }





}
