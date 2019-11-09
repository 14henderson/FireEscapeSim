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
import fireescapedemo.Tile.BlockType;

/*
public class Floor {
    public ArrayList<Employee> employees;
    public ArrayList<Line> walls;
    private Pane floor;
    private Tile[][] floorBlocks;
    private Rectangle[][] wallBlocks;
 */

public class Floor extends MapObject implements Serializable {
    public ArrayList<Employee> employees;
    public Tile[][] floorBlocks;
    int mapHeight;
    int mapWidth;
    int tileSize;
    boolean lineClicked = false;
    double lineCords[] = new double[2];
    public ArrayList<TileObject> tileObjects;
    ArrayList<Exit> exits;
    Rectangle lastRec = null;
    private ArrayList<double[]> walls;
    private transient ArrayList<Line> wallsNodes;



    public Floor(int height, int width, int size){
        this.mapHeight = height;
        this.mapWidth = width;
        this.tileSize = size;

        this.floorBlocks = new Tile[this.mapWidth][this.mapHeight];
        this.employees = new ArrayList<>();
        this.walls = new ArrayList<>();
        this.wallsNodes = new ArrayList<>();
        this.exits = new ArrayList<>();


        System.out.println("Tile size: "+this.tileSize);
        for(int i = 0; i < this.mapWidth; i++){
            for(int j = 0; j< this.mapHeight; j++){
                this.floorBlocks[i][j] = new Tile(i*this.tileSize,j*this.tileSize, i, j, this.tileSize);
        }}
    }


    public void addEmployee(Employee employee) { this.employees.add(employee); }
    public void addExit(Exit exit)          { this.exits.add(exit); }



    @Override
    public void updateView(){
        if(this.wallsNodes.size() != this.walls.size()){
            this.initialiseView();
        }

        for(Tile[] tileRow : this.floorBlocks){
            for(Tile aTile : tileRow){
                aTile.updateView();
        }}

        for(int n=0; n<this.walls.size(); n++){
            this.wallsNodes.get(n).setStartX(this.walls.get(n)[0]);
            this.wallsNodes.get(n).setStartY(this.walls.get(n)[1]);
            this.wallsNodes.get(n).setEndX(this.walls.get(n)[2]);
            this.wallsNodes.get(n).setEndY(this.walls.get(n)[3]);
        }
    }

    public void zoom(int zoomValue){
        int newZoomValue = this.tileSize + zoomValue;
        for(int i = 0; i < this.mapWidth; i++){
            for(int j = 0; j< this.mapHeight; j++){
                this.floorBlocks[i][j].setActualCords(
                        (int)(this.floorBlocks[i][j].getActualX()/(double)this.tileSize*newZoomValue),
                        (int)(this.floorBlocks[i][j].getActualY()/(double)this.tileSize*newZoomValue));
                this.floorBlocks[i][j].setDimensions(newZoomValue, newZoomValue);
                this.floorBlocks[i][j].updateView();
        }}
        System.out.println(this.floorBlocks[0][0].toString());
        System.out.println(this.floorBlocks[0][1].toString());
        for(int n=0; n<this.walls.size(); n++){
            this.walls.get(n)[0] = this.walls.get(n)[0]/(double)this.tileSize*newZoomValue;
            this.walls.get(n)[1] = this.walls.get(n)[1]/(double)this.tileSize*newZoomValue;
            this.walls.get(n)[2] = this.walls.get(n)[2]/(double)this.tileSize*newZoomValue;
            this.walls.get(n)[3] = this.walls.get(n)[3]/(double)this.tileSize*newZoomValue;
            this.wallsNodes.get(n).setStartX(this.walls.get(n)[0]);
            this.wallsNodes.get(n).setStartY(this.walls.get(n)[1]);
            this.wallsNodes.get(n).setEndX(this.walls.get(n)[2]);
            this.wallsNodes.get(n).setEndY(this.walls.get(n)[3]);
            this.wallsNodes.get(n).setStrokeWidth(10*this.getTileSize()/50.0);
            this.wallsNodes.get(n).toFront();
        }
        FXMLBuildingController.zoomLineBlocks(this.tileSize, newZoomValue);
        this.tileSize = newZoomValue;
    }

