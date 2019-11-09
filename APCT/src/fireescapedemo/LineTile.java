package fireescapedemo;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class LineTile{
    private double[] actualCords = new double[2];
    private int size;
    private Building mainBuilding;
    private Rectangle lineTileRect;

    public LineTile(int XSetter, int YSetter, int sizeSetter, Building mainBuilding){
        this.actualCords[0] = XSetter;
        this.actualCords[1] = YSetter;
        this.size = sizeSetter;
        this.mainBuilding = mainBuilding;
    }
    public void bringToFront(){
        this.lineTileRect.toFront();
    }
    public void render(){
        this.lineTileRect = new Rectangle(this.getX(), this.getY(), size, size);
        this.lineTileRect.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(Tile.isBuildEnabled() ){
                    if(mouseEvent.getButton() == MouseButton.PRIMARY) {
                        FXMLBuildingController.setLineClicked(mouseEvent);
                    }else{
                        FXMLBuildingController.cancelLineClicked();
                    }
                }
            }
        });
        this.lineTileRect.setOpacity(0.2);
        this.lineTileRect.setFill(Color.RED);
        this.mainBuilding.windowContainer.getChildren().add(this.lineTileRect);
        this.lineTileRect.toFront();

        System.out.println("Old x: "+this.getX());
        System.out.println("Old y: "+this.getY());
    }

    public double getX(){
        return this.actualCords[0];
    }
    public double getY(){
        return this.actualCords[1];
    }
    public void setX(double newX){
        this.actualCords[0] = newX;
    }
    public void setY(double newY){
        this.actualCords[1] = newY;
    }
    public int getSize(){
        return this.size;
    }

    public Rectangle getLineTileRect(){return this.lineTileRect;}



    public void zoom(int oldZoom, int newZoom){
        System.out.println("Old x: "+this.getX());
        System.out.println("Old y: "+this.getY());
        System.out.println("Old and new zoom: "+oldZoom+" "+newZoom);
        System.out.println("New x 2:"+((this.getX()/(double)oldZoom)*newZoom));
        this.setX((this.getX()/(double)oldZoom)*newZoom);
        this.setY((this.getY()/(double)oldZoom)*newZoom);
        System.out.println("New x 3:"+this.getX());

        this.size = newZoom;
        this.lineTileRect.setX(Math.round(this.getX()));
        this.lineTileRect.setY(Math.round(this.getY()));
        this.lineTileRect.setWidth(this.getSize());
        this.lineTileRect.setHeight(this.getSize());
        this.lineTileRect.toFront();
    }
    public void pan(int xinc, int yinc){
        this.actualCords[0] += xinc;
        this.actualCords[1] += yinc;
        this.lineTileRect.setX(this.actualCords[0]);
        this.lineTileRect.setY(this.actualCords[1]);
        this.lineTileRect.toFront();
    }
}
