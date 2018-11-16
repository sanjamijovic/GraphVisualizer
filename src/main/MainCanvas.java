package main;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextArea;

import java.util.LinkedList;


public class MainCanvas extends Canvas {
    private Graph graph;
    private TextArea selectedItem;
    private LinkedList<GraphicElement> selectedElements = new LinkedList<>();
    private double oldMouseX, oldMouseY;

    public MainCanvas() {
        widthProperty().addListener(e -> repaint());
        heightProperty().addListener(e -> repaint());
        setOnScroll(e -> {
            if (graph != null) {
                if (e.getDeltaY() > 0)
                    graph.zoomIn(e.getX(), e.getY());
                else
                    graph.zoomOut(e.getX(), e.getY());
                repaint();
            }
        });

        setOnMousePressed(e -> {
            oldMouseX = e.getX();
            oldMouseY = e.getY();

            if (graph == null)
                return;
            GraphicElement selectedElement = graph.getElement(e.getX(), e.getY());

            if (selectedElement != null && selectedElements.contains(selectedElement)) {
                return;
            } else if (selectedElement != null) {
                selectElement(selectedElement);
                selectedItem.appendText(selectedElement.toString());

            } else {
                deselectAll();

            }
            repaint();
        });

        setOnMouseDragged(e -> {
            if (graph == null || selectedElements.size() == 0)
                return;
            double dx = e.getX() - oldMouseX;
            double dy = e.getY() - oldMouseY;

            for (GraphicElement element : selectedElements) {
                if (element instanceof Vertex) {
                    ((Vertex) element).setX(((Vertex) element).getX() + dx);
                    ((Vertex) element).setY(((Vertex) element).getY() + dy);
                }
            }

            oldMouseX = e.getX();
            oldMouseY = e.getY();

            repaint();

        });
    }

    public void paint() {
        if (graph != null)
            graph.paint(this);
    }

    public void repaint() {
        getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());
        paint();
    }

    void setGraph(Graph graph, boolean toInitialize, boolean showLabels) {
        this.graph = graph;
        graph.showLabels(showLabels);
        if (toInitialize)
            initializeGraphLayout();
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    private void initializeGraphLayout() {
        if (graph == null)
            return;
        double limitLowX = getWidth() / 2 - 0.4 * getHeight();
        double limitHighX = getWidth() / 2 + 0.4 * getHeight();
        double limitLowY = getHeight() / 2 - 0.4 * getHeight();
        double limitHighY = getHeight() / 2 + 0.4 * getHeight();

        for (Vertex v : graph.getVertices().values()) {
            double x = limitLowX + (limitHighX - limitLowX) * Math.random();
            double y = limitLowY + (limitHighY - limitLowY) * Math.random();
            v.setX(x);
            v.setY(y);
        }
    }

    public void setSelectedItem(TextArea selectedItem) {
        this.selectedItem = selectedItem;
    }

    public LinkedList<GraphicElement> getSelectedElements() {
        return selectedElements;
    }

    public void selectElement(GraphicElement element) {
        selectedElements.add(element);
        element.setSelected(true);
    }

    public void selectAll() {
        if (graph == null)
            return;
        if (selectedElements.size() != 0)
            selectedElements.clear();
        for (Vertex v : graph.getVertices().values())
            selectElement(v);
        for (Edge e : graph.getEdges())
            selectElement(e);
        selectedItem.setText("Whole graph selected");
        repaint();
    }

    public void deselectAll() {
        for (GraphicElement element : selectedElements)
            element.setSelected(false);
        selectedItem.setText("");
        selectedElements.clear();
    }

    public double getZoomFactor() {
        return graph.getZoomFactor();
    }
}
