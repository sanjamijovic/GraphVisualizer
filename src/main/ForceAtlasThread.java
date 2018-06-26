package main;

import javafx.application.Platform;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ForceAtlasThread extends AlgorithmThread {
    private AttractionForce attractionForce;
    private RepulsionForce repulsionForce;

    public ForceAtlasThread(Graph graph, double coefficient, MainCanvas canvas) {
        super(graph, coefficient, canvas);
        attractionForce = new AttractionForce(coefficient);
        repulsionForce = new RepulsionForce(coefficient);
    }

    @Override
    public void run() {
        List<Vertex> vertices = new LinkedList<>(graph.getVertices().values());
        List<Edge> edges = new LinkedList<>(graph.getEdges());

        HashMap<Vertex, Integer> degrees = graph.findDegrees();
        for(Vertex vertex : vertices)
            vertex.getDisplacement().degree = degrees.getOrDefault(vertex, 0);

        try {
            while (true) {
                for (Vertex first : vertices) {
                    for (Vertex second : vertices) {
                        repulsionForce.apply(graph, first, second);
                        if (interrupted())
                            return;
                    }
                }

                for (Edge edge : edges) {
                    attractionForce.apply(graph, edge.getSource(), edge.getTarget());
                    if (interrupted())
                        return;
                }

                for(Vertex vertex : vertices) {
                    Displacement displacement = vertex.getDisplacement();
                    double dist = Math.pow(displacement.dx - displacement.oldDx, 2) +
                            Math.pow(displacement.dy - displacement.oldDy, 2);
                    double swinging = (displacement.degree + 1) * Math.sqrt(dist);

                    double factor = 1 / (1 + Math.sqrt(swinging));

                    vertex.setX(vertex.getX() + factor * displacement.dx);
                    vertex.setY(vertex.getY() + factor * displacement.dy);
                    displacement.oldDx = displacement.dx;
                    displacement.oldDy = displacement.dy;
                    displacement.dx = 0;
                    displacement.dy = 0;
                }

                Platform.runLater(() -> canvas.repaint());
                sleep(5);
            }

        } catch (InterruptedException e) {}
    }
}
