package main;

public class AttractionForce extends Force {

    public AttractionForce(double coefficient) {
        super(coefficient);
    }

    @Override
    public void apply(Graph graph, Vertex first, Vertex second) {
        double k = -coefficient / (graph.getDegree(first) + 1);

        double displacementX, displacementY;
        displacementX = k * (first.getX() - second.getX());
        displacementY = k * (first.getY() - second.getY());

        first.setX(first.getX() + displacementX);
        second.setX(second.getX() - displacementX);
        first.setY(first.getY() + displacementY);
        second.setY(second.getY() - displacementY);
    }
}
