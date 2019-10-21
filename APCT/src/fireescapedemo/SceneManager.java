package fireescapedemo;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

public class SceneManager {
    static private HashMap<String, Scene> scenes = new HashMap<>();
    static private Stage rootStage;

    public SceneManager(Stage mainStage){
        this.rootStage = mainStage;
    }

    public SceneManager() throws RuntimeException{
        if(this.rootStage == null){
            throw new RuntimeException();
        }
    }

    public void addScene(String filename, String label) throws IOException {
        Parent tmpRef = FXMLLoader.load(getClass().getResource(filename));
        Scene tmpSceneRef = new Scene(tmpRef);

        this.scenes.put(label, tmpSceneRef);
    }

    public void showScene(String name){
        this.rootStage.setScene(this.scenes.get(name));
        this.rootStage.show();
    }
}
