package fireescapedemo;

import javafx.geometry.Point2D;
import javafx.scene.Node;

import java.io.Serializable;
import java.util.ArrayList;

public class Employee extends Actor implements Serializable{
    private int counter = 0;
    private transient ArrayList<Point2D> path;
    private boolean findingPath, exited;
    public Tile proTile;
    enum State{
        Idle {
            @Override
            public void act(Employee employee, Floor floor) {

            }
        },
        FindRoute{
            @Override
            public void act(Employee employee, Floor floor){
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
            public void act(Employee employee, Floor floor) {
                System.out.println("Now escaping");
                Point2D vel = new Point2D(0,0);
                for(Point2D p : employee.getPath()){
                    System.out.println(p);
                }
                if(employee.getPath().isEmpty() ){
                    employee.exited = true;
                }
                else if(employee.getPath() != null){
                    vel = employee.getPath().remove(0);
                }
                employee.setVelocity(vel);
            }
        },

        Extinguish{
            @Override
            public void act(Employee employee, Floor floor) {

            }
        };

        public abstract void act(Employee employee, Floor floor);
    }
    private State currentState;

    public Employee(Node view, Tile tile)
    {
        super (view, tile);
        this.currentState = State.Idle;
        this.findingPath = false;
        this.exited = false;
        this.oriTile = tile;
    }

    public Employee(Node view, Tile tile, Point2D vector){
        super(view,tile,vector);
        this.currentState = State.Idle;
        this.findingPath = false;
        this.exited = false;
        this.oriTile = tile;
    }


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

        this.view.setLayoutX(view.getLayoutX() + velocity.getX());
        this.view.setLayoutY(view.getLayoutY() + velocity.getY());
        counter++;
        }else{
            this.view.setOpacity(0);
        }
    }

    public ArrayList<Point2D> getPath(){return this.path;}

    public void setCurrentState(State state){this.currentState = state;}
    public void setPath(ArrayList<Point2D> newPath){this.path = newPath;}

    public boolean hasExited(){return this.exited;}

}
