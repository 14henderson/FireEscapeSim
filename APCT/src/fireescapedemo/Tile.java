package fireescapedemo;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URISyntaxException;
import java.io.Serializable;
import java.util.Arrays;


public class Tile extends MapObject implements Serializable, Comparable<fireescapedemo.Tile> {
    public TileObject tileObject = null;              //ref for any object residing on the tile
    private Actor currentActor;
    public boolean[] walls;

    //Tile Geometry Variables
    public int[] gridCords = new int[2];              //X, Y
    public double[] actualCords = new double[2];      //X, Y
    public double[] dimensions = new double[2];       //Width, Height
    public int rotation = 0;
    public int floorNum;                                 //which floor is this located on.

    //Node Building Variables
    private transient Rectangle fxRef = null;          //Tile Rectangle object
    private Floor parentFloor;
    private static boolean buildEnabled = true;

    //Path-finding Variables
    private double gCost,fCost,hCost;
    private static int tileID = 0;
    private Tile parent;



    enum BlockType{
        Exit {
            @Override
            public void initialiseView(int index,Tile tile, boolean isPLacing) {
                this.flushTile(tile);
                this.prepareTile(tile);

                Exit exit = new Exit(tile);
                tile.setTileObject(exit);
                tile.getFloor().addExit(exit);
                this.finalPreparation(tile);
            }
        },

        Stairs {
            @Override
            public void initialiseView(int index,Tile tile, boolean isPLacing) {
                this.prepareTile(tile);
                if(!isPLacing){
                    tile.tileObject.flushNodes();
                    tile.tileObject.initialiseView();
                    this.finalPreparation(tile);
                    return;
                }

                //Get provisional variables for possible later use
                int rotation = FXMLBuildingController.currStairOrientation;
                String direction = FXMLBuildingController.currStairDirection;
                String newObjID = Staircase.generateUniqueID(tile, rotation, direction);

                //If no stairs tile existed before (placing it first time)
                if(tile.tileObject == null || !tile.tileObject.hasNode() || !(tile.tileObject instanceof Staircase)){
                    Staircase stair = new Staircase(tile);
                    stair.setRotation(rotation);
                    stair.setDirection(direction);
                    int newID=0;    //finding the next available ID to take
                    while (mainBuilding.getStairs().containsKey(newID)) {newID++;}
                    stair.setID(newID);
                    mainBuilding.getStairs().put(newID, stair);      //add stair and ID to mainBuilding object
                    stair.initialiseView();
                    tile.setTileObject(stair);

                //else if it IS of type Staircase, but not the same stairs (e.g. placing stairs on existing stairs)
                }else if(((Staircase) tile.tileObject).getUniqueObjID().compareTo(newObjID) != 0) {//comparing the IDs
                    tile.tileObject.destroy();
                    Staircase tmpRef = (Staircase) tile.tileObject;
                    mainBuilding.getStairs().remove(tmpRef.ID);
                    tile.tileObject = null;
                    this.initialiseView(index, tile, true);   //should default to re-initialising stairs on tile

                //assuming a zoom or pan
                }else{
                    tile.tileObject.updateView();
                }
                this.finalPreparation(tile);
            }
        },

        Blocked{
            @Override
            public void initialiseView(int index, Tile tile, boolean isPLacing) {
                this.flushTile(tile);
                this.prepareTile(tile);
                DefaultAssetHolder blocked = new Tile.DefaultAssetHolder(tile, "/Assets/no_entrance.fw.png");
                tile.setTileObject(blocked);
                tile.removeAccess(0);
                tile.getFloor().getTile(tile.getGridX(), tile.getGridY()-1).removeAccess(2);

                tile.removeAccess(1);
                tile.getFloor().getTile(tile.getGridX()+1, tile.getGridY()).removeAccess(3);

                tile.removeAccess(2);
                tile.getFloor().getTile(tile.getGridX(), tile.getGridY()+1).removeAccess(0);

                tile.removeAccess(3);
                tile.getFloor().getTile(tile.getGridX()-1, tile.getGridY()).removeAccess(1);

                this.finalPreparation(tile);
            }
        },

