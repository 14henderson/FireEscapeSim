package fireescapedemo;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.shape.Line;
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

import static javax.swing.text.html.HTML.Tag.HEAD;

public class FXMLBuildingController implements Initializable {

    @FXML
    private Rectangle play;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private Pane assetPane;
    @FXML
    private Pane mapPane;
    @FXML
    private Label floorLevel;
    @FXML
    private Button exitButton;
    @FXML
    private Button stairsButton;
    @FXML
    private Button employeeButton;
    @FXML
    private Button defaultButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveToSim;
    @FXML
    private Text errorText;
    @FXML
    private Button normWall;
    @FXML
    private Button stairWall;
    int floorNum = 0;

    SceneManager manager;
    public static LineTile[][] lineTiles = null;
    private static double lineCords[] = new double[2];
    private static boolean lineClicked;
    ArrayList<Employee> characters;
    public static Building mainBuilding;// = new Building();
    static Color c = Color.WHITESMOKE;
    private static Tile.BlockType actionType;
    private static Line movingWall;

    @FXML
    private void handleButtonAction(ActionEvent event) {
    }

    @FXML
    private void renderBlocks() throws IOException {

    }

    public static void refreshLineTiles(){
        for(LineTile[] tiles : lineTiles){
            for(LineTile tile : tiles){
                tile.bringToFront();
            }
        }
    }


    public void initLineBlocks(){
        int size = this.mainBuilding.getSize();
        int width = this.mainBuilding.getWidth();
        int height = this.mainBuilding.getHeight();
        this.lineTiles = new LineTile[width+1][height+1];
        for(int i = 0; i < this.lineTiles.length; i ++){
            for(int j = 0; j< this.lineTiles[i].length; j++) {
                this.lineTiles[i][j] = new LineTile(((i*size) - (size / 2)) ,((j*size) - (size / 2)),
                                            size, this.mainPane, this.mainBuilding);
            }}
    }
    public void renderLineBlocks(){
        for (LineTile[] lineTileRow : this.lineTiles) {
            for (LineTile lineTile : lineTileRow) {
                lineTile.render();
            }
        }
    }


    public void enableLineBlocks(){
        for (LineTile[] lineTileRow : this.lineTiles) {
            for (LineTile lineTile : lineTileRow) {
                lineTile.getLineTileRect().setVisible(true);
            }
        }
    }

    public void disableLineBlocks(){
        for (LineTile[] lineTileRow : this.lineTiles) {
            for (LineTile lineTile : lineTileRow) {
                lineTile.getLineTileRect().setVisible(false);
            }
        }
    }



    @FXML
    private void nextRoom(){
        if(mainBuilding.hasNextFloor()){
            mainBuilding.increaseFloor();
            mainBuilding.initialiseView();
            this.renderLineBlocks();
            this.disableLineBlocks();
            this.renderDragLine();
            cancelLineClicked();
        }else{
            System.out.println("No next floor");
        }
        floorLevel.setText("Floor " + mainBuilding.getCurrentFloorIndex());
    }

    @FXML
    private void prevRoom(){
        if(mainBuilding.hasPrevFloor()){
            mainBuilding.decreaseFloor();
            mainBuilding.initialiseView();
            this.renderLineBlocks();
            this.disableLineBlocks();
            this.renderDragLine();
            cancelLineClicked();
        }else{
            System.out.println("No prev floor");
        }
        floorLevel.setText("Floor " + mainBuilding.getCurrentFloorIndex());
    }

    @FXML
    private void addRoom(){
        mainBuilding.addFloor();
        this.nextRoom();
    }


    private void onUpdate(){
        /*characters.forEach((prefab) -> {
            //prefab.update();
        });*/
    }


    public static Tile.BlockType getActionType(){return actionType;}


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.manager = new SceneManager();
        mainBuilding = manager.getGlobalBuilding();
        if(mainBuilding == null){
            System.out.println("Building is null. Making new one");
            mainBuilding = new Building(40,30,20, mapPane);
        }
        this.actionType = Tile.BlockType.Default;
        this.mainBuilding.enableBuild();
        this.mainBuilding.setWindowContainer(mapPane);
        System.out.println("This has been loaded");
        floorLevel.setText("Floor " + floorNum);
        mainBuilding.updateView();
        normWall.setOnAction((ActionEvent e) -> {
           //
        });
        mainBuilding.initialiseView();
        normWall.setOnAction((ActionEvent e) -> {
            this.actionType = Tile.BlockType.Default;
            this.enableLineBlocks();
        });
        stairWall.setOnAction((ActionEvent e) -> {
            this.actionType = Tile.BlockType.Default;
            this.enableLineBlocks();
        });
        errorText.setText("");



