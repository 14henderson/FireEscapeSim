package fireescapedemo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class FireEscapeDemo extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Stage mainStage = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        //Scene buildingScene = FXMLLoader.load(getClass().getResource("FXMLBuilding.fxml"));
        mainStage.show();
        //Scene root = new Scene(mainStage);



        //mainStage.setTitle("Hello World!");
        //mainStage.show();




/*
        SceneManager manager = new SceneManager(mainStage);
        Scene buildingScene = FXMLLoader.load(getClass().getResource("FXMLBuilding.fxml"));
        manager.addScene(buildingScene, "Building");
        manager.showScene("building");
  */

        //mainStage.setScene(buildingScene);
//        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));

  //      Scene scene = new Scene(root);


    //    stage.setScene(scene);
      //  stage.setResizable(false);
        //stage.show();
    }

    /**
     * @param args the command line arguments
     *            this is a test lol
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
