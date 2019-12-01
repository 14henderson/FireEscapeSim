package fireescapedemo;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class QuadTree{
    private final int cap;
    private final double x,y,w,h;
    private int iter;
    private boolean divided;
    private final Actor[] points;
    private final QuadTree[] children;
    private static ArrayList<Line> walls;
    public static boolean col = false;


    public QuadTree(int cap, double x, double y, double w, double h){
        this.cap = cap;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.iter = 0;
        this.divided = false;
        this.points = new Actor[this.cap];
        this.children = new QuadTree[4];
    }
    public QuadTree(ArrayList<Line> walls, int cap, double x, double y, double w, double h){
        this.cap = cap;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.iter = 0;
        this.divided = false;
        this.points = new Actor[this.cap];
        this.children = new QuadTree[4];
        QuadTree.walls = walls;
    }

    //inserting all actors at once
    public void insertAll(ArrayList<?> actors){
        int i, len = actors.size();
        for( i = 0; i < len; i++){
            if(actors.get(i) instanceof Actor){
                this.insert((Actor)actors.get(i));
            }
        }
    }

    public void insert(Actor a){
        if(this.contains(a)) {
            if (!this.divided && this.iter + 1 <= this.cap) {
                this.points[this.iter] = a;
                iter++;
            } else {
                if (!this.divided) {
                    this.createChildren();
                }
                int i = 0, len = this.children.length;
                for (i = 0; i < len; i++) {
                    if (this.children[i].contains(a)) {
                        this.children[i].insert(a);
                    }
                }
            }
        }
    }
    private void createChildren(){
        double hW = this.w/2, hH = this.h/2;
        //northwest
        QuadTree northwest = new QuadTree(this.cap,    this.x,         this.y,       hW,hH);
        //northeast
        QuadTree northeast = new  QuadTree(this.cap,this.x + hW,    this.y,       hW,hH);
        //southwest
        QuadTree southwest = new QuadTree(this.cap,    this.x,      this.y + hH,  hW,hH);
        //southeast (represent)
        QuadTree southeast = new QuadTree(this.cap, this.x  + hW,this.y + hH,  hW,hH);
        this.children[0] = northeast;
        this.children[1] = northwest;
        this.children[2] = southwest;
        this.children[3] = southeast;
        this.divided = true;
    }

    public boolean contains(Actor a){
        double aX = a.fxNode.getLayoutX(), aY =  a.fxNode.getLayoutY();

        return  aX >= this.x&&
                aX <= this.x + this.w &&
                aY >= this.y&&
                aY <= this.y + this.h;
    }



    public void checkCollisions(){
        int i,j, pointLen = this.points.length, childLen = this.children.length;
        double avX = 0, avY = 0;
        Actor point, target;
        double size,newX, newY,distance, displacement,px,py,tx,ty;
        Point2D newV;
        //apply collisions localy
        for(i = 0; i < this.iter; i++){
            point = this.points[i];
            avX += point.velocity.getX();
            avY += point.velocity.getY();

            for(j = 0; j < this.iter; j++){
                target = this.points[j];
                if(point == target){continue;}
                if(point instanceof Employee && target instanceof Employee){
                    if(this.collisionBetween((Employee)point,(Employee)target)){
                        QuadTree.col = true;
                        px = point.fxNode.getLayoutX();py = point.fxNode.getLayoutY();
                        tx = target.fxNode.getLayoutX();ty = point.fxNode.getLayoutY();
                        size = ((Circle)((Employee)(point)).fxNode).getRadius() ;
                        distance =  Math.sqrt(Math.pow(px - tx,2) + Math.pow(py - ty,2));
                        displacement = 0.5d * (distance- size - size);

                        point.fxNode.setLayoutX(px - (displacement * (px - tx) / distance));
                        point.fxNode.setLayoutY(py - (displacement * (py - ty) / distance));

                        target.fxNode.setLayoutX(tx + (displacement * (px - tx) / distance));
                        target.fxNode.setLayoutY(ty + (displacement * (py - ty) / distance));

                        ((Circle)point.fxNode).setFill(Color.GREEN);
                        ((Circle)target.fxNode).setFill(Color.GREEN);
                    }else{
                        ((Circle)point.fxNode).setFill(Color.LIGHTBLUE);
                        ((Circle)target.fxNode).setFill(Color.LIGHTBLUE);
                    }
                }
            }
        }
        newV = new Point2D(avX/this.iter, avY/iter);
        //System.out.println("x: " + newV.getX() + ", y: " + newV.getY());
       // System.out.println("col: " + col);
        /*
        if(col == true)
            for(i = 0; i < this.iter; i++){ this.points[i].setVelocity(newV); ((Employee)this.points[i]).setAvgMove(true);}
        */
        if(this.divided){
            for(i = 0; i < childLen; i++){
                this.children[i].checkCollisions();
            }
        }
        boolean work_properly = true;

    }

    private boolean collisionBetween(Employee a, Employee b){
        if(a.hasExited() || b.hasExited()) return false;
        double dis = Math.sqrt(Math.pow(a.fxNode.getLayoutX() - b.fxNode.getLayoutX(),2) +
                Math.pow(a.fxNode.getLayoutY() - b.fxNode.getLayoutY(),2));
        return dis < ((Circle)(a.fxNode)).getRadius() + ((Circle)(b.fxNode)).getRadius();
    }





    public void drawLines(Pane floor){
        //LEFT 2 RIGHT UPPER
        Line line1 = new Line(this.x,this.y,this.x + this.w, this.y);
        //LEFT 2 RIGHT LOWER
        Line line2 = new Line(this.x,this.y + this.h,this.x, this.y + this.h);
        //UP TO DOWN LEFT
        Line line3 = new Line(this.x,this.y,this.x, this.y + this.h);
        //UP TO DOWN RIGHT
        Line line4 = new Line(this.x+ this.w,this.y,this.x + this.w, this.y + this.h);

        line1.setStroke(Color.ORANGE);
        line2.setStroke(Color.ORANGE);
        line3.setStroke(Color.ORANGE);
        line4.setStroke(Color.ORANGE);
        floor.getChildren().add(line1);
        floor.getChildren().add(line2);
        floor.getChildren().add(line3);
        floor.getChildren().add(line4);
        if(this.divided){
            int i, len = this.children.length;
            for(i = 0; i < len; i++){
                this.children[i].drawLines(floor);
            }
        }
    }

    public void clear(){
        int i, lenP = this.points.length, lenC = this.children.length;
        this.iter = 0;
        this.divided = false;
        for(i = 0; i < lenP; i++){this.points[i] = null;}
        for(i = 0; i < lenC; i++){this.children[i] = null;}
    }


}