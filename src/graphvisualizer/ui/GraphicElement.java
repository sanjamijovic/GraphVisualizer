package graphvisualizer.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;


public abstract class GraphicElement {

    public GraphicElement() {
        this.color = INITIAL_COLOR;
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
    }

    abstract public void paint(Canvas c);

    protected Color color;
    private boolean isSelected;
    protected final static Color SELECTED_COLOR = Color.BLUE;
    private final static Color INITIAL_COLOR = Color.BLACK;
}
