package fireescapedemo;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class FXMLBuildingController implements Initializable {

    @FXML
    private Rectangle play;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private Pane assetPane;
    @FXML
    private Label floorLevel;
    int floorNum = 0;

    View currentView;
    enum View
    {
        BUILDING,
        SIMULATION
    }


    ArrayList<Employee> characters;
    ArrayList<Tile> floors;

    public static Building mainBuilding = new Building(14,13,50);
    static Color c = Color.WHITESMOKE;

    @FXML
    private void handleButtonAction(ActionEvent event) {

    }

    @FXML
    private void renderBlocks(){
        //mainBuilding.renderBlocks();
    }

    @FXML
    private void nextRoom(){
        if(mainBuilding.hasNextFloor()){
            mainPane.getChildren().remove(mainPane.getChildren().size()-1);
            System.out.println("Wow");
            mainBuilding.nextFloor();
            mainPane.getChildren().add(mainBuilding.getCurrentFloor());
            floorNum++;
        }else{
            System.out.println("No next floor");
        }
        floorLevel.setText("Floor " + floorNum);
    }

    @FXML
    private void prevRoom(){
        if(mainBuilding.hasPrevFloor()){
            mainPane.getChildren().remove(mainPane.getChildren().size()-1);
            System.out.println("Wow");
            mainBuilding.prevFloor();
            mainPane.getChildren().add(mainBuilding.getCurrentFloor());
            floorNum--;
        }else{
            System.out.println("No prev floor");
        }
        floorLevel.setText("Floor " + floorNum);
    }

    @FXML
    private void addRoom(){mainBuilding.addFloor(); System.out.println("Floor added");}

    private void onUpdate(){
        characters.forEach((prefab) -> {
            prefab.update();
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //initFloors(20,10,25);

        floorLevel.setText("Floor " + floorNum);

        mainPane.getChildren().add(mainBuilding.getCurrentFloor());

        Button wallButton = new Button("Wall Button");
        wallButton.setOnAction((ActionEvent e) -> {
            System.out.println("Grey");
            c = Color.GRAY;
        });
        wallButton.setLayoutX(50);
        wallButton.setLayoutY(50);
        System.out.println(wallButton.getLayoutX());
        Button employeeButton = new Button("Employee Button");
        employeeButton.setOnAction((ActionEvent e) -> {
            c = Color.RED;
        });

        employeeButton.setLayoutX(50);
        employeeButton.setLayoutY(100);


        Button stairsButton = new Button("Stairs Button");
        stairsButton.setOnAction((ActionEvent e) -> {
            c = Color.AQUAMARINE;
        });
        stairsButton.setLayoutX(50);
        stairsButton.setLayoutY(150);
        assetPane.getChildren().add(wallButton);
        assetPane.getChildren().add(employeeButton);
        assetPane.getChildren().add(stairsButton);
        /*initCharacters();
        initTestLabel();
        initAnimation();*/
    }




    public void initTestLabel(){
        Label l;

        for(int i = 0; i < 550; i+=50){
            l = new Label();
            l.setLayoutX(i);
            l.setLayoutY(0);
            l.setText(Double.toString(l.getLayoutX()));
            mainPane.getChildren().add(l);
        }


    }

    public void initCharacters(){
        characters = new ArrayList();
        characters.add(new Employee(play, floors.get(0)));
        characters.get(0).setVelocityX(1);
    }

    public void initFloors(int length, int width, double size){
        floors = new ArrayList();
        for(int i = 0; i < size * length; i += size){
            for(int j = 0; j< size * width; j+= size){
                floors.add(new Tile(j,i,size));
            }
        }

        for(Tile floor : floors){mainPane.getChildren().add(floor.block);}
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

    class Employee extends Actor{
        int speed;
        boolean foundSteps;
        Tile currentFloor;
        Employee(Node view, Tile startingFloor){
            super(view);
            Random rand = new Random();
            this.speed = (rand.nextInt(2)+1);
            this.foundSteps = false;
            this.currentFloor = startingFloor;

        }

        Employee(Node view, Point2D vector, Tile startingFloor){
            super(view,vector);
            Random rand = new Random();
            this.speed = (rand.nextInt(2)+1);
            this.foundSteps = false;
            this.currentFloor = startingFloor;
        }

        @Override
        public void update(){

        }


        /*
        @Override
        public void update(){
            if(this.foundSteps == false){
                double width = ((Rectangle)(this.view)).getWidth();
                double fWidth = this.currentFloor.getWidth();
                if(this.view.getLayoutX() >= fWidth - width){
                    this.setVelocityX(-1);
                    this.speed = -speed;
                }else if(this.view.getLayoutX() <= this.currentFloor.getLayoutX()){
                    this.setVelocityX(1);
                    this.speed = -speed;
                }
            }

            //compute gravity
            if(applyGravity())  { this.setVelocityY(3); }
            else                { this.setVelocityY(0); }

            this.view.setLayoutX(this.view.getLayoutX() +  velocity.getX() + speed);
            this.view.setLayoutY(this.view.getLayoutY() +  velocity.getY());
        }
        */
        public int getSpeed(){
            return this.speed;
        }



    }

}
