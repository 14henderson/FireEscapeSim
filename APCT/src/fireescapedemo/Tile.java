package fireescapedemo;

import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;

public class Tile extends MapObject implements Serializable {
  //  public transient Pane container;
    //public transient Rectangle block;
    public boolean[] walls;
    private Actor currentActor;
    //private transient Color color;
    public final int gridX, gridY;
    public final double width, height;
    enum BlockType{
        Office {
            @Override
            public void render() {
                System.out.println("I'll render the office wall bois");
            }

        },
        Stairs {
            @Override
            public void render() {
                System.out.println("Oooh I'll render some cheeky stair boyos");
            }
        },
        Employee {
            @Override
            public void render() {
                for(Floor floor : mainBuilding.getFloors()){
                    floor.addEmployee(new Actor(new Circle(50)));
                    floor.addEmployee(new Actor(new Circle(50)));
                    System.out.println(floor.employees.size());
                    System.out.println("Oh oh and I, I am an employee");
                }
            }
        };
        
        public abstract void render();
    }
    
    public BlockType type;


    public Tile(int x, int y, double size){
        this.type = null;
        this.gridX = x;
        this.gridY = y;
        this.width = size;
        this.height = size;
        render();

        this.currentActor = null;
    }

    @Override
    public void render(){
        Pane container = new Pane();
        container.setLayoutX(0);
        container.setLayoutY(0);

        container.setPadding(new Insets(0));

        Rectangle block = new Rectangle(this.gridX, this.gridY, this.width, this.height);
        block.setFill(Color.WHITESMOKE);
        block.setStroke(Color.BLACK);
        block.toFront();

        block.setOnMouseClicked((MouseEvent event) -> {
            this.type = FXMLBuildingController.getActionType();
            if(canEdit) {
                Color tileColor;
                switch (this.type){
                    case Employee: tileColor = Color.web(getColor("RED"));break;
                    case Office: tileColor = Color.web(getColor("GREY"));break;
                    case Stairs: tileColor = Color.web(getColor("AQUAMARINE"));break;
                    default: tileColor = Color.web(getColor("WHITESMOKE"));break;
                }
                block.setFill(tileColor);
                block.setOpacity(0.5);
                this.printType();
            }
        });
        mainBuilding.windowContainer.getChildren().add(container);
        container.getChildren().add(block);
        block.toFront();
        this.walls = new boolean[4];
        for(Boolean b : this.walls){
            b = true;
        }
    }







    private void initBlock(double x, double y,double size){

    }


    public boolean containsActor(){return this.currentActor != null;}

    public boolean setActor(Actor a){
        if(canEdit && this.containsActor()){
            this.currentActor = a;
            return true;
        }
        return false;
    }

    /**
     * Check if actor has access to a certain direction
     *
     * @param dir   Direction player is to move:
     *              0:  Checks Up
     *              1:  Checks Right
     *              2:  Checks Down
     *              3:  Checks Left
     */
    public boolean getAccess(int dir){ return this.walls[dir]; }

    public void removeAccess(int dir){ this.walls[dir] = false;}

    public void setWalls(){

    }
    //public void setColor(Color c){this.color = c;}
    
    public void removeActor(){this.currentActor = null;}
    
    public Actor getActor(){return this.currentActor;}
    
    public void printType(){
        String addOn;
        if(this.type == null){
            addOn = "NO TYPE";
        }else{
            addOn = this.type.toString();
        }
        System.out.println("Type " + addOn);
    }
}
