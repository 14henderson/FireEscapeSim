package fireescapedemo;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class LineTile extends MapObject{
    private int gridX;
    private int gridY;
    private double size;

    public LineTile(int gridXSetter, int gridYSetter, int sizeSetter){
        this.gridX = gridXSetter;
        this.gridY = gridYSetter;
        this.size = sizeSetter;
    }

    @Override
    public void render(){
        Rectangle lineTileRect = new Rectangle(gridX, gridY, size, size);

        //Rectangle finalR = r;
        lineTileRect.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(Tile.isBuildEnabled()){
                    mainBuilding.getCurrentFloor().setLineClicked(mouseEvent);
                }
            }
        });
        lineTileRect.setOpacity(0.2);
        lineTileRect.setFill(Color.RED);
        this.mainBuilding.windowContainer.getChildren().add(lineTileRect);
        lineTileRect.toFront();
        //wallBlocks[i][j] = r;
    }
}
