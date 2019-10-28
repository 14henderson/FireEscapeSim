package fireescapedemo;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;
import java.util.ArrayList;

public class Floor extends MapObject implements Serializable {
    public ArrayList<Actor> employees;
   // public transient Pane floor;
    public Tile[][] floorBlocks;
   // private transient Rectangle[][] wallBlocks;
    int height;
    int width;
    int size;
    boolean lineClicked = false;
    private ArrayList<double[]> walls;
    //
    //private transient Rectangle lastRec = null;
    //Building mainBuilding;

    public Floor(int heigh, int widt, int siz){
        this.height = heigh;
        this.width = widt;
        this.size = siz;

        this.floorBlocks = new Tile[this.width][this.height];
        this.employees = new ArrayList<>();
        this.walls = new ArrayList<>();

        for(int i = 0; i < this.floorBlocks.length; i ++){
            for(int j = 0; j< this.floorBlocks[i].length; j++){
                this.floorBlocks[i][j] = new Tile(i*size,j*size,size);
        }}


        this.render();
    }



    @Override
    public void render(){
        int i,j;
        double newSize =  size / 2;
        mainBuilding.windowContainer.getChildren().removeAll();
        mainBuilding.windowContainer.setPrefHeight(500);
        mainBuilding.windowContainer.setPrefWidth(500);
        mainBuilding.windowContainer.setLayoutX(25);
        mainBuilding.windowContainer.setLayoutY(25);
        //mainBuilding.windowContainer.getChildren()

        for(Tile[] tileRow : this.floorBlocks){
            for(Tile aTile : tileRow){
                aTile.render();
            }
        }




        System.out.println("You have this many walls: "+this.walls.size());

        for(double[] line : this.walls){
            System.out.println("Hello world!!!!"+line.toString());
            Line wallLine = new Line(line[0], line[1], line[2], line[3]);
            wallLine.setStroke(Color.BLUE);
            wallLine.setStrokeWidth(10);
            mainBuilding.windowContainer.getChildren().add(wallLine);

        }
    }


    public Tile getTile(int x, int y){
        return this.floorBlocks[x][y];
    }

    public void addEmployee(Actor employee){employees.add(employee);}

    public void addWall(double[] cords){
        this.walls.add(cords);
    }
    public ArrayList<double[]> getWalls(){
        return this.walls;
    }


    public final Tile[][] getCurrentFloorBlock(){return  floorBlocks;}

    //public Pane getFloor() { return this.floor; }

    public void renderBlocks(){
        for(Tile[] floor :  floorBlocks){
            for(Tile block : floor){
                if(block.type != null) { block.type.render(); }
            }
        }
    }








}
