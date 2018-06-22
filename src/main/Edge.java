package main;

public class Edge {
    private Vertex source;
    private Vertex target;
    private String label;

    public Edge(Vertex source, Vertex target) {
        this.source = source;
        this.target = target;
    }

    public Edge(Vertex source, Vertex target, String label) {
        this.source = source;
        this.target = target;
        this.label = label;
    }

    public Vertex getSource() {
        return source;
    }

    public Vertex getTarget() {
        return target;
    }

    public String getLabel() {
        return label;
    }
}
