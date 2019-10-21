package fireescapedemo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class FireEscapeDemo extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {

        //Stage mainStage = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        Scene buildingScene = FXMLLoader.load(getClass().getResource("FXMLBuilding.fxml"));
        //Scene buildingScene = FXMLLoader.load(getClass().getResource("FXMLBuilding.fxml"));



        //Scene root = new Scene(mainStage);
        Stage root  = new Stage();
        SceneManager manager = new SceneManager(root);
        manager.addScene("FXMLBuilding.fxml", "building");
        manager.addScene("FXMLSimulation.fxml", "tmp");

        manager.showScene("building");
        manager.showScene("tmp");
        System.out.println("Hello");
        //root.setScene(buildingScene);
        //root.show();


        //mainStage.setTitle("Hello World!");
        //mainStage.show();







        //manager.addScene(buildingScene, "Building");
        //manager.showScene("building");


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
