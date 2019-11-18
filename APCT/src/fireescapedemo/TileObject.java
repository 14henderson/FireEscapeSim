package fireescapedemo;

import javafx.scene.Node;

import java.io.Serializable;

public abstract class TileObject implements Serializable {
    public transient Node fxNode = null;
    public Tile parent;
    public abstract Node getNode();
    public abstract void setNode(Node n);
    public abstract void destroy();
    public void flushNodes() {
        this.parent.mainBuilding.windowContainer.getChildren().remove(fxNode);
    }
    public boolean hasNode(){return this.fxNode!=null;}
}
