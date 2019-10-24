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
    private boolean findingPath;
    private static int idCounter = 0;
    enum State{
        Idle {
            @Override
            public void act(Actor employee, Floor floor, boolean s) {

            }
        },
        FindRoute{
          @Override
          public void act(Actor employee, Floor floor, boolean s){

              System.out.println("Route found");
                s = false;
          }
        },
        Escape {
            @Override
            public void act(Actor employee, Floor floor, boolean s) {

            }
        },

        Extinguish{
            @Override
            public void act(Actor employee, Floor floor, boolean s) {

            }
        };

        public abstract void act(Actor employee, Floor floor, boolean s);
    }
    private State currentState;
    public Actor(Node view){
        this.view = view;
        this.velocity = new Point2D(0,0);
        this.swap = false;
        this.id =idCounter;
        this.currentState = State.Idle;
        this.findingPath = false;
        idCounter++;
    } 
    
    public Actor(Node view, Point2D vector){
        this.view = view;
        this.velocity = vector;
        this.swap = false;
        this.id =idCounter;
        idCounter++;
    } 
    
    public void update(Floor floor){
        switch(this.currentState){
            case Idle:{
                this.currentState.act(this,floor,false);
                break;
            }
            case FindRoute:{
                if(!this.findingPath){
                    findingPath = true;
                    this.currentState.act(this,floor,this.findingPath);
                }
                break;
            }
            case Escape:{
                this.currentState.act(this,floor,false);
                break;
            }
            case Extinguish:{
                this.currentState.act(this,floor,false);
                break;
            }
            default:{
                System.out.println("Nothing matched");
                break;
            }
        }
        this.view.setLayoutX(view.getLayoutX() + velocity.getX());
        this.view.setLayoutY(view.getLayoutY() + velocity.getY());
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

