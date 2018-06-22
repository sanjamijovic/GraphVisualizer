package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller {

    final FileChooser fileChooser = new FileChooser();
    public Controller() {
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GML", "*.gml"),
                new FileChooser.ExtensionFilter("CSV Edges", "*.csv"),
                new FileChooser.ExtensionFilter("CSV Adjacency", "*.csv"),
                new FileChooser.ExtensionFilter("CSV Matrix", "*.csv")
        );
    }


    @FXML
    private MenuItem openMenu;

    public void open(ActionEvent actionEvent) {
        Graph g = null;
        File file = fileChooser.showOpenDialog(openMenu.getParentPopup().getScene().getWindow());
        if(file == null) {
            System.out.println("File not found");
        }
        Parser p;

        if(fileChooser.getSelectedExtensionFilter().getDescription().equals("GML")) {
            p = new GMLParser();
        } else if (fileChooser.getSelectedExtensionFilter().getDescription().equals("CSV Edges")){
            p = new CSVEdges();
        } else if(fileChooser.getSelectedExtensionFilter().getDescription().equals("CSV Adjacency")) {
            p = new CSVAdjacency();
        } else {
            p = new CSVMatrix();
        }

        try {
            g = p.parseFile(file);
        } catch (IOException | IllegalFileException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Validan fajl");
        System.out.println("Cvorova: " + g.numOfVertices() + " grana: " + g.numOfEdges());


    }
}
