package main;

public class AlgorithmThread extends Thread {
    protected MainCanvas canvas;
    protected Graph graph;
    protected double coefficient;

    public AlgorithmThread(Graph graph, double coefficient, MainCanvas canvas) {
        this.canvas = canvas;
        this.graph = graph;
        this.coefficient = coefficient;
    }
}
