package fireescapedemo;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;
import java.net.URISyntaxException;

public class Exit extends TileObject implements Serializable {
    //public transient Rectangle view;
    public Exit(Tile position) {
        this.parent = position;
        this.initialiseView();
    }

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
        this.parent.getFloor().getPane().getChildren().removeAll(fxNode);
    }
    @Override
    public void destroy(){}
    @Override
    public void updateView(){
        Rectangle tmp = (Rectangle)this.fxNode;
        tmp.setX(this.parent.getActualX());
        tmp.setY(this.parent.getActualY());
        tmp.setWidth(this.parent.getWidth());
        tmp.setHeight(this.parent.getHeight()/2);
    }
    @Override
    public void initialiseView(){
        Rectangle rec = new Rectangle(this.parent.getActualX(),this.parent.getActualY(),this.parent.mainBuilding.getSize(),this.parent.mainBuilding.getSize()/2);
        Image image;
        try {
            image = new Image(getClass().getResource("/Assets/estExit.PNG").toURI().toString());
            rec.setFill(new ImagePattern(image));
            this.parent.getFloor().getPane().getChildren().add(rec);
        } catch (URISyntaxException ex) {
            System.out.println(ex);
        }
        this.fxNode = rec;
    }
}
