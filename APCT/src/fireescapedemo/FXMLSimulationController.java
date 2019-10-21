package fireescapedemo;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLSimulationController implements Initializable {
    @FXML
    AnchorPane mainPane;
    Building mainBuilding = FXMLBuildingController.mainBuilding.createCopy();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mainPane.getChildren().add(mainBuilding.getCurrentFloor());
    }





}
