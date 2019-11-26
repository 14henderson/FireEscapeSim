package fireescapedemo;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;
import java.util.ArrayList;
import fireescapedemo.Tile.BlockType;


public class Floor extends MapObject implements Serializable {
    public ArrayList<Employee> employees;
    public Tile[][] floorBlocks;
    int mapHeight;
    int mapWidth;
    int tileSize;
    int panXOffset;
    int panYOffset;
    boolean lineClicked = false;
    double lineCords[] = new double[2];
    public ArrayList<TileObject> tileObjects;
    ArrayList<Exit> exits;
    private ArrayList<int[]> walls;
    private transient ArrayList<Line> wallsNodes;
    public int floorNum;



    public Floor(int height, int width, int size, int floorIndex){
        this.mapHeight = height;
        this.mapWidth = width;
        this.tileSize = size;
        this.floorNum = floorIndex;

        this.floorBlocks = new Tile[this.mapWidth][this.mapHeight];
        this.employees = new ArrayList<>();
        this.walls = new ArrayList<>();
        this.wallsNodes = new ArrayList<>();
        this.exits = new ArrayList<>();

        this.panXOffset = 0;
        this.panYOffset = 0;

        System.out.println("Tile size: "+this.tileSize);
        for(int i = 0; i < this.mapWidth; i++){
            for(int j = 0; j< this.mapHeight; j++){
                this.floorBlocks[i][j] = new Tile(i*this.tileSize,j*this.tileSize, i, j, this.tileSize);
                this.floorBlocks[i][j].setFloorNum(this.floorNum);
        }}
    }


    public void addEmployee(Employee employee) { this.employees.add(employee); }
    public void addExit(Exit exit)          { this.exits.add(exit); }
    public void setFloorNum(int n){this.floorNum = n;}





    @Override
    public void updateView(){
        if (this.wallsNodes == null) {this.initialiseView();}       //if wall nodes never initialised.
        if(this.wallsNodes.size() != this.walls.size()){this.initialiseWalls();}    //re-draw walls if required
        for(Tile[] tileRow : this.floorBlocks){                     //update tiles
            for(Tile aTile : tileRow){
                aTile.updateView();
        }}
        this.updateWalls();
    }


    public void updateWalls(){
        for(int n=0; n<this.walls.size(); n++){                     //reposition walls with cords
            this.wallsNodes.get(n).setStartX((this.walls.get(n)[0]*this.mainBuilding.getSize())+this.mainBuilding.getXPanOffset());
            this.wallsNodes.get(n).setStartY((this.walls.get(n)[1]*this.mainBuilding.getSize())+this.mainBuilding.getYPanOffset());
            this.wallsNodes.get(n).setEndX((this.walls.get(n)[2]*this.mainBuilding.getSize())+this.mainBuilding.getXPanOffset());
            this.wallsNodes.get(n).setEndY((this.walls.get(n)[3]*this.mainBuilding.getSize())+this.mainBuilding.getYPanOffset());
            this.wallsNodes.get(n).setStrokeWidth(10 * mainBuilding.getSize() / 50.0);
            this.wallsNodes.get(n).toFront();
        }
    }
    public void initialiseWalls(){
        for(Node wall : this.wallsNodes) {
            this.mainBuilding.windowContainer.getChildren().remove(wall);
        }
        this.wallsNodes.clear();
        for(int[] line : this.walls){
            Line wallLine = new Line(
                    (line[0]*this.mainBuilding.getSize())+this.mainBuilding.getXPanOffset(),
                    (line[1]*this.mainBuilding.getSize())+this.mainBuilding.getYPanOffset(),
                    (line[2]*this.mainBuilding.getSize())+this.mainBuilding.getXPanOffset(),
                    (line[3]*this.mainBuilding.getSize())+this.mainBuilding.getYPanOffset());
            this.wallsNodes.add(wallLine);
            wallLine.setStroke(Color.BLUE);
            wallLine.setStrokeWidth(10*this.tileSize/50.0);
            mainBuilding.windowContainer.getChildren().add(wallLine);
        }
    }


