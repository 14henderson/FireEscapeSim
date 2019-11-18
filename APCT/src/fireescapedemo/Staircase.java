package fireescapedemo;

import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;

public class Staircase extends TileObject implements Serializable {
    public transient Rectangle view;
    public final Tile position;
    public int rotation;
    public String direction;
    public Staircase(Rectangle r, Tile position) {this.view = r; this.position = position;}

    public void setRotation(int n){
        this.rotation = n;
    }
    public void setDirection(String n){
        this.direction = n;
    }
    public int getRotation(){return this.rotation;}
    public String getDirection(){return this.direction;}

    @Override
    public Node getNode() {
        return this.view;
    }

    @Override
    public void setNode(Node n) {
        this.view = (Rectangle) n;
    }
}
