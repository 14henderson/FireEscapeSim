package fireescapedemo;

import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
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
import java.util.Iterator;
import java.util.ResourceBundle;

public class FXMLSimulationController implements Initializable {
    @FXML
    AnchorPane mainPane;
    @FXML
    Pane assetPane;
    @FXML
    TabPane floorPaneContainer;
    @FXML
    Label floorLevel;
    @FXML
    Label timerLabel;
    @FXML
    Pane firstFloorPane;
    @FXML
    Button StartSimButton;
    @FXML
    Button PauseSimButton;
    @FXML
    Button ResetSimButton;
    @FXML
    ImageView scaleImage;
    @FXML
    Button zoomInButton, zoomOutButton;
    @FXML
    Button panUpButton, panLeftButton, panRightButton, panDownButton;
    QuadTree quadTree;
    public Building mainBuilding;// = new Building();
    SceneManager manager;
   // Timeline timeline;
    static int speedScaler = 1;
    PDFCreator.simResults provisionalResults;
    @FXML
    Label employeesLeft;
    int second = 0;
    boolean paused = false;
    public final int minZoom = 30;
    public final int maxZoom = 100;
    private String reportFilename;
    private Alert reportAlert;
    private int timeTick = 0;
    private AnimationTimer t;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.manager = new SceneManager();
        this.mainBuilding = manager.getGlobalBuilding();
        mainBuilding.setTabPane(floorPaneContainer);
        mainBuilding.setCalledBySim(true);
        //
        // timeline = new Timeline(new KeyFrame(Duration.seconds(1), e-> addSecond()));
        //if issue with loading
        if(mainBuilding == null){
            System.out.println("Building is null. ERROR");
            mainBuilding = new Building(14,13, new TabPane());
        }

        for(Floor f : mainBuilding.getFloors()){
            Tab newPane = new Tab(f.getId());
            Pane newFloorPane = new Pane();
            newPane.setContent(newFloorPane);
            f.setPane(newFloorPane);
            this.floorPaneContainer.getTabs().add(newPane);
        }

        floorPaneContainer.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
                if(started == false) {
                    mainBuilding.getCurrentFloor().initialiseView();
                }
            }
        });


        mainBuilding.initialiseAll();
        mainBuilding.getCurrentFloor().setTileSize(50);
        mainBuilding.zoom(0);
        employeesLeft.setText("x" + mainBuilding.getInitialEmployeeCount());
        reportFilename = "[your local directory]";
        reportAlert = new Alert(Alert.AlertType.INFORMATION);
        reportAlert.setTitle("Simulation Complete");
        reportAlert.setContentText("Simulation has been completed. Report has been generated.");
        mainBuilding.calculateInitialEmployeeCount();
        provisionalResults = new PDFCreator.simResults(mainBuilding.getInitialEmployeeCount(), -1, "0s");

        mainBuilding.disableBuild();
