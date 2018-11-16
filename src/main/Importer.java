package main;

import javafx.scene.paint.Color;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class Importer extends Parser {

    private Graph graph = new Graph();

    public Graph parseFile(File file) throws IllegalFileException {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(file);
            doc.getDocumentElement().normalize();

            Element rootElement = doc.getDocumentElement();
            if (!rootElement.getNodeName().equals("graph"))
                throw new IllegalFileException("Invalid XML file format.");

            NodeList vertices = rootElement.getElementsByTagName("vertices");
            NodeList edges = rootElement.getElementsByTagName("edges");
            if (vertices.getLength() != 1 || edges.getLength() != 1)
                throw new IllegalFileException("Invalid XML file format.");

            NodeList verticesList;
            NodeList edgesList;

            if (vertices.item(0).getNodeType() == Node.ELEMENT_NODE) {
                verticesList = ((Element) vertices.item(0)).getElementsByTagName("vertex");
            } else
                throw new IllegalFileException("Invalid XML file format.");

            if (edges.item(0).getNodeType() == Node.ELEMENT_NODE) {
                edgesList = ((Element) edges.item(0)).getElementsByTagName("edge");
            } else
                throw new IllegalFileException("Invalid XML file format.");


            for (int i = 0; i < verticesList.getLength(); i++) {
                Node vertexNode = verticesList.item(i);
                if (vertexNode.getNodeType() == Node.ELEMENT_NODE) {
                    Vertex vertex = makeVertex(vertexNode);
                    graph.addVertex(vertex);
                }
            }

            for (int i = 0; i < edgesList.getLength(); i++) {
                Node edgeNode = edgesList.item(i);
                if (edgeNode.getNodeType() == Node.ELEMENT_NODE) {
                    Edge edge = makeEdge(edgeNode);
                    graph.addEdge(edge);
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalFileException("Invalid XML file format.");
        }

        return graph;
    }

    private Vertex makeVertex(Node vertexNode) {
        Element vertexElement = (Element) vertexNode;
        Vertex v = new Vertex(vertexElement.getAttribute("id"), vertexElement.getAttribute("label"));
        v.setX(Double.parseDouble(vertexElement.getAttribute("x")));
        v.setY(Double.parseDouble(vertexElement.getAttribute("y")));
        v.setRadius(Double.parseDouble(vertexElement.getAttribute("radius")));
        v.setFontColor(Color.valueOf(vertexElement.getAttribute("fontColor")));
        v.setFontSize(Double.parseDouble(vertexElement.getAttribute("fontSize")));
        v.setColor(Color.valueOf(vertexElement.getAttribute("color")));
        return v;
    }

    private Edge makeEdge(Node edgeNode) throws IllegalFileException {
        Element edgeElement = (Element) edgeNode;
        Vertex source = graph.getVertex(edgeElement.getAttribute("source"));
        Vertex target = graph.getVertex(edgeElement.getAttribute("target"));
        if (source == null || target == null)
            throw new IllegalFileException("Invalid XML file format.");
        Edge e = new Edge(source, target, edgeElement.getAttribute("label"));
        e.setColor(Color.valueOf(edgeElement.getAttribute("color")));
        return e;
    }
}
