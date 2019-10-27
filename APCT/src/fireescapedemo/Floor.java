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
    public LineTile[][] lineTiles;
   // private transient Rectangle[][] wallBlocks;
    int height;
    int width;
    int size;
    boolean lineClicked = false;
    private ArrayList<Integer[]> walls;
    double lineCords[] = new double[2];
    //private transient Rectangle lastRec = null;
    //Building mainBuilding;

    public Floor(int heigh, int widt, int siz){
        this.height = heigh;
        this.width = widt;
        this.size = siz;

        this.floorBlocks = new Tile[this.width][this.height];
        this.lineTiles = new LineTile[this.width+1][this.height+1];
        this.employees = new ArrayList<>();


        for(int i = 0; i < this.floorBlocks.length; i ++){
            for(int j = 0; j< this.floorBlocks[i].length; j++){
                this.floorBlocks[i][j] = new Tile(i*size,j*size,size);
        }}
        for(int i = 0; i < this.lineTiles.length; i ++){
            for(int j = 0; j< this.lineTiles[i].length; j++) {
                this.lineTiles[i][j] = new LineTile(((i*size) - (size / 4)) ,((j*size) - (size / 4)), size/2);
        }}



        this.render();
    }



    @Override
    public void render(){
        int i,j;
        double newSize =  size / 2;
         this.walls = new ArrayList<>();
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


        for(LineTile[] lineTileRow : this.lineTiles){
            for(LineTile lineTile : lineTileRow){
                lineTile.render();
            }
        }



        for(Integer[] line : this.walls){
            Line wallLine = new Line(line[0], line[1], line[2], line[3]);
            wallLine.setStroke(Color.BLUE);
            wallLine.setStrokeWidth(10);
            mainBuilding.windowContainer.getChildren().add(wallLine);

        }
    }


    /*


     */

    public Tile getTile(int x, int y){
        return this.floorBlocks[x][y];
    }

    public void addEmployee(Actor employee){employees.add(employee);}


    public final double[] normaliseCords(double cords[]){
        int i;
        double a;
        double[] finalCords = new double[4];
        for(i = 0; i < 4; i++){
            a = cords[i] %  size;
            System.out.println("Before: " + cords[i]);
            finalCords[i] = a < ( size/2) ? cords[i] - a : cords[i] + (50 - a);
            System.out.println("After: " + finalCords[i]);
        }
        return finalCords;
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





    @FXML
    public void setLineClicked(MouseEvent event){//, Rectangle finalR){
        if(lineClicked){
            double x1 = event.getX(),y1 = event.getY(), x2 = lineCords[0], y2 = lineCords[1];
            double [] cords = {x1,y1,x2,y2};
            cords = normaliseCords(cords);
            //Line l = new Line(cords[0],cords[1],cords[2],cords[3]);
            //l.setStroke(Color.BLUE);
            //l.setStrokeWidth(10);
            //floor.getChildren().add(l);
            //System.out.println("x1: " + cords[0] + ", x2: " + cords[2]);
            //System.out.println("y1: " + cords[1] + ", y2: " + cords[3]);
            if(cords[1] == cords[3]){
                Integer[] wallVec = {(int)x1, (int)y1, (int)x2, (int)y2};
                this.walls.add(wallVec);
                int x = (int)x1 == 0 ? 0 : (int)cords[0] / 50;
                int xx = (int)x2 == 0 ? 0 : (int)cords[2] / 50;
                int y = (int)y1 == 0 ? 0 : (int)cords[1] / 50;
                int max = x - xx < 0 ? xx : x;
                int min = x - xx < 0 ? x : xx;
                System.out.println("Min: " + min + ", Max: " + max);
                System.out.println("x: " + x + ", xx: " + xx + ", y: " + y);
                for(int i = min; i < max; i++){
                    floorBlocks[i][y].removeAccess(0);
                    System.out.println("Block [" + i + "], [" + y + "] has removed access " + 0);
                }

                if(y != 0){
                    y -= 1;
                    for(int i = min; i < max; i++){
                        floorBlocks[i][y].removeAccess(2);
                        System.out.println("Block [" + i + "], [" + y + "] has removed access " + 2);
                    }
                }

            }else if (cords[0] == cords[2]){
                int x = (int)x1 == 0 ? 0 : (int)cords[0] / 50;
                int y = (int)y1 == 0 ? 0 : (int)cords[1] / 50;
                int yy = (int)y2 == 0 ? 0 : (int)cords[3] / 50;
                int max = y - yy < 0 ? yy : y;
                int min = y - yy < 0 ? y : yy;
                System.out.println("Min: " + min + ", Max: " + max);
                System.out.println("y: " + y + ", yy: " + yy + ", x: " + x);
                for(int i = min; i < max; i++){
                    floorBlocks[x][i].removeAccess(3);
                    System.out.println("Block [" + x + "], [" + i + "] has removed access " + 3);
                }

                if(x != 0){
                    x -= 1;
                    for(int i = min; i < max; i++){
                        floorBlocks[x][i].removeAccess(1);
                        System.out.println("Block [" + i + "], [" + y + "] has removed access " + 1);
                    }
                }
            }
            //finalR.toFront();
            //lastRec.toFront();
            lineClicked = false;
            lineCords[0] = 0;
            lineCords[1] = 0;
            //lastRec = null;
            System.out.println("Line Deactivated");
        }else{
            lineClicked = true;
            lineCords[0] = event.getX();
            lineCords[1] = event.getY();
            System.out.println("Line Active");
            //lastRec = finalR;
        }
    }


}
