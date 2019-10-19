package fireescapedemo;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

public class SceneManager {
    static private HashMap<String, Scene> scenes = new HashMap<>();
    private Stage rootStage;

    public SceneManager(Stage mainStage){
        this.rootStage = mainStage;
    }


    public void addScene(String filename, String label) throws IOException {
        Scene tmpRef = FXMLLoader.load(getClass().getResource(filename));

        this.scenes.put(label, tmpRef);
    }

    public void showScene(String name){
        this.rootStage.setScene(this.scenes.get(name));
        this.rootStage.show();
    }
}