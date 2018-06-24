package main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GMLParser implements Parser {
    @Override
    public Graph parseFile(File file) throws IOException, IllegalFileException {
        Graph g = new Graph();

        String all = new String(Files.readAllBytes(Paths.get(file.getPath())));

        String pattern = "graph\\s*\\[(?:\\s*(?:node|edge)\\s*\\[[^]]+]\\s*)+]\\s*";
        if(!all.matches(pattern))
            throw new IllegalFileException("Invalid file format");

        Matcher m = Pattern.compile("(node|edge)\\s*\\[([^]]+)]").matcher(all);
        while(m.find()) {

            if(m.toMatchResult().group(1).equals("node")) {
                // id + [label]
                String node = m.toMatchResult().group(2);
                String nodePattern = "id\\s+(\\w+)\\s+(?:label\\s+(\\w+))?\\s*";
                Matcher nodeMatcher = Pattern.compile(nodePattern).matcher(node);
                if(!nodeMatcher.find()) {
                    throw new IllegalFileException("Illegal node format");
                }
                if(nodeMatcher.groupCount() == 2) {
                    g.addVertex(new Vertex(nodeMatcher.group(1)));
                }
                else {
                    g.addVertex(new Vertex(nodeMatcher.group(1), nodeMatcher.group(2)));
                }

            } else {
                // source + target + [label]
                String edge = m.toMatchResult().group(2);
                String edgePattern = "source\\s+(\\w+)\\s+target\\s+(\\w+)\\s+(?:label\\s+(\\w+))?\\s*";
                Matcher edgeMatcher = Pattern.compile(edgePattern).matcher(edge);
                if(!edgeMatcher.find()) {
                    throw new IllegalFileException("Illegal edge format");
                }
                Vertex source = g.getVertex(edgeMatcher.group(1));
                Vertex target = g.getVertex(edgeMatcher.group(2));
                if(source == null || target == null)
                    throw new IllegalFileException("Edge for non existent node");
                if(edgeMatcher.groupCount() == 3) {
                    g.addEdge(new Edge(source, target));
                }
                else {
                    g.addEdge(new Edge(source, target, edgeMatcher.group(3)));
                }

            }

        }


        return g;
    }
}
