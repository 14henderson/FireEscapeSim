package fireescapedemo;

import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.Serializable;

public class Staircase extends TileObject implements Serializable {
    public transient Rectangle view;
    public transient Text IDText;
    //public final Tile position;
    public int rotation;
    public String direction;
    public int ID;
    public int joinedID;
    public Staircase(Rectangle r, Tile position) {this.view = r; this.parent = position;}

    public void setRotation(int n){
        this.rotation = n;
    }
    public void setDirection(String n){
        this.direction = n;
    }
    public int getRotation(){return this.rotation;}
    public String getDirection(){return this.direction;}
    public void setID(int n){this.ID = n;}
    public void setJoinedID(int n){this.joinedID = n;}
    public void setIDFxml(Node n){this.IDText = (Text)n;}
    @Override
    public Node getNode() {
        return this.view;
    }


    @Override
    public void setNode(Node n) {
        this.view = (Rectangle) n;
    }
    @Override
    public void flushNodes(){
        parent.mainBuilding.windowContainer.getChildren().removeAll(this.fxNode, this.view, this.IDText);
    }
    @Override
    public void destroy(){
        this.parent.mainBuilding.stairs.remove(this.ID);
//        this.parent.mainBuilding.stairs.get(this.joinedID).joinedID = -1;
    }
}
