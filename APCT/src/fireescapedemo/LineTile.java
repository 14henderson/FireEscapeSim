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


    public LineTile(int gridXSetter, int gridYSetter, int sizeSetter, Pane windowontainer, Building mainBuilding){
        this.gridX = gridXSetter;
        this.gridY = gridYSetter;
        this.size = sizeSetter;
        this.windowContainer = windowontainer;
        this.mainBuilding = mainBuilding;
    }

    public void render(){
        Rectangle lineTileRect = new Rectangle(gridX, gridY, size, size);

        //Rectangle finalR = r;
        lineTileRect.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(Tile.isBuildEnabled()){
                    FXMLBuildingController.setLineClicked(mouseEvent);
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
