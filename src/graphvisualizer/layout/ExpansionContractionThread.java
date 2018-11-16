package graphvisualizer.layout;

import graphvisualizer.graph.Graph;
import graphvisualizer.ui.MainCanvas;
import graphvisualizer.graph.Vertex;
import javafx.application.Platform;

import java.util.Iterator;

public class ExpansionContractionThread extends AlgorithmThread {
    private double relativeToX, relativeToY;

    public ExpansionContractionThread(Graph graph, double coefficient, MainCanvas canvas) {
        super(graph, coefficient, canvas);
        this.relativeToX = canvas.getWidth() / 2;
        this.relativeToY = canvas.getHeight() / 2;
    }

    @Override
    public void run() {
        Iterator iterator = graph.getVertices().values().iterator();
        while (!interrupted()) {
            if (iterator.hasNext()) {
                Vertex v = (Vertex) iterator.next();
                v.setX((v.getX() - relativeToX) * coefficient + relativeToX);
                v.setY((v.getY() - relativeToY) * coefficient + relativeToY);
            } else {
                break;
            }
        }
        Platform.runLater(() -> canvas.repaint());
    }

}