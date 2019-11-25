package fireescapedemo;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLHomeCreateDiagController implements Initializable {
    private SceneManager manager;
    @FXML
    TextField widthField;
    @FXML
    TextField heightField;
    @Override
    public void initialize(URL location, ResourceBundle resources){
        this.manager = new SceneManager();
    }

    @FXML
    public void ReturnHome() throws IOException {
        manager.showScene("home");
    }
    @FXML
    public void Create() throws IOException{
        int width = Integer.parseInt(this.widthField.getText());
        int height = Integer.parseInt(this.heightField.getText());
        BuildingSettings config = new BuildingSettings(width, height);
        this.manager.setGlobalBuildingSettings(config);
        this.manager.setLoadType(0);
        manager.showScene("building");
    }



    public class BuildingSettings{
        private final int WIDTH;
        private final int HEIGHT;
        private final int size = 50;
        BuildingSettings(int width, int height){
            this.HEIGHT = height;
            this.WIDTH = width;
        }
        public int getWidth(){
            return this.WIDTH;
        }
        public int getHeight(){
            return this.HEIGHT;
        }
        public int getSize(){
            return this.size;
        }
    }



}
