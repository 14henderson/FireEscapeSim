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
import javafx.scene.text.Text;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
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
    @FXML
    private Button saveButton;
    @FXML
    private Text errorText;
    int floorNum = 0;
    SceneManager manager;



    ArrayList<Employee> characters;
    //ArrayList<Tile> floors;

    public Building mainBuilding;// = new Building();
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
        mainBuilding.render();
    }



    @FXML
    private void nextRoom(){
        if(mainBuilding.hasNextFloor()){
            mainBuilding.increaseFloor();
            mainBuilding.render();
            //mainPane.getChildren().remove(mainPane.getChildren().size()-1);
           //System.out.println("Wow");
            //mainBuilding.increaseFloor();
            //mainPane.getChildren().add(mainBuilding.getCurrentFloor());
           // floorNum++;
        }else{
            System.out.println("No next floor");
        }
        floorLevel.setText("Floor " + mainBuilding.getCurrentFloorIndex());
    }

    @FXML
    private void prevRoom(){
        if(mainBuilding.hasPrevFloor()){
            mainBuilding.decreaseFloor();
            mainBuilding.render();
           // mainPane.getChildren().remove(mainPane.getChildren().size()-1);
           // System.out.println("Wow");
           // mainBuilding.prevFloor();
           // mainPane.getChildren().add(mainBuilding.getCurrentFloor());
         //   floorNum--;
        }else{
            System.out.println("No prev floor");
        }
        floorLevel.setText("Floor " + mainBuilding.getCurrentFloorIndex());
    }

    @FXML
    private void addRoom(){
        mainBuilding.addFloor();
        System.out.println("Floor added");
    }


    private void onUpdate(){
        characters.forEach((prefab) -> {
            prefab.update();
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.manager = new SceneManager();
        //if(manager.globalBuilding != null){
        //    this.mainBuilding = manager.globalBuilding;
        //}


        this.mainBuilding = null;
        String filename = "C:\\Users\\Niklas Henderson\\Documents\\my_building.map";
        try {
            java.io.FileInputStream fis = new java.io.FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(fis);
            this.mainBuilding = (Building) in.readObject();
            in.close();
            fis.close();
            //System.out.println("Building has: "+loadedBuilding.getFloors().size());
            //this.mainBuilding = loadedBuilding;
            System.out.println("Building has: "+this.mainBuilding.getFloors().size()+" floors.");
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Serializable Error thrown: " + ex);
            this.mainBuilding = new Building(14,13,50, mainPane);
        }


        //

        System.out.println("This has been loaded");
        floorLevel.setText("Floor " + floorNum);
        mainBuilding.render();
        wallButton.setOnAction((ActionEvent e) -> {
            c = Color.GRAY;
        });
        employeeButton.setOnAction((ActionEvent e) -> {
            c = Color.RED;
        });
        stairsButton.setOnAction((ActionEvent e) -> {
            c = Color.AQUAMARINE;
        });
        errorText.setText("");
    }

    /**
     * Opens the save file dialog to save the serializable building object to be opened in the simulation.
     * @throws IOException
     */
    public void saveBuilding() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Copy of Graphs");
        fileChooser.setFileFilter(new FileNameExtensionFilter("map files (*.map)","map"));
        fileChooser.setSelectedFile(new File("my_building.map"));

        //If save window has been closed after successfully pressing save
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getPath();
            try{
                //Building object is serializable which allows the saving of the object.
                java.io.FileOutputStream fos = new java.io.FileOutputStream(filename);
                ObjectOutputStream out = new ObjectOutputStream(fos);
                out.writeObject(mainBuilding);
                out.close();
                fos.close();
                System.out.println("Current Building Object"+this.mainBuilding.toString());

                this.mainBuilding = null;
                this.manager.showScene("home");
            }catch(IOException ex){
                errorText.setText("Error: Error saving your map. Please submit a ticket on our forum.");
                System.out.println("Serializable Error thrown: "+ex);
            }
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
        characters.add(new Employee(play, mainBuilding.getCurrentFloor().getTile(0, 0)));
        characters.get(0).setVelocityX(1);
    }

    /*
    public void initFloors(int length, int width, double size){
        floors = new ArrayList();
        for(int i = 0; i < size * length; i += size){
            for(int j = 0; j< size * width; j+= size){
                floors.add(new Tile(j,i,size));
            }
        }

        for(Tile floor : floors){mainPane.getChildren().add(floor.block);}
    }
     */


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
