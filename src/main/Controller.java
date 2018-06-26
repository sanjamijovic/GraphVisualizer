package main;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.Stack;

public class Controller implements Initializable{

    private Graph graph;
    private Thread algorithmThread = null;

    private ContextMenu rightClickMenu = new ContextMenu();
    private  double rightClickX, rightClickY;

    private final FileChooser openFileChooser = new FileChooser();
    private final FileChooser exportFileChooser = new FileChooser();

    private Stack<Graph> undoStack = new Stack<>();
    private Stack<Graph> redoStack = new Stack<>();


    @FXML
    private MainCanvas canvas;
    @FXML
    private Pane canvasPane;
    @FXML
    private MenuItem openMenu;
    @FXML
    private MenuItem exportMenu;
    @FXML
    private CheckMenuItem checkLabels;
    @FXML
    private ComboBox algorithmChooser;
    @FXML
    private ComboBox sizeChooser;
    @FXML
    private ComboBox changeType;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private ColorPicker nodeFormattingColorPicker;
    @FXML
    private ColorPicker labelFormattingColorPicker;
    @FXML
    private TextField scale;
    @FXML
    private TextField nodeMinSize;
    @FXML
    private TextField nodeMaxSize;
    @FXML
    private TextField labelMinSize;
    @FXML
    private TextField labelMaxSize;
    @FXML
    private TextArea selectedItem;
    @FXML
    private TextArea path;
    @FXML
    private Label numNodes;
    @FXML
    private Label numEdges;

