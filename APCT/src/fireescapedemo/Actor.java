/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fireescapedemo;

import javafx.geometry.Point2D;
import javafx.scene.Node;

import java.awt.*;
import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 *
 * @author Leem
 */
public class Actor {
    Point2D velocity;
    Node view;public
    Tile oriTile;
    boolean swap;

    public Actor(Node view, Tile tile){
        this.view = view;
        this.oriTile = tile;
        this.velocity = new Point2D(0,0);
        this.swap = false;
    } 
    
    public Actor(Node view, Tile tile, Point2D vector){
        this.view = view;
        this.oriTile = tile;
        this.velocity = vector;
        this.swap = false;
    } 
    public int counter = 0;
    public void update(Floor floor){
        this.view.setLayoutX(view.getLayoutX() + velocity.getX());
        this.view.setLayoutY(view.getLayoutY() + velocity.getY());
    }
    
    public Node getView(){return this.view;}
    public Point2D getVelocity() {return this.velocity;}
    public boolean getSwap(){return this.swap;}
    
    public void setVelocity(Point2D newVelocity){this.velocity = newVelocity;}
    public void setVelocityX(double x) {this.velocity = new Point2D(x,this.velocity.getY());}
    public void setVelocityY(double y) {this.velocity = new Point2D(this.velocity.getX(),y);}
    public void setSwap(boolean b){ this.swap = b;}
    public void toggleSwap(){this.swap = !this.swap;}


    public String printVelocity(){
        return "Velocity Vector: [X: " + this.velocity.getX() + ", Y: " + this.velocity.getY() + "]\n";
    }
    
}

