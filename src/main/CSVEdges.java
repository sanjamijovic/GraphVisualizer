package main;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSVEdges implements Parser {
    @Override
    public Graph parseFile(File file) throws IllegalFileException, IOException {

        Graph g = new Graph();
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
                if ((src = g.getVertex(m.group(1))) == null)
                    g.addVertex(src = new Vertex(m.group(1)));
                if ((dst = g.getVertex(m.group(2))) == null)
                    g.addVertex(dst = new Vertex(m.group(2)));
                g.addEdge(new Edge(src, dst));
            }
        }
        finally {

            br.close();
        }

        return g;
    }
}
