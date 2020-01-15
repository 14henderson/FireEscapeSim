package fireescapedemo;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import sun.security.krb5.internal.crypto.CksumType;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.ResourceBundle;

import static javax.swing.text.html.HTML.Tag.HEAD;

public class FXMLBuildingController implements Initializable {

    @FXML
    private Rectangle play;
    @FXML
    private Rectangle stairTileContainer;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private Pane assetPane;
    @FXML
    private Pane stairOptionsPane;
    @FXML
    private TabPane floorPaneContainer;
    @FXML
    private ListView buttonList;
    @FXML
    private Button cancelButton, saveToSim, saveButton;
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
    public final int minZoom = 10;
    public final int maxZoom = 100;
    public boolean buildingWalls;
    public wallType currentWall;
    public Rectangle stairPreview;
    public static int currStairOrientation = 0;
    public static String currStairDirection = "up";
    public HashMap<String, Integer> stairChoiceBox;
    public ArrayList<Pair<String, Tile.BlockType>> tileButtons = new ArrayList<>();
    public String currentFloorId;
    enum wallType{
        Wall,
        Delete
    }



    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.manager = new SceneManager();
        this.stairChoiceBox = new HashMap<>();
        this.currentFloorId = "Floor 0";

        if(this.manager.getLoadType() == 0){
            System.out.println("Creating new with specifications");
            int width = manager.getGlobalBuildingSettings().getWidth();
            int height = manager.getGlobalBuildingSettings().getHeight();
            int size = manager.getGlobalBuildingSettings().getSize();
            mainBuilding = new Building(width, height, this.floorPaneContainer);

        }else if(this.manager.getLoadType() == 1){
            mainBuilding = manager.getGlobalBuilding();
            mainBuilding.setTabPane(floorPaneContainer);
            for(Floor f : mainBuilding.getFloors()){
                Tab newPane = new Tab(f.getId());
                Pane newFloorPane = new Pane();
                newPane.setContent(newFloorPane);
                f.setPane(newFloorPane);
                this.floorPaneContainer.getTabs().add(newPane);
                addWallClickEventListener(newFloorPane);
            }

        }else{
            System.out.println("Building is null. Making new one");
            mainBuilding = new Building(20, 20, this.floorPaneContainer);
        }
        this.actionType = Tile.BlockType.Default;
        this.mainBuilding.enableBuild();
        mainBuilding.initialiseView();
        errorText.setText("");

        this.tileButtons.add(new Pair<>("Default", Tile.BlockType.Default));
        this.tileButtons.add(new Pair<>("Exit", Tile.BlockType.Exit));
        this.tileButtons.add(new Pair<>("Employee", Tile.BlockType.Employee));

        this.tileButtons.add(new Pair<>("Stair Up", Tile.BlockType.Stairs));
        this.tileButtons.add(new Pair<>("Stair Down", Tile.BlockType.Stairs));
        this.tileButtons.add(new Pair<>("Blocked", Tile.BlockType.Blocked));

