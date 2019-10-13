/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fireescapedemo;

import javafx.geometry.Point2D;
import javafx.scene.Node;

/**
 *
 * @author Leem
 */
public class Actor {
    Point2D velocity;
    Node view;
    boolean swap;
    final int id;
    private static int idCounter = 0;
    
    public Actor(Node view){
        this.view = view;
        this.velocity = new Point2D(0,0);
        this.swap = false;
        this.id =idCounter;
        idCounter++;
    } 
    
    public Actor(Node view, Point2D vector){
        this.view = view;
        this.velocity = vector;
        this.swap = false;
        this.id =idCounter;
        idCounter++;
    } 
    
    public void update(){
        
        this.view.setTranslateX(view.getTranslateX() + velocity.getX());
        this.view.setTranslateY(view.getTranslateY() + velocity.getY());
    }
    
    public Node getView(){return this.view;}
    public int getId(){return this.id;}
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

