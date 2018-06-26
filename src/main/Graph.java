package main;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Graph implements Cloneable {
    private HashSet<Edge> edges;
    private HashMap<String, Vertex> vertices;
    private final static double ZOOM_SCALE = 1.2;
    private double zoomFactor = 1;

    enum ChangeType { VERTEX_CHANGE, LABEL_CHANGE };

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

    public HashMap<String, Vertex> getVertices() {
        return vertices;
    }

    public HashSet<Edge> getEdges() {
        return edges;
    }

    public int numOfVertices() {
        return vertices.size();
    }

    public int numOfEdges() {
        return edges.size();
    }

    @Override
    protected Object clone() {
        Graph g = null;
        try {
            g = (Graph) super.clone();
        } catch (CloneNotSupportedException e) {
        }
        g.vertices = new HashMap<>();
        for(Vertex v : vertices.values()) {
            g.vertices.put(v.getId(), v.clone());
        }
        g.edges = new HashSet<>();
        for(Edge e : edges) {
            Edge newEdge = new Edge(g.getVertex(e.getSource().getId()), g.getVertex(e.getTarget().getId()), e.getLabel());
            newEdge.setColor(e.getColor());
            g.edges.add(newEdge);
        }
        return  g;
    }

    public void paint(Canvas c) {
        for(Edge e : edges)
            e.paint(c);
        for (Vertex v : vertices.values())
            v.paint(c);
    }

    public void zoomIn(double x, double y) {
        zoom(x, y, ZOOM_SCALE);
        zoomFactor *= ZOOM_SCALE;
    }

    public void zoomOut(double x, double y) {
        zoom(x, y, 1 / ZOOM_SCALE);
        zoomFactor /= ZOOM_SCALE;
    }

    public void setZoomFactor(double zoomFactor, double x, double y) {
        double relativeZoomFactor = zoomFactor / this.zoomFactor;
        this.zoomFactor = zoomFactor;
        zoom(x, y, relativeZoomFactor);
    }


    private void zoom(double x, double y, double zoomFactor) {
        for(Vertex v : vertices.values()) {
            v.setX((v.getX() - x) * zoomFactor + x);
            v.setY((v.getY() - y) * zoomFactor + y);
            v.setRadius(v.getRadius() * zoomFactor);
            v.setFontSize(v.getFontSize() * zoomFactor);
        }
    }


    public double getZoomFactor() {
        return zoomFactor;
    }


    public GraphicElement getElement(double x, double y) {
        for(Vertex v : vertices.values())
            if(v.hasInside(x, y))
                return v;
        for(Edge e : edges)
            if(e.hasInside(x, y))
                return e;
        return null;
    }

    public void deleteElement(GraphicElement e) {
        if(e instanceof Vertex) {
            vertices.remove(((Vertex) e).getId());
            edges.removeIf(edge -> edge.getSource().equals(e) || edge.getTarget().equals(e));
        }
        else
            edges.remove(e);
    }

    public LinkedList<Vertex> shortestPath(Vertex source, Vertex target) {

        System.out.println(source + " " + target);

        HashMap<Vertex, Vertex> predecessors = new HashMap<>();
        LinkedList<Vertex> vertexQueue = new LinkedList<>();
        HashSet<Vertex> visited = new HashSet<>();

        for(Vertex vertex : vertices.values()) {
            predecessors.put(vertex, null);
        }

        visited.add(source);
        vertexQueue.add(source);

        while (!vertexQueue.isEmpty()) {
            Vertex vertex = vertexQueue.removeFirst();

            for(Vertex other : nextVertices(vertex)) {
                if(!visited.contains(other)) {
                    visited.add(other);
                    vertexQueue.add(other);
                    predecessors.put(other, vertex);
                    if(other == target)
                        return calculatePath(source, target, predecessors);
                }
            }
        }

        return null;
    }

    private LinkedList<Vertex> calculatePath(Vertex source, Vertex target, HashMap<Vertex, Vertex> predecessors) {
        Vertex current = target;
        LinkedList<Vertex> nodesInPath = new LinkedList<>();

        while (current != source) {
            if(current == null)
                return null;
            nodesInPath.addFirst(current);
            current = predecessors.get(current);
        }

        nodesInPath.addFirst(current);
        return nodesInPath;
    }

    private LinkedList<Vertex> nextVertices(Vertex v) {
        LinkedList<Vertex> nextVertices = new LinkedList<>();
        for(Edge edge : edges) {
            if(edge.getSource() == v)
                nextVertices.add(edge.getTarget());
            else if(edge.getTarget() == v)
                nextVertices.add(edge.getSource());
        }

        return nextVertices;
    }

    public void showLabels(boolean labels) {
        for(Vertex v : vertices.values())
            v.setShowLabels(labels);
    }

    public HashMap<Vertex, Integer> findDegrees() {
        HashMap<Vertex, Integer> degrees = new HashMap<>();
        for(Edge edge : edges) {
            degrees.put(edge.getSource(), degrees.getOrDefault(edge.getSource(), 0) + 1);
            degrees.put(edge.getTarget(), degrees.getOrDefault(edge.getTarget(), 0) + 1);
        }
        return degrees;
    }

    public void setColorsByDegree(Color maxColor, ChangeType type) {
        HashMap<Vertex, Integer> degrees = findDegrees();
        int maxDegree = Collections.max(degrees.values());
        double minLight = maxColor.getBrightness();

        for(Vertex vertex : vertices.values()) {
            int degree = degrees.getOrDefault(vertex, 0);
            double light = minLight + (1 - minLight) * (maxDegree - degree) / maxDegree;
            if(type == ChangeType.VERTEX_CHANGE)
                vertex.setColor(maxColor.deriveColor(0, 1, light / minLight, 1));
            else
                vertex.setFontColor(maxColor.deriveColor(0, 1, light / minLight, 1));
        }
    }

    public void setSizeByDegree(double minSize, double maxSize, ChangeType type) {
        HashMap<Vertex, Integer> degrees = findDegrees();
        int maxDegree = Collections.max(degrees.values());

        for(Vertex vertex : vertices.values()) {
            int degree = degrees.getOrDefault(vertex, 0);
            double size = (maxSize - minSize) * degree / maxDegree + minSize;
            if(type == ChangeType.VERTEX_CHANGE)
                vertex.setRadius(size);
            else
                vertex.setFontSize(size);
        }
    }
}
