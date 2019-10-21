package fireescapedemo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLHomeController implements Initializable{
    @FXML
    Parent HomeAnchorPane;
    SceneManager manager;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        //Stage root  = new Stage();
        //this.manager = new SceneManager(root);
        this.manager = new SceneManager();


    }

    public void simulateCurrent(ActionEvent actionEvent) {
    }

    public void buildCurrent(ActionEvent actionEvent) {
    }

    public void buildNew(ActionEvent actionEvent) throws IOException {

        this.manager.addScene("FXMLBuilding.fxml", "building");
        this.manager.showScene("building");
    }
}
