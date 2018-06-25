package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class Parser {
    protected Graph graph = new Graph();

    abstract Graph parseFile(File file) throws IOException, IllegalFileException;
}
