package fireescapedemo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


public class FXMLDocumentController implements Initializable {
    @FXML
    private Stage documentStage;
    //private Scene Building;
    //@FXML
    //private Scene Simulation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SceneManager tmpManager = new SceneManager(documentStage);
        tmpManager.showScene("building");
        System.out.println("In Document Controller");
        //this.Building.getRoot().setVisible(true);
        //this.Simulation.getRoot().setVisible(false);
    }
}