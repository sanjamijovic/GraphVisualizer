package main;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.text.DecimalFormat;

public class Vertex extends GraphicElement{
    private String id, label;
    private double x, y, radius;
    private static final double DEFAULT_RADIUS = 10;

    public Vertex(String id) {
        this.id = id;
        this.radius = DEFAULT_RADIUS;
    }

    public Vertex(String id, String label) {
        this.id = id;
        this.label = label;
        this.radius = DEFAULT_RADIUS;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public String getLabel() {
        return label;
    }

    public String getId() {
        return id;
    }

    @Override
    public void paint(Canvas canvas) {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(color);
        graphics.fillOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    public boolean hasInside(double x, double y) {
        return x >= this.x - radius && x <= this.x + radius && y >= this.y - radius && y <= this.y + radius;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.##");
        return "[Vertex] id: " + id + "\nx: " + df.format(x) + "\ny: " + df.format(y) + "\n";
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(!(obj instanceof Vertex))
            return false;
        return ((Vertex) obj).getId().equals(id);
    }
}