//        timeline.setCycleCount(Timeline.INDEFINITE);
        initAnimation();
    }

    void addSecond(){second++; timerLabel.setText(Integer.toString(second) + "s");}



    public void initAnimation(){
        this.t = new AnimationTimer(){
            @Override
            public void handle(long now) {
                onUpdate();
            }
        };
    }
    double nX, nY, nW, nH;
    int cap = 1000000;
    boolean drawLines = false;
    boolean started = false;
    private void onUpdate() {
        int floorNum = 0;
        if(!paused){
            EmployeeCollision();
            timeTick++;
            if(timeTick % 100 == 0){
                addSecond();
                timeTick = 0;
            }
            if(employeesLeft.getText().equals("x0") && started == true && !timerLabel.getText().equals("0s")){
                provisionalResults.setSoulsEnd(mainBuilding.getInitialEmployeeCount());
                provisionalResults.setTimeTaken(timerLabel.getText());
                if(!reportAlert.isShowing()){
                    reportAlert.show();
                    PDFCreator.createReport(this.provisionalResults);
                }
                this.started = false;
                this.ResetSim();
            }

            Iterator<Floor> floorIt = mainBuilding.getFloors().iterator();
            Floor f;
            Iterator<Employee> employeeIt;
            Employee e;
            Iterator<Line> wallIt;
            Line wall;
            boolean b;
            while(floorIt.hasNext()) {
                f = floorIt.next();
                employeeIt = f.getEmployees().iterator();
                while (employeeIt.hasNext()) {
                    e = employeeIt.next();
                    e.update(f);
                    wallIt = f.getWallsNodes().iterator();
                    /*while(wallIt.hasNext()){
                        wall = wallIt.next();
                        employeeAgainstWall(e,wall);
                    }*/
                }

                if(started == true && f.employees.size() > 0){

                    nW = f.getActualWidth();
                    nH = f.getActualHeight();
                    nX = f.getActualX();
                    nY = f.getActualY();
                    quadTree = new QuadTree(cap, nX, nY, nW, nH);
                    quadTree.insertAll(f.employees);
                    quadTree.checkCollisions();
                }

                if (drawLines) {
                    quadTree.drawLines(firstFloorPane);
                }
                floorNum++;
            }
            mainBuilding.calculateInitialEmployeeCount();
            employeesLeft.setText("x" + mainBuilding.getInitialEmployeeCount());
        }
    }


    public boolean employeeAgainstWall(Employee e, Line wall){
        boolean col = false;
        double px = e.fxNode.getLayoutX() , py = e.fxNode.getLayoutY();
        double wallX1 = wall.getEndX() - wall.getStartX(), wallX2 = px - wall.getStartX();
        double wallY1 = wall.getEndY() - wall.getStartY(), wallY2 = py - wall.getStartY();

        double wallLength = wallX1*wallX1 + wallY1 *wallY1;
        //find dot product against the wall lengt
        double t = Math.min(0,Math.max(wallLength, (wallX1 * wallX2 + wallY1 * wallY2)))/wallLength;

        double dx = wall.getStartX() + t * wallX1;
        double dy = wall.getStartY() + t * wallY1;

        double distance = Math.sqrt(Math.pow(px - dx, 2) + Math.pow(py - dy, 2));
        double raduis = ((Circle)e.fxNode).getRadius();
        if(distance <= (raduis + wall.getStrokeWidth())){
            col = true;
            double overlap = (distance - raduis - wall.getStrokeWidth());

            e.fxNode.setLayoutX(px - (overlap * (px - dx) / distance));
            e.fxNode.setLayoutY(py - (overlap * (py - dy) / distance));
        }

        return col;
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

        }
    }





    public void StartSim() {
        this.PauseSimButton.setDisable(false);
        this.zoomInButton.setDisable(true);
        this.zoomOutButton.setDisable(true);
        this.panDownButton.setDisable(true);
        this.panLeftButton.setDisable(true);
        this.panRightButton.setDisable(true);
        this.panUpButton.setDisable(true);

        t.start();

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
       // this.timeline.pause();
    }


    public void ResetSim() {
        second = 0;
      //  this.timeline.stop();
        this.zoomInButton.setDisable(false);
        this.zoomOutButton.setDisable(false);
        this.panDownButton.setDisable(false);
        this.panLeftButton.setDisable(false);
        this.panRightButton.setDisable(false);
        this.panUpButton.setDisable(false);

        for(Floor floor : mainBuilding.getFloors()){
            for(Employee employee : floor.employees){
                employee.fxNode.setOpacity(0);
                employee.fxNode.setLayoutX(employee.oriTile.getGridX());
                employee.fxNode.setLayoutY(employee.oriTile.getGridY());
                employee.setCurrentState(Employee.State.Idle);
                employee.toggleExited();
            }
        }
        timerLabel.setText("0s");
        mainBuilding.calculateInitialEmployeeCount();
        employeesLeft.setText("x" + mainBuilding.getInitialEmployeeCount());
        this.StartSimButton.setDisable(false);
        paused = false;
        started = false;
        PauseSimButton.setDisable(true);
        t.stop();
        mainBuilding.initialiseAll();
    }



    public void returnHome(ActionEvent actionEvent) throws IOException {
        this.manager.showScene("home");
    }

    public void uninitialise(){
        this.firstFloorPane.getChildren().clear();
        this.mainBuilding = null;
    }


    @FXML
    public void zoomIn(){
        if(this.mainBuilding.getSize()+2 > this.minZoom && this.mainBuilding.getSize()+2 < this.maxZoom) {
            mainBuilding.zoom(2);
            this.mainBuilding.initialiseView();
            this.scaleImage.setFitWidth(this.mainBuilding.getSize());
        }
    }
    @FXML
    public void zoomOut(){
        if(this.mainBuilding.getSize()-2 > this.minZoom && this.mainBuilding.getSize()-2 < this.maxZoom) {
            mainBuilding.zoom(-2);
            this.mainBuilding.initialiseView();
            this.scaleImage.setFitWidth(this.mainBuilding.getSize());
        }
    }
    @FXML
    public void panUp(){
        mainBuilding.pan(0, 30);
    }
    @FXML
    public void panDown() {
        mainBuilding.pan(0, -30);
    }
    @FXML
    public void panRight(){
        mainBuilding.pan(-30, 0);
    }
    @FXML
    public void panLeft(){
        mainBuilding.pan(30, 0);
    }

    @FXML
    public void createReportButton(){
        PDFCreator.createReport(new PDFCreator.simResults(0, 0, "0"));
    }


}



