package fireescapedemo;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Circle;

public class Fire extends Actor {
    Circle[] particals;


    public Fire(Node view, Tile tile) {
        super(view, tile);
        this.particals = new Circle[100];
    }

    public Fire(Node view, Tile tile, Point2D vel) {
        super(view, tile,vel);
    }
}
