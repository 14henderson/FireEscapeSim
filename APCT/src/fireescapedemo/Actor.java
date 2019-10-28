/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fireescapedemo;

import javafx.geometry.Point2D;
import javafx.scene.Node;

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
    private boolean findingPath;
    private static int idCounter = 0;
    private SystemTools tools;
    public Tile oriTile, proTile, prevTile;
    private PriorityQueue<Tile> path;
    enum State{
        Idle {
            @Override
            public void act(Actor employee, Floor floor, boolean s) {

            }
        },
        FindRoute{
          @Override
          public void act(Actor employee, Floor floor, boolean s){
              SystemTools tools = new SystemTools(employee.oriTile,floor.getTestFirstExit(),floor.getCurrentFloorBlock());
              System.out.println("Route found");
              boolean stateSwitch = tools.pathFinder.findPath();
              if(stateSwitch){ employee.setPath(tools.pathFinder.getPath()); }
              State state = stateSwitch? State.Escape : State.Idle;
              System.out.println("Now set to " + state.toString());
              employee.setCurrentState(state);
              s = false;
          }
        },
        Escape {
            @Override
            public void act(Actor employee, Floor floor, boolean s) {
                System.out.println("Now escaping");
                if(employee.getPath() != null){
                    employee.prevTile = employee.proTile;
                    employee.proTile = employee.getPath().poll();
                    
                }
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

    public Actor(Node view, Tile tile,State state){
        this.view = view;
        this.velocity = new Point2D(0,0);
        this.swap = false;
        this.id =idCounter;
        this.currentState = state;
        this.findingPath = false;
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
        this.oriTile = tile;
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
                    this.findingPath = true;
                    this.currentState.act(this,floor,this.findingPath);

                }
                break;
            }
            case Escape:{
                if(!this.findingPath){
                    this.currentState.act(this,floor,false);
                    this.findingPath = false;
                }
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
    public PriorityQueue<Tile> getPath(){return this.path;}
    
    public void setVelocity(Point2D newVelocity){this.velocity = newVelocity;}
    public void setCurrentState(State state){this.currentState = state;}
    public void setVelocityX(double x) {this.velocity = new Point2D(x,this.velocity.getY());}
    public void setVelocityY(double y) {this.velocity = new Point2D(this.velocity.getX(),y);}
    public void setSwap(boolean b){ this.swap = b;}
    public void toggleSwap(){this.swap = !this.swap;}
    public void setPath(PriorityQueue<Tile> newPath){this.path = newPath;}


    public String printVelocity(){
        return "Velocity Vector: [X: " + this.velocity.getX() + ", Y: " + this.velocity.getY() + "]\n";
    }
    
}

