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
import java.io.Serializable;


/*
public class Tile implements {
    private static Building mainBuilding;
    public Pane container;
    public Rectangle block;
    public boolean[] walls;
    private Actor currentActor;
    private Color color;
    private Tile perant;

 */



public class Tile extends MapObject implements Serializable, Comparable<fireescapedemo.Tile> {
    public boolean[] walls;
    private Actor currentActor;
    public final int[] gridCords = new int[2];
    public final int[] actualCords = new int[2];
    public final double width, height;
    private double gCost,fCost,hCost;
    private static int idCount = 0;
    private static boolean buildEnabled = true;
    private int id;
    private Tile parent;
    private transient Rectangle fxRef = null;

    enum BlockType{
        Exit {
            @Override
            public void initialiseView(int index,Tile tile) {
                tile.fxRef = new Rectangle(tile.getActualX(), tile.getActualY(), tile.width, tile.height);
                tile.fxRef.setStroke(Color.BLACK);
                tile.fxRef.setFill(tile.getColor(tile.type));
                tile.fxRef.setOpacity(0.5);
                mainBuilding.windowContainer.getChildren().add(tile.fxRef);

                Rectangle rec = new Rectangle(tile.getActualX(),tile.getActualY(),50,25);
                Image image;
                try {
                    image = new Image(getClass().getResource("/Assets/estExit.PNG").toURI().toString());
                    rec.setFill(new ImagePattern(image));
                    mainBuilding.windowContainer.getChildren().add(rec);
                } catch (URISyntaxException ex) {
                    System.out.println(ex);
                }
                Exit exit = new Exit(rec,tile);

                mainBuilding.getFloors().get(index).addExit(exit);
            }
        },
        Stairs {
            @Override
            public void initialiseView(int index,Tile tile) {
                tile.fxRef = new Rectangle(tile.getActualX(), tile.getActualY(), tile.width, tile.height);
                tile.fxRef.setStroke(Color.BLACK);
                tile.fxRef.setFill(tile.getColor(tile.type));
                tile.fxRef.setOpacity(0.5);
                mainBuilding.windowContainer.getChildren().add(tile.fxRef);
            }
        },
        Path {
            @Override
            public void initialiseView(int index,Tile tile) {
                tile.fxRef = new Rectangle(tile.getActualX(), tile.getActualY(), tile.width, tile.height);
                tile.fxRef.setStroke(Color.RED);
                tile.fxRef.setFill(tile.getColor(tile.type));
                tile.fxRef.setOpacity(0.5);
                mainBuilding.windowContainer.getChildren().add(tile.fxRef);
            }
        },
        Default {
            @Override
            public void initialiseView(int index, Tile tile) {
                tile.fxRef = new Rectangle(tile.getActualX(), tile.getActualY(), tile.width, tile.height);
                tile.fxRef.setStroke(Color.BLACK);
                tile.fxRef.setFill(tile.getColor(tile.type));
                tile.fxRef.setOpacity(0.5);
                mainBuilding.windowContainer.getChildren().add(tile.fxRef);
            }
        },
        Employee {
            @Override
            public void initialiseView(int index,Tile tile) {
                tile.fxRef = new Rectangle(tile.getActualX(), tile.getActualY(), tile.width, tile.height);
                tile.fxRef.setStroke(Color.BLACK);
                tile.fxRef.setFill(tile.getColor(tile.type));
                tile.fxRef.setOpacity(0.5);
                mainBuilding.windowContainer.getChildren().add(tile.fxRef);




                Actor a;
                Circle c;
                c = new Circle(10);
                c.setFill(Color.PINK);
                c.setLayoutX(tile.getActualX()+25);
                c.setLayoutY(tile.getActualY()+15);
                Image image;
                try {
                    image = new Image(getClass().getResource("/Assets/testEmployee.PNG").toURI().toString());
                    c.setFill(new ImagePattern(image));
                    System.out.println("Complete");
                } catch (URISyntaxException ex) {
                    System.out.println(ex);
                }
                mainBuilding.getCurrentFloor().addEmployee(new Employee(c,tile));
                mainBuilding.windowContainer.getChildren().add(c);
                System.out.println("HEERREEE");





                c = new Circle(10);
                c.setFill(Color.PINK);
                c.setLayoutX(tile.getActualX()+25);
                c.setLayoutY(tile.getActualY()+35);
                try {
                    image = new Image(getClass().getResource("/Assets/testEmployee.PNG").toURI().toString());
                    c.setFill(new ImagePattern(image));
                    System.out.println("Complete");
                } catch (URISyntaxException ex) {
                    System.out.println(ex);
                }
                mainBuilding.getCurrentFloor().addEmployee(new Employee(c,tile));
                mainBuilding.windowContainer.getChildren().add(c);
                System.out.println(mainBuilding.getFloors().get(index).employees.size());
                System.out.println("Oh oh and I, I am an employee");

            }
        };
        
