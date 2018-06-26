package main;

import java.io.*;

public class CSVMatrix extends Parser {
    @Override
    public Graph parseFile(File file) throws IllegalFileException, IOException {
        FileInputStream fstream = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String line = br.readLine();
        if(!line.matches("(;[^;]+)+")) {
            throw new IllegalFileException("Illegal file format");
        }
        String[] nodeLabels = line.split(";");
        for(int i = 1; i < nodeLabels.length; i++)
            graph.addVertex(new Vertex(nodeLabels[i]));


        try {
            int i = 1;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(";");
                if(tokens.length != nodeLabels.length || !tokens[0].equals(nodeLabels[i]))
                    throw new IllegalFileException("Illegal file format");
                for(int j = 1; j < tokens.length; j++) {
                    if(tokens[j].equals("1")) {
                        graph.addEdge(new Edge(graph.getVertex(nodeLabels[i]), graph.getVertex(nodeLabels[j])));
                    }
                    else if(!tokens[j].equals("0"))
                        throw new IllegalFileException("Illegal file exception");
                }
                i++;
            }
            if(i != nodeLabels.length)
                throw new IllegalFileException("Illegal file exception");
        } finally {
            br.close();
        }

        return graph;
    }
}
