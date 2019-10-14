package fireescapedemo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class FireEscapeDemo extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Stage test = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        test.setTitle("Hello World!");
       // Scene tmp = test.getScene();
        test.show();
        //stage.show();

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
