package fireescapedemo;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FXMLSimulationController implements Initializable {
    @FXML
    AnchorPane mainPane;
    @FXML
    Pane assetPane;
    @FXML
    Pane simControlPane;
    @FXML
    Label floorLevel;
    @FXML
    Label timerLabel;
    @FXML
    Pane mapPane;
    @FXML
    Button StartSimButton;
    @FXML
    Button PauseSimButton;
    @FXML
    Button ResetSimButton;
    QuadTree quadTree;
    public Building mainBuilding;// = new Building();
    SceneManager manager;
    Timeline timeline;
   // Label timer;
    Label employeesLeft;
    int second = 0;
    boolean paused = false;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ResetSimButton.setText("Reset");
        PauseSimButton.setDisable(true);
        this.manager = new SceneManager();
        this.mainBuilding = manager.getGlobalBuilding();
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e-> addSecond())
        );
        if(mainBuilding == null){
            System.out.println("Building is null. ERROR");
            mainBuilding = new Building(14,13,50, mapPane);
        }
        mainBuilding.setWindowContainer(mapPane);
        mainBuilding.disableBuild();
        //mainBuilding.enableSim();

        floorLevel.setText("Floor " + mainBuilding.getCurrentFloorIndex());
        mainBuilding.initialiseView();
        this.PauseSimButton.setDisable(true);
        timeline.setCycleCount(Timeline.INDEFINITE);
        employeesLeft = new Label("Employees Left: " + mainBuilding.getInitialEmployeeCount());
        employeesLeft.setLayoutX(50);
        employeesLeft.setLayoutY(100);
        assetPane.getChildren().add(employeesLeft);
        initAnimation();
        simControlPane.toFront();
    }

    void addSecond(){second++; timerLabel.setText(Integer.toString(second) + "s");}



    public void initAnimation(){
        AnimationTimer t = new AnimationTimer(){
            @Override
            public void handle(long now) {
                onUpdate();
            }
        };
        t.start();
    }
    double nX, nY, nW, nH;
    int cap = 50;
    boolean drawLines = false;
    boolean started = false;
    private void onUpdate() {
        int floorNum = 0;
        if(!paused){
            for(Floor floor : mainBuilding.getFloors()) {
                for (Employee e : floor.employees) {
                    e.update(floor);
                }
                //collisionBetweenWallandEmployee(floor.employees,floor.getWallsNodes(),floor.employees.get(0).getSize());

                if(started == true && floor.employees.size() > 0){

                    nW = floor.getActualWidth();
                    nH = floor.getActualHeight();
                    nX = floor.getActualX();
                    nY = floor.getActualY();
                    quadTree = new QuadTree(cap, nX, nY, nW, nH);
                    quadTree.insertAll(floor.employees);
                    quadTree.checkCollisions();
                }else{
                    //System.out.println("Floor error " + floorNum);
                }
                //quadTree.insertAll(floor.employees);
                //quadTree.drawLines(mapPane);
                if (drawLines) {
                    quadTree.drawLines(mapPane);
                }
                floorNum++;
            }
            mainBuilding.calculateInitialEmployeeCount();
            employeesLeft.setText("Employees Left: " + mainBuilding.getInitialEmployeeCount());

            if (mainBuilding.getInitialEmployeeCount() <= 0) {
                timeline.stop();
                // timer.setTextFill(Color.RED);
            }
        }
    }

    private void EmployeeCollision(){
        for(Employee e : this.mainBuilding.getCurrentFloor().getEmployees()){
            double actualx = e.getNode().getLayoutX();
            double actualy = e.getNode().getLayoutY();
            int gridX = Building.normaliseXCoord(actualx, this.mainBuilding);
            int gridY = Building.normaliseYCoord(actualy, this.mainBuilding);
            Tile currTile = mainBuilding.getCurrentFloor().getTile(gridX, gridY);

            //handle N Collisions
            double nBorder = actualy-(e.getSize());
            if(currTile.getAccess(0) == false){
                if(nBorder <= currTile.getActualY() && actualy >= currTile.getActualY()){
                    System.out.println("COLLISION ON N BORDER");
                }
            }

            //handle E Collisions
            double eBorder = actualx+(e.getSize());
            if(currTile.getAccess(1) == false){
                if(eBorder >= (currTile.getActualX()+currTile.getWidth()) && actualx <= (currTile.getActualX()+currTile.getWidth())){
                    System.out.println("COLLISION ON E BORDER");
                }
            }

            //handle S Collisions
            double sBorder = actualy+(e.getSize());
            if(currTile.getAccess(2) == false){
                if(sBorder >= (currTile.getActualY()+currTile.getHeight()) && actualy <= (currTile.getActualY()+currTile.getHeight())){
                    System.out.println("COLLISION ON S BORDER");
                }
            }

            //handle W Collisions
            double wBorder = actualx-(e.getSize());
            if(currTile.getAccess(3) == false){
                if(wBorder <= currTile.getActualX() && actualx >= currTile.getActualY()){
                    System.out.println("COLLISION ON W BORDER");
                }
            }

/*

            if(mainBuilding.getInitialEmployeeCount() <= 0){
                timeline.stop();
               // timer.setTextFill(Color.RED);
            }

 */
        }
    }

    private void collisionBetweenWallandEmployee(ArrayList<Employee> employees,ArrayList<Line> walls, double size){
        int i,j, empLen = employees.size(), wallLen = walls.size();
        double newX, newY;
        double distance, displacement,px,py,tx,ty;
        Point2D newV;
        Employee emp;
        Line wall;
        boolean[] b;
        for(i = 0; i < empLen; i++){
            emp = employees.get(i);
            for(j = 0; j < wallLen; j++){
                wall = walls.get(j);
                b = intersection((Circle)(emp.fxNode),wall);
                if(b[0]){
                    /*
                    if(!b[1]){newX = emp.velocity.getX(); newY = 0;}
                    else{newX = 0; newY = emp.velocity.getY();}
                    px = point.view.getLayoutX();py = point.view.getLayoutY();
                    tx = target.view.getLayoutX();ty = point.view.getLayoutY();
                    size = ((Circle)((Employee)(point)).view).getRadius() ;
                    distance =  Math.sqrt(Math.pow(px - tx,2) + Math.pow(py - ty,2));
                    displacement = 0.5d * (distance- size - size);
                    newV = new Point2D((newX*size), (newY*size));
                    if(!b[1]){}
                    newX = emp.view.getLayoutX() - newV.getX(); newY = emp.view.getLayoutY() - newV.getY();
                    emp.view.setLayoutX(newX);
                    emp.view.setLayoutY(newY);
                    break;*/
                }
            }
        }

    }

    private boolean[] intersection(Circle employee, Line wall){
        //System.out.println("wall coords xS: " + wall.getStartX() + ", yS: " + wall.getStartY()
       // + " - xE: " + wall.getEndX() +  ", yE: " + wall.getEndY());

        if(wall.getStartX() == wall.getEndX()){
            double minY = wall.getStartY() > wall.getEndY()? wall.getEndY() : wall.getStartY();
            double maxY = wall.getStartY() < wall.getEndY()? wall.getEndY() : wall.getStartY();
            boolean [] b = {employee.getLayoutX() - employee.getRadius() < wall.getStartX() &&
                    employee.getLayoutX() + employee.getRadius() > wall.getStartX() &&
                    employee.getLayoutY() - employee.getRadius() > minY &&
                    employee.getLayoutY() + employee.getRadius() < maxY, false};
            return b;
        }

        double minX = wall.getStartX() > wall.getEndX()? wall.getEndX() : wall.getStartX();
        double maxX = wall.getStartX() < wall.getEndX()? wall.getEndX() : wall.getStartX();

        boolean[] b =  {employee.getLayoutY() - employee.getRadius() < wall.getStartY() &&
                employee.getLayoutY() + employee.getRadius() > wall.getStartY() &&
                employee.getLayoutX() - employee.getRadius() > minX &&
                employee.getLayoutX() + employee.getRadius() < maxX,true};
        return b;
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


    public void StartSim() {
        this.PauseSimButton.setDisable(false);
/*
        System.out.println("in alarm handler");
        timerLabel.setText(0 + "s");
        for(Floor floor : mainBuilding.getFloors()){
            for(Employee employee : floor.employees){
                employee.setCurrentState(Employee.State.FindRoute);
            }
        }
*/
        timeline.play();
        started= true;
        PauseSimButton.setText("Pause");
        PauseSimButton.setDisable(false);
        this.StartSimButton.setDisable(true);

        if(!paused) {
            System.out.println("in alarm handler");
            for (Floor floor : mainBuilding.getFloors()) {
                for (Employee employee : floor.employees) {
                    employee.setCurrentState(Employee.State.FindRoute);
                }
            }
        }
        this.paused = false;
    }

    public void PauseSim() {
        this.StartSimButton.setDisable(false);
        this.PauseSimButton.setDisable(true);
        this.paused = !this.paused;
        this.timeline.pause();
    }

    public void StopSim(ActionEvent actionEvent) {
        this.PauseSimButton.setDisable(false);
        this.StartSimButton.setDisable(false);
        this.paused = false;
        timeline.stop();
    }

    public void ResetSim() {
        second = 0;
        this.timeline.stop();
       // timer.setTextFill(Color.BLACK);
        for(Floor floor : mainBuilding.getFloors()){
            for(Employee employee : floor.employees){
                employee.fxNode.setOpacity(0);
                employee.fxNode.setLayoutX(employee.oriTile.getGridX());
                employee.fxNode.setLayoutY(employee.oriTile.getGridY());
                employee.setCurrentState(Employee.State.Idle);
                employee.toggleExited();
            }
        }
        mainBuilding.calculateInitialEmployeeCount();
        employeesLeft.setText("Employees Left: " + mainBuilding.getInitialEmployeeCount());
        this.StartSimButton.setDisable(false);
        paused = false;
        started = false;
        PauseSimButton.setDisable(true);
        PauseSimButton.setText("Pause");
        timeline.stop();
        mainBuilding.initialiseView();
    }

    public void returnHome(ActionEvent actionEvent) throws IOException {
        //this.manager.setGlobalBuilding(null);
        //this.uninitialise();
        this.manager.showScene("home");
    }

    public void uninitialise(){
        this.mapPane.getChildren().clear();
        this.mainBuilding = null;
    }
}
