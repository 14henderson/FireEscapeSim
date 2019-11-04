package fireescapedemo;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

public class SceneManager {
    static private HashMap<String, String> scenes = new HashMap<>();
    static private Stage rootStage;
    static private boolean buildingSet;
    static public Building globalBuilding;

    public SceneManager(Stage mainStage){
        this.rootStage = mainStage;
    }
    public void setGlobalBuilding(Building b){
        this.rootStage.setUserData(b);
        this.buildingSet = true;
    }
    public Building getGlobalBuilding(){
        if(!this.buildingSet) {
            return null;
        }
        Building tmp = (Building) this.rootStage.getUserData();
        this.buildingSet = false;
        return tmp;

    }



    public SceneManager() throws RuntimeException{
        if(this.rootStage == null){
            throw new RuntimeException();
        }
    }

    public void addScene(String filename, String label) throws IOException {
        this.scenes.put(label, filename);
    }

    public void showScene(String name) throws IOException {
        Parent tmpRef = FXMLLoader.load(getClass().getResource(this.scenes.get(name)));
        Scene tmpSceneRef = new Scene(tmpRef);

        this.rootStage.setScene(tmpSceneRef);
        this.rootStage.show();
    }
}
