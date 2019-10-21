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

    public SceneManager(Stage mainStage){
        this.rootStage = mainStage;
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
