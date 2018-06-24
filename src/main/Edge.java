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
        graphics.setStroke(color);
        if(isSelected())
            graphics.setLineWidth(graphics.getLineWidth() * 2);
        graphics.strokeLine(source.getX(), source.getY(), target.getX(), target.getY());
        if(isSelected())
            graphics.setLineWidth(graphics.getLineWidth() / 2);
    }

    public boolean hasInside(double x, double y) {
        double epsilon = 0.01;
        double difference = Vertex.distance(source.getX(), source.getY(), x, y) + Vertex.distance(x, y, target.getX(), target.getY())
                - Vertex.distance(source.getX(), source.getY(), target.getX(), target.getY());
        return Math.abs(difference) < epsilon;
    }

    @Override
    public String toString() {
        return "[Edge] source: " + source + " target: " + target + "\n";
    }
}
