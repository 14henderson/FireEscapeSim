package fireescapedemo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class FireEscapeDemo extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Stage root  = new Stage();
        ProgramManager.initSceneManager(root);
    }

    /**
     * @param args the command line arguments
     *            this is a test lol
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
