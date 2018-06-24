package main;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    private Graph graph;
    private Thread graphAlgorithmThread = null;@FXML
    private MenuItem openMenu;
    @FXML
    private MenuItem exportMenu;
    @FXML
    private MainCanvas canvas;
    @FXML
    private Pane canvasPane;
    @FXML
    private Label numNodes;
    @FXML
    private Label numEdges;
    @FXML
    private ComboBox algorithmChooser;
    @FXML
    private TextField scale;
    @FXML
    private TextArea selectedItem;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private ComboBox sizeChooser;

    private final FileChooser openFileChooser = new FileChooser();
    private final FileChooser exportFileChooser = new FileChooser();

    public Controller() {
        openFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GML", "*.gml"),
                new FileChooser.ExtensionFilter("CSV Edges", "*.csv"),
                new FileChooser.ExtensionFilter("CSV Adjacency", "*.csv"),
                new FileChooser.ExtensionFilter("CSV Matrix", "*.csv")
        );
        exportFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("Nas format", ".nas")
        );
    }

    public void open(ActionEvent actionEvent) {
        File file = openFileChooser.showOpenDialog(openMenu.getParentPopup().getScene().getWindow());
        if(file == null) {
            System.out.println("File not found");
        }
        Parser p;

        if(openFileChooser.getSelectedExtensionFilter().getDescription().equals("GML")) {
            p = new GMLParser();
        } else if (openFileChooser.getSelectedExtensionFilter().getDescription().equals("CSV Edges")){
            p = new CSVEdges();
        } else if(openFileChooser.getSelectedExtensionFilter().getDescription().equals("CSV Adjacency")) {
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
        canvas.repaint();


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());
        canvas.setSelectedItem(selectedItem);
        selectedItem.setEditable(false);
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

    public void export() {
        File file = exportFileChooser.showSaveDialog(exportMenu.getParentPopup().getScene().getWindow());
        if(file != null) {
            if (exportFileChooser.getSelectedExtensionFilter().getDescription() == "PNG")
                pngExport(file);
            else {
                // TODO: napraviti za svoj format
            }
        }
    }

    public void pngExport(File file) {
        WritableImage image = canvas.snapshot(new SnapshotParameters(), null);
        if(file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException e) {
                // TODO: handle exception here
            }
        }
    }

    public void startThread() {
        if(algorithmChooser.getValue() == null)
            return;

        String algorithm = algorithmChooser.getValue().toString();
        if(algorithm.equals("Expansion/Contraction")) {
            try {
                startExpansionContraction(Double.parseDouble(scale.getText()));
            } catch (NumberFormatException e) {}
        } else {
            // TODO: force atlas
        }

    }

    public void stopThread() {
        if(graphAlgorithmThread != null)
            graphAlgorithmThread.interrupt();
    }

    public void startExpansionContraction(double scale) {
        if(graphAlgorithmThread != null)
            graphAlgorithmThread.interrupt();
        if(graph == null)
            return;
        graphAlgorithmThread = new ExpansionContractionThread(graph, scale, canvas);
        graphAlgorithmThread.start();
    }

    public void delete() {
        HashSet<GraphicElement> elements = canvas.getSelectedElements();
        for(GraphicElement element : elements) {
            if (element != null) {
                graph.deleteElement(element);
                canvas.repaint();
            }
        }
    }

    public void changeColor() {
        if(graph == null)
            return;

        for(GraphicElement element : canvas.getSelectedElements())
            element.setColor(colorPicker.getValue());
        canvas.repaint();
    }

    public void changeSize() {
        if(graph == null || sizeChooser.getValue() == null)
            return;

        for(GraphicElement element : canvas.getSelectedElements()) {
            if(element instanceof Vertex) {
                String sizePercentage = sizeChooser.getValue().toString();
                double alpha = 1;
                switch (sizePercentage) {
                    case "50%" : alpha = 0.5; break;
                    case "75%" : alpha = 0.75; break;
                    case "125%" : alpha = 1.25; break;
                    case "150%" : alpha = 1.5; break;
                    case "200%" : alpha = 2; break;
                }
                ((Vertex) element).setRadius(((Vertex) element).getRadius() * alpha);
            }
        }
        canvas.repaint();
    }

    public void shortestPath() {
        System.out.println("shortest path");
    }
}
