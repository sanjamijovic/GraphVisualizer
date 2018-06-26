package main;

public class RepulsionForce extends Force {

    public RepulsionForce(double coefficient) {
        super(coefficient);
    }

    @Override
    public void apply(Graph graph, Vertex first, Vertex second) {
        double distance = Math.sqrt(Math.pow(first.getX() - second.getX(), 2) + Math.pow(first.getY() - second.getY(), 2))
                - first.getRadius() - second.getRadius();

        Displacement displacementFirst = first.getDisplacement();
        Displacement displacementSecond = second.getDisplacement();

        double k;
        if(distance > 0) {

            k = coefficient * (displacementFirst.degree + 1) * (displacementSecond.degree + 1) / distance / distance;
            double deltaX, deltaY;
            deltaX = (first.getX() - second.getX()) * k;
            deltaY = (first.getY() - second.getY()) * k;

            displacementFirst.dx += deltaX;
            displacementSecond.dx -= deltaX;
            displacementFirst.dy +=deltaY;
            displacementSecond.dy -= deltaY;
        }
        else {
            k = 100 * coefficient * (displacementFirst.degree + 1) * (displacementSecond.degree + 1);
        }


    }
}
