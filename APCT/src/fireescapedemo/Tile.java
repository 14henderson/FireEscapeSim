package fireescapedemo;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.net.URISyntaxException;

public class Tile implements Comparable<Tile>{
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
    private double gCost,fCost,hCost;
    private Tile perant;
    public int compareTo(Tile t) {
        if(this.getFCost() < t.getFCost()){
            return -1;
        }else if(this.getFCost() > t.getFCost()){
            return 1;
        }else {
            if(this.getHCost() < t.getHCost()){
                return -1;
            }else if(this.getHCost() < t.getHCost()){
                return 1;
            }
        }
        return 0;
    }

    public enum BlockType{
        Exit {
            @Override
            public void render(int index, double x, double y,Tile tile) {
                System.out.println("I'll render the Exit wall bois");
                Rectangle rec = new Rectangle(x,y,50,25);
                Image image;
                try {
                    image = new Image(getClass().getResource("/Assets/estExit.PNG").toURI().toString());
                    rec.setFill(new ImagePattern(image));
                    System.out.println("Complete");
                } catch (URISyntaxException ex) {
                    System.out.println(ex);
                }
                Exit exit = new Exit(rec,tile);
                mainBuilding.getFloor(index).addExit(exit);
            }

        },
        Stairs {
            @Override
            public void render(int index, double x, double y,Tile tile) {
                System.out.println("Oooh I'll render some cheeky stair boyos");
            }
        },
        Employee {
            @Override
            public void render(int index, double x, double y,Tile tile) {
                Actor a;
                Circle c;
                c = new Circle(10);
                c.setFill(Color.PINK);
                c.setLayoutX(x+25);
                c.setLayoutY(y+15);
                Image image;
                try {
                    image = new Image(getClass().getResource("/Assets/testEmployee.PNG").toURI().toString());
                    c.setFill(new ImagePattern(image));
                    System.out.println("Complete");
                } catch (URISyntaxException ex) {
                    System.out.println(ex);
                }
                mainBuilding.getFloor(index).addEmployee(new Employee(c,tile));
                c = new Circle(10);
                c.setFill(Color.PINK);
                c.setLayoutX(x+25);
                c.setLayoutY(y+35);
                try {
                    image = new Image(getClass().getResource("/Assets/testEmployee.PNG").toURI().toString());
                    c.setFill(new ImagePattern(image));
                    System.out.println("Complete");
                } catch (URISyntaxException ex) {
                    System.out.println(ex);
                }
                mainBuilding.getFloor(index).addEmployee(new Employee(c,tile));
                System.out.println(mainBuilding.getFloor(index).employees.size());
                System.out.println("Oh oh and I, I am an employee");

            }
        };
        
        public abstract void render(int index, double x, double y,Tile tile);
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
        this.hCost = 0;
        this.fCost = 0;
        this.perant = null;
        idCount++;

    }

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
        for(int i = 0; i < 4; i++){
            this.walls[i] = true;
        }
    }


    public boolean containsActor(){return this.currentActor != null;}

    public void setPerant(Tile t) {this.perant = t;}
    public void setGCost(double i) {this.gCost = i;}
    public void setHCost(double i) {this.hCost = i;}
    public void setColor(Color c){this.color = c;}
    public boolean setActor(Actor a){
        if(buildEnabled && this.containsActor()){
            this.currentActor = a;
            return true;
        }
        return false;
    }


    public final int getId(){return this.id;}
    public final boolean getAccess(int dir){ return this.walls[dir]; }
    public final Tile getPerant(){return this.perant;}
    public final double getFCost(){return this.fCost;}
    public final double getGCost(){return this.gCost;}
    public final double getHCost(){return this.hCost;}
    public final Actor getActor(){return this.currentActor;}

    /**
     * Check if actor has access to a certain direction
     *
     * @param dir   Direction player is to move:
     *              0:  Checks Up
     *              1:  Checks Right
     *              2:  Checks Down
     *              3:  Checks Left
     */
    public final void removeAccess(int dir){ this.walls[dir] = false;}
    public final void removeActor(){this.currentActor = null;}

    public static void toggleBuild() {buildEnabled = !buildEnabled;}
    public static boolean isBuildEnabled(){return buildEnabled;}

    public void calculateFCost(){ this.fCost = this.gCost + this.hCost;}
    public void computeDistance(Tile endNode) {
        this.hCost = Math.abs(this.gridX - endNode.gridX) + Math.abs(this.gridY - endNode.gridY);
    }
    
    public final void printType(){
        String addOn;
        if(this.type == null){
            addOn = "NO TYPE";
        }else{
            addOn = this.type.toString();
        }
        System.out.println("Type " + addOn);
    }
}
