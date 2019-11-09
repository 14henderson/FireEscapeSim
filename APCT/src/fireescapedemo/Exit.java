package fireescapedemo;

import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;

public class Exit extends TileObject implements Serializable {
    public transient Rectangle view;
    public final Tile position;
    public Exit(Rectangle r, Tile position) {this.view = r; this.position = position;}

    @Override
    public Node getNode() {
        return this.view;
    }

    @Override
    public void setNode(Node n) {
        this.view = (Rectangle) n;
    }
}
