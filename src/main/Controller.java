package main;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    private Graph graph;
    private Thread graphAlgorithmThread = null;@FXML
    private MenuItem openMenu;

    private final FileChooser openFileChooser = new FileChooser();
    private final FileChooser exportFileChooser = new FileChooser();
    private ContextMenu rightClickMenu = new ContextMenu();
    private  double rightClickX, rightClickY;

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
    @FXML
    private TextArea path;
    @FXML
    private CheckMenuItem checkLabels;
    @FXML
    private ComboBox changeType;

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
        if(file == null) {
            System.out.println("File not found");
        }
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
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Validan fajl");
        System.out.println("Cvorova: " + graph.numOfVertices() + " grana: " + graph.numOfEdges());
        updateNumbers();
        boolean randomLayout = !formatDescription.equals("XML");
        canvas.setGraph(graph, randomLayout);
        showLabels();
//        canvas.repaint();


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());
        canvas.setSelectedItem(selectedItem);
        selectedItem.setEditable(false);

        canvas.setOnMousePressed(e -> {
            if(e.getButton() == MouseButton.PRIMARY)
                rightClickMenu.hide();
        });
        canvas.setOnContextMenuRequested(event ->  {
            rightClickMenu.show(canvas, event.getScreenX(), event.getScreenY());
            rightClickX = event.getX();
            rightClickY = event.getY();
        });
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

    public void save() {
        if(graph == null)
            return;
        File file = exportFileChooser.showSaveDialog(exportMenu.getParentPopup().getScene().getWindow());
        if(file != null) {
            if (exportFileChooser.getSelectedExtensionFilter().getDescription() == "PNG")
                pngExport(file);
            else {
                Exporter exporter = new Exporter(graph, file);
                exporter.makeFile();
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
        LinkedList<GraphicElement> elements = canvas.getSelectedElements();
        for(GraphicElement element : elements) {
            if (element != null) {
                graph.deleteElement(element);
                canvas.repaint();
            }
        }
        updateNumbers();
    }

    public void change() {
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
            path.setText("Nedostizni");
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
        LinkedList<GraphicElement> selected = canvas.getSelectedElements();
        if(selected.size() != 2)
            return;
        Vertex source = (Vertex) selected.get(0);
        Vertex target = (Vertex) selected.get(1);
        graph.addEdge(new Edge(source, target));
        canvas.repaint();
    }

    private void addVertex(ActionEvent event) {
        String id = Integer.toString((int) (Math.random() * Short.MAX_VALUE));
        while (graph.getVertex(id) != null)
            id = Integer.toString((int) (Math.random() * Integer.MAX_VALUE));
        Vertex vertex = new Vertex(id, id);
        graph.addVertex(vertex);
        vertex.setX(rightClickX);
        vertex.setY(rightClickY);
        vertex.setRadius(vertex.getRadius() * graph.getZoomFactor());
        boolean checked = checkLabels.isSelected();
        vertex.setShowLabels(checked);
        canvas.repaint();
    }

}
