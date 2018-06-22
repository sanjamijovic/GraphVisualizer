package main;

import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSVAdjacency implements Parser {
    @Override
    public Graph parseFile(File file) throws IllegalFileException, IOException {
        Graph g = new Graph();


        FileInputStream fstream = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String line;
        try {
            while ((line = br.readLine()) != null) {

                String[] tokens = line.split(";");

                if(tokens.length < 2)
                    throw new IllegalFileException("Illegal file format");

                Vertex src, dst;
                if((src = g.getVertex(tokens[0])) == null)
                    g.addVertex(src = new Vertex(tokens[0]));

                for (String token : tokens) {
                    if (token.equals(""))
                        throw new IllegalFileException("Illegal file format");
                    if ((dst = g.getVertex(token)) == null)
                        g.addVertex(dst = new Vertex(token));
                    g.addEdge(new Edge(src, dst));

                }

            }
        }
        finally {
            br.close();
        }

        return g;
    }
}
