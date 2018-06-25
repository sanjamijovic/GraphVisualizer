package main;

public class RepulsionForce extends Force {

    public RepulsionForce(double coefficient) {
        super(coefficient);
    }

    @Override
    public void apply(Graph graph, Vertex first, Vertex second) {
        double distance = Math.sqrt(Math.pow(first.getX() - second.getX(), 2) + Math.pow(first.getY() - second.getY(), 2))
                - first.getRadius() - second.getRadius();

        double k;
        if(distance > 0) {
            k = coefficient * (graph.getDegree(first) + 1) * (graph.getDegree(second) + 1) / Math.pow(distance, 2);
        }
        else {
            k = 100 * coefficient * (graph.getDegree(first) + 1) * (graph.getDegree(second) + 1);
        }

        double displacementX, displacementY;
        displacementX = (first.getX() - second.getX()) * k;
        displacementY = (first.getY() - second.getY()) * k;

        first.setX(first.getX() + displacementX);
        second.setX(second.getX() - displacementX);
        first.setY(first.getY() + displacementY);
        second.setY(second.getY() - displacementY);
    }
}
