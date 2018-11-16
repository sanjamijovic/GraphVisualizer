package graphvisualizer.layout;

import graphvisualizer.graph.Graph;
import graphvisualizer.graph.Vertex;

public class AttractionForce extends Force {

    public AttractionForce(double coefficient) {
        super(coefficient);
    }

    @Override
    public void apply(Graph graph, Vertex first, Vertex second) {

        Displacement displacementFirst = first.getDisplacement();
        Displacement displacementSecond = second.getDisplacement();

        double k = - coefficient / (displacementFirst.degree + 1);

        double deltaX, deltaY;
        deltaX = k * (first.getX() - second.getX());
        deltaY = k * (first.getY() - second.getY());


        displacementFirst.dx += deltaX;
        displacementSecond.dx -= deltaX;
        displacementFirst.dy +=deltaY;
        displacementSecond.dy -= deltaY;
    }
}
