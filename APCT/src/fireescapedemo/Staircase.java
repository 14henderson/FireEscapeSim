package fireescapedemo;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.Serializable;
import java.net.URISyntaxException;

public class Staircase extends TileObject implements Serializable {
    public transient Text IDText;
    public int rotation;
    public String direction;
    public int ID;
    public String uniqueObjID;
    public int joinedID;

    public Staircase(Tile position) {
        this.parent = position;
        this.joinedID = -1;
        this.refreshObjID();
    }

    public void refreshObjID(){
        this.uniqueObjID = String.format("%d%d%d%s", this.parent.getGridX(), this.parent.getGridY(), this.getRotation(), this.getDirection());
    }

    public void setRotation(int n){
        this.rotation = n;
        this.refreshObjID();
    }

    public void setDirection(String n){
        this.direction = n;
        this.refreshObjID();
    }

    public int getRotation(){return this.rotation;}
    public String getDirection(){return this.direction;}
    public void setID(int n){this.ID = n;}
    public void setJoinedID(int n){this.joinedID = n;}
    public void setIDFxml(Node n){this.IDText = (Text)n;}

    public String getUniqueObjID(){
        return this.uniqueObjID;
    }
    @Override
    public Node getNode() {
        return this.fxNode;
    }

    @Override
    public String toString(){
        String s = "_____________________\n" +
                "Rotation: %d\n" +
                "Direction: %s\n" +
                "ID: %d\n" +
                "Joined ID: %d\n" +
                "ObjID: %s";
        return String.format(s, this.getRotation(), this.getDirection(), this.ID, this.joinedID, this.uniqueObjID);
    }
    @Override
    public void setNode(Node n) {
        this.fxNode = (Rectangle) n;
    }
    @Override
    public void flushNodes(){
        this.parent.mainBuilding.windowContainer.getChildren().removeAll(this.fxNode, this.fxNode, this.IDText);
    }
    @Override
    public void destroy(){
        this.parent.mainBuilding.stairs.remove(this.ID);
//        this.parent.mainBuilding.stairs.get(this.joinedID).joinedID = -1;
    }
    public static String generateUniqueID(Tile t, int rotation, String direction){
        return String.format("%d%d%d%s", t.getGridX(), t.getGridY(), rotation, direction);
    }
    @Override
    public void updateView(){
        System.out.println(this.toString());
        if(!parent.mainBuilding.windowContainer.getChildren().contains(this.fxNode)){
            System.out.println("Initialised view");
            this.initialiseView();
        }
        Rectangle tmp = (Rectangle)this.fxNode;
        tmp.setX(this.parent.getActualX());
        tmp.setY(this.parent.getActualY());
        tmp.setWidth(this.parent.getWidth());
        tmp.setHeight(this.parent.getHeight());
        this.IDText.setFill(Color.RED);
        this.IDText.setX(this.parent.getActualX()+this.parent.getSize()/8);
        this.IDText.setY(this.parent.getActualY()+this.parent.getSize()/4);
        tmp.toFront();
        this.IDText.toFront();

    }


    @Override
    public void initialiseView(){
        Rectangle rec = new Rectangle(this.parent.getActualX(), this.parent.getActualY(), this.parent.mainBuilding.getSize(), this.parent.mainBuilding.getSize());
        rec.getStrokeDashArray().addAll(10d, 10d);
        String filename = "/Assets/stairs_%s_%c.fw.png";
        char orientation = 'N';
        switch (rotation) {
            case 0:
                orientation = 'N';
                this.parent.removeAccess(1);
                this.parent.removeAccess(2);
                this.parent.removeAccess(3);
                break;
            case 90:
                orientation = 'E';
                this.parent.removeAccess(0);
                this.parent.removeAccess(2);
                this.parent.removeAccess(3);
                break;
            case 180:
                orientation = 'S';
                this.parent.removeAccess(0);
                this.parent.removeAccess(1);
                this.parent.removeAccess(3);
                break;
            case 270:
                orientation = 'W';
                this.parent.removeAccess(0);
                this.parent.removeAccess(1);
                this.parent.removeAccess(2);
                break;
        }
        Image image;
        filename = String.format(filename, this.direction, orientation);
        try {
            image = new Image(getClass().getResource(filename).toURI().toString());
            rec.setFill(new ImagePattern(image));
        } catch (URISyntaxException ex) {}
        this.IDText = new Text(Integer.toString(this.ID));
        this.IDText.setFill(Color.RED);
        this.IDText.setX(this.parent.getActualX()+this.parent.getSize()/8);
        this.IDText.setY(this.parent.getActualY()+this.parent.getSize()/4);
        this.parent.mainBuilding.windowContainer.getChildren().addAll(rec, this.IDText);
        this.IDText.toFront();
        this.fxNode = rec;
    }



}
