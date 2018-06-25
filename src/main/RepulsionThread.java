package main;

import javafx.application.Platform;

import java.util.LinkedList;
import java.util.List;

public class RepulsionThread extends AlgorithmThread {
    private RepulsionForce force;

    public RepulsionThread(Graph graph, double coefficient, MainCanvas canvas) {
        super(graph, coefficient, canvas);
        force = new RepulsionForce(coefficient);
    }

    @Override
    public void run() {
        List<Vertex> vertices = new LinkedList<>(graph.getVertices().values());

        while (true) {
            for(Vertex first : vertices) {
                for (Vertex second : vertices) {
                    force.apply(graph, first, second);
                    if(interrupted())
                        return;
                }

                Platform.runLater(() -> canvas.repaint());
            }
        }
    }

    public void stopWorking() {
        interrupt();
    }
}
