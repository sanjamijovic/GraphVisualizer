package main;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;


public abstract class GraphicElement {

    public GraphicElement() {
        this.color = Color.BLACK;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    abstract public void paint(Canvas c);

    protected Color color;
}