        this.mapPane.setOnScroll((ScrollEvent event) -> {
            if(event.getDeltaY() < 0) {
                mainBuilding.zoom(-1);
            }else if(event.getDeltaY() > 0){
                mainBuilding.zoom(1);
            }
            //System.out.println(mainBuilding.getSize());
            //mainBuilding.render();
            //renderLineBlocks();
        });
        this.initLineBlocks();
        this.renderLineBlocks();
        this.disableLineBlocks();
        this.renderDragLine();
    }

    @FXML
    public void defaultTileButton(){
        this.actionType = Tile.BlockType.Default;
        this.disableLineBlocks();
    }
    @FXML
    public void exitTileButton(){
        this.actionType = Tile.BlockType.Exit;
        this.disableLineBlocks();
    }
    @FXML
    public void stairsTileButton(){
        this.actionType = Tile.BlockType.Stairs;
        this.disableLineBlocks();
    }
    @FXML
    public void employeeTileButton(){
        System.out.println("Bere");
        this.actionType = Tile.BlockType.Employee;
        this.disableLineBlocks();
    }



    /**
     * Opens the save file dialog to save the serializable building object to be opened in the simulation.
     * @throws IOException
     */
    public void saveToHome() throws IOException {
        if(saveMap()){
            this.manager.setGlobalBuilding(null);
            this.uninitialise();
            this.manager.showScene("home");
        }
    }

    public void cancelToHome() throws IOException {
        this.manager.setGlobalBuilding(null);
        this.uninitialise();
        this.manager.showScene("home");
    }

    public void saveToSim() throws IOException{
        if(saveMap()){
            manager.setGlobalBuilding(this.mainBuilding);
            this.uninitialise();
            manager.showScene("simulation");
        }
    }

    public void initTestLabel(){
        Label l;
        for(int i = 0; i < 550; i+=50) {
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



    public void renderDragLine(){
        this.movingWall = new Line(0, 0, 0, 0);
        this.movingWall.setStroke(Color.GREEN);
        this.movingWall.setStrokeWidth(10*mainBuilding.getSize()/50.0);
        mainBuilding.windowContainer.getChildren().add(this.movingWall);
        this.movingWall.setVisible(false);

        this.mapPane.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(lineClicked){
                    double[] newCords = {lineCords[0], lineCords[1], event.getX(), event.getY()};
                    newCords = normaliseCords(newCords);
                    movingWall.setStartX(newCords[0]);
                    movingWall.setStartY(newCords[1]);
                    movingWall.setEndX(newCords[2]);
                    movingWall.setEndY(newCords[3]);
                    movingWall.setVisible(true);

                    if(newCords[0] != newCords[2] && newCords[1] != newCords[3]){
                        movingWall.setStroke(Color.RED);
                    }else{
                        movingWall.setStroke(Color.GREEN);
                    }
                }else{
                    movingWall.setVisible(false);
                }
            }
        });
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

    public void uninitialise(){
        this.mapPane.getChildren().clear();
        cancelLineClicked();
        lineCords[0] = 0;lineCords[1] = 0;
        this.actionType = Tile.BlockType.Default;
        this.mainBuilding = null;
    }


    public static final double[] normaliseCords(double cords[]){
        int i;
        double a;
        double[] finalCords = new double[4];
        for(i = 0; i < 4; i++){
            a = cords[i] %  mainBuilding.getSize();
            System.out.println("Before: " + cords[i]);
            finalCords[i] = a < ( mainBuilding.getSize()/2) ? cords[i] - a : cords[i] + (mainBuilding.getSize() - a);
            System.out.println("After: " + finalCords[i]);
            finalCords[i] = a < ( mainBuilding.getSize()/2) ? cords[i] - a : cords[i] + (mainBuilding.getSize() - a);
        }
        return finalCords;
    }

    public boolean saveMap(){
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
                System.out.println("Walls from Save function: "+mainBuilding.getCurrentFloor().getWalls().size());
                out.writeObject(mainBuilding);
                out.close();
                fos.close();
                System.out.println("Current Building Object"+this.mainBuilding.toString());
                return true;
            }catch(IOException ex){
                errorText.setText("Error: Error saving your map. Please submit a ticket on our forum.");
                System.out.println("Serializable Error thrown: "+ex);
                return false;
            }
        }
        return false;
    }

    public static void cancelLineClicked(){
        lineClicked = false;
    }
    public static void setLineClicked(MouseEvent event){
        refreshLineTiles();
        System.out.println("Line clicked: "+lineClicked);
        //if()
        if(lineClicked){
            double x1 = event.getX(),y1 = event.getY(), x2 = lineCords[0], y2 = lineCords[1];
            double [] cords = {x1,y1,x2,y2};
            cords = normaliseCords(cords);
            System.out.println(cords[0]+" "+cords[1]+" "+cords[2]+" "+cords[3]);
            if(cords[0] == cords[2] && cords[1] == cords[3]){
                lineClicked = false;
                System.out.println("same click");
            }

            if(cords[1] == cords[3]){
                System.out.println("Drawing Line!");
                mainBuilding.getCurrentFloor().addWall(cords);
                Line l = new Line(cords[0],cords[1],cords[2],cords[3]);
                l.setStroke(Color.BLUE);
                l.setStrokeWidth(10*mainBuilding.getSize()/50.0);
                l.toBack();
                mainBuilding.windowContainer.getChildren().add(l);

                int x = (int)x1 == 0 ? 0 : (int)cords[0] / mainBuilding.getSize();
                int xx = (int)x2 == 0 ? 0 : (int)cords[2] / mainBuilding.getSize();
                int y = (int)y1 == 0 ? 0 : (int)cords[1] / mainBuilding.getSize();
                int max = x - xx < 0 ? xx : x;
                int min = x - xx < 0 ? x : xx;
                System.out.println("Min: " + min + ", Max: " + max);
                System.out.println("x: " + x + ", xx: " + xx + ", y: " + y);
                for(int i = min; i < max; i++){
                    mainBuilding.getCurrentFloor().getTile(i,y).removeAccess(0);
                    System.out.println("Block [" + i + "], [" + y + "] has removed access " + 0);
                }
                if(y != 0){
                    y -= 1;
                    for(int i = min; i < max; i++){
                        mainBuilding.getCurrentFloor().getTile(i,y).removeAccess(2);
                        System.out.println("Block [" + i + "], [" + y + "] has removed access " + 2);
                    }
                }
            }else if (cords[0] == cords[2]){
                System.out.println("Drawing Line!");
                mainBuilding.getCurrentFloor().addWall(cords);
                Line l = new Line(cords[0],cords[1],cords[2],cords[3]);
                l.setStroke(Color.BLUE);
                l.setStrokeWidth(10*mainBuilding.getSize()/50.0);
                l.toBack();
                mainBuilding.windowContainer.getChildren().add(l);
                int x = (int)x1 == 0 ? 0 : (int)cords[0] / mainBuilding.getSize();
                int y = (int)y1 == 0 ? 0 : (int)cords[1] / mainBuilding.getSize();
                int yy = (int)y2 == 0 ? 0 : (int)cords[3] / mainBuilding.getSize();
                int max = y - yy < 0 ? yy : y;
                int min = y - yy < 0 ? y : yy;
                System.out.println("Min: " + min + ", Max: " + max);
                System.out.println("y: " + y + ", yy: " + yy + ", x: " + x);
                for(int i = min; i < max; i++){
                    mainBuilding.getCurrentFloor().getTile(x,i).removeAccess(3);
                    System.out.println("Block [" + x + "], [" + i + "] has removed access " + 3);
                }
                if(x != 0){
                    x -= 1;
                    for(int i = min; i < max; i++){
                        mainBuilding.getCurrentFloor().getTile(x,i).removeAccess(1);
                        System.out.println("Block [" + i + "], [" + y + "] has removed access " + 1);
                    }
                }
            }
            if(cords[0] == cords[2] || cords[1] == cords[3]) {
                lineCords[0] = cords[0];
                lineCords[1] = cords[1];
            }
        }else{
            System.out.println("FIrst click");
            lineClicked = true;
            lineCords[0] = event.getX();
            lineCords[1] = event.getY();
        }
        refreshLineTiles();
    }
}
