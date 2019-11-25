package fireescapedemo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.TreeSet;


public class FireEscapeDemo extends Application {
    Building mainBuilding;
    @Override
    public void start(Stage stage) throws Exception {
        Stage root  = new Stage();
        SceneManager manager = new SceneManager(root);

        manager.addScene("FXMLBuilding.fxml", "building");
        manager.addScene("FXMLHome.fxml", "home");
        manager.addScene("FXMLHomeCreateDiag.fxml", "homeCreateDiag");
        manager.addScene("FXMLSimulation.fxml", "simulation");

        manager.showScene("home");

    }

    /**
     * @param args the command line arguments
     *            this is a test lol
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
