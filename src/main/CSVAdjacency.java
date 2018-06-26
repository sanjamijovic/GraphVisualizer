package main;

import java.io.*;

public class CSVAdjacency extends Parser {
    @Override
    public Graph parseFile(File file) throws IllegalFileException, IOException {
        FileInputStream fstream = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String line;
        try {
            while ((line = br.readLine()) != null) {

                String[] tokens = line.split(";");

                Vertex src, dst;
                if((src = graph.getVertex(tokens[0])) == null)
                    graph.addVertex(src = new Vertex(tokens[0]));

                for (String token : tokens) {
                    if (token.equals(""))
                        throw new IllegalFileException("Illegal file format");
                    if ((dst = graph.getVertex(token)) == null)
                        graph.addVertex(dst = new Vertex(token));
                    graph.addEdge(new Edge(src, dst));

                }

            }
        }
        finally {
            br.close();
        }

        return graph;
    }
}