        Fire{
            @Override
            public void initialiseView(int index, Tile tile, boolean isPLacing) {
                this.flushTile(tile);
                this.prepareTile(tile);
                DefaultAssetHolder fire = new Tile.DefaultAssetHolder(tile, "/Assets/fire.png");
                tile.setTileObject(fire);

                tile.removeAccess(0);
                tile.getFloor().getTile(tile.getGridX(), tile.getGridY()-1).removeAccess(2);

                tile.removeAccess(1);
                tile.getFloor().getTile(tile.getGridX()+1, tile.getGridY()).removeAccess(3);

                tile.removeAccess(2);
                tile.getFloor().getTile(tile.getGridX(), tile.getGridY()+1).removeAccess(0);

                tile.removeAccess(3);
                tile.getFloor().getTile(tile.getGridX()-1, tile.getGridY()).removeAccess(1);

                this.finalPreparation(tile);
            }
        },

        Default {
            @Override
            public void initialiseView(int index, Tile tile, boolean isPLacing) {
                this.flushTile(tile);
                this.prepareTile(tile);

                if(tile.tileObject != null){
                    tile.tileObject.destroy();
                }
                this.finalPreparation(tile);
            }
        },

        Employee {
            @Override
            public void initialiseView(int index,Tile tile, boolean isPLacing) {
                this.flushTile(tile);
                this.prepareTile(tile);

                Actor a;
                Circle c;

                c = new Circle((tile.getFloor().getBuilding().getActorSize()/2)*tile.getFloor().tileSize/50.0);
                c.setFill(Color.PINK);

                c.setLayoutX(tile.getActualX()+tile.mainBuilding.getSize()/2);
                c.setLayoutY(tile.getActualY()+tile.mainBuilding.getSize()/2);


                Image image;
                try {
                    image = new Image(getClass().getResource("/Assets/testEmployee.PNG").toURI().toString());
                    c.setFill(new ImagePattern(image));
                } catch (URISyntaxException ex) {
                    System.out.println(ex);
                }
                Employee e = new Employee(c,tile);
                tile.getFloor().addEmployee(e);
                tile.parentFloor.getPane().getChildren().add(c);
                tile.setActor(e);
                tile.setTileObject(e);

                this.finalPreparation(tile);
            }
        };
        
        public abstract void initialiseView(int index,Tile tile, boolean isPlacing);
        public void flushTile(Tile tile){
            if(tile.tileObject != null) {
                tile.tileObject.flushNodes();
                tile.tileObject.destroy();
                tile.tileObject = null;
            }
        }


        public void prepareTile(Tile tile){
            //System.out.println("Tile: "+tile.parentFloor);
            tile.parentFloor.getPane().getChildren().remove(tile.fxRef);
            tile.fxRef = new Rectangle(tile.getActualX(), tile.getActualY(), tile.getWidth(), tile.getHeight());
            if(tile.getFloor().getBuilding().getSimState()){
                tile.fxRef.setStroke(Color.TRANSPARENT);
            }else{
                tile.fxRef.setStroke(Color.BLACK);
            }

            tile.fxRef.setFill(Color.rgb(0,0,0,0));
            tile.fxRef.setOpacity(1);
            tile.parentFloor.getPane().getChildren().add(tile.fxRef);
        }
        public void finalPreparation(Tile tile){
            tile.fxRef.setOnMouseClicked((MouseEvent event) -> {
                System.out.println("Tile clicked (Tile Type handler) on "+tile.parentFloor.getId());
                if(mainBuilding.canEdit && !FXMLBuildingController.buildingWalls) {
                    tile.type = FXMLBuildingController.getActionType();
                    tile.type.initialiseView(0, tile, true);
                    FXMLBuildingController.refreshLineTiles();
                }
            });
            tile.fxRef.toFront();
        }


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