    public void zoom(int zoomValue){
        int newZoomValue = this.tileSize + zoomValue;
        this.mainBuilding.setSize(newZoomValue);
        this.tileSize = newZoomValue;

        FXMLBuildingController.zoomLineBlocks();
        for(int i = 0; i < this.mapWidth; i++){
            for(int j = 0; j< this.mapHeight; j++){         //for every tile
                this.floorBlocks[i][j].setActualCords(
                        (this.floorBlocks[i][j].getGridX()*this.mainBuilding.getSize())+this.mainBuilding.getXPanOffset(),
                        (this.floorBlocks[i][j].getGridY()*this.mainBuilding.getSize())+this.mainBuilding.getYPanOffset());
                this.floorBlocks[i][j].setDimensions(this.mainBuilding.getSize(), this.mainBuilding.getSize());
                if(this.floorBlocks[i][j].tileObject != null){
                    this.floorBlocks[i][j].updateView();
                }else {
                    this.floorBlocks[i][j].initialiseView();
                }
            }
        }

        for(int n=0; n<this.walls.size(); n++){
            this.wallsNodes.get(n).setStartX((this.walls.get(n)[0]*this.mainBuilding.getSize())+this.mainBuilding.getXPanOffset());
            this.wallsNodes.get(n).setStartY((this.walls.get(n)[1]*this.mainBuilding.getSize())+this.mainBuilding.getYPanOffset());
            this.wallsNodes.get(n).setEndX((this.walls.get(n)[2]*this.mainBuilding.getSize())+this.mainBuilding.getXPanOffset());
            this.wallsNodes.get(n).setEndY((this.walls.get(n)[3]*this.mainBuilding.getSize())+this.mainBuilding.getYPanOffset());
            this.wallsNodes.get(n).setStrokeWidth(10 * mainBuilding.getSize() / 50.0);
            this.wallsNodes.get(n).toFront();
        }
    }

    public int getPanXOffset(){return this.panXOffset;}
    public int getPanYOffset(){return this.panYOffset;}

    public void pan(int xinc, int yinc){
        this.panXOffset += xinc;
        this.panYOffset += yinc;
        for(int i = 0; i < this.mapWidth; i++){
            for(int j = 0; j< this.mapHeight; j++){
                this.floorBlocks[i][j].translate(xinc, yinc);
                this.floorBlocks[i][j].updateView();
            }
        }
        for(int n=0; n<this.walls.size(); n++){
            this.wallsNodes.get(n).setStartX((this.walls.get(n)[0]*this.mainBuilding.getSize())+this.mainBuilding.getXPanOffset());
            this.wallsNodes.get(n).setStartY((this.walls.get(n)[1]*this.mainBuilding.getSize())+this.mainBuilding.getYPanOffset());
            this.wallsNodes.get(n).setEndX((this.walls.get(n)[2]*this.mainBuilding.getSize())+this.mainBuilding.getXPanOffset());
            this.wallsNodes.get(n).setEndY((this.walls.get(n)[3]*this.mainBuilding.getSize())+this.mainBuilding.getYPanOffset());
            this.wallsNodes.get(n).setStrokeWidth(10 * mainBuilding.getSize() / 50.0);
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
        initialiseWalls();
        for(Employee e : this.employees){
            e.fxNode.toFront();
        }
    }




    public final Tile getTestFirstExit(){
        return this.exits.isEmpty() ? null : this.exits.get(0).parent;
    }

    public Tile getTile(int x, int y){
        if(x<this.floorBlocks.length && y<this.floorBlocks[0].length
        && x>= 0 && y >= 0) {
            return this.floorBlocks[x][y];
        }else{
            return new Tile();
        }
    }
    public void addWall(int[] cords, Line l){
        this.walls.add(cords);
        this.wallsNodes.add(l);
    }
    public void removeWall(int[] cords){
        for(int n=this.walls.size()-1; n>0; n--){
            //System.out.println(this.walls.get(n)[0]+", "+this.walls.get(n)[1]+", "+this.walls.get(n)[2]+", "+this.walls.get(n)[3]);


            if(this.walls.get(n)[0] == cords[0] && this.walls.get(n)[1] == cords[1]
            && this.walls.get(n)[2] == cords[2] && this.walls.get(n)[3] == cords[3]){
                this.walls.remove(n);
               // System.out.println("REMOVED WALL");
            }else if(this.walls.get(n)[0] == cords[2] && this.walls.get(n)[1] == cords[3]
                    && this.walls.get(n)[2] == cords[0] && this.walls.get(n)[3] == cords[1]){
                this.walls.remove(n);
             //   System.out.println("REMOVED WALL");
            }
        }
    }

    public ArrayList<int[]> getWalls(){return this.walls;}
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


