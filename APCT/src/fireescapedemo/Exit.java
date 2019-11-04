package fireescapedemo;

import javafx.scene.shape.Rectangle;

import java.io.Serializable;

public class Exit implements Serializable {
    public final transient Rectangle VIEW;
    public final Tile position;
    public Exit(Rectangle r, Tile position) {this.VIEW = r; this.position = position;}

}