    public Tile(){
        this.type = null;
        this.walls = new boolean[4];
    }


    public Tile(Floor thisFloor, int x, int y, int gridX, int gridY, double size){
        this.type = null;
        this.actualCords[0] = x;
        this.actualCords[1] = y;
        this.gridCords[0] = gridX;
        this.gridCords[1] = gridY;
        this.rotation = FXMLBuildingController.currStairOrientation;
        this.parentFloor = thisFloor;

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
    public double[] getActualCords(){return this.actualCords;}
    public int[] getGridCords(){return this.gridCords;}
    public void setActualCords(double x, double y){
        this.actualCords[0] = x;
        this.actualCords[1] = y;
    }
    public void setDimensions(int width, int height){
        this.dimensions[0] = width;
        this.dimensions[1] = height;
    }

    public double getActualX(){return this.actualCords[0];}
    public double getActualY(){return this.actualCords[1];}
    public int getGridX(){return this.gridCords[0];}
    public int getGridY(){return this.gridCords[1];}
    public double getWidth(){return this.dimensions[0];}
    public double getHeight(){return this.dimensions[1];}
    public void setTileObject(TileObject t){this.tileObject = t;}
    public TileObject getTileObject(){return this.tileObject;}
    public void setFloorNum(int n){this.floorNum = n;}
    public int getFloorNum(){return this.getFloor().floorNum;}
    public Floor getFloor(){return this.parentFloor;}

    @Override
    public void initialiseView(){
        try{
            this.parentFloor.getPane().getChildren().remove(this.fxRef);
            this.parentFloor.getPane().getChildren().remove(this.currentActor.fxNode);
        } catch(Exception e){}
        this.type.initialiseView(0, this, false);
        this.currentActor = null;
        this.fxRef.setOnMouseClicked((MouseEvent event) -> {
            System.out.println("Tile clicked on "+this.parentFloor.getId());
            if(mainBuilding.canEdit && !FXMLBuildingController.buildingWalls) {
                this.type = FXMLBuildingController.getActionType();
                this.type.initialiseView(0, this, true);
                FXMLBuildingController.refreshLineTiles();
            }
        });
        this.fxRef.toFront();
    }


    @Override
    public void updateView(){
        if(this.fxRef == null){
            this.initialiseView();
        }
        //this.fxRef.setFill(this.getColor(this.type));
        this.fxRef.setX(this.getActualX());
        this.fxRef.setY(this.getActualY());
        this.fxRef.setWidth(this.getWidth());
        this.fxRef.setHeight(this.getWidth());
        this.fxRef.setRotate(this.rotation);
        if(this.tileObject != null) {
            this.tileObject.updateView();
        }
        //this.fxRef.toFront();

    }


    public void translate(int xinc, int yinc){
        this.actualCords[0] += xinc;
        this.actualCords[1] += yinc;
    }




    //Returns Colour of block depending on a type of block
    public Color getColor(BlockType type){
        Color tileColor;
        switch (this.type){
            case Employee: tileColor = Color.web(getColor("BROWN"));break;
            case Exit: tileColor = Color.web(getColor("BROWN"));break;
            case Stairs: tileColor = Color.web(getColor("BROWN"));break;
            default: tileColor = Color.web(getColor("WHITESMOKE"));break;
        }
        return tileColor;
    }

    public boolean containsActor(){return this.currentActor != null;}
    public void setActor(Actor a){this.currentActor = a;}

    public void setParent(Tile t) {this.parent = t;}
    public void setColour(Color c){this.fxRef.setFill(c);}
    public void setGCost(double i) {this.gCost = i;}
    public void setHCost(double i) {this.hCost = i;}
    public void setType(BlockType t){this.type = t;}


    public final int getID(){return this.tileID;}
    public final Rectangle getFxRef(){return (Rectangle)this.fxRef;}
    public final Tile getParent(){return this.parent;}
    public final double getFCost(){return this.fCost;}
    public final double getGCost(){return this.gCost;}
    public final double getHCost(){return this.hCost;}

    public final double getSize(){return this.fxRef.getHeight();}


    public final void removeAccess(int dir){ this.walls[dir] = false;}
    public final void grantAccess(int dir){ this.walls[dir] = true;}
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
    public boolean checkAccess(Tile t){
        if(t == this){return true;}
        if(t.getGridX()-this.getGridX() == 1 && t.getGridY()-this.getGridY() == 1){
            return
                    (this.walls[1] && t.getFloor().getTile(this.getGridX()+1, this.getGridY()).walls[2])
                            || (this.walls[2] && t.getFloor().getTile(this.getGridX(), this.getGridY()+1).walls[1]);
        }

        if(t.getGridX()-this.getGridX() == -1 && t.getGridY()-this.getGridY() == 1){
            return
                    (this.walls[2] && t.getFloor().getTile(this.getGridX(), this.getGridY()+1).walls[3])
                            || (this.walls[3] && t.getFloor().getTile(this.getGridX()-1, this.getGridY()).walls[2]);
        }

        if(t.getGridX()-this.getGridX() == 1 && t.getGridY()-this.getGridY() == -1){
            return
                    (this.walls[0] && t.getFloor().getTile(this.getGridX(), this.getGridY()-1).walls[1])
                            || (this.walls[1] && t.getFloor().getTile(this.getGridX()+1, this.getGridY()).walls[0]);
        }

        if(t.getGridX()-this.getGridX() == -1 && t.getGridY()-this.getGridY() == -1){
            return
                    (this.walls[0] && t.getFloor().getTile(this.getGridX(), this.getGridY()-1).walls[3])
                            || (this.walls[3] && t.getFloor().getTile(this.getGridX()-1, this.getGridY()).walls[0]);
        }

        if(t.getGridX()-this.getGridX() == 1){return this.walls[1];}
        if(t.getGridX()-this.getGridX() == -1){return this.walls[3];}
        if(t.getGridY()-this.getGridY() == 1){return this.walls[2];}
        if(t.getGridY()-this.getGridY() == -1){return this.walls[0];}

        else{
            return false;
        }
    }

    public void removeActor(){this.currentActor = null;}
    public Actor getActor(){return this.currentActor;}

    @Override
    public String toString(){
        return ("X:"+this.actualCords[0]+" Y:"+this.actualCords[1]+" Size:"+this.fxRef.getWidth()+"x"+this.fxRef.getHeight());
    }


    //default tile holder for tiles. Tiles with limited functionality can extent this.
    public static class DefaultAssetHolder extends TileObject implements Serializable{
        String filename;
        DefaultAssetHolder(Tile t, String fname){
            this.parent = t;
            this.filename = fname;
            this.initialiseView();
        }
        @Override
        public Node getNode(){
            return this.fxNode;
        }
        @Override
        public void setNode(Node n) {
            this.fxNode = n;
        }
        @Override
        public void flushNodes(){
            this.parent.parentFloor.getPane().getChildren().remove(this.fxNode);
        }
        @Override
        public void destroy(){}
        @Override
        public void updateView(){
            Rectangle tmp = (Rectangle)this.fxNode;
            tmp.setX(this.parent.getActualX());
            tmp.setY(this.parent.getActualY());
            tmp.setWidth(parent.getWidth());
            tmp.setHeight(parent.getHeight());
        }
        @Override
        public void initialiseView(){
            Rectangle rec = new Rectangle(this.parent.getActualX(),this.parent.getActualY(),this.parent.mainBuilding.getSize(),this.parent.mainBuilding.getSize());
            rec.setX(rec.getX());
            rec.setY(rec.getY());
            System.out.println(rec);
            Image image;
            try {
                image = new Image(getClass().getResource(this.filename).toURI().toString());
                rec.setFill(new ImagePattern(image));
                this.parent.parentFloor.getPane().getChildren().add(rec);
            } catch (URISyntaxException ex) {
                System.out.println(ex);
            }
            this.fxNode = rec;
        }


    }





}
