package viewpointIOModule.fileIO.xmlExport;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import kernel.knowledgeGraph.KnowledgeGraph;
import kernel.knowledgeGraph.nodes.superModel.resources.Resource;
import kernel.knowledgeGraph.nodes.superModel.viewpoints.ConnectedViewpoint;
import org.w3c.dom.Element;

/**
 *
 * @author WillhelmK
 */
public class XMLExportModule {
    
    /**
     * Permet d'exporter le KnowledgeGraph KG dans le fichier file
     * 
     * @param file
     * @param KG
     * @throws ParserConfigurationException
     * @throws TransformerConfigurationException
     * @throws TransformerException 
     */
    public static void exportToXML(KnowledgeGraph KG, File file) throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
        System.out.print("Exporting knowledge graph to xml ... ");
        
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        org.w3c.dom.Document doc = docBuilder.newDocument();
        Element root = doc.createElement("KnowledgeGraph");
        doc.appendChild(root);
        
        Element O = doc.createElement("KnowledgeObjects");
        root.appendChild(O);
        
        for(Resource o : KG.getO()) {
            Element e = doc.createElement(o.getClass().getName());
            e.setAttribute("label", o.getLabel());
            e.setAttribute("ID", String.valueOf(o.getId()));
            O.appendChild(e);
        }
        
        Element V = doc.createElement("Viewpoints");
        root.appendChild(V);
        
        for(ConnectedViewpoint v : KG.getViewpoints()) {
            Element viewpoint = doc.createElement(v.getClass().getName());
            viewpoint.setAttribute("emitterID", String.valueOf(v.getEmitter().getId()));
            viewpoint.setAttribute("o1ID", String.valueOf(v.getO1().getId()));
            viewpoint.setAttribute("o2ID", String.valueOf(v.getO2().getId()));
            V.appendChild(viewpoint);
        }
        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
        
        System.out.println("Done (" + file.getName() + ").");
    }
    
}
