package graphvisualizer.parser;

import graphvisualizer.graph.Graph;

import java.io.File;
import java.io.IOException;

public abstract class Parser {
    protected Graph graph = new Graph();

    public abstract Graph parseFile(File file) throws IOException, IllegalFileException;
}