    public void pan(int xinc, int yinc){
        for(int i = 0; i < this.mapWidth; i++){
            for(int j = 0; j< this.mapHeight; j++){
                this.floorBlocks[i][j].translate(xinc, yinc);
                this.floorBlocks[i][j].updateView();
            }}
        for(int n=0; n<this.walls.size(); n++){
            this.walls.get(n)[0] += xinc;
            this.walls.get(n)[1] += yinc;
            this.walls.get(n)[2] += xinc;
            this.walls.get(n)[3] += yinc;
            this.wallsNodes.get(n).setStartX(this.walls.get(n)[0]);
            this.wallsNodes.get(n).setStartY(this.walls.get(n)[1]);
            this.wallsNodes.get(n).setEndX(this.walls.get(n)[2]);
            this.wallsNodes.get(n).setEndY(this.walls.get(n)[3]);
            this.wallsNodes.get(n).toFront();
        }
        FXMLBuildingController.panLineBlocks(xinc, yinc);
    }

    @Override
    public void initialiseView(){
        this.wallsNodes = new ArrayList<>();
        try{this.exits.clear();}catch(Exception e){}
        try{this.employees.clear();}catch(Exception e){}
        mainBuilding.windowContainer.getChildren().clear();
        mainBuilding.windowContainer.setPrefHeight(500);
        mainBuilding.windowContainer.setPrefWidth(500);
        mainBuilding.windowContainer.setLayoutX(15);
        mainBuilding.windowContainer.setLayoutY(15);
        for(Tile[] tileRow : this.floorBlocks){
            for(Tile aTile : tileRow){
                aTile.initialiseView();
        }}
        for(double[] line : this.walls){
            Line wallLine = new Line(line[0], line[1], line[2], line[3]);
            this.wallsNodes.add(wallLine);
            wallLine.setStroke(Color.BLUE);
            wallLine.setStrokeWidth(10*this.tileSize/50.0);
            mainBuilding.windowContainer.getChildren().add(wallLine);
        }
        for(Employee e : this.employees){
            e.view.toFront();
        }
    }




    public final Tile getTestFirstExit(){
        return this.exits.isEmpty() ? null : this.exits.get(0).position;
    }

    public Tile getTile(int x, int y){return this.floorBlocks[x][y];}
    public void addWall(double[] cords, Line l){
        this.walls.add(cords);
        this.wallsNodes.add(l);
    }
    public ArrayList<double[]> getWalls(){return this.walls;}
    public final Tile[][] getCurrentFloorBlock(){return  floorBlocks;}
    public int getMapHeight(){return this.mapHeight;}
    public int getMapWidth(){return this.mapWidth;}
    public int getTileSize(){return this.tileSize;}
    public void setTileSize(int size){this.tileSize = size;}

}
/*
    public void refactorFloorForSim(){
        this.floor = new Pane();
        int i,j, sizei = this.floorBlocks.length,sizej = this.floorBlocks[0].length;
        for(i = 0; i < sizei; i++){
            for(j = 0; j < sizej; j++){
                if(this.floorBlocks[i][j].type != BlockType.Exit) {
                    this.floorBlocks[i][j].block.setStroke(Color.WHITESMOKE);
                    this.floorBlocks[i][j].block.setFill(Color.WHITESMOKE);
                    this.floor.getChildren().add(this.floorBlocks[i][j].block);
                }
            }
        }

        /*
        //this is purley for testing purposes
        int empX,empY,exiX,exiY;
        for(Line l : this.walls){
            this.floor.getChildren().add(l);
        }
        for(Actor emp : employees){
            System.out.println("Wow 1");
            this.floor.getChildren().add(emp.view);
        }
        for(Exit exit : this.exits){
            System.out.println("Exit added");
            this.floor.getChildren().add(exit.VIEW);
        }
        System.out.println("Number of Exits: " + this.exits.size());
    }


    public void renderBlocks(int index){

        for(Tile[] floor :  this.floorBlocks){
            for(Tile block : floor){
                if(block.type != null) { block.type.render(); }
    }}}
    */


