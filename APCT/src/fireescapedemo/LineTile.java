package fireescapedemo;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class LineTile{
    private int gridX;
    private int gridY;
    private double size;
    private Pane windowContainer;
    private Building mainBuilding;
    private Rectangle lineTileRect;

    public LineTile(int gridXSetter, int gridYSetter, int sizeSetter, Pane windowontainer, Building mainBuilding){
        this.gridX = gridXSetter;
        this.gridY = gridYSetter;
        this.size = sizeSetter;
        this.windowContainer = windowontainer;
        this.mainBuilding = mainBuilding;
    }
    public void bringToFront(){
        this.lineTileRect.toFront();
    }
    public void render(){
        this.lineTileRect = new Rectangle(gridX, gridY, size, size);
        this.lineTileRect.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(Tile.isBuildEnabled()){
                    FXMLBuildingController.setLineClicked(mouseEvent);
                }
            }
        });
        this.lineTileRect.setOpacity(0.2);
        this.lineTileRect.setFill(Color.RED);
        this.mainBuilding.windowContainer.getChildren().add(this.lineTileRect);
        this.lineTileRect.toFront();
    }
    public Rectangle getLineTileRect(){return this.lineTileRect;}
}
