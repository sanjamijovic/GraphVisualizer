package main;


import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.text.DecimalFormat;

public class Vertex extends GraphicElement implements Cloneable {
    private String id, label;
    private double x, y, radius;
    private static final double DEFAULT_RADIUS = 10;
    private static final double DEFAULT_FONT_SIZE = 12;
    private static final Color DEFAULT_FONT_COLOR = Color.WHITE;
    private Color fontColor = DEFAULT_FONT_COLOR;
    private boolean showLabels;
    private double fontSize = DEFAULT_FONT_SIZE;

    private Displacement displacement = new Displacement();

    public Color getFontColor() {
        return fontColor;
    }

    public void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
    }

    public Vertex(String id) {
        this.id = id;
        this.radius = DEFAULT_RADIUS;
    }

    public Vertex(String id, String label) {
        this.id = id;
        this.label = label;
        this.radius = DEFAULT_RADIUS;
    }

    public Displacement getDisplacement() {
        return displacement;
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
        if (isSelected()) {
            graphics.setStroke(SELECTED_COLOR);
            graphics.setLineWidth(graphics.getLineWidth() * 3);
            graphics.strokeOval(x - radius, y - radius, radius * 2, radius * 2);
            graphics.setLineWidth(graphics.getLineWidth() / 3);
        }
        graphics.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        if (showLabels) {
            graphics.setFont(new Font(fontSize));
            graphics.setTextAlign(TextAlignment.CENTER);
            graphics.setTextBaseline(VPos.CENTER);
            graphics.setStroke(Color.BLACK);
            graphics.strokeText(id, x, y);
            graphics.setFill(fontColor);
            graphics.fillText(id, x, y);
        }
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
        if (obj == this)
            return true;
        if (!(obj instanceof Vertex))
            return false;
        return ((Vertex) obj).getId().equals(id);
    }

    @Override
    public Vertex clone() {
        Vertex vertex = null;
        try {
            vertex =  (Vertex) super.clone();
            vertex.setSelected(false);
        } catch (CloneNotSupportedException e) {
        }
        return vertex;
    }

    public void setShowLabels(boolean labels) {
        showLabels = labels;
    }

    public boolean getShowLabels() {
        return showLabels;
    }

    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
    }

    public double getFontSize() {
        return fontSize;
    }

}
