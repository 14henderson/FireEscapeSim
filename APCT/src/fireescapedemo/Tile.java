package fireescapedemo;

import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class Tile {
    private static Building mainBuilding;
    public Pane container;
    public Rectangle block;
    public boolean[] walls;
    private Actor currentActor;
    private Color color;
    private final int id;
    public final int gridX, gridY;
    private static int idCount = 0;
    private static boolean buildEnabled = true;
    enum BlockType{
        Exit {
            @Override
            public void render(int index, double x, double y) {
                System.out.println("I'll render the Exit wall bois");
            }

        },
        Stairs {
            @Override
            public void render(int index, double x, double y) {
                System.out.println("Oooh I'll render some cheeky stair boyos");
            }
        },
        Employee {
            @Override
            public void render(int index, double x, double y) {
                Actor a;
                Circle c;
                c = new Circle(10);
                c.setFill(Color.PINK);
                c.setLayoutX(x+25);
                c.setLayoutY(y+15);
                mainBuilding.getFloor(index).addEmployee(new Actor(c));
                c = new Circle(10);
                c.setFill(Color.PINK);
                c.setLayoutX(x+25);
                c.setLayoutY(y+35);
                mainBuilding.getFloor(index).addEmployee(new Actor(c));
                System.out.println(mainBuilding.getFloor(index).employees.size());
                System.out.println("Oh oh and I, I am an employee");

            }
        };
        
        public abstract void render(int index, double x, double y);
    }
    
    public BlockType type;


    public Tile(int x, int y, double size){
        mainBuilding = new Building();
        this.color = Color.WHITESMOKE;
        initBlock(x*size,y*size,size);
        this.gridX = x;
        this.gridY = y;
        this.currentActor = null;
        this.id = idCount;
        idCount++;

    }


    public static void enableBuild() {buildEnabled = true; }
    public static void disableBuild() {buildEnabled = false;}

    public static boolean isBuildEnabled(){return buildEnabled;}

    private void initBlock(double x, double y,double size){
        this.container = new Pane();
        this.container.setLayoutX(x);
        this.container.setLayoutY(y);
        this.container.setPadding(new Insets(0));
        this.block = new Rectangle(x,y,size,size);
        this.block.setFill(Color.WHITESMOKE);
        this.block.setStroke(Color.BLACK);
        this.type = null;
        this.block.setOnMouseClicked((MouseEvent event) -> {
                    if(buildEnabled) {
                        Color c = FXMLBuildingController.c;
                        if (!this.color.equals(c)) {
                            this.color = c;
                            if (this.color.equals(Color.RED)) {
                                this.type = BlockType.Employee;
                            } else if (c.equals(Color.GREY)) {
                                this.type = BlockType.Exit;
                            } else if (c.equals(Color.AQUAMARINE)) {
                                this.type = BlockType.Stairs;
                            }
                        } else {
                            this.color = Color.WHITESMOKE;
                            this.type = null;
                        }
                        this.block.setFill(this.color);
                        this.block.setOpacity(0.5);

                        this.printType();
                    }
        });
        this.container.getChildren().add(this.block);
        this.walls = new boolean[4];
        for(Boolean b : this.walls){
            b = true;
        }
    }


    public boolean containsActor(){return this.currentActor != null;}

    public final int getId(){return this.id;}
    
    public boolean setActor(Actor a){
        if(buildEnabled && this.containsActor()){
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
    public void setColor(Color c){this.color = c;}
    
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
