package fireescapedemo;

import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;

public class Exit extends TileObject implements Serializable {
    //public transient Rectangle view;
    public Exit(Rectangle r, Tile position) {this.fxNode = r; this.parent = position;}

    @Override
    public Node getNode() {
        return this.fxNode;
    }

    @Override
    public void setNode(Node n) {
        this.fxNode = n;
    }

    @Override
    public void flushNodes(){
        this.parent.mainBuilding.windowContainer.getChildren().removeAll(fxNode);
    }
    @Override
    public void destroy(){}
}
