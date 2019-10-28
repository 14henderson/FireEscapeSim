package fireescapedemo;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLSimulationController implements Initializable {
    @FXML
    AnchorPane mainPane;
    @FXML
    Pane assetPane;
    @FXML
    Label floorLevel;

    Building mainBuilding = new Building();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        floorLevel.setText("Floor " + mainBuilding.getFloorNum());
        mainPane.getChildren().add(mainBuilding.getCurrentFloor());
        Button alarm = new Button("FIRE ALARM");
        alarm.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                for(Floor floor : mainBuilding.getFloors()){
                    for(Actor employee : floor.employees){
                        employee.setCurrentState(Actor.State.FindRoute);
                    }
                }
                alarm.setDisable(true);
            }
        });
        assetPane.getChildren().add(alarm);
        initAnimation();
    }

    @FXML
    private void startAlarm(){
        for(Floor floor : mainBuilding.getFloors()){
            for(Actor employee : floor.employees){
                employee.setCurrentState(Actor.State.FindRoute);
            }
        }
    }



    public void initAnimation(){
        AnimationTimer t = new AnimationTimer(){
            @Override
            public void handle(long now) {
                onUpdate();
            }

        };
        t.start();
    }

    private void onUpdate() {
        for(Floor floor : mainBuilding.getFloors()){
            for(Actor a : floor.employees){
                a.update(floor);
            }
        }
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
