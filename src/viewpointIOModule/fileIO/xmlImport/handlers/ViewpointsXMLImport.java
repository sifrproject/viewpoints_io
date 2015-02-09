package viewpointIOModule.fileIO.xmlImport.handlers;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import kernel.knowledgeGraph.KnowledgeGraph;
import kernel.knowledgeGraph.nodes.superModel.resources.Agent;
import kernel.knowledgeGraph.nodes.superModel.resources.ArtificialAgent;
import kernel.knowledgeGraph.nodes.superModel.resources.HumanAgent;
import kernel.knowledgeGraph.nodes.superModel.resources.Resource;
import kernel.knowledgeGraph.nodes.superModel.viewpoints.ViewpointFactory;
import kernel.knowledgeGraph.nodes.superModel.viewpoints.ViewpointPolarity;
import kernel.knowledgeGraph.nodes.webSpecificModel.resources.Document;
import kernel.knowledgeGraph.nodes.webSpecificModel.resources.Topic;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import tests.Tests;
import viewpointIOModule.fileIO.xmlExport.XMLExportModule;
import viewpointIOModule.fileIO.xmlImport.ViewpointsXMLHandler;
import viewpointIOModule.fileIO.xmlImport.XMLImportModule;

/**
 *
 * @author WillhelmK
 */
public class ViewpointsXMLImport extends ViewpointsXMLHandler {

    public ViewpointsXMLImport(KnowledgeGraph KG) {
        super(KG);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        switch(qName) {
            case "HumanAgent":
                HumanAgent ha = new HumanAgent(Integer.parseInt(attributes.getValue("ID")));
                ha.setLabel(attributes.getValue("label"));
                KG.addResource(ha);
                break;
                
            case "ArtificialAgent":
                ArtificialAgent aa = new ArtificialAgent(Integer.parseInt(attributes.getValue("ID")));
                aa.setLabel(attributes.getValue("label"));
                KG.addResource(aa);
                break;
                
            case "Document":
                Document d = new Document(Integer.parseInt(attributes.getValue("ID")));
                d.setLabel(attributes.getValue("label"));
                KG.addResource(d);
                break;
                
            case "Topic":
                Topic t = new Topic(Integer.parseInt(attributes.getValue("ID")));
                t.setLabel(attributes.getValue("label"));
                KG.addResource(t);
                break;
        }
        
        if(qName.contains("Viewpoint")) {
            Resource o1 = KG.getObjectByID(Integer.parseInt(attributes.getValue("o1ID")));
            Resource o2 = KG.getObjectByID(Integer.parseInt(attributes.getValue("o2ID")));
            Agent emitter = (Agent) KG.getObjectByID(Integer.parseInt(attributes.getValue("emitterID")));
            KG.addViewpoint(ViewpointFactory.newInstance(emitter, o1, o2, ViewpointPolarity.POSITIVE));
        }
    }
    
    public static void main(String[] args) {
        KnowledgeGraph KG = new KnowledgeGraph();
        try {
            XMLImportModule importModule = new XMLImportModule(new HALHandler(KG));
            importModule.importFromFile(new File("./xml/Meta-Donnes_HAL_Lirmm_07_08_14.xml"));
            XMLExportModule.exportToXML(KG, new File("./xml/KG_LIRMM_07_08_14.xml"));
        } catch (ParserConfigurationException | SAXException ex) {
            Logger.getLogger(Tests.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(ViewpointsXMLImport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
