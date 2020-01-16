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
import java.util.*;

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
    //@FXML
    //private Pane toolContainer;
    @FXML
    private Pane stairOptionsPane;
    @FXML
    private TabPane floorPaneContainer;
    @FXML
    private ListView buttonList, stairsListView;
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
    public static boolean buildingWalls;
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
        mainBuilding.setActorSize(20);
        mainBuilding.initialiseView();
        errorText.setText("");

        this.tileButtons.add(new Pair<>("Default", Tile.BlockType.Default));
        this.tileButtons.add(new Pair<>("Exit", Tile.BlockType.Exit));
        this.tileButtons.add(new Pair<>("Employee", Tile.BlockType.Employee));
        this.tileButtons.add(new Pair<>("Fire", Tile.BlockType.Fire));

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

                   // System.out.println("Button: "+buttons.getKey());
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
                        //System.out.println("Current Floor: "+mainBuilding.getCurrentFloor().getId());
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


    //LINE BLOCK HANDLING
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

            //mainBuilding.getStairs().values().removeIf(n -> (n.parent.getFloor() == mainBuilding.getCurrentFloor()));
            for(int n=mainBuilding.getStairs().size()-1; n>=0; n--){
                if(mainBuilding.getStairs().get(n).parent.getFloor() == mainBuilding.getCurrentFloor()){
                    mainBuilding.getStairs().remove(n);
                }
            }

            mainBuilding.removeFloor(mainBuilding.getCurrentFloor().getId());
            this.floorPaneContainer.getTabs().remove(this.floorPaneContainer.getSelectionModel().getSelectedItem());
            mainBuilding.refreshFloorLabels();
            refreshStairToolContainer();
            this.currentFloorId = this.mainBuilding.getCurrentFloor().getId();


        }else{
            raiseError("Cannot Remove Only Remaining Floor");
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
    //if stair up/down tile build buttons
    public void stairsUpButton(){
        this.currStairDirection = "up";
        this.stairOptionsPane.setVisible(true);
        Image image;
        try {
            image = new Image(getClass().getResource("/Assets/stairs_up.fw.png").toURI().toString());
            this.stairPreview.setFill(new ImagePattern(image));
            stairTileContainer.toFront();
        } catch (URISyntaxException ex) {}
        this.stairOptionsPane.toFront();
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
        this.stairOptionsPane.toFront();
    }


    //refresh all the stair ID+joinedIDs in the listBox object in asset pane
    public void refreshStairToolContainer(){
        stairsListView.getItems().clear();
        String details;
        for(int id : mainBuilding.getStairs().keySet()){        //create a record in tool listbox for each stair
            details = "Floor:%d      ID:%d";
            details = String.format(details, mainBuilding.getStairs().get(id).parent.getFloorNum(), id);
            Text stairRecord = new Text(details);
            stairRecord.setX(10);
            stairRecord.setY(17);

            //set layout for listbox view
            ChoiceBox link = new ChoiceBox();
            link.setLayoutX(120);
            link.setLayoutY(0);
            link.setPrefWidth(30);
            Pane tmpP = new Pane();
            tmpP.setPrefHeight(25);
            tmpP.setMaxHeight(25);

            tmpP.getChildren().add(stairRecord);
            tmpP.getChildren().add(link);
            stairsListView.getItems().add(tmpP);

            String choiceBoxID = "Stair_"+id;
            link.setId(choiceBoxID);
            this.stairChoiceBox.put(choiceBoxID, id);
            if(mainBuilding.getStairs().keySet().size() == 1){link.setDisable(true);}

            //add other stair IDs to linkbox
            for(int tmpID : mainBuilding.getStairs().keySet()) {link.getItems().add(Integer.toString(tmpID));}
            if(mainBuilding.getStairs().get(id).joinedID != -1){
                if(!mainBuilding.getStairs().containsKey(mainBuilding.getStairs().get(id).joinedID)){//if joined IDs are invalid or missing, set to -1
                    mainBuilding.getStairs().get(id).joinedID = -1;
                }
                link.getSelectionModel().select(Integer.toString(mainBuilding.getStairs().get(id).joinedID));
            }


            link.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                //if the selection of the selectBox changes.
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                    int selectedOption = Integer.parseInt((String) link.getItems().get((Integer) number2));   //id of stair to link to
                    String eventChoiceBoxId = link.getId();
                    int stairId = stairChoiceBox.get(eventChoiceBoxId);
                    if(mainBuilding.getStairs().get(stairId).parent.getFloor() == mainBuilding.getStairs().get(selectedOption).parent.getFloor()) {
                        raiseError("Cannot join stairs that share the same floor");
                        refreshStairToolContainer();
                    }else if(stairId == selectedOption){
                        raiseError("Stair cannot link with itself.");
                        refreshStairToolContainer();
                    }else {
                        mainBuilding.getStairs().get(stairId).joinedID = selectedOption;
                    }
                }
            });
        }
    }


    //rotate stair before placing.
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





    //rendering a line between the first wall point and mouse.
    public void renderDragLine(){
        this.movingWall = new Line(0, 0, 0, 0);
        this.movingWall.setStroke(Color.GREEN);
        mainBuilding.getCurrentFloor().getPane().getChildren().add(this.movingWall);
        this.movingWall.setVisible(false);          //by default hide wall.

        mainBuilding.getCurrentFloor().getPane().setOnMouseMoved(new EventHandler<MouseEvent>() {       //if mouse moves
            @Override
            public void handle(MouseEvent event) {
                movingWall.setStrokeWidth(10*mainBuilding.getSize()/50.0);
                if(lineClicked){
                    double[] newCords = {lineCords[0], lineCords[1], event.getX(), event.getY()};
                    newCords = normaliseCords(newCords);
                    movingWall.setStartX(newCords[0]);
                    movingWall.setStartY(newCords[1]);
                    movingWall.setEndX(newCords[2]);
                    movingWall.setEndY(newCords[3]);
                    movingWall.setVisible(true);
                    if(newCords[0] != newCords[2] && newCords[1] != newCords[3]){           //if wall is not vertical or horizontal, colour grey
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
        cancelLineClicked();
        lineCords[0] = 0;lineCords[1] = 0;
        this.actionType = Tile.BlockType.Default;
        this.mainBuilding = null;
    }


    //removing pan and zoom, and using Math.round to get nearest absolute actual coordinate
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




    //save the current building object to file.
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


    //cancel line that's currently being drawn
    public static void cancelLineClicked(){lineClicked = false;}


    //converting between coord types
    public static int convertToGridXCord(double n){return (int)(n-mainBuilding.getXPanOffset())/mainBuilding.getSize();}
    public static int convertToGridYCord(double n){return (int)(n-mainBuilding.getYPanOffset())/mainBuilding.getSize();}
    public static double convertToActualXCord(int n){return (double)n*(mainBuilding.getSize())+mainBuilding.getXPanOffset();}
    public static double convertToActualYCord(int n){return (double)n*(mainBuilding.getSize())+mainBuilding.getYPanOffset();}


    //checks if a wall that has been placed is within the limits of the current floor
    public boolean checkLimits(int[] gridCoords){
        System.out.println(Arrays.toString(gridCoords));
        if(gridCoords[0] > this.mainBuilding.getWidth() || gridCoords[0] < 0){return false;}
        if(gridCoords[1] > this.mainBuilding.getHeight()|| gridCoords[1] < 0){return false;}
        if(gridCoords[2] > this.mainBuilding.getWidth()|| gridCoords[2] < 0){return false;}
        if(gridCoords[3] > this.mainBuilding.getHeight() || gridCoords[3] < 0){return false;}
        return true;
    }


    //Called when the pane is clicked to place a wall.
    public void setLineClicked(MouseEvent event){
        if(lineClicked){               //if placing the second half of the wall
            double x1 = event.getX(),y1 = event.getY(), x2 = lineCords[0], y2 = lineCords[1];
            double [] cords = {x1,y1,x2,y2};
            cords = normaliseCords(cords);                      //removed any pan or zoom placed on the view
            if(cords[0] == cords[2] && cords[1] == cords[3]){   //if wall has started and ended in the same place, cancel wall.
                lineClicked = false;
            }
            int gridCord1, gridCord2, length;
            Line l;
            double[] lActualCords;
            int[] lGridCords;

            if(cords[1] == cords[3]) {      //If placing a HORIZONTAL LINE
                gridCord1 = convertToGridXCord(cords[0]);
                gridCord2 = convertToGridXCord(cords[2]);
                length = Math.abs(gridCord2 - gridCord1);
                for (int n = 0; n < length; n++) {      //must draw an individual line for each line segment.
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

                    if(checkLimits(lGridCords) == false){           //if wall is outside of tile size, cancel wall.
                        lineClicked = false;
                        return;
                    }

                    if(this.currentWall == wallType.Wall) {     //If placing wall
                        l = new Line(lActualCords[0], lActualCords[1], lActualCords[2], lActualCords[3]);
                        l.setStroke(Color.BLUE);
                        l.setStrokeWidth(10 * mainBuilding.getSize() / 50.0);
                        l.toBack();                             //create wall javafx object
                        mainBuilding.getCurrentFloor().getPane().getChildren().add(l);
                        mainBuilding.getCurrentFloor().addWall(lGridCords, l);
                        mainBuilding.getCurrentFloor().getTile(lGridCords[0], lGridCords[1]-1).removeAccess(2);
                        mainBuilding.getCurrentFloor().getTile(lGridCords[0], lGridCords[1]).removeAccess(0);
                    }else if(this.currentWall == wallType.Delete){  //if deleting wall
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
            if(cords[0] == cords[2]) {      //If placing a VERTICAL LINE
                gridCord1 = convertToGridYCord(cords[1]);
                gridCord2 = convertToGridYCord(cords[3]);
                length = Math.abs(gridCord2 - gridCord1);
                for (int n = 0; n < length; n++) {      //must draw an individual line for each line segment.
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

                    if(checkLimits(lGridCords) == false){       //if wall is outside of tile size, cancel wall.
                        lineClicked = false;
                        return;
                    }
                    if(this.currentWall == wallType.Wall) {     //if placing wall
                        l = new Line(lActualCords[0], lActualCords[1], lActualCords[2], lActualCords[3]);
                        l.setStroke(Color.BLUE);
                        l.setStrokeWidth(10 * mainBuilding.getSize() / 50.0);
                        l.toBack();
                        mainBuilding.getCurrentFloor().getPane().getChildren().add(l);
                        mainBuilding.getCurrentFloor().addWall(lGridCords, l);
                        mainBuilding.getCurrentFloor().getTile(lGridCords[0]-1, lGridCords[1]).removeAccess(1);
                        mainBuilding.getCurrentFloor().getTile(lGridCords[0], lGridCords[1]).removeAccess(3);
                    }else if(this.currentWall == wallType.Delete){  //if deleting wall
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
            lineCords[1] = (event.getY()+event.getY()+event.getY())/3.0;
            System.out.println("Clicked X: "+lineCords[0]+" Y: "+lineCords[1]);
            mainBuilding.disableBuild();
            this.renderDragLine();
        }
    }




    //shows dialog box with error message. Not a blocking window.
    public void raiseError(String errormessage){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(errormessage);
        alert.show();
        refreshStairToolContainer();
    }





}
