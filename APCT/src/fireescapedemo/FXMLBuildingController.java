package fireescapedemo;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.io.ObjectOutputStream;
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
    @FXML
    private Button wallButton;
    @FXML
    private Button stairsButton;
    @FXML
    private Button employeeButton;

    int floorNum = 0;
    SceneManager manager= new SceneManager();



    ArrayList<Employee> characters;
    ArrayList<Tile> floors;

    public Building mainBuilding = new Building();
    static Color c = Color.WHITESMOKE;


    @FXML
    private void handleButtonAction(ActionEvent event) {
    }

    @FXML
    private void renderBlocks() throws IOException {
        //mainBuilding.renderBlocks();
        manager.addScene("FXMLSimulation.fxml", "simulation");
        manager.showScene("simulation");
        Tile.disableBuild();
        mainBuilding.renderBlocks();
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
        System.out.println("This has been loaded");
        floorLevel.setText("Floor " + floorNum);
        mainPane.getChildren().add(mainBuilding.getCurrentFloor());
        wallButton.setOnAction((ActionEvent e) -> {
            c = Color.GRAY;
        });
        employeeButton.setOnAction((ActionEvent e) -> {
            c = Color.RED;
        });
        stairsButton.setOnAction((ActionEvent e) -> {
            c = Color.AQUAMARINE;
        });
    }


    public void saveBuilding(){
        try{
            String filename = "hand.ser";
            java.io.FileOutputStream fos = new java.io.FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(this.mainBuilding);
            out.close();

        }catch(IOException ex){
            System.out.println("Serializable Error thrown: "+ex);
        }
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

    class Employee extends Actor{        int speed;
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
