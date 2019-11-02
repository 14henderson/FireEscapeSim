package fireescapedemo;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import fireescapedemo.Tile.BlockType;

public class Floor {
    public ArrayList<Employee> employees;
    public ArrayList<Line> walls;
    private Pane floor;
    private Tile[][] floorBlocks;
    private Rectangle[][] wallBlocks;
    int height;
    int width;
    int size;
    boolean lineClicked = false;
    double lineCords[] = new double[2];
    ArrayList<Exit> exits;
    Rectangle lastRec = null;

    public Floor(int heigh, int widt, int siz){

        this.floor = new Pane();
        this.floorBlocks = new Tile[width][height];
        this.employees = new ArrayList<>();
        this.walls = new ArrayList<>();
        this.exits = new ArrayList<>();
        this.height = heigh;
        this.width = widt;
        this.size = siz;
        addFloor();
    }

    public final void addFloor(){
        int i,j;
        double newSize =  size / 2;
        this.floor = new Pane();
        this.floor.setPrefHeight(500);
        this.floor.setPrefWidth(500);
        this.floor.setLayoutX(25);
        this.floor.setLayoutY(25);
        Tile b;
        Rectangle r;
        this.floorBlocks = new Tile[ size* width / size][ size *  width / size];
        this.wallBlocks =
                new Rectangle[(this.floorBlocks.length + 1)][(this.floorBlocks[0].length+1)];

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
        }

    }

    public void addEmployee(Employee employee) { this.employees.add(employee); }
    public void addExit(Exit exit)          { this.exits.add(exit); }


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

    public final Tile getTestFirstExit(){
        return this.exits.isEmpty() ? null : this.exits.get(0).position;
    }

    public final Tile[][] getCurrentFloorBlock(){return  floorBlocks;}

    public Pane getFloor() { return this.floor; }


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
                if(block.type != null) { block.type.render(index,block.block.getX(),block.block.getY(),block); }
            }
        }
        refactorFloorForSim();
    }



    @FXML
    public void setLineClicked(MouseEvent event, Rectangle finalR){
        if(lineClicked){
            double x1 = event.getX(),y1 = event.getY(), x2 = lineCords[0], y2 = lineCords[1];
            double [] cords = {x1,y1,x2,y2};
            cords = normaliseCords(cords);
            Line l = new Line(cords[0],cords[1],cords[2],cords[3]);
            l.setStroke(Color.BLUE);
            l.setStrokeWidth(10);
            this.floor.getChildren().add(l);
            System.out.println("x1: " + cords[0] + ", x2: " + cords[2]);
            System.out.println("y1: " + cords[1] + ", y2: " + cords[3]);
            if(cords[1] == cords[3]){

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
            finalR.toFront();
            lastRec.toFront();
            this.walls.add(l);
            lineClicked = false;
            lineCords[0] = 0;
            lineCords[1] = 0;
            lastRec = null;
            System.out.println("Line Deactivated");
        }else{
            lineClicked = true;
            lineCords[0] = event.getX();
            lineCords[1] = event.getY();
            System.out.println("Line Active");
            lastRec = finalR;
        }
    }


}
