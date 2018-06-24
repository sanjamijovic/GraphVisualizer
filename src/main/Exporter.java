package main;


import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class Exporter {

    private Graph graph;
    private Document doc;
    private File file;

    public Exporter(Graph graph, File file) {
        this.graph = graph;
        this.file = file;
    }

    public void makeFile() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            doc = (Document) docBuilder.newDocument();

            // root element
            Element rootElement = doc.createElement("graph");
            doc.appendChild(rootElement);

            // vertices element
            Element verticesElement = doc.createElement("vertices");
            rootElement.appendChild(verticesElement);
            makeVertices(verticesElement);

            // edges element
            Element edgesElement = doc.createElement("edges");
            rootElement.appendChild(edgesElement);
            makeEdges(edgesElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(file.getPath()));
            transformer.transform(source, result);

        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    private void makeEdges(Element edgesElement) {
        for (Edge edge : graph.getEdges()) {
            // edge element
            Element edgeElement = doc.createElement("edge");

            // source attribute
            Attr sourceAtribute = doc.createAttribute("source");
            sourceAtribute.setValue(edge.getSource().getId());
            edgeElement.setAttributeNode(sourceAtribute);

            //target attribute
            Attr targetAtribute = doc.createAttribute("target");
            targetAtribute.setValue(edge.getTarget().getId());
            edgeElement.setAttributeNode(targetAtribute);

            //label attribute
            Attr labelAtribute = doc.createAttribute("label");
            labelAtribute.setValue(edge.getLabel());
            edgeElement.setAttributeNode(labelAtribute);

            edgesElement.appendChild(edgeElement);
        }
    }

    private void makeVertices(Element verticesElement){
        for(Vertex vertex : graph.getVertices().values()) {
            // vertex element
            Element vertexElement = doc.createElement("vertex");

            // id attribute
            Attr idAtribute = doc.createAttribute("id");
            idAtribute.setValue(vertex.getId());
            vertexElement.setAttributeNode(idAtribute);

            // label attribute
            Attr labelAttribute = doc.createAttribute("label");
            labelAttribute.setValue(vertex.getLabel());
            vertexElement.setAttributeNode(labelAttribute);

            // x attribute
            Attr xAttribute = doc.createAttribute("x");
            xAttribute.setValue(Double.toString(vertex.getX()));
            vertexElement.setAttributeNode(xAttribute);

            // y attribute
            Attr yAttribute = doc.createAttribute("y");
            yAttribute.setValue(Double.toString(vertex.getY()));
            vertexElement.setAttributeNode(yAttribute);

            // radius attribute
            Attr radiusAttribute = doc.createAttribute("radius");
            radiusAttribute.setValue(Double.toString(vertex.getRadius()));
            vertexElement.setAttributeNode(radiusAttribute);

            // font color attribute
            Attr fontColorAttribute = doc.createAttribute("fontColor");
            fontColorAttribute.setValue(vertex.getFontColor().toString());
            vertexElement.setAttributeNode(fontColorAttribute);

            // font size attribute
            Attr fontSizeAttribute = doc.createAttribute("fontSize");
            fontSizeAttribute.setValue(Double.toString(vertex.getFontSize()));
            vertexElement.setAttributeNode(fontSizeAttribute);

            // show labels attribute
            Attr showLabelsAttribute = doc.createAttribute("showLabels");
            showLabelsAttribute.setValue(Boolean.toString(vertex.getShowLabels()));
            vertexElement.setAttributeNode(showLabelsAttribute);

            verticesElement.appendChild(vertexElement);
        }
    }
}
