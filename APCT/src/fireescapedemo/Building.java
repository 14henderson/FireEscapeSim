package fireescapedemo;

import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;



public class Building {
    public static ArrayList<Actor> employees;
    private static ArrayList<Pane> floors;
    private static ArrayList<Tile[][]> floorBlocks;
    private static ArrayList<Rectangle[][]> wallBlocks;
    static int currentFloor;
    static int height;
    static int width;
    static int size;
    static boolean lineClicked = false;
    static double lineCords[] = new double[2];
    static Rectangle lastRec = null;

    public Building(){
        if(floors == null){
            throw new RuntimeException();
        }
    }

    public Building(int heigh, int widt, int siz){
        if ( floors == null){
             floors = new ArrayList();
             floorBlocks = new ArrayList<>();
             wallBlocks = new ArrayList<>();
             employees = new ArrayList<>();
             height = heigh;
             width = widt;
             size = siz;
            addFloor();
             currentFloor = 0;
        }
    }

    public final void addFloor(){
        int i,j;
        double newSize =  size / 2;
        Pane p = new Pane();
        p.setPrefHeight(500);
        p.setPrefWidth(500);
        p.setLayoutX(25);
        p.setLayoutY(25);
        Tile b;
        Rectangle r;
        Tile[][] bb = new Tile[ size* width / size][ size *  width / size];
        Rectangle[][] lineDots =
                new Rectangle[(bb.length + 1)][(bb[0].length+1)];

        for(i = 0; i < bb.length; i ++){
            for(j = 0; j< bb[i].length; j++){
                bb[i][j] = new Tile(i,j,size);
                p.getChildren().add(bb[i][j].block);
            }
        }

        for(i = 0; i < lineDots.length; i ++){
            for(j = 0; j< lineDots[i].length; j++){

                r = new Rectangle(((i*size) - (newSize / 2)) ,((j*size) - (newSize / 2)), newSize, newSize);
                Rectangle finalR = r;
                r.setOnMouseClicked(new EventHandler <MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if(Tile.isBuildEnabled()){setLineClicked(mouseEvent, finalR);}
                    }
                });
                r.setOpacity(0.2);
                r.setFill(Color.RED);
                p.getChildren().add(r);
                lineDots[i][j] = r;
            }
        }


         floorBlocks.add(bb);
         floors.add(p);
         wallBlocks.add(lineDots);
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

    public void renderForSim(){
        for(Pane floor:  floors){
            for(Rectangle[][] walls :  wallBlocks)
                for(Rectangle[] wall : walls){
                    for(Rectangle w : wall){
                        floor.getChildren().remove(w);
                    }
                }
        }
    }

    public final Pane getCurrentFloor(){ return  floors.get( currentFloor); }
    public final Tile[][] getCurrentFloorBlock(){return  floorBlocks.get( currentFloor);}
    
    public boolean hasNextFloor(){return ( currentFloor + 1) <  floors.size(); }
    public boolean hasPrevFloor(){return ( currentFloor - 1) > -1; }
    
    public Pane nextFloor() {
        if(hasNextFloor()){
             currentFloor += 1;
        }
        return  floors.get( currentFloor);
    }
    public Pane prevFloor() {
        if(hasPrevFloor()){
             currentFloor -= 1;
        }
        return  floors.get( currentFloor);
    }
    
    public void renderBlocks(){
        for(Tile[][] floors :  floorBlocks){
            for(Tile[] floor : floors){
                for(Tile block : floor){
                    if(block.type != null) { block.type.render(); }
                }
            }
        }
        renderForSim();
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
             floors.get( currentFloor).getChildren().add(l);
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
                     floorBlocks.get( currentFloor)[i][y].removeAccess(0);
                    System.out.println("Block [" + i + "], [" + y + "] has removed access " + 0);
                }

                if(y != 0){
                    y -= 1;
                    for(int i = min; i < max; i++){
                         floorBlocks.get( currentFloor)[i][y].removeAccess(2);
                        System.out.println("Block [" + i + "], [" + y + "] has removed access " + 2);
                    }
                }

            }else if (cords[0] == cords[2]){
                int y = (int)y1 == 0 ? 0 : (int)cords[1] / 50;
                int yy = (int)y2 == 0 ? 0 : (int)cords[3] / 50;
                int x = (int)x1 == 0 ? 0 : (int)cords[0] / 50;
                int max = y - yy < 0 ? yy : y;
                int min = y - yy < 0 ? y : yy;
                System.out.println("Min: " + min + ", Max: " + max);
                System.out.println("y: " + y + ", yy: " + yy + ", x: " + x);
                for(int i = min; i < max; i++){
                     floorBlocks.get( currentFloor)[x][i].removeAccess(3);
                    System.out.println("Block [" + x + "], [" + i + "] has removed access " + 3);
                }

                if(x != 0){
                    x -= 1;
                    for(int i = min; i < max; i++){
                         floorBlocks.get( currentFloor)[x][i].removeAccess(1);
                        System.out.println("Block [" + i + "], [" + y + "] has removed access " + 1);
                    }
                }
            }
            finalR.toFront();
            lastRec.toFront();
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
