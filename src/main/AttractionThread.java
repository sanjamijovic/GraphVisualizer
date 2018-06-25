package main;

import javafx.application.Platform;

public class AttractionThread extends AlgorithmThread {
    private AttractionForce force;

    public AttractionThread(Graph graph, double coefficient, MainCanvas canvas) {
        super(graph, coefficient, canvas);
            force = new AttractionForce(coefficient);
    }

    @Override
    public void run() {
        while (true) {
            for(Edge edge : graph.getEdges()) {
                force.apply(graph, edge.getSource(), edge.getTarget());
                if(interrupted())
                    return;
            }
            Platform.runLater(() -> canvas.repaint());
        }
    }

    public void stopWorking() {
        interrupt();
    }
}
