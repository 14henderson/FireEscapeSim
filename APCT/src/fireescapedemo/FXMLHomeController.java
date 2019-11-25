package fireescapedemo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.stage.Stage;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLHomeController implements Initializable{
    @FXML
    Parent HomeAnchorPane;
    SceneManager manager;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        this.manager = new SceneManager();
    }

    public void simulateCurrent(ActionEvent actionEvent) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Copy of Graphs");
        fileChooser.setFileFilter(new FileNameExtensionFilter("map files (*.map)", "map"));
        fileChooser.setSelectedFile(new File("my_building.map"));

        //If save window has been closed after successfully pressing save
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getPath();
            try {
                java.io.FileInputStream fis = new java.io.FileInputStream(filename);
                ObjectInputStream in = new ObjectInputStream(fis);
                Building loadedBuilding = (Building) in.readObject();
                in.close();
                fis.close();
                System.out.println("Loading from homecont"+loadedBuilding.toString());
                loadedBuilding.setCurrentFloor(0);
                manager.setLoadType(1);
                manager.setGlobalBuilding(loadedBuilding);
                manager.showScene("simulation");
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("Serializable Error thrown: " + ex);

            }
        }else{
            System.out.println("If clause not entered");
        }

    }




    public void buildCurrent(ActionEvent actionEvent) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Copy of Graphs");
        fileChooser.setFileFilter(new FileNameExtensionFilter("map files (*.map)", "map"));
        fileChooser.setSelectedFile(new File("my_building.map"));

        //If save window has been closed after successfully pressing save
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getPath();
            try {
                java.io.FileInputStream fis = new java.io.FileInputStream(filename);
                ObjectInputStream in = new ObjectInputStream(fis);
                Building loadedBuilding = (Building) in.readObject();
                in.close();
                fis.close();
                System.out.println("Loading from homecont"+loadedBuilding.toString());
                loadedBuilding.setCurrentFloor(0);
                manager.setLoadType(1);
                manager.setGlobalBuilding(loadedBuilding);
                manager.showScene("building");
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
                System.out.println("Serializable Error thrown: " + ex);
            }
        }else{
            System.out.println("If clause not entered");
        }
    }


    public void buildNew(ActionEvent actionEvent) throws IOException {
            this.manager.showScene("homeCreateDiag");
        }
    }
