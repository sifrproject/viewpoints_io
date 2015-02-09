package viewpointIOModule.fileIO.xmlImport.handlers;

import com.alchemyapi.api.AlchemyAPI;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import kernel.knowledgeGraph.KnowledgeGraph;
import kernel.knowledgeGraph.nodes.superModel.resources.Agent;
import kernel.knowledgeGraph.nodes.superModel.resources.ArtificialAgent;
import kernel.knowledgeGraph.nodes.superModel.resources.HumanAgent;
import kernel.knowledgeGraph.nodes.superModel.viewpoints.ConnectedViewpoint;
import kernel.knowledgeGraph.nodes.superModel.viewpoints.ViewpointFactory;
import kernel.knowledgeGraph.nodes.superModel.viewpoints.ViewpointPolarity;
import kernel.knowledgeGraph.nodes.webSpecificModel.resources.Document;
import kernel.knowledgeGraph.nodes.webSpecificModel.resources.Topic;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import viewpointIOModule.fileIO.xmlImport.ViewpointsXMLHandler;

/**
 *
 * @author WillhelmK
 */
public class HALHandler extends ViewpointsXMLHandler {

    private final AlchemyAPI alchemyAPI;
    private ArrayList<Agent> tmpAuthors;
    private ArrayList<Topic> tmpAnnotations;
    private URL tmpURL;
    private String tmpTitle;
    private String tmpForename;
    private String tmpSurname;
    private String tmpJournal;
    private org.w3c.dom.Document alchemyResponse;
    private final AlchemyResultParser alchemyParser;
    private ArtificialAgent alchemyAgent;
    private int i;
    
    public HALHandler(KnowledgeGraph KG) {
        super(KG);
        
        alchemyAPI = AlchemyAPI.GetInstanceFromString("093cf54dbd973d85666e8b401838beb3294547af");
        alchemyParser = new AlchemyResultParser();
        
        tmpAnnotations = new ArrayList<>();
        tmpAuthors = new ArrayList<>();
        
        alchemyAgent = (ArtificialAgent) KG.getNamedObject("Alchemy API");
        if(alchemyAgent == null)
            alchemyAgent = new ArtificialAgent("Alchemy API");
    }

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        switch(qName) {
            case "document":
                try {
                    tmpURL = new URL(attributes.getValue("url"));
                } catch (MalformedURLException ex) {
                    Logger.getLogger(HALHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch(qName) {
            case "forename":
                tmpForename = tmp;
                break;
                
            case "surname":
                tmpSurname = tmp;
                break;
            
            case "author":
                tmpAuthors.add(new HumanAgent(tmpForename + " " + tmpSurname));
                break;
                
            case "title":
                tmpTitle = tmp;
                break;
                
            case "journal":
                tmpJournal = tmp;
                break;
                
            case "keyword":
            case "abstract":
                if(tmp != null) {
                    try {
                        alchemyResponse = alchemyAPI.TextGetRankedConcepts(tmp);
                        tmpAnnotations.addAll(alchemyParser.getExtractedTopics(alchemyResponse, 0.0f));
                    } catch (IOException | ParserConfigurationException | XPathExpressionException ex) {
                        //Logger.getLogger(HALHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
            
            case "document":
                Document d = (Document) KG.getNamedObject(tmpTitle);
                
                if(d == null) {
                    System.out.println("\n-------------------- Document " + i++ + " / 5179 --------------------");
                    d = new Document(tmpTitle);
                    d.setUrl(tmpURL);
                    KG.addResource(d);
                    
                    Document journal = null;
                    if(tmpJournal != null) {
                        journal = (Document) KG.getNamedObject(tmpJournal);
                        if(journal == null) {
                            journal = new Document(tmpJournal);
                            KG.addResource(new Document(tmpJournal));
                        }
                    }
                    
                    for(Agent author : tmpAuthors) {
                        Agent a = (Agent) KG.getNamedObject(author.getLabel());
                        if(a == null) {
                            a = author;
                            KG.addResource(a);
                        }

                        for(Topic annotation : tmpAnnotations) {
                            Topic t = (Topic) KG.getNamedObject(annotation.getLabel());
                            if(t == null) {
                                t = annotation;
                                KG.addResource(t);
                            }
                            
                            ConnectedViewpoint v1 = ViewpointFactory.newInstance(alchemyAgent, a, t, ViewpointPolarity.POSITIVE);
                            KG.addViewpoint(v1);
                            System.out.println(v1);
                            ConnectedViewpoint v2 = ViewpointFactory.newInstance(alchemyAgent, d, t, ViewpointPolarity.POSITIVE);
                            KG.addViewpoint(v2);
                            System.out.println(v2);
                        }

                        ConnectedViewpoint v3 = ViewpointFactory.newInstance(a, a, d, ViewpointPolarity.POSITIVE);
                        KG.addViewpoint(v3);
                        System.out.println(v3);
                        
                        if(journal != null) {
                            ConnectedViewpoint v4 = ViewpointFactory.newInstance(a, d, journal, ViewpointPolarity.POSITIVE);
                            KG.addViewpoint(v4);
                            System.out.println(v4);
                        }
                    }
                }
                
                tmpAnnotations.clear();
                tmpAuthors.clear();
                
                break;
        }
    }

    @Override
    public void endDocument() throws SAXException {
    }
    
}
