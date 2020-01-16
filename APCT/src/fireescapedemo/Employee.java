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
import java.util.Random;

public class Employee extends Actor implements Serializable{
    private double counter = 0, range = 0, releaseTime;
    private transient ArrayList<Tile> path;
    private boolean findingPath, exited, stairs;
    Tile curPoint,postPoint;
    public Tile proTile;

    enum State {
        Idle {
            @Override
            public void act(Employee employee, Floor floor) {

            }
        },
        FindRoute {
            @Override
            public void act(Employee employee, Floor floor) {
                System.out.println("\n\nContains exit: " + floor.containsExit());
                
                System.out.println("x: " + employee.proTile.getActualX() + ", y: " + employee.proTile.getActualY());
                SystemTools tools = new SystemTools(employee.proTile, floor.getCurrentFloorBlock(), floor.getWallsNodes());
                System.out.println("Route found");
                boolean stateSwitch = tools.pathFinder.exitFound();
                if (stateSwitch) {
                    tools.pathFinder.findPath(false);
                    employee.setPath(tools.pathFinder.getPath());
                }
                State state = stateSwitch ? State.Escape : State.Idle;

                System.out.println("Now set to " + state.toString());
                if (state.equals(State.Escape)) {
                    employee.proTile = employee.oriTile;
                    employee.findingPath = false;
                    employee.setrange(tools.pathFinder.getRange());
                    employee.curPoint = employee.getPath().get(0);
                    employee.setCurPoint();
                    employee.setReleaseTime(1);

                }

                employee.setCurrentState(state);
            }
        },
        Escape {
            @Override
            public void act(Employee employee, Floor floor) {

                //if(employee.getPath() != null){
                employee.updatePathPoint();
                //Circle view = (Circle)employee.fxNode;
                //Circle cc = new Circle( view.getLayoutX(), view.getLayoutY(),2, Color.RED);
                //Building.windowContainer.getChildren().add(cc);
                //}
            }
        },

        Extinguish {
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
        System.out.println("new point X:" + this.curPoint.getActualX() + ", y: " + this.curPoint.getActualY());
        //this.velocity = this.curPoint.getKey();
    }

    private State currentState;

    public Employee(Node view, Tile tile)
    {
        super (view, tile);
        this.currentState = State.Idle;
        this.proTile = this.oriTile;
        this.findingPath = false;
        this.exited = false;
        this.stairs = false;
        this.oriTile = tile;

    }

    public Employee(Node view, Tile tile, Point2D vector){
        super(view,tile,new Point2D(0,0));
        this.currentState = State.Idle;
        this.proTile = this.oriTile;
        this.findingPath = false;
        this.exited = false;
        this.stairs = false;
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
                    if(this.counter >= this.releaseTime) {
                        this.currentState.act(this, floor);
                        this.fxNode.setLayoutX(this.fxNode.getLayoutX() + this.velocity.getX());
                        this.fxNode.setLayoutY(this.fxNode.getLayoutY() + this.velocity.getY());
                    }else{this.counter++;}

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



    public ArrayList<Tile> getPath(){return this.path;}

    public void setCurrentState(State state){this.currentState = state;}
    public void setPath(ArrayList<Tile> newPath){this.path = newPath;}
    public void setrange(double vel){this.range = vel;}
    public void setReleaseTime(double d){this.releaseTime = d; this.counter = 0; }
    public void toggleExited(){this.exited = !this.exited;}
    public boolean hasExited(){return this.exited;}

    void updatePathPoint(){
        double linePathValue = findLineAndRotate();
       // System.out.println("Linepathvale: " + linePathValue);

        //System.out.println(((Circle)this.fxNode).getRadius());
        if(linePathValue <= this.curPoint.getSize()/2){
            if(this.path.isEmpty()){
                if(this.curPoint.type.equals(Tile.BlockType.Stairs)){

                    int id = 0;
                    for(Staircase s : curPoint.getFloor().getBuilding().getStairs().values()){
                        if(s.parent == curPoint){
                           id = s.ID;
                        }
                    }
                     //= this.curPoint.getID();
                    int jId = Building.mainBuilding.getStairs().get(id).joinedID;
                    this.curPoint = (Building.mainBuilding.getStairs().get(jId)).getParent();

                    System.out.println("id: " + id + ", jId: " + jId);
                    Floor prevFloor = this.curPoint.getFloor();
                    Floor curFloor = this.curPoint.getFloor();
                    prevFloor.removeEmployee(this);
                    curFloor.addEmployee(this);
                    prevFloor.floorPane.getChildren().remove(this.fxNode);
                    this.fxNode.setLayoutX(this.curPoint.getActualX() + this.curPoint.getSize()/2);
                    this.fxNode.setLayoutY(this.curPoint.getActualY() + this.curPoint.getSize()/2);
                    curFloor.floorPane.getChildren().add(this.fxNode);
                    //curFloor.floorPane.getChildren().add(new Rectangle(20,20,20,20));
                    this.stairs = true;
                    this.proTile = this.curPoint;
                    this.currentState = State.FindRoute;
                }else{
                    this.exited = true;
                }
            }else{
                this.setCurPoint();
            }
        }


    }
    public double getSize(){
        return ((Circle)(this.fxNode)).getRadius();
    }





    private double pow2(double d){return d*d;}

    public double findLineAndRotate(){
        Tile target = this.curPoint;
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

        this.fxNode.setRotate(angle);
        this.velocity = direction.normalize().multiply(0.5);

        return Math.sqrt(Math.pow(startX - endX,2) + Math.pow(startY - endY,2));
    }
}
