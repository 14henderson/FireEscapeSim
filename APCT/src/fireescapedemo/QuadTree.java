package fireescapedemo;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import javafx.geometry.Point2D;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

import static java.lang.Double.NaN;

public class QuadTree{
    private final int cap;
    private final double x,y,w,h;
    private int iter;
    private boolean divided;
    private final Actor[] points;
    private final QuadTree[] children;
    private static ArrayList<Line> walls;


    public QuadTree(int cap, double x, double y, double w, double h){
        this.cap = cap;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.iter = 1;
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
        this.iter = 1;
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
        Actor point, target;
        Employee p,t;
        double size,distance, displacement,px,py,tx,ty,rad1,rad2, moveX, moveY;
        //apply collisions localy
        for(i = 0; i < this.iter; i++){
            point = this.points[i];

            for(j = 0; j < this.iter; j++){
                target = this.points[j];
                if(i == j){continue;}
                if(point instanceof Employee && target instanceof Employee){
                    p = (Employee)point;
                    t = (Employee)target;
                    if(!p.hasExited() && !t.hasExited()){
                        px = p.fxNode.getLayoutX();py = p.fxNode.getLayoutY();
                        tx = t.fxNode.getLayoutX();ty = t.fxNode.getLayoutY();
                        rad1 = ((Circle)p.fxNode).getRadius();rad2 = ((Circle)t.fxNode).getRadius();
                        if(px==tx && py==ty){continue;}
                        if(Math.pow(px-tx,2)+Math.pow(py-ty,2) <= Math.pow(rad1+rad2,2)){
                            distance =  Math.sqrt(Math.pow(px - tx,2) + Math.pow(py - ty,2));
                            System.out.println(distance);
                            displacement = (distance- rad1 - rad2)/2;
                            double dx = px == tx ? 1 : px-tx;
                            double dy = py == ty ? 1: py-ty;
                            moveX = px - displacement * (px-tx)/distance;
                            moveY = py - displacement * (py-ty)/distance;

                            System.out.println("px: " + px + ", py: " + py + "/tx: " + tx + ", ty: " + ty);
                            p.fxNode.setLayoutX(moveX);
                            p.fxNode.setLayoutY(moveY);
                            p.findLineAndRotate();

                            moveX = tx + displacement * (px-tx)/distance;
                            moveY = ty + displacement * (py-ty)/distance;

                            t.fxNode.setLayoutX(moveX);
                            t.fxNode.setLayoutY(moveY);

                            System.out.println("new px: " + p.fxNode.getLayoutX() + ", py: " + p.fxNode.getLayoutY() + "/tx: " + t.fxNode.getLayoutX() + ", ty: " + t.fxNode.getLayoutY() + "\n");
                            t.findLineAndRotate();
                            //((Circle)point.fxNode).setFill(Color.GREEN);
                            //((Circle)target.fxNode).setFill(Color.GREEN);
                        }else{
                            //((Circle)point.fxNode).setFill(Color.LIGHTBLUE);
                           // ((Circle)target.fxNode).setFill(Color.LIGHTBLUE);
                        }
                    }
                }
            }
        }
        if(this.divided){
            for(i = 0; i < childLen; i++){
                this.children[i].checkCollisions();
            }
        }
        boolean work_properly = true;

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