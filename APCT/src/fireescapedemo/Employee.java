package fireescapedemo;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;

public class Employee extends Actor implements Serializable{
    private double counter = 0, velAmount = 0;
    private transient ArrayList<Pair<Point2D,Tile>> path;
    private boolean findingPath, exited;
    Pair<Point2D,Tile> curPoint;
    public Tile proTile;
    public SystemTools tools;

    enum State{
        Idle {
            @Override
            public void act(Employee employee, Floor floor) {

            }
        },
        FindRoute{
            @Override
            public void act(Employee employee, Floor floor){
                employee.tools = new SystemTools(employee.oriTile,floor.getCurrentFloorBlock());
                System.out.println("Route found");
                boolean stateSwitch = employee.tools.pathFinder.exitFound();
                if(stateSwitch){
                    employee.tools.pathFinder.findPath(false);
                    employee.setPath(employee.tools.pathFinder.getVelocities());
                }
                State state = stateSwitch? State.Escape : State.Idle;
                System.out.println("Now set to " + state.toString());
                employee.proTile = employee.oriTile;
                employee.setCurrentState(state);
                employee.findingPath = false;
                employee.setVelAmount(employee.tools.pathFinder.getRange());
                employee.setCurPoint();
            }
        },
        Escape {
            @Override
            public void act(Employee employee, Floor floor) {
                System.out.println("Now escaping");

                if(employee.getPath().isEmpty() ){
                    employee.exited = true;
                }
                else if(employee.getPath() != null){
                    employee.updatePathPoint();
                }
            }
        },

        Extinguish{
            @Override
            public void act(Employee employee, Floor floor) {

            }
        };

        public abstract void act(Employee employee, Floor floor);
    }

    private void setCurPoint() { this.curPoint = this.path.remove(0);}

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
                    //if(this.counter >= this.oriTile.getWidth()){
                      //  this.findingPath = false;
                     //   this.counter = 0;
                    //}
                    //if(!this.findingPath){
                   //     this.findingPath = true;
                    this.currentState.act(this,floor);
                    //}
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

            counter += velAmount;
        this.view.setLayoutX(view.getLayoutX() + velocity.getX());
        this.view.setLayoutY(view.getLayoutY() + velocity.getY());
        }else{
            this.view.setOpacity(0);
        }
    }



    @Override
    public String toString(){
        String a = "Length of path: "+this.path.size()+"\n";
        String b = "Finding path:"+this.findingPath+" Excited: "+this.exited+"\n";
        String c = "Tile Details: "+this.oriTile.toString()+"\n";
        String d = "Vel: "+this.velocity.toString()+"\n";
        return a+b+c+d;
    }



    public ArrayList<Pair<Point2D, Tile>> getPath(){return this.path;}

    public void setCurrentState(State state){this.currentState = state;}
    public void setPath(ArrayList<Pair<Point2D, Tile>> newPath){this.path = newPath;}
    public void setVelAmount(double vel){this.velAmount = vel;}
    public void toggleExited(){this.exited = !exited;}
    public boolean hasExited(){return this.exited;}

    void updatePathPoint(){
        double linePathValue = findLineAndRotate(curPoint.getValue());
        System.out.println("Line path value: " + linePathValue);
        if(linePathValue < 5){
            if(!this.path.isEmpty()){
                this.exited = true;
            }else{
                this.setCurPoint();
            }
        }


    }


    double findLineAndRotate(Tile end){
        //line of sight to end node
        double rotX = this.view.getLayoutX() - end.getGridX();
        double rotY = this.view.getLayoutY() - end.getGridY();
        double rot = Math.atan2(rotX, rotY);
        System.out.println("rot: " + rot);
        this.view.setRotate(rotX + rotY);
        //distance from line of site
        rotX = this.velocity.getX() - rot;
        rotY = this.velocity.getY() - rot;
        rotX = rot < 1 && rot > -1 ? this.curPoint.getKey().getX() : rotX;
        rotY = rot < 1 && rot > -1 ? this.curPoint.getKey().getY() : rotY;
        this.velocity = new Point2D(rotX, rotY);
        return Math.sqrt(this.view.getLayoutX() - end.getGridX() + this.view.getLayoutY() - end.getGridY());
    }
}
