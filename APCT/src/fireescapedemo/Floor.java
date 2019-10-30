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
    public Tile[][] floorBlocks;
    int height;
    int width;
    int size;
    boolean lineClicked = false;
    private ArrayList<double[]> walls;



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
    }

    public void zoom(int zoomValue){
        this.size += zoomValue;
        for(int i = 0; i < this.floorBlocks.length; i ++){
            for(int j = 0; j< this.floorBlocks[i].length; j++){
                this.floorBlocks[i][j].setActualCords(i*this.size, j*this.size);
                this.floorBlocks[i][j].zoom(zoomValue);
            }}

        double zoomFactor = this.size/20.0;
        for(double[] line : this.walls) {
            line[0] *= zoomFactor;
            line[1] *= zoomFactor;
            line[2] *= zoomFactor;
            line[3] *= zoomFactor;
        }
        this.render();
    }

    @Override
    public void render(){
        int i,j;
        mainBuilding.windowContainer.getChildren().clear();
        mainBuilding.windowContainer.setPrefHeight(500);
        mainBuilding.windowContainer.setPrefWidth(500);
        mainBuilding.windowContainer.setLayoutX(15);
        mainBuilding.windowContainer.setLayoutY(15);
        for(Tile[] tileRow : this.floorBlocks){
            for(Tile aTile : tileRow){
                aTile.render();
        }}

        for(double[] line : this.walls){
            Line wallLine = new Line(line[0], line[1], line[2], line[3]);
            wallLine.setStroke(Color.BLUE);
            wallLine.setStrokeWidth(10*this.size/50.0);
            mainBuilding.windowContainer.getChildren().add(wallLine);
        }
    }






    public Tile getTile(int x, int y){return this.floorBlocks[x][y];}
    public void addEmployee(Actor employee){employees.add(employee);}
    public void addWall(double[] cords){this.walls.add(cords);}
    public ArrayList<double[]> getWalls(){return this.walls;}
    public final Tile[][] getCurrentFloorBlock(){return  floorBlocks;}


    public void renderBlocks(){
        for(Tile[] floor :  floorBlocks){
            for(Tile block : floor){
                if(block.type != null) { block.type.render(); }
    }}}








}
