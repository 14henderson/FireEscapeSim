package fireescapedemo;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLSimulationController implements Initializable {
    Building mainBuilding = FXMLBuildingController.mainBuilding;
    @FXML
    AnchorPane mainPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mainPane.getChildren().add(mainBuilding.getCurrentFloor());

    }
}
