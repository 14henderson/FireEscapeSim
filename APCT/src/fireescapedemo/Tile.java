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
    public boolean[] walls;
    private Actor currentActor;
    public final int[] gridCords = new int[2];
    public final int[] actualCords = new int[2];
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
        Default {
            @Override
            public void render() {
                System.out.println("This is the default");
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
        }}};
        public abstract void render();
    }
    public BlockType type;





    public Tile(int x, int y, double size){
        this.type = null;
        this.actualCords[0] = x;
        this.actualCords[1] = y;

        this.width = size;
        this.height = size;
        this.type = BlockType.Default;
        this.currentActor = null;
    }

    public BlockType getType(){return this.type;}
    public int[] getActualCords(){return this.actualCords;}
    public int[] getGridCords(){return this.gridCords;}


    @Override
    public void render(){
        Rectangle block = new Rectangle(this.actualCords[0], this.actualCords[1], this.width, this.height);
        block.setFill(Color.WHITESMOKE);
        block.setStroke(Color.BLACK);
        block.setOnMouseClicked((MouseEvent event) -> {
            this.type = FXMLBuildingController.getActionType();
            block.setFill(getColor(this.type));
            block.setOpacity(0.5);
        });
        block.setFill(getColor(this.type));
        block.setOpacity(0.5);
        mainBuilding.windowContainer.getChildren().add(block);
        block.toFront();
        this.walls = new boolean[4];
        for(Boolean b : this.walls){
            b = true;
    }}


    //Returns Colour of block depending on a type of block
    public Color getColor(BlockType type){
        Color tileColor;
        switch (this.type){
            case Employee: tileColor = Color.web(getColor("RED"));break;
            case Office: tileColor = Color.web(getColor("GREY"));break;
            case Stairs: tileColor = Color.web(getColor("AQUAMARINE"));break;
            default: tileColor = Color.web(getColor("WHITESMOKE"));break;
        }
        return tileColor;
    }


    public void removeAccess(int dir){ this.walls[dir] = false;}
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
    public boolean getAccess(int dir){ return this.walls[dir];}
    public void removeActor(){this.currentActor = null;}
    public Actor getActor(){return this.currentActor;}
    

}
