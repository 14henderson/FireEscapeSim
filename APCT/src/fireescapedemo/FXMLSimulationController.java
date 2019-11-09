package fireescapedemo;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

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

        if(mainBuilding == null){
            System.out.println("Building is null. ERROR");
            mainBuilding = new Building(14,13,50, mapPane);
        }


        mainBuilding.setWindowContainer(mapPane);
        mainBuilding.disableBuild();

        p = new Fire(50,200, new Tile(0,0, 0, 0, 0));
        floorLevel.setText("Floor " + mainBuilding.getCurrentFloorIndex());
        mainBuilding.initialiseView();

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
                timeline.play();
                alarm.setDisable(true);
            }
        });

        Button reset = new Button("RESET");
        reset.setLayoutY(30);
        reset.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                second = 0;
                timer.setTextFill(Color.BLACK);
                for(Floor floor : mainBuilding.getFloors()){
                    for(Employee employee : floor.employees){
                        employee.view.setOpacity(0);
                        employee.view.setLayoutX(employee.oriTile.getGridX());
                        employee.view.setLayoutY(employee.oriTile.getGridY());
                        employee.setCurrentState(Employee.State.Idle);
                        employee.toggleExited();
                    }
                }
                mainBuilding.calculateInitialEmployeeCount();
                employeesLeft.setText("Employees Left: " + mainBuilding.getInitialEmployeeCount());
            }
        });

        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e-> addSecond())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timer = new Label("Timer: 0s");
        timer.setLayoutX(50);
        timer.setLayoutY(50);
        employeesLeft = new Label("Employees Left: " + mainBuilding.getInitialEmployeeCount());
        employeesLeft.setLayoutX(50);
        employeesLeft.setLayoutY(100);
        assetPane.getChildren().add(alarm);
        //assetPane.getChildren().add(reset);
        assetPane.getChildren().add(p.view);
        assetPane.getChildren().add(timer);
        assetPane.getChildren().add(employeesLeft);
        initAnimation();
    }
    Timeline timeline;
    Label timer;
    Label employeesLeft;
    int second = 0;
    void addSecond(){second++; timer.setText("Timer: " + Integer.toString(second) + "s");}



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
        mainBuilding.calculateInitialEmployeeCount();
        employeesLeft.setText("Employees Left: " + mainBuilding.getInitialEmployeeCount());
        if(mainBuilding.getInitialEmployeeCount() <= 0){
            timeline.stop();
            timer.setTextFill(Color.RED);
        }
    }

    @FXML
    private void nextRoom(){
        if(mainBuilding.hasNextFloor()){
            mainBuilding.increaseFloor();
            mainBuilding.initialiseView();
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
            mainBuilding.initialiseView();
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
