package graphvisualizer.parser;

import graphvisualizer.graph.Edge;
import graphvisualizer.graph.Graph;
import graphvisualizer.graph.Vertex;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSVEdges extends Parser {
    @Override
    public Graph parseFile(File file) throws IllegalFileException, IOException {
        FileInputStream fstream = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String line;
        try {
            while ((line = br.readLine()) != null) {
                if (line.matches("source\\s*;target\\s*")) {
                    continue;
                }
                String pattern = "^([^;]+);([^;\\s]+)$";
                Matcher m = Pattern.compile(pattern).matcher(line);
                if (!m.find())
                    throw new IllegalFileException("Invalid file format");
                Vertex src, dst;
                if ((src = graph.getVertex(m.group(1))) == null)
                    graph.addVertex(src = new Vertex(m.group(1)));
                if ((dst = graph.getVertex(m.group(2))) == null)
                    graph.addVertex(dst = new Vertex(m.group(2)));
                graph.addEdge(new Edge(src, dst));
            }
        } finally {

            br.close();
        }

        return graph;
    }
}
