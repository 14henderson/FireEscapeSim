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
    Node view;
    boolean swap;
    final int id;
    private boolean findingPath, exited;
    private static int idCounter = 0;
    private SystemTools tools;
    public Tile oriTile, proTile, prevTile;

    private ArrayList<Point2D> path;
    enum State{
        Idle {
            @Override
            public void act(Actor employee, Floor floor) {

            }
        },
        FindRoute{
          @Override
          public void act(Actor employee, Floor floor){
              SystemTools tools = new SystemTools(employee.oriTile,floor.getTestFirstExit(),floor.getCurrentFloorBlock());
              System.out.println("Route found");
              boolean stateSwitch = tools.pathFinder.findPath();
              if(stateSwitch){ employee.setPath(tools.pathFinder.getVelocities()); }
              State state = stateSwitch? State.Escape : State.Idle;
              System.out.println("Now set to " + state.toString());
              employee.proTile = employee.oriTile;
              employee.setCurrentState(state);
              employee.findingPath = false;
          }
        },
        Escape {
            @Override
            public void act(Actor employee, Floor floor) {
                System.out.println("Now escaping");
                Point2D vel = new Point2D(0,0);
                for(Point2D p : employee.getPath()){
                    System.out.println(p);
                }
                if(employee.getPath() != null){
                    employee.exited = true;
                }
                else if(!employee.getPath().isEmpty() ){
                    vel = employee.getPath().remove(0);
                }
                employee.setVelocity(vel);
            }
        },

        Extinguish{
            @Override
            public void act(Actor employee, Floor floor) {

            }
        };

        public abstract void act(Actor employee, Floor floor);
    }
    private State currentState;

    public Actor(Node view, Tile tile,State state){
        this.view = view;
        this.velocity = new Point2D(0,0);
        this.swap = false;
        this.id =idCounter;
        this.currentState = state;
        this.findingPath = false;
        this.exited = false;
        this.oriTile = tile;
        idCounter++;
    } 
    
    public Actor(Node view, Tile tile, Point2D vector){
        this.view = view;
        this.velocity = vector;
        this.swap = false;
        this.id =idCounter;
        this.currentState = State.Idle;
        this.findingPath = false;
        this.exited = false;
        this.oriTile = tile;
        idCounter++;
    } 
    public int counter = 0;
    public void update(Floor floor){
        if(!this.exited){
            switch(this.currentState){
                case Idle:{
                    this.currentState.act(this,floor);
                    break;
                }
                case FindRoute:{
                    if(!this.findingPath){
                        this.findingPath = true;
                        this.currentState.act(this,floor);

                    }
                    break;
                }
                case Escape:{
                    if(this.counter >= 50){
                        this.findingPath = false;
                        this.counter = 0;
                    }
                    if(!this.findingPath){
                        this.findingPath = true;
                        this.currentState.act(this,floor);
                    }
                    break;
                }
                case Extinguish:{
                    this.currentState.act(this,floor);
                    break;
                }
                default:{
                    System.out.println("Nothing matched");
                    break;
                }
            }
        }
        this.view.setLayoutX(view.getLayoutX() + velocity.getX());
        this.view.setLayoutY(view.getLayoutY() + velocity.getY());
        counter++;
    }
    
    public Node getView(){return this.view;}
    public int getId(){return this.id;}
    public Point2D getVelocity() {return this.velocity;}
    public boolean getSwap(){return this.swap;}
    public ArrayList<Point2D> getPath(){return this.path;}
    
    public void setVelocity(Point2D newVelocity){this.velocity = newVelocity;}
    public void setCurrentState(State state){this.currentState = state;}
    public void setVelocityX(double x) {this.velocity = new Point2D(x,this.velocity.getY());}
    public void setVelocityY(double y) {this.velocity = new Point2D(this.velocity.getX(),y);}
    public void setSwap(boolean b){ this.swap = b;}
    public void toggleSwap(){this.swap = !this.swap;}
    public void setPath(ArrayList<Point2D> newPath){this.path = newPath;}


    public String printVelocity(){
        return "Velocity Vector: [X: " + this.velocity.getX() + ", Y: " + this.velocity.getY() + "]\n";
    }
    
}

