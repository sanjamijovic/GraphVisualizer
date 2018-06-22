package main;

public class Vertex {
    private String id, label;

    public Vertex(String id) {
        this.id = id;
    }

    public Vertex(String id, String label) {
        this.id = id; this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getId() {
        return id;
    }
}
