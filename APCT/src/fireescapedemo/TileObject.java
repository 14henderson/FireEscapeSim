package fireescapedemo;

import javafx.scene.Node;

import java.io.Serializable;

public abstract class TileObject implements Serializable {
    public Node fxNode;
    public abstract Node getNode();
    public abstract void setNode(Node n);
}
