package main;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;


public abstract class GraphicElement {

    public GraphicElement() {
        this.color = NON_SELECTED_COLOR;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        if(isSelected)
            color = SELECTED_COLOR;
        else
            color = NON_SELECTED_COLOR;
    }

    abstract public void paint(Canvas c);

    protected Color color;
    private boolean isSelected;
    private final static Color SELECTED_COLOR = Color.BLUE;
    private final static Color NON_SELECTED_COLOR = Color.BLACK;
}
