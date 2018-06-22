package main;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Graph implements Cloneable {
    HashSet<Edge> edges;
    HashMap<String, Vertex> vertices;

    public Graph() {
        edges = new HashSet<>();
        vertices = new HashMap<>();
    }

    public void addVertex(Vertex v) {
        vertices.put(v.getId(), v);
    }

    public void removeVertex(Vertex v) {
        vertices.remove(v);
    }

    public void addEdge(Edge e) {
        edges.add(e);
    }

    public void removeEdge(Edge e) {
        edges.remove(e);
    }

    public Vertex getVertex(String id) {
        return vertices.get(id);
    }

    public int numOfVertices() {
        return vertices.size();
    }

    public int numOfEdges() {
        return edges.size();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Graph g = (Graph) super.clone();
        g.vertices = new HashMap<>(vertices);
        g.edges = new HashSet<>();
        for(Edge e : edges) {
            g.edges.add(new Edge(g.vertices.get(e.getSource().getId()), g.vertices.get(e.getTarget().getId())));
        }
        return  g;
    }
}
