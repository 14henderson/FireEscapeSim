package fireescapedemo;

import javafx.stage.Stage;

import java.io.IOException;

public class ProgramManager {
    public static SceneManager sceneManager;

    public static void initSceneManager(Stage root) throws IOException {
        sceneManager = new SceneManager(root);
        sceneManager.addScene("FXMLBuilding.fxml", "building");
        sceneManager.addScene("FXMLSimulation.fxml", "tmp");
        sceneManager.showScene("building");
    }
}
