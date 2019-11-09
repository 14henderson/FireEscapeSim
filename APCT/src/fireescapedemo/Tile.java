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



public class Tile extends MapObject implements Serializable, Comparable<fireescapedemo.Tile> {
    public TileObject tileObject;                           //ref for any object residing on the tile
    private Actor currentActor;
    public boolean[] walls;

    //Tile Geometry Variables
    public final int[] gridCords = new int[2];              //X, Y
    public final int[] actualCords = new int[2];            //X, Y
    public final double[] dimensions = new double[2];       //Width, Height

    //Node Building Variables
    private transient Rectangle fxRef = null;               //Tile Rectangle object
    private static boolean buildEnabled = true;

    //Path-finding Variables
    private double gCost,fCost,hCost;
    private static int tileID = 0;
    private Tile parent;



    enum BlockType{
        Exit {
            @Override
            public void initialiseView(int index,Tile tile) {
                tile.fxRef = new Rectangle(tile.getActualX(), tile.getActualY(), tile.getWidth(), tile.getHeight());
                tile.fxRef.setStroke(Color.BLACK);
                tile.fxRef.setFill(tile.getColor(tile.type));
                tile.fxRef.setOpacity(0.5);
                mainBuilding.windowContainer.getChildren().add(tile.fxRef);

                Rectangle rec = new Rectangle(tile.getActualX(),tile.getActualY(),tile.mainBuilding.getSize(),tile.mainBuilding.getSize()/2);
                Image image;
                try {
                    image = new Image(getClass().getResource("/Assets/estExit.PNG").toURI().toString());
                    rec.setFill(new ImagePattern(image));
                    mainBuilding.windowContainer.getChildren().add(rec);
                } catch (URISyntaxException ex) {
                    System.out.println(ex);
                }
                Exit exit = new Exit(rec,tile);
                tile.setTileObject(exit);
                mainBuilding.getFloors().get(index).addExit(exit);
            }
        },
        Stairs {
            @Override
            public void initialiseView(int index,Tile tile) {
                tile.fxRef = new Rectangle(tile.getActualX(), tile.getActualY(), tile.getWidth(), tile.getHeight());
                tile.fxRef.setStroke(Color.BLACK);
                tile.fxRef.setFill(tile.getColor(tile.type));
                tile.fxRef.setOpacity(0.5);
                mainBuilding.windowContainer.getChildren().add(tile.fxRef);
            }
        },
        Path {
            @Override
            public void initialiseView(int index,Tile tile) {
                tile.fxRef = new Rectangle(tile.getActualX(), tile.getActualY(), tile.getWidth(), tile.getHeight());
                tile.fxRef.setStroke(Color.RED);
                tile.fxRef.setFill(tile.getColor(tile.type));
                tile.fxRef.setOpacity(0.5);
                mainBuilding.windowContainer.getChildren().add(tile.fxRef);
            }
        },
        Default {
            @Override
            public void initialiseView(int index, Tile tile) {
                tile.fxRef = new Rectangle(tile.getActualX(), tile.getActualY(), tile.getWidth(), tile.getHeight());
                tile.fxRef.setStroke(Color.BLACK);
                tile.fxRef.setFill(tile.getColor(tile.type));
                tile.fxRef.setOpacity(0.5);
                mainBuilding.windowContainer.getChildren().add(tile.fxRef);
            }
        },
        Employee {
            @Override
            public void initialiseView(int index,Tile tile) {
                tile.fxRef = new Rectangle(tile.getActualX(), tile.getActualY(), tile.getWidth(), tile.getHeight());
                tile.fxRef.setStroke(Color.BLACK);
                tile.fxRef.setFill(tile.getColor(tile.type));
                tile.fxRef.setOpacity(0.5);
                mainBuilding.windowContainer.getChildren().add(tile.fxRef);




                Actor a;
                Circle c;
                c = new Circle(tile.mainBuilding.getSize()/5);
                c.setFill(Color.PINK);
                c.setLayoutX(tile.getActualX()+tile.mainBuilding.getSize()/2);
                c.setLayoutY(tile.getActualY()+tile.mainBuilding.getSize()/3);
                Image image;
                try {
                    image = new Image(getClass().getResource("/Assets/testEmployee.PNG").toURI().toString());
                    c.setFill(new ImagePattern(image));
                    System.out.println("Complete");
                } catch (URISyntaxException ex) {
                    System.out.println(ex);
                }
                Employee e = new Employee(c,tile);
                mainBuilding.getCurrentFloor().addEmployee(e);
                mainBuilding.windowContainer.getChildren().add(c);
                tile.setActor(e);
                tile.setTileObject(e);

/*


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
                */
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

        this.dimensions[0] = size;
        this.dimensions[1] = size;
        this.type = BlockType.Default;
        this.currentActor = null;

        this.tileID = 0;
        this.hCost = 0;
        this.fCost = 0;
        this.parent = null;
        tileID++;
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
    public void setActualCords(int x, int y){
        this.actualCords[0] = x;
        this.actualCords[1] = y;
    }
    public void setDimensions(int width, int height){
        this.dimensions[0] = width;
        this.dimensions[1] = height;
    }
    public int getActualX(){return this.actualCords[0];}
    public int getActualY(){return this.actualCords[1];}
    public int getGridX(){return this.gridCords[0];}
    public int getGridY(){return this.gridCords[1];}
    public double getWidth(){return this.dimensions[0];}
    public double getHeight(){return this.dimensions[1];}
    public void setTileObject(TileObject t){this.tileObject = t;}
    public TileObject getTileObject(){return this.tileObject;}

    @Override
    public void initialiseView(){
        try{
            this.mainBuilding.windowContainer.getChildren().remove(this.fxRef);
            this.mainBuilding.windowContainer.getChildren().remove(this.currentActor.view);
        } catch(Exception e){}
        if(this.tileObject != null){
            this.mainBuilding.windowContainer.getChildren().remove(this.tileObject.getNode());
            this.tileObject = null;
        }
        this.currentActor = null;
        this.type.initialiseView(0, this);
        this.fxRef.setOnMouseClicked((MouseEvent event) -> {
            if(mainBuilding.canEdit) {
                this.type = FXMLBuildingController.getActionType();
                this.fxRef.setFill(this.getColor(this.type));
                this.fxRef.setOpacity(0.5);
                this.initialiseView();
                FXMLBuildingController.refreshLineTiles();
            }
        });
    }


    @Override
    public void updateView(){
        if(this.fxRef == null){
            this.initialiseView();
        }
        this.fxRef.setFill(this.getColor(this.type));
        this.fxRef.setX(this.getActualX());
        this.fxRef.setY(this.getActualY());
        this.fxRef.setWidth(this.getWidth());
        this.fxRef.setHeight(this.getWidth());

        if(this.getActualX() > 700 || this.getActualY() > 700){
            this.fxRef.setVisible(false);
        }else{
            this.fxRef.setVisible(true);
        }
        this.fxRef.toFront();
    }


    public void translate(int xinc, int yinc){
        this.actualCords[0] += xinc;
        this.actualCords[1] += yinc;
    }




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
    public void setActor(Actor a){this.currentActor = a;}

    public void setParent(Tile t) {this.parent = t;}
    public void setGCost(double i) {this.gCost = i;}
    public void setHCost(double i) {this.hCost = i;}
    public void setType(BlockType t){this.type = t;}


    public final int getID(){return this.tileID;}
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

    @Override
    public String toString(){
        return ("X:"+this.fxRef.getLayoutX()+" Y:"+this.fxRef.getLayoutY()+" Size:"+this.fxRef.getWidth()+"x"+this.fxRef.getHeight());
    }
}
