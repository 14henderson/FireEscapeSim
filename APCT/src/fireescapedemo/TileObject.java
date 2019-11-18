package fireescapedemo;

import javafx.scene.Node;

import java.io.Serializable;

public abstract class TileObject implements Serializable {
    public transient Node fxNode = null;
    public abstract Node getNode();
    public abstract void setNode(Node n);
    public boolean hasNode(){return this.fxNode!=null;}
}
