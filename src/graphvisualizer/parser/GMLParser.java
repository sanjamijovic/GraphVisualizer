package graphvisualizer.parser;

import graphvisualizer.graph.Edge;
import graphvisualizer.graph.Graph;
import graphvisualizer.graph.Vertex;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GMLParser extends Parser {
    @Override
    public Graph parseFile(File file) throws IOException, IllegalFileException {

        String all = new String(Files.readAllBytes(Paths.get(file.getPath())));

        Matcher m = Pattern.compile("(node|edge)\\s*\\[([^]]+)]").matcher(all);
        while (m.find()) {

            if (m.toMatchResult().group(1).equals("node")) {
                // id + [label]
                String node = m.toMatchResult().group(2);
                String nodePattern = "id\\s+(\\w+)\\s+(?:label\\s+(\\w+))?\\s*";
                Matcher nodeMatcher = Pattern.compile(nodePattern).matcher(node);
                if (!nodeMatcher.find()) {
                    throw new IllegalFileException("Illegal node format");
                }
                if (nodeMatcher.groupCount() == 2) {
                    graph.addVertex(new Vertex(nodeMatcher.group(1)));
                } else {
                    graph.addVertex(new Vertex(nodeMatcher.group(1), nodeMatcher.group(2)));
                }

            } else {
                // source + target + [label]
                String edge = m.toMatchResult().group(2);
                String edgePattern = "source\\s+(\\w+)\\s+target\\s+(\\w+)\\s+(?:label\\s+(\\w+))?\\s*";
                Matcher edgeMatcher = Pattern.compile(edgePattern).matcher(edge);
                if (!edgeMatcher.find()) {
                    throw new IllegalFileException("Illegal edge format");
                }
                Vertex source = graph.getVertex(edgeMatcher.group(1));
                Vertex target = graph.getVertex(edgeMatcher.group(2));
                if (source == null || target == null)
                    throw new IllegalFileException("Edge for non existent node");
                if (edgeMatcher.groupCount() == 3) {
                    graph.addEdge(new Edge(source, target));
                } else {
                    graph.addEdge(new Edge(source, target, edgeMatcher.group(3)));
                }

            }

        }


        return graph;
    }
}
