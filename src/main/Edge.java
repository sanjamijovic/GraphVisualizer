package main;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class Edge extends GraphicElement {
    private Vertex source;
    private Vertex target;
    private String label;

    public Edge(Vertex source, Vertex target) {
        this.source = source;
        this.target = target;
    }

    public Edge(Vertex source, Vertex target, String label) {
        this.source = source;
        this.target = target;
        this.label = label;
    }

    public Vertex getSource() {
        return source;
    }

    public Vertex getTarget() {
        return target;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public void paint(Canvas canvas) {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(color);
        graphics.strokeLine(source.getX(), source.getY(), target.getX(), target.getY());
    }
}
