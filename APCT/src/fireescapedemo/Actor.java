/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fireescapedemo;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.io.Serializable;





public class Actor extends TileObject implements Serializable {
    protected transient Point2D velocity;
    //protected transient Node view;
    boolean swap;
    //final int id;
    private static int idCounter = 0;
    public transient Tile oriTile;

    @Override
    public Node getNode(){
        return this.fxNode;
    }
    @Override
    public void setNode(Node n){
        this.fxNode = n;
    }
    @Override
    public void destroy(){}
    @Override
    public void updateView(){
        Circle tmp = (Circle)this.fxNode;
        tmp.setLayoutX(this.parent.getActualX()+(this.parent.getWidth()/2));
        tmp.setLayoutY(this.parent.getActualY()+(this.parent.getHeight()/2));
        tmp.setRadius((this.parent.getFloor().getBuilding().getActorSize()/2)*this.parent.getFloor().tileSize/50.0);
        System.out.println("Updating view!");
    }
    @Override
    public void initialiseView(){}

    public Actor(Node view){
        this.fxNode = view;
        //this.oriTile;// = tile;
        this.velocity = new Point2D(0,0);
        this.swap = false;
    }

    public Actor(Node view, Tile tile){
        this.fxNode = view;
        this.oriTile = tile;
        this.velocity = new Point2D(0,0);
        this.swap = false;
        this.parent = tile;
    }

    public Actor(Node view, Tile tile, Point2D vector){
        this.fxNode = view;
        this.oriTile = tile;
        this.velocity = vector;
        this.swap = false;
        this.parent = tile;
    } 

    
    public void update(){
        this.fxNode.setTranslateX(fxNode.getTranslateX() + velocity.getX());
        this.fxNode.setTranslateY(fxNode.getTranslateY() + velocity.getY());
    }
    
    public Node getView(){return this.fxNode;}
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

