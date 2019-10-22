package fireescapedemo;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLSimulationController implements Initializable {
    @FXML
    AnchorPane mainPane;
    @FXML
    Label floorLevel;

    Building mainBuilding = new Building();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        floorLevel.setText("Floor " + mainBuilding.getFloorNum());
        mainPane.getChildren().add(mainBuilding.getCurrentFloor());
    }





    @FXML
    private void nextRoom(){
        if(mainBuilding.hasNextFloor()){
            mainPane.getChildren().remove(mainPane.getChildren().size()-1);
            System.out.println("Wow");
            mainBuilding.nextFloor();
            mainPane.getChildren().add(mainBuilding.getCurrentFloor());
        }else{
            System.out.println("No next floor");
        }
        floorLevel.setText("Floor " + mainBuilding.getFloorNum());
    }

    @FXML
    private void prevRoom(){
        if(mainBuilding.hasPrevFloor()){
            mainPane.getChildren().remove(mainPane.getChildren().size()-1);
            System.out.println("Wow");
            mainBuilding.prevFloor();
            mainPane.getChildren().add(mainBuilding.getCurrentFloor());
        }else{
            System.out.println("No prev floor");
        }
        floorLevel.setText("Floor " + mainBuilding.getFloorNum());
    }



}