    public Controller() {
        openFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GML", "*.gml"),
                new FileChooser.ExtensionFilter("CSV Edges", "*.csv"),
                new FileChooser.ExtensionFilter("CSV Adjacency", "*.csv"),
                new FileChooser.ExtensionFilter("CSV Matrix", "*.csv"),
                new FileChooser.ExtensionFilter("XML", "*.xml")
        );
        exportFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("XML", "*.xml")
        );

        MenuItem addNode = new MenuItem("Add node");
        addNode.setOnAction(this::addVertex);
        rightClickMenu.getItems().addAll(addNode);
    }

    public void open(ActionEvent actionEvent) {
        File file = openFileChooser.showOpenDialog(openMenu.getParentPopup().getScene().getWindow());
        if(file == null)
            return;
        Parser p;
        String formatDescription = openFileChooser.getSelectedExtensionFilter().getDescription();

        if(formatDescription.equals("XML")) {
            p = new Importer();
        } else if(formatDescription.equals("GML")) {
            p = new GMLParser();
        } else if (formatDescription.equals("CSV Edges")){
            p = new CSVEdges();
        } else if(formatDescription.equals("CSV Adjacency")) {
            p = new CSVAdjacency();
        } else {
            p = new CSVMatrix();
        }

        try {
            graph = p.parseFile(file);
        } catch (IOException | IllegalFileException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid file format");
            alert.setContentText("Selected file does not match " + formatDescription + " format.");
            alert.showAndWait();
            return;
        }

        updateNumbers();
        boolean randomLayout = !formatDescription.equals("XML");
        canvas.setGraph(graph, randomLayout, checkLabels.isSelected());
        canvas.repaint();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());
        canvas.setSelectedItem(selectedItem);
        selectedItem.setEditable(false);

        canvas.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY)
                rightClickMenu.hide();
        });
        canvas.setOnContextMenuRequested(event ->  {
            rightClickMenu.show(canvas, event.getScreenX(), event.getScreenY());
            rightClickX = event.getX();
            rightClickY = event.getY();
        });
    }

    public void zoomIn() {
        // prosledjuju se koordinate u odnosu na koje se relativno zoomira (centar canvasa)
        graph.zoomIn(canvas.getWidth() / 2, canvas.getHeight() / 2);
        canvas.repaint();
    }

    public void zoomOut() {
        graph.zoomOut(canvas.getWidth() / 2, canvas.getHeight() / 2);
        canvas.repaint();
    }

    public void save() {
        if(graph == null)
            return;
        File file = exportFileChooser.showSaveDialog(exportMenu.getParentPopup().getScene().getWindow());
        if(file != null) {
            if (exportFileChooser.getSelectedExtensionFilter().getDescription().equals("PNG"))
                pngExport(file);
            else {
                xmlExport(file);
            }
        }
    }

    private void pngExport(File file) {

        double zoom = graph.getZoomFactor();
        if(zoom !=  1) {
            graph.setZoomFactor(1, canvas.getWidth() / 2, canvas.getHeight() / 2);
            canvas.repaint();
        }

        WritableImage image = canvas.snapshot(new SnapshotParameters(), null);

        if(zoom != 1) {
            graph.setZoomFactor(zoom, canvas.getWidth() / 2, canvas.getHeight() / 2);
            canvas.repaint();
        }

        if(file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException e) {
            }
        }
    }

    private void xmlExport(File file) {
        double zoom = graph.getZoomFactor();
        if(zoom !=  1)
            graph.setZoomFactor(1, canvas.getWidth() / 2, canvas.getHeight() / 2);
        Exporter exporter = new Exporter(graph, file);
        exporter.makeFile();
        if(zoom != 1)
            graph.setZoomFactor(zoom, canvas.getWidth() / 2, canvas.getHeight() / 2);
    }

    public void startThread() {
        if(algorithmChooser.getValue() == null || graph == null)
            return;

        String algorithm = algorithmChooser.getValue().toString();
        if(algorithm.equals("Expansion/Contraction")) {
            try {
                startExpansionContraction(Double.parseDouble(scale.getText()));
            } catch (NumberFormatException e) {}
        } else {
            try {
                startForceAtlas(Double.parseDouble(scale.getText()));
            } catch (NumberFormatException e) {}
        }

    }

    public void stopThread() {
        if(algorithmThread != null)
            algorithmThread.interrupt();
    }

    public void startExpansionContraction(double scale) {
        if(algorithmThread != null)
            algorithmThread.interrupt();
        if(graph == null)
            return;
        algorithmThread = new ExpansionContractionThread(graph, scale, canvas);
        algorithmThread.start();
    }

    public void startForceAtlas(double coefficient) {
        if(algorithmThread != null)
            algorithmThread.interrupt();

        if(graph == null)
            return;

        algorithmThread = new ForceAtlasThread(graph, coefficient, canvas);
        algorithmThread.start();
    }

    public void delete() {
        putToUndo();
        LinkedList<GraphicElement> elements = canvas.getSelectedElements();
        for(GraphicElement element : elements) {
            if (element != null) {
                graph.deleteElement(element);
            }
        }
        elements.clear();
        selectedItem.setText("");
        canvas.repaint();
        updateNumbers();
    }

    public void change() {
        putToUndo();
        if(colorPicker.getValue() != null)
            changeColor();
        if(sizeChooser.getValue() != null)
            changeSize();
    }

    public void changeColor() {
        if(graph == null || changeType.getValue() == null)
            return;
        for(GraphicElement element : canvas.getSelectedElements()) {
            if (changeType.getValue().toString().equals("Element")) {
                element.setColor(colorPicker.getValue());
            }
            else if(element instanceof Vertex) {
                ((Vertex) element).setFontColor(colorPicker.getValue());
            }
        }
        canvas.repaint();
    }

    public void changeSize() {
        if(graph == null || changeType.getValue() == null)
            return;

        String sizePercentage = sizeChooser.getValue().toString();
        double alpha;

        switch (sizePercentage) {
            case "50%":
                alpha = 0.5;
                break;
            case "75%":
                alpha = 0.75;
                break;
            case "125%":
                alpha = 1.25;
                break;
            case "150%":
                alpha = 1.5;
                break;
            case "200%":
                alpha = 2;
                break;
                default:
                    alpha = 1;
                    break;
        }

        for(GraphicElement element : canvas.getSelectedElements()) {
            if (element instanceof Vertex) {
                if (changeType.getValue().toString().equals("Element")) {
                    ((Vertex) element).setRadius(((Vertex) element).getRadius() * alpha);
                } else {
                    ((Vertex) element).setFontSize(((Vertex) element).getFontSize() * alpha);
                }
            }
        }
        canvas.repaint();
    }

    public void shortestPath() {
        if(canvas.getSelectedElements().size() != 2) {
            path.setText("Nevalidan broj cvorova.");
            return;
        }

        Vertex source = (Vertex) canvas.getSelectedElements().get(0);
        Vertex target = (Vertex) canvas.getSelectedElements().get(1);
        LinkedList<Vertex> nodes = graph.shortestPath(source, target);

        if(nodes == null) {
            path.setText("Nedostizni cvorovi. ");
            return;
        }

        path.setText("Shortest path: " + nodes.size() + "\n");
        for(Vertex v : nodes) {
            path.appendText(v.getId());
            path.appendText("\n");
        }
    }

    public void selectAll() {
        canvas.selectAll();
    }

    private void updateNumbers() {
        numNodes.setText(Integer.toString(graph.numOfVertices()));
        numEdges.setText(Integer.toString(graph.numOfEdges()));
    }

    public void  showLabels() {
        if(graph == null)
            return;
        boolean checked = checkLabels.isSelected();
        graph.showLabels(checked);
        canvas.repaint();
    }

    public void createEdge() {
        putToUndo();
        LinkedList<GraphicElement> selected = canvas.getSelectedElements();
        if(selected.size() != 2)
            return;
        Vertex source = (Vertex) selected.get(0);
        Vertex target = (Vertex) selected.get(1);
        graph.addEdge(new Edge(source, target));
        canvas.repaint();
    }

    private void addVertex(ActionEvent event) {
        putToUndo();
        String id = Integer.toString((int) (Math.random() * Short.MAX_VALUE));
        while (graph.getVertex(id) != null)
            id = Integer.toString((int) (Math.random() * Integer.MAX_VALUE));
        Vertex vertex = new Vertex(id, id);
        graph.addVertex(vertex);
        vertex.setX(rightClickX);
        vertex.setY(rightClickY);
        vertex.setRadius(vertex.getRadius() * graph.getZoomFactor());
        vertex.setFontSize(vertex.getFontSize() * graph.getZoomFactor());
        boolean checked = checkLabels.isSelected();
        vertex.setShowLabels(checked);
        canvas.repaint();
    }

    public void undo() {
        undoRedo(redoStack, undoStack);
    }

    public void redo() {
        undoRedo(undoStack, redoStack);
    }

    private void undoRedo(Stack<Graph> pushStack, Stack<Graph> popStack) {
        if(popStack.size() == 0)
            return;
        pushStack.push(graph);
        graph = popStack.pop();
        canvas.setGraph(graph, false, checkLabels.isSelected());
        canvas.repaint();
    }

    public void putToUndo() {
        redoStack.clear();
        Graph cloneGraph = (Graph) graph.clone();
        undoStack.push(cloneGraph);
    }

    public void nodeColorFormatting(){
        Color maxColor = nodeFormattingColorPicker.getValue();
        graph.setColorsByDegree(maxColor, Graph.ChangeType.VERTEX_CHANGE);
        canvas.repaint();
    }

    public void nodeSizeFormatting() {
        double minSize, maxSize;
        try {
            minSize = Double.parseDouble(nodeMinSize.getText());
            maxSize = Double.parseDouble(nodeMaxSize.getText());
        } catch (NumberFormatException e) { return; }
        graph.setSizeByDegree(minSize, maxSize, Graph.ChangeType.VERTEX_CHANGE);
        canvas.repaint();
    }

    public void labelColorFormatting() {
        Color maxColor = labelFormattingColorPicker.getValue();
        graph.setColorsByDegree(maxColor, Graph.ChangeType.LABEL_CHANGE);
        canvas.repaint();
    }

    public void labelSizeFormatting() {
        double minSize, maxSize;
        try {
            minSize = Double.parseDouble(labelMinSize.getText());
            maxSize = Double.parseDouble(labelMaxSize.getText());
        } catch (NumberFormatException e) { return; }
        graph.setSizeByDegree(minSize, maxSize, Graph.ChangeType.LABEL_CHANGE);
        canvas.repaint();
    }

}
