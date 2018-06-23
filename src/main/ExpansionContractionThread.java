package main;

import javafx.application.Platform;

import java.awt.*;
import java.util.Iterator;

public class ExpansionContractionThread extends Thread {
    private Graph graph;
    private double scale;
    private double relativeToX, relativeToY;
    private MainCanvas canvas;

    public ExpansionContractionThread(Graph graph, double scale, MainCanvas canvas) {
        this.graph = graph;
        this.scale = scale;
        this.canvas = canvas;
        this.relativeToX = canvas.getWidth() / 2;
        this.relativeToY = canvas.getHeight() / 2;
    }

    @Override
    public void run() {
        Iterator iterator = graph.getVertices().values().iterator();
        while (!interrupted()) {
            if(iterator.hasNext()) {
                Vertex v = (Vertex) iterator.next();
                v.setX((v.getX() - relativeToX) * scale + relativeToX);
                v.setY((v.getY() - relativeToY) * scale + relativeToY);
            } else {
                break;
            }
        }
        Platform.runLater(() -> canvas.repaint());
    }

}