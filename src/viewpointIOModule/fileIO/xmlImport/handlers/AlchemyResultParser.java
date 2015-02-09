package viewpointIOModule.fileIO.xmlImport.handlers;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import kernel.knowledgeGraph.nodes.webSpecificModel.resources.Topic;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author WillhelmK
 */
public class AlchemyResultParser {
    
    /**
     * 
     * @param doc
     * @param threshold
     * @return 
     */
    public ArrayList<Topic> getExtractedTopics(org.w3c.dom.Document doc, float threshold) {
        ArrayList<Topic> extractedTopics = new ArrayList<>();
        NodeList concepts = doc.getDocumentElement().getElementsByTagName("concepts").item(0).getChildNodes();
        
        for(int i = 0; i < concepts.getLength(); i++) {
            org.w3c.dom.Node concept = concepts.item(i);
            
            if(concept instanceof Element) {
                Topic t = new Topic();
                
                NodeList conceptsAttributes = concept.getChildNodes();
                for(int j = 0; j < conceptsAttributes.getLength(); j++) {
                    
                    org.w3c.dom.Node conceptAttribute = conceptsAttributes.item(j);
                    if(conceptAttribute instanceof Element) {
                        String content = conceptAttribute.getTextContent();
                        
                        switch(conceptAttribute.getNodeName()) {
                            case "text":
                                t.setLabel(content.toLowerCase());
                                break;
                                
                            case "relevance":
                                if(Float.parseFloat(content) >= threshold)
                                    extractedTopics.add(t);
                                break;
                                
                            case "dbpedia":
                            case "freebase":
                            case "yago":
                            case "opencyc":
                                try {
                                    t.addURI(conceptAttribute.getNodeName(), new URI(content));
                                } catch (URISyntaxException ex) {
                                    Logger.getLogger(AlchemyResultParser.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                break;
                        }
                    }
                    
                }
            }
        }
        
        return extractedTopics;
    }
    
}
