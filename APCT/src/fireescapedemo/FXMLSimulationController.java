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
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLSimulationController implements Initializable {
    @FXML
    AnchorPane mainPane;
    @FXML
    Pane assetPane;
    @FXML
    Label floorLevel;
    @FXML
    Pane mapPane;

    public Building mainBuilding;// = new Building();
    Fire p;
    SceneManager manager;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.manager = new SceneManager();
        this.mainBuilding = manager.getGlobalBuilding();
        mainBuilding.setWindowContainer(mapPane);
        mainBuilding.disableBuild();

        p = new Fire(50,200, new Tile(0,0, 0, 0, 0));
        floorLevel.setText("Floor " + mainBuilding.getCurrentFloorIndex());
        mainBuilding.rerender();

        //mainPane.getChildren().add(mainBuilding.getCurrentFloor());
        Button alarm = new Button("FIRE ALARM");


        alarm.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println("in alarm handler");
                for(Floor floor : mainBuilding.getFloors()){
                    for(Employee employee : floor.employees){
                        employee.setCurrentState(Employee.State.FindRoute);
                    }
                }
                alarm.setDisable(true);
            }
        });
        assetPane.getChildren().add(alarm);
        assetPane.getChildren().add(p.view);
        initAnimation();
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
            for(Employee a : floor.employees){
                a.update(floor);
            }
        }
        p.update(mainBuilding.getCurrentFloor());
    }

    @FXML
    private void nextRoom(){
        if(mainBuilding.hasNextFloor()){
            mainBuilding.increaseFloor();
            mainBuilding.render();
            //mainPane.getChildren().remove(mainPane.getChildren().size()-1);
            //System.out.println("Wow");

            //mainPane.getChildren().add(mainBuilding.getCurrentFloor());
        }else{
            System.out.println("No next floor");
        }
        floorLevel.setText("Floor " + mainBuilding.getCurrentFloorIndex());
    }

    @FXML
    private void prevRoom(){
        if(mainBuilding.hasPrevFloor()){
            mainBuilding.increaseFloor();
            mainBuilding.render();
            //mainPane.getChildren().remove(mainPane.getChildren().size()-1);
            //System.out.println("Wow");
            //mainBuilding.prevFloor();
            //mainPane.getChildren().add(mainBuilding.getCurrentFloor());
        }else{
            System.out.println("No prev floor");
        }
        floorLevel.setText("Floor " + mainBuilding.getCurrentFloorIndex());
    }



}
