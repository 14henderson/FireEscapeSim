package fireescapedemo;

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
        if(!this.contains(a)){return;}
        if(!this.divided && this.iter+1 <= this.cap){
            this.points[this.iter] = a;
            System.out.println("Actor added ");
            iter++;
        }else{
            if(!this.divided){
                this.createChildren();
            }
            int i = 0, len = this.children.length;
            for(i  = 0; i < len; i++){  this.children[i].insert(a); }
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
        double aX = a.view.getLayoutX(), aY =  a.view.getLayoutX();

        return  aX > this.x &&
                aX < this.x + this.w &&
                aY > this.y &&
                aY < this.y + this.h;
    }



    public void checkCollisions(){
        int i,j, pointLen = this.points.length, childLen = this.children.length;
        Actor point, target;
        //apply collisions localy
        for(i = 0; i < pointLen; i++){

            point = this.points[i];

            for(j = 0; j < pointLen; j++){
                target = this.points[j];
                if(point == null || target == null || point == target){continue;}
                //if(point instanceof Employee && target instanceof Employee){
                    if(this.collisionBetween((Employee)point,(Employee)target)){
                        ((Circle)point.view).setFill(Color.GREEN);
                        ((Circle)target.view).setFill(Color.GREEN);
                    }else{
                        ((Circle)point.view).setFill(Color.LIGHTBLUE);
                        ((Circle)target.view).setFill(Color.LIGHTBLUE);
                    }
                //}
            }
        }
        if(this.divided){
            for(i = 0; i < childLen; i++){
                this.children[i].checkCollisions();
            }
        }

    }

    private boolean collisionBetween(Employee a, Employee b){
        double dis = Math.sqrt(Math.pow(a.view.getLayoutX() - b.view.getLayoutX(),2) +
                Math.pow(a.view.getLayoutY() - b.view.getLayoutY(),2));
        return dis < ((Circle)(a.view)).getRadius() + ((Circle)(b.view)).getRadius();
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