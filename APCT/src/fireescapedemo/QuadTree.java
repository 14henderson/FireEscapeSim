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

    private void createChildren(){
        //northwest
        QuadTree northwest = new QuadTree(this.cap,this.x + this.w/2,this.y,this.w/2,this.h/2);
        //northeast
        QuadTree northeast = new  QuadTree(this.cap, this.x,this.y,this.h/2,this.w/2);
        //southwest
        QuadTree southwest = new QuadTree(this.cap, this.x, this.y - this.h/2,this.w/2,this.h/2);
        //southeast (represent)
        QuadTree southeast = new QuadTree(this.cap,this.x  + this.w/2,this.y - this.h/2,this.w/2,this.h/2);
        this.children[0] = northeast;
        this.children[1] = northwest;
        this.children[2] = southwest;
        this.children[3] = southeast;
    }

    public boolean contains(Actor a){
        double aX = a.view.getLayoutX(), aY =  a.view.getLayoutX();
        return aX > this.x && aX < this.x+this.w && aY > this.y && aY < this.x+this.h;
    }

    public void insert(Actor a){
        if(this.iter+1 <= this.cap){
            this.points[this.iter] = a;
            System.out.println("Child added");
            iter++;
        }else{
            if(!this.divided){
                this.createChildren();
                System.out.println("Split created");
                this.divided = true;
            }
            int i = 0, len = this.children.length;
            //for each child, if the actor is within the bounds, run childs insert method
            for(i  = 0; i < len; i++){ if(this.children[i].contains(a)){ this.children[i].insert(a);} }
        }
    }
    public void checkCollisions(){
        int i,j, pointLen = this.points.length, childLen = this.children.length;
        Actor point, target;
        //apply collisions localy
        for(i = 0; i < pointLen; i++){

            point = this.points[i];

            for(j = 0; j < pointLen; j++){
                target = this.points[j];
                if(point == null || target == null){continue;}
                if(point instanceof Employee && target instanceof Employee){
                    if(collisionBetween((Employee)point,(Employee)target)){
                        ((Circle)point.view).setFill(Color.GREEN);
                        ((Circle)target.view).setFill(Color.GREEN);
                    }else{
                            ((Circle)point.view).setFill(Color.LIGHTBLUE);
                            ((Circle)target.view).setFill(Color.LIGHTBLUE);
                    }
                }
            }
        }
        if(this.divided){
            for(i = 0; i < childLen; i++){
                this.children[i].checkCollisions();
            }
        }

    }

    private boolean collisionBetween(Employee a, Employee b){
        return ((((a.view.getLayoutX()-b.view.getLayoutX())*2) + ((a.view.getLayoutY()-b.view.getLayoutY())*2)) <
                ((Circle)(a.view)).getRadius() + ((Circle)(b.view)).getRadius());
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


    public void drawLines(Pane floor){
        Rectangle rec = new Rectangle(this.x,this.y,this.w,this.h);
        rec.setStroke(Color.TRANSPARENT);
        rec.setStroke(Color.ORANGE);
        rec.setOpacity(0.01);
        floor.getChildren().add(rec);
        if(this.divided){
            int i, len = this.children.length;
            for(i = 0; i < len; i++){
                this.children[i].drawLines(floor);
            }
        }
    }


}