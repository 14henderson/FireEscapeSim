package fireescapedemo;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;

public class SceneManager {
    static private HashMap<String, Scene> scenes = new HashMap<>();
    private Stage rootStage;

    public SceneManager(Stage mainStage){
        this.rootStage = mainStage;
    }


    public void addScene(Scene newScene, String name){
        this.scenes.put(name, newScene);
    }

    public void showScene(String name){
        this.rootStage.setScene(this.scenes.get(name));
    }
}
