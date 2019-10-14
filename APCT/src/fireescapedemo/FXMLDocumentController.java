package fireescapedemo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;

import java.net.URL;
import java.util.ResourceBundle;


public class FXMLDocumentController implements Initializable {
    @FXML
    private Scene Building;
    @FXML
    private Scene Simulation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.Building.getRoot().setVisible(false);
        this.Simulation.getRoot().setVisible(true);
    }
}