package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    private Graph graph;

    private final FileChooser fileChooser = new FileChooser();

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
    @FXML
    private MainCanvas canvas;
    @FXML
    private Pane canvasPane;
    @FXML
    private Label numNodes;
    @FXML
    private Label numEdges;


    public void open(ActionEvent actionEvent) {
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
            graph = p.parseFile(file);
        } catch (IOException | IllegalFileException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Validan fajl");
        System.out.println("Cvorova: " + graph.numOfVertices() + " grana: " + graph.numOfEdges());
        numNodes.setText(Integer.toString(graph.numOfVertices()));
        numEdges.setText(Integer.toString(graph.numOfEdges()));
        canvas.setGraph(graph);
        paint();


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());
    }

    private void paint() {
        graph.paint(canvas);
    }

    public void zoomIn() {
        graph.zoomIn(canvas.getWidth() / 2, canvas.getHeight() / 2);
        canvas.repaint();
    }

    public void zoomOut() {
        graph.zoomOut(canvas.getWidth() / 2, canvas.getHeight() / 2);
        canvas.repaint();
    }
}