        Button a;
        for(Pair<String, Tile.BlockType> buttons : this.tileButtons){
            a = new Button(buttons.getKey());
            a.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent Event){
                    buildingWalls = false;
                    actionType = buttons.getValue();
                    unrenderLineBlocks(mainBuilding.getCurrentFloor().getPane());

                    switch (buttons.getKey()){
                        case "Stair Up":
                            stairsUpButton();
                            break;
                        case "Stair Down":
                            stairsDownButton();
                            break;
                        default:
                            stairOptionsPane.setVisible(false);
                            break;
                    }
                }
            });
            this.buttonList.getItems().add(a);
        }

        if(this.mainBuilding.getFloors().isEmpty()){this.newFloor();}
        this.mainBuilding.initialiseView();

        this.initLineBlocks();
        this.renderLineBlocks();
        this.disableLineBlocks();

        this.renderDragLine();
        this.stairPaneInitialise();
        this.refreshStairToolContainer();

        Image image;
        try {
            image = new Image(getClass().getResource("/Assets/stair_direction.fw.png").toURI().toString());
            this.stairTileContainer.setFill(new ImagePattern(image));
        } catch (URISyntaxException ex) {}



        floorPaneContainer.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Tab>() {
                    @Override
                    public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
                        System.out.println("Current Floor: "+mainBuilding.getCurrentFloor().getId());
                        try{unrenderLineBlocks(mainBuilding.getFloor(currentFloorId).getPane());
                        }catch(NullPointerException e){}

                        mainBuilding.getCurrentFloor().initialiseView();

                        //renderDragLine();
                        cancelLineClicked();
                        currentFloorId = mainBuilding.getCurrentFloor().getId();
                        mainBuilding.getCurrentFloor().initialiseView();
                        lineClicked = false;
                        buildingWalls = false;
                    }
                }
        );
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
                this.lineTiles[i][j] = new LineTile(i*size, j*size, i, j, this.mainBuilding, this.mainBuilding.getCurrentFloor().getPane());
            }}
    }


    //LINE BLOCK HANDLING
    public void renderLineBlocks(){
        for (LineTile[] lineTileRow : this.lineTiles) {
            for (LineTile lineTile : lineTileRow) {
                lineTile.render(this.mainBuilding.getCurrentFloor().getPane());
            }}}
    public void unrenderLineBlocks(Pane p){
        for (LineTile[] lineTileRow : this.lineTiles) {
            for (LineTile lineTile : lineTileRow) {
                p.getChildren().remove(lineTile.getLineTileRect());
            }}}
    public static void zoomLineBlocks(){
        for(int i = 0; i < lineTiles.length; i ++) {
            for (int j = 0; j < lineTiles[i].length; j++) {
                lineTiles[i][j].zoom();
            }}}
    public static void panLineBlocks(int xinc, int yinc){
        for(int i = 0; i < lineTiles.length; i ++) {
            for (int j = 0; j < lineTiles[i].length; j++) {
                lineTiles[i][j].pan(xinc, yinc);
            }}}
    public void disableLineBlocks(){
        this.buildingWalls = false;
        for (LineTile[] lineTileRow : this.lineTiles) {
            for (LineTile lineTile : lineTileRow) {
                lineTile.getLineTileRect().setVisible(false);
            }}}




    //FLOOR MANAGEMENT
    @FXML
    public void newFloor(){
        String newFloorID = "Floor "+mainBuilding.getTotalFloors();
        Tab newPane = new Tab(newFloorID);
        Pane newFloorPane = new Pane();
        newPane.setContent(newFloorPane);
        mainBuilding.addFloor(newFloorPane, newFloorID);
        this.floorPaneContainer.getTabs().add(newPane);
        this.currentFloorId = this.mainBuilding.getCurrentFloor().getId();
        addWallClickEventListener(newFloorPane);
    }


    public void addWallClickEventListener(Pane p){
        p.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                refreshStairToolContainer();
                if(buildingWalls){
                    if(event.getButton() == MouseButton.PRIMARY) {
                        setLineClicked(event);
                    }else{
                        lineClicked = false;
                        FXMLBuildingController.cancelLineClicked();
                    }}}});
    }


    @FXML
    public void removeFloor(){
        if(this.floorPaneContainer.getTabs().size() > 1) {
            Pane paneToDelete = (Pane) this.floorPaneContainer.getSelectionModel().getSelectedItem().getContent();
            paneToDelete.getChildren().removeAll();
            mainBuilding.removeFloor(mainBuilding.getCurrentFloor().getId());
            this.floorPaneContainer.getTabs().remove(this.floorPaneContainer.getSelectionModel().getSelectedItem());
            mainBuilding.refreshFloorLabels();
            this.currentFloorId = this.mainBuilding.getCurrentFloor().getId();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Cannot Remove Only Remaining Floor");
            alert.show();
        }
    }







    //BUTTONS (AND MAP MOVEMENT)
    public static Tile.BlockType getActionType(){return actionType;}
    @FXML
    public void normWallButton(){
        this.renderLineBlocks();
        this.buildingWalls = true;
        this.currentWall = wallType.Wall;
        this.actionType = Tile.BlockType.Default;
        this.stairOptionsPane.setVisible(false);
    }
    @FXML
    public void deleteWallButton(){
        this.renderLineBlocks();
        this.buildingWalls = true;
        this.currentWall = wallType.Delete;
        this.actionType = Tile.BlockType.Default;
        this.stairOptionsPane.setVisible(false);
    }
    @FXML
    public void zoomIn(){
        if(this.mainBuilding.getSize()+2 > this.minZoom && this.mainBuilding.getSize()+2 < this.maxZoom) {
            this.lineCords[0] = (this.lineCords[0]-this.mainBuilding.getXPanOffset())/this.mainBuilding.getSize();
            this.lineCords[1] = (this.lineCords[1]-this.mainBuilding.getYPanOffset())/this.mainBuilding.getSize();
            mainBuilding.zoom(2);
            this.lineCords[0] = (this.lineCords[0]*this.mainBuilding.getSize())+this.mainBuilding.getXPanOffset();
            this.lineCords[1] = (this.lineCords[1]*this.mainBuilding.getSize())+this.mainBuilding.getYPanOffset();
        }
    }
    @FXML
    public void zoomOut(){
        if(this.mainBuilding.getSize()-2 > this.minZoom && this.mainBuilding.getSize()-2 < this.maxZoom) {
            this.lineCords[0] = (this.lineCords[0]-this.mainBuilding.getXPanOffset())/this.mainBuilding.getSize();
            this.lineCords[1] = (this.lineCords[1]-this.mainBuilding.getYPanOffset())/this.mainBuilding.getSize();
            mainBuilding.zoom(-2);
            this.lineCords[0] = (this.lineCords[0]*this.mainBuilding.getSize())+this.mainBuilding.getXPanOffset();
            this.lineCords[1] = (this.lineCords[1]*this.mainBuilding.getSize())+this.mainBuilding.getYPanOffset();
        }
    }
    @FXML
    public void panUp(){
        mainBuilding.pan(0, 30);
        this.lineCords[1] += 30;
    }
    @FXML
    public void panDown() {
        mainBuilding.pan(0, -30);
        this.lineCords[1] -= 30;
    }
    @FXML
    public void panRight(){
        mainBuilding.pan(-30, 0);
        this.lineCords[0] -= 30;
    }
    @FXML
    public void panLeft(){
        mainBuilding.pan(30, 0);
        this.lineCords[0] += 30;
    }





    //STAIRS
    public void stairPaneInitialise(){
        this.stairOptionsPane.setVisible(false);
        this.stairPreview = new Rectangle(60, 35, 50, 50);
        this.stairPreview.setFill(Color.WHITE);
        this.stairPreview.setStrokeWidth(2);
        this.stairPreview.setStroke(Color.BLACK);
        this.stairOptionsPane.getChildren().add(this.stairPreview);
        currStairOrientation = 0;
        currStairDirection = "up";
    }

    public void stairsUpButton(){
        this.currStairDirection = "up";
        this.stairOptionsPane.setVisible(true);
        Image image;
        try {
            image = new Image(getClass().getResource("/Assets/stairs_up.fw.png").toURI().toString());
            this.stairPreview.setFill(new ImagePattern(image));
            stairTileContainer.toFront();
        } catch (URISyntaxException ex) {}
    }

    public void stairsDownButton(){
        this.currStairDirection = "down";
        this.stairOptionsPane.setVisible(true);
        Image image;
        try {
            image = new Image(getClass().getResource("/Assets/stairs_down.fw.png").toURI().toString());
            this.stairPreview.setFill(new ImagePattern(image));
            stairTileContainer.toFront();
        } catch (URISyntaxException ex) {}
    }
    public void refreshStairToolContainer(){
        System.out.println("In tool container!");
        System.out.println("Stairs: "+mainBuilding.getStairs().size());
       // toolContainer.getChildren().clear();
        String details;
        int recordCount = 1;
        for(int id : mainBuilding.getStairs().keySet()){
            details = "Floor:%d      ID:%d";
            details = String.format(details, mainBuilding.getStairs().get(id).parent.floorNum, id);
            Text stairRecord = new Text(details);
            stairRecord.setX(10);
            stairRecord.setY(25*recordCount);
            ChoiceBox link = new ChoiceBox();
            link.setLayoutX(120);
            link.setLayoutY(25*recordCount-15);
            link.setPrefWidth(30);
            link.setPrefHeight(20);
            String choiceBoxID = "Stair_"+id;
            link.setId(choiceBoxID);
            this.stairChoiceBox.put(choiceBoxID, id);
            if(mainBuilding.getStairs().keySet().size() == 1){
                link.setDisable(true);
            }
            //add other stair IDs to linkbox
            for(int tmpID : mainBuilding.getStairs().keySet()) {
                if (tmpID != mainBuilding.getStairs().get(id).ID) {      //can't link to self
                    link.getItems().add(tmpID);
                } else {}
            }
            if(mainBuilding.getStairs().get(id).joinedID != -1){
                link.getSelectionModel().select(mainBuilding.getStairs().get(id).joinedID);
            }
            link.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                //if the selection of the selectBox changes.
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                    int selectedOption = (int)link.getItems().get((Integer) number2);   //id of stair to link to
                    String eventChoiceBoxId = link.getId();
                    int stairId = stairChoiceBox.get(eventChoiceBoxId);
                    mainBuilding.getStairs().get(stairId).joinedID = selectedOption;
                }
            });
            recordCount++;
        }
    }
    @FXML
    public void rotateLeft(){
        currStairOrientation-=90;
        if(currStairOrientation == -90){currStairOrientation = 270;}
        this.stairTileContainer.setRotate(this.stairTileContainer.getRotate()-90);
    }
    @FXML
    public void rotateRight(){
        currStairOrientation+=90;
        if(currStairOrientation == 360){currStairOrientation = 0;}
        this.stairTileContainer.setRotate(this.stairTileContainer.getRotate()+90);
    }




    //MAP SAVING AND APPLICATION NAVIGATION
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
            System.out.println("Saving Map");
            manager.setGlobalBuilding(this.mainBuilding);
            System.out.println("Uninitializing");
            this.uninitialise();
            System.out.println("Showing Scene");
            manager.showScene("simulation");
        }
    }






    public void renderDragLine(){
        System.out.println("RENDER DRAG LINE CALLED");
        this.movingWall = new Line(0, 0, 0, 0);
        this.movingWall.setStroke(Color.GREEN);
        mainBuilding.getCurrentFloor().getPane().getChildren().add(this.movingWall);
        this.movingWall.setVisible(false);
        mainBuilding.getCurrentFloor().getPane().setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //System.out.println("------------");
                //System.out.println("Line clicked: "+lineClicked);
                //System.out.println("Building walls: "+buildingWalls);
                //System.out.println("----------------");
                movingWall.setStrokeWidth(10*mainBuilding.getSize()/50.0);
                if(lineClicked){
                    double[] newCords = {lineCords[0], lineCords[1], event.getX(), event.getY()};
                    newCords = normaliseCords(newCords);
                    movingWall.setStartX(newCords[0]);
                    movingWall.setStartY(newCords[1]);
                    movingWall.setEndX(newCords[2]);
                    movingWall.setEndY(newCords[3]);
                    movingWall.setVisible(true);
                    if(newCords[0] != newCords[2] && newCords[1] != newCords[3]){
                        movingWall.setStroke(Color.rgb(191, 191, 191));
                    }else{
                        switch(currentWall){
                            case Wall:
                                movingWall.setStroke(Color.GREEN);
                                break;
                            case Delete:
                                movingWall.setStroke(Color.RED);
                                break;
                        }
                    }
                }else{
                    movingWall.setVisible(false);
                }
            }
        });
    }



    public void uninitialise(){
        //this.firstFloorPane.getChildren().clear();
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
            if(i == 0 || i == 2){
                a = Math.round((cords[i]-mainBuilding.getXPanOffset())/mainBuilding.getSize());
                finalCords[i] = (a*mainBuilding.getSize())+mainBuilding.getXPanOffset();
            }else if (i == 1 || i == 3){
                a = Math.round((cords[i]-mainBuilding.getYPanOffset())/mainBuilding.getSize());
                   finalCords[i] = (a*mainBuilding.getSize())+mainBuilding.getYPanOffset();
            }else{return null;}
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
                System.out.println("Current Building Stairs Length IN SAVE"+this.mainBuilding.getStairs().size());
                out.writeObject(mainBuilding);
                out.close();
                fos.close();

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





    public static int convertToGridXCord(double n){return (int)(n-mainBuilding.getXPanOffset())/mainBuilding.getSize();}
    public static int convertToGridYCord(double n){return (int)(n-mainBuilding.getYPanOffset())/mainBuilding.getSize();}
    public static double convertToActualXCord(int n){return (double)n*(mainBuilding.getSize())+mainBuilding.getXPanOffset();}
    public static double convertToActualYCord(int n){return (double)n*(mainBuilding.getSize())+mainBuilding.getYPanOffset();}







    public void setLineClicked(MouseEvent event){
        System.out.println("Line Clicked: "+lineClicked);
        //refreshLineTiles();
        if(lineClicked){
            double x1 = event.getX(),y1 = event.getY(), x2 = lineCords[0], y2 = lineCords[1];
            double [] cords = {x1,y1,x2,y2};
            cords = normaliseCords(cords);
           // System.out.println(cords[0]+" "+cords[1]+" "+cords[2]+" "+cords[3]);
            if(cords[0] == cords[2] && cords[1] == cords[3]){
                lineClicked = false;
            }
            int gridCord1;
            int gridCord2;
            int length;
            Line l;
            double[] lActualCords;
            int[] lGridCords;

            if(cords[1] == cords[3]) {      //HORIZONTAL LINE
                gridCord1 = convertToGridXCord(cords[0]);
                gridCord2 = convertToGridXCord(cords[2]);
                length = Math.abs(gridCord2 - gridCord1);
                for (int n = 0; n < length; n++) {
                    lActualCords = new double[4];
                    lGridCords = new int[4];
                    lActualCords[0] = convertToActualXCord(Math.min(gridCord1, gridCord2) + n);
                    lActualCords[1] = cords[1];
                    lActualCords[2] = convertToActualXCord(Math.min(gridCord1, gridCord2) + n + 1);
                    lActualCords[3] = cords[1];

                    lGridCords[0] = Math.min(gridCord1, gridCord2)+n;
                    lGridCords[1] = convertToGridYCord(cords[1]);
                    lGridCords[2] = Math.min(gridCord1, gridCord2)+n+1;
                    lGridCords[3] = convertToGridYCord(cords[1]);

                    if(this.currentWall == wallType.Wall) {
                        l = new Line(lActualCords[0], lActualCords[1], lActualCords[2], lActualCords[3]);
                        l.setStroke(Color.BLUE);
                        l.setStrokeWidth(10 * mainBuilding.getSize() / 50.0);
                        l.toBack();
                        mainBuilding.getCurrentFloor().getPane().getChildren().add(l);
                        mainBuilding.getCurrentFloor().addWall(lGridCords, l);
                        mainBuilding.getCurrentFloor().getTile(lGridCords[0], lGridCords[1]-1).removeAccess(2);
                        mainBuilding.getCurrentFloor().getTile(lGridCords[0], lGridCords[1]).removeAccess(0);
                    }else if(this.currentWall == wallType.Delete){
                        mainBuilding.getCurrentFloor().getTile(lGridCords[0], lGridCords[1]-1).grantAccess(2);
                        mainBuilding.getCurrentFloor().getTile(lGridCords[0], lGridCords[1]).grantAccess(0);
                        mainBuilding.getCurrentFloor().removeWall(lGridCords);
                        mainBuilding.updateView();
                        lineClicked = false;
                    }
                    this.movingWall.toFront();
                    this.mainBuilding.enableBuild();
                }
            }


            if(cords[0] == cords[2]) {      //VERTICAL LINE
                gridCord1 = convertToGridYCord(cords[1]);
                gridCord2 = convertToGridYCord(cords[3]);
                length = Math.abs(gridCord2 - gridCord1);
                for (int n = 0; n < length; n++) {
                    lActualCords = new double[4];
                    lGridCords = new int[4];

                    lActualCords[0] = cords[0];
                    lActualCords[1] = convertToActualYCord(Math.min(gridCord1, gridCord2) + n);
                    lActualCords[2] = cords[0];
                    lActualCords[3] = convertToActualYCord(Math.min(gridCord1, gridCord2) + n + 1);

                    lGridCords[0] = convertToGridXCord(cords[0]);
                    lGridCords[1] = Math.min(gridCord1, gridCord2)+n;
                    lGridCords[2] = convertToGridXCord(cords[0]);
                    lGridCords[3] = Math.min(gridCord1, gridCord2)+n+1;

                    if(this.currentWall == wallType.Wall) {
                        l = new Line(lActualCords[0], lActualCords[1], lActualCords[2], lActualCords[3]);
                        l.setStroke(Color.BLUE);
                        l.setStrokeWidth(10 * mainBuilding.getSize() / 50.0);
                        l.toBack();
                        mainBuilding.getCurrentFloor().getPane().getChildren().add(l);
                        mainBuilding.getCurrentFloor().addWall(lGridCords, l);
                        mainBuilding.getCurrentFloor().getTile(lGridCords[0]-1, lGridCords[1]).removeAccess(1);
                        mainBuilding.getCurrentFloor().getTile(lGridCords[0], lGridCords[1]).removeAccess(3);
                    }else if(this.currentWall == wallType.Delete){
                        mainBuilding.getCurrentFloor().getTile(lGridCords[0]-1, lGridCords[1]).grantAccess(1);
                        mainBuilding.getCurrentFloor().getTile(lGridCords[0], lGridCords[1]).grantAccess(3);
                        mainBuilding.getCurrentFloor().removeWall(lGridCords);
                        mainBuilding.updateView();
                        lineClicked = false;
                    }
                    this.movingWall.toFront();
                    this.mainBuilding.enableBuild();
                }
            }
            if(cords[0] == cords[2] || cords[1] == cords[3]) {
                lineCords[0] = cords[0];
                lineCords[1] = cords[1];
            }
        }else{
            lineClicked = true;
            lineCords[0] = event.getX();
            lineCords[1] = event.getY();
            mainBuilding.disableBuild();
            this.renderDragLine();
        }
        //refreshLineTiles();
    }
}
