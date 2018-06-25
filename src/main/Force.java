package main;

public abstract class Force {
    protected double coefficient;

    public Force(double coefficient) {
        this.coefficient = coefficient;
    }
    abstract void apply(Graph graph, Vertex first, Vertex second);
}
