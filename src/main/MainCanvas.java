package main;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;


public class MainCanvas extends Canvas {
    private Graph graph;
    private TextArea selectedItem;
    private GraphicElement selectedElement;

    public MainCanvas() {
        widthProperty().addListener(e->repaint());
        heightProperty().addListener(e->repaint());
        setOnScroll(e -> {
            if(graph != null) {
                if(e.getDeltaY() > 0)
                    graph.zoomIn(e.getX(), e.getY());
                else
                    graph.zoomOut(e.getX(), e.getY());
                repaint();
            }
        });

        setOnMousePressed(e -> {
            if(graph == null)
                return;
            selectedElement = graph.getElement(e.getX(), e.getY());
            if(selectedElement != null) {
                selectedElement.setColor(Color.PINK);
                selectedItem.setText(selectedElement.toString());
                repaint();
            }
        });

        setOnMouseDragged(e -> {
            if(graph == null || selectedElement == null)
                return;
            if(selectedElement instanceof Vertex) {
                ((Vertex)selectedElement).setX(e.getX());
                ((Vertex)selectedElement).setY(e.getY());
                repaint();
            }

        });
    }

    public void paint() {
        if(graph != null)
            graph.paint(this);
    }

    public void repaint() {
        // initializeGraphLayout();
        getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());
        paint();
    }

    void setGraph(Graph graph) {
        this.graph = graph;
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
        if(graph == null)
            return;
        double limitLowX = getWidth() / 2 - 0.4 * getHeight();
        double limitHighX = getWidth() / 2 + 0.4 * getHeight();
        double limitLowY = getHeight() / 2 - 0.4 * getHeight();
        double limitHighY = getHeight() / 2 + 0.4 * getHeight();

        for(Vertex v : graph.getVertices().values()) {
            double x = limitLowX + (limitHighX - limitLowX) * Math.random();
            double y = limitLowY + (limitHighY - limitLowY) * Math.random();
            v.setX(x);
            v.setY(y);
        }
    }

    public void setSelectedItem(TextArea selectedItem) {
        this.selectedItem = selectedItem;
    }

    public GraphicElement getSelectedElement() {
        return selectedElement;
    }
}