        public abstract void initialiseView(int index,Tile tile);

    }
    public BlockType type;

    @Override
    public int compareTo(fireescapedemo.Tile t) {
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

    public Tile(int x, int y, int gridX, int gridY, double size){
        this.type = null;
        this.actualCords[0] = x;
        this.actualCords[1] = y;
        this.gridCords[0] = gridX;
        this.gridCords[1] = gridY;

        this.width = size;
        this.height = size;
        this.type = BlockType.Default;
        this.currentActor = null;

        this.id = idCount;
        this.hCost = 0;
        this.fCost = 0;
        this.parent = null;
        idCount++;
        this.walls = new boolean[4];
        int i;
        for(i = 0; i < this.walls.length; i++){
            this.walls[i] = true;
        }
        this.initialiseView();
    }


    public BlockType getType(){return this.type;}
    public int[] getActualCords(){return this.actualCords;}
    public int[] getGridCords(){return this.gridCords;}
    public int getActualX(){return this.actualCords[0];}
    public int getActualY(){return this.actualCords[1];}
    public int getGridX(){return this.gridCords[0];}
    public int getGridY(){return this.gridCords[1];}

    @Override
    public void initialiseView(){
        try{
            this.mainBuilding.windowContainer.getChildren().remove(this.fxRef);
        } catch(Exception e){}
        this.type.initialiseView(0, this);


        this.fxRef.setOnMouseClicked((MouseEvent event) -> {
            if(mainBuilding.canEdit) {
                this.type = FXMLBuildingController.getActionType();
                this.fxRef.setFill(this.getColor(this.type));
                this.fxRef.setOpacity(0.5);
                this.initialiseView();}
        });

    }


    @Override
    public void updateView(){
        if(this.fxRef == null){
            this.initialiseView();
        }
        this.fxRef.setFill(this.getColor(this.type));
        this.fxRef.toFront();
    }


/*
           this.fxRef = new Rectangle(this.actualCords[0], this.actualCords[1], this.width, this.height);
            this.fxRef.setStroke(Color.BLACK);
            this.fxRef.setOnMouseClicked((MouseEvent event) -> {
                if(mainBuilding.canEdit) {
                    this.type = FXMLBuildingController.getActionType();
                    this.fxRef.setFill(this.getColor(this.type));
                    this.fxRef.setOpacity(0.5);
                }
            });
            this.fxRef.setFill(this.getColor(this.type));
            this.fxRef.setOpacity(0.5);
            mainBuilding.windowContainer.getChildren().add(this.fxRef);
            this.fxRef.toFront();



    @Override
    public void render(){

        }
 */


    //Returns Colour of block depending on a type of block
    public Color getColor(BlockType type){
        Color tileColor;
        switch (this.type){
            case Employee: tileColor = Color.web(getColor("RED"));break;
            case Exit: tileColor = Color.web(getColor("GREY"));break;
            case Stairs: tileColor = Color.web(getColor("AQUAMARINE"));break;
            default: tileColor = Color.web(getColor("WHITESMOKE"));break;
        }
        return tileColor;
    }

    public boolean containsActor(){return this.currentActor != null;}


    public void setParent(Tile t) {this.parent = t;}
    public void setGCost(double i) {this.gCost = i;}
    public void setHCost(double i) {this.hCost = i;}
    //public void setColor(Color c){this.color = c;}
    public void setType(BlockType t){this.type = t;}

    public boolean setActor(Actor a){
        if(canEdit && this.containsActor()){
            this.currentActor = a;
            return true;
        }
        return false;
    }


    public final int getId(){return this.id;}
    public final Tile getParent(){return this.parent;}
    public final double getFCost(){return this.fCost;}
    public final double getGCost(){return this.gCost;}
    public final double getHCost(){return this.hCost;}


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

    public static void toggleBuild() {buildEnabled = !buildEnabled;}
    public static boolean isBuildEnabled(){return buildEnabled;}

    public void calculateFCost(){ this.fCost = this.gCost + this.hCost;}
    public void computeDistance(Tile endNode) {
        this.hCost = Math.abs(this.getGridCords()[0] - endNode.getGridCords()[0])
                + Math.abs(this.getGridCords()[1] - endNode.getGridCords()[1]);
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

    public boolean getAccess(int dir){ return this.walls[dir];}
    public void removeActor(){this.currentActor = null;}
    public Actor getActor(){return this.currentActor;}
}
