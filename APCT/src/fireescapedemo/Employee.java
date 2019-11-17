package fireescapedemo;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;

public class Employee extends Actor implements Serializable{
    private double counter = 0, range = 0;
    private transient ArrayList<Pair<Point2D,Tile>> path;
    private boolean findingPath, exited;
    Pair<Point2D,Tile> curPoint,postPoint;
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
                employee.setrange(employee.tools.pathFinder.getRange());
                employee.curPoint = employee.getPath().get(0);
                employee.setCurPoint();
            }
        },
        Escape {
            @Override
            public void act(Employee employee, Floor floor) {
                System.out.println("Now escaping");

                //if(employee.getPath() != null){
                    employee.updatePathPoint();
                //}
            }
        },

        Extinguish{
            @Override
            public void act(Employee employee, Floor floor) {

            }
        };

        public abstract void act(Employee employee, Floor floor);
    }

    private void setCurPoint() {
        this.postPoint = this.curPoint;
        System.out.println("Test 3: " + this.path.isEmpty());
        this.curPoint = this.path.remove(0);
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
                    this.currentState.act(this,floor);
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
        }else{
            this.view.setOpacity(0.5);
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
    public void setrange(double vel){this.range = vel;}
    public void toggleExited(){this.exited = !exited;}
    public boolean hasExited(){return this.exited;}

    void updatePathPoint(){
        double linePathValue = findLineAndRotate(curPoint.getValue());
        System.out.println("Line path value: " + linePathValue);
        if(linePathValue < 1){
            System.out.println("Test 1: " + this.path.isEmpty());
            if(this.path.isEmpty()){
                this.exited = true;
            }else{
                System.out.println("Test 2: " + this.path.isEmpty());
                this.setCurPoint();
            }
        }


    }


    double findLineAndRotate(Tile end){
        //line of sight to end node
        double xThreshold = this.curPoint.getKey().getX();
        double yThreshold = this.curPoint.getKey().getY();
        double startX = this.view.getLayoutX(), endX = end.getActualX() + (end.getWidth()/2) ,
                startY=  this.view.getLayoutY(), endY =  end.getActualY() + (end.getHeight()/2);
        /*double magStart = Math.sqrt(Math.pow(startX,2) + Math.pow(startY,2));
        double magEnd = Math.sqrt(Math.pow(endX,2) + Math.pow(endY,2));
        double dotProd = (startX * endX) + (startY * endY);
        double crossProd = (startX + xThreshold - startX)*(endY - startY)-(endX - startX)*(startY + yThreshold - startY);
        double theta = Math.acos(dotProd / (magStart * magEnd));
        if(crossProd > 0 ){theta = -theta;}
        this.view.setRotate(theta);
        *///System.out.println("Theta: " + theta);
        double mX = endX - startX ;
        double mY = endY - startY;
        if(mX == mY){
            mX = mX < 0 ? -1 : 1;
            mY = mY < 0 ? -1 : 1;

        }else{
            //set x velocity
            if(mX ==0){ mX = 0; }
            else{ mX= startX <= endX ? startX / endX : -(endX / startX); }


            if(mY ==0 ){ mY = 0; }
            else{ mY= startY <= endY ? startY / endY : -(endY / startY); }
        }



        System.out.println("mX: " + mX + ", mY: " + mY);


        this.velocity = new Point2D(mX, mY);
        return Math.sqrt(Math.pow(endX - startX,2) + Math.pow(endY - startY,2));
    }
}
