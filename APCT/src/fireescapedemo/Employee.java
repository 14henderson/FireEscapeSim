package fireescapedemo;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;

public class Employee extends Actor implements Serializable{
    private double counter = 0, range = 0;
    private transient ArrayList<Pair<Point2D,Tile>> path;
    private boolean findingPath, exited, avgMove;
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
                employee.tools = new SystemTools(employee.oriTile,floor.getCurrentFloorBlock(),floor.getWallsNodes());
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

                //if(employee.getPath() != null){
                    employee.updatePathPoint();
                    Tile t = employee.curPoint.getValue();
                    Circle view = (Circle)employee.fxNode;
                    Circle c = new Circle(t.getActualX() + (t.getWidth()/2), t.getActualY() + (t.getHeight()/2),2, Color.PINK);
                    Circle cc = new Circle( view.getLayoutX(), view.getLayoutY(),2, Color.PINK);
                    Building.windowContainer.getChildren().add(c);
                    Building.windowContainer.getChildren().add(cc);
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
        System.out.println("new point X:" + this.curPoint.getValue().getActualX() + ", y: " + this.curPoint.getValue().getActualY());
        //this.velocity = this.curPoint.getKey();
    }

    private State currentState;

    public Employee(Node view, Tile tile)
    {
        super (view, tile);
        this.currentState = State.Idle;
        this.findingPath = false;
        this.exited = false;
        this.avgMove = false;
        this.oriTile = tile;

    }

    public Employee(Node view, Tile tile, Point2D vector){
        super(view,tile,new Point2D(0,0));
        this.currentState = State.Idle;
        this.findingPath = false;
        this.exited = false;
        this.avgMove = false;
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
                    this.fxNode.setLayoutX(this.fxNode.getLayoutX() + this.velocity.getX());
                    this.fxNode.setLayoutY(this.fxNode.getLayoutY() + this.velocity.getY());
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



        counter++;

        }else{
            this.fxNode.setOpacity(0);
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
    public void toggleExited(){this.exited = !this.exited;}
    public void setAvgMove(boolean b){this.avgMove = b;}
    public boolean hasExited(){return this.exited;}

    void updatePathPoint(){
        double linePathValue = findLineAndRotate(curPoint);
       // System.out.println("Linepathvale: " + linePathValue);
        if(linePathValue <= 15){
            System.out.println("NOICE");
            if(this.path.isEmpty()){
                this.exited = true;
            }else{
                this.setCurPoint();
            }
        }


    }
    public double getSize(){
        return ((Circle)(this.fxNode)).getRadius();
    }

    private void setRotation(double rot){
        this.fxNode.setRotate(rot);
    }



    private double pow2(double d){return d*d;}

    double findLineAndRotate(Pair<Point2D,Tile> pair){
        Point2D vel = pair.getKey();
        Tile target = pair.getValue();
        /*
        double startX = this.fxNode.getLayoutX(), endX = target.getActualX() + (target.getWidth()/2) ,
                startY=  this.fxNode.getLayoutY(), endY =  target.getActualY() + (target.getHeight()/2);
        System.out.println("Start x : " + startX + " - Start y: " + startY + "\n End x: " + endX + " - End y: " + endY);
        /*double numarator = startX * endX + startY + endY;
        double u = Math.sqrt(pow2(startX)+pow2(endX));
        double v = Math.sqrt(pow2(startY)+pow2(endY));
        double angle = Math.cos(numarator/u*v);

        //test
        double angle = Math.atan2(startY - endY, startX - endX) - Math.PI / 2;

        double bX = startX + pow2(this.velocity.getX());
        double bY = startY + pow2(this.velocity.getY());
        double twoArea = ((bX - startX)*(endY-startY))-((endX-startX)*(bY-startY));
        boolean reverse = twoArea < 0 ? false : true;

        setRotation(angle);

*/
        double startX = this.fxNode.getLayoutX(), startY = this.fxNode.getLayoutY(),
        endX = (target.getActualX() + (target.getWidth()/2)), endY = (target.getActualY() + (target.getHeight()/2));
        double dx = endX - startX;
        double dy = endY - startY;
        double angle = Math.atan2(dy, dx) - Math.PI / 2;
        Point2D direction = new Point2D(dx,dy);
        setRotation(angle);
        this.velocity = direction.normalize();
        return Math.sqrt(Math.pow(startX - endX,2) + Math.pow(startY - endY,2));
    }
}
