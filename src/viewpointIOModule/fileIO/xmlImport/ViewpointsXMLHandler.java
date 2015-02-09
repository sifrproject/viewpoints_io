package viewpointIOModule.fileIO.xmlImport;

import java.util.logging.Level;
import java.util.logging.Logger;
import kernel.knowledgeGraph.KnowledgeGraph;
import kernel.knowledgeGraph.nodes.superModel.resources.Agent;
import kernel.knowledgeGraph.nodes.superModel.resources.Resource;
import kernel.knowledgeGraph.nodes.superModel.viewpoints.ConnectedViewpoint;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author WillhelmK
 */
public class ViewpointsXMLHandler extends DefaultHandler {

    protected KnowledgeGraph KG;
    protected String tmp;
    private boolean inKO;

    public ViewpointsXMLHandler(KnowledgeGraph KG) {
        this.KG = KG;
        inKO = false;
    }

    public KnowledgeGraph getKG() {
        return KG;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        try {
            // last version

            //if(! (qName.equals("KnowledgeGraph") && qName.equals("KnowledgeObjects") && qName.equals("Viewpoints")) ) {
            
                Class<?> type;

                if(qName.contains("resources")) {
                    type = Class.forName(qName);
                    Resource o = (Resource) type.newInstance();
                    o.setLabel(attributes.getValue("label"));
                    o.setId(Integer.parseInt(attributes.getValue("ID")));
                    KG.addResource(o);
                } else if(qName.contains("viewpoints")) {
                    type = Class.forName(qName);
                    ConnectedViewpoint v = (ConnectedViewpoint) type.newInstance();
                    v.setEmitter((Agent) KG.getObjectByID(Integer.parseInt(attributes.getValue("emitterID"))));
                    v.setO1((Resource) KG.getObjectByID(Integer.parseInt(attributes.getValue("o1ID"))));
                    v.setO2((Resource) KG.getObjectByID(Integer.parseInt(attributes.getValue("o2ID"))));
                    KG.addViewpoint(v);
                }
            //}

            // old fashion
        /*
            Resource o = null;

            if(! qName.contains("Viewpoint"))
                o = KG.getNamedObject( attributes.getValue("name") );

            if(o == null) {

                switch(qName) {
                    case "Agent":
                        o = new HumanAgent( attributes.getValue("name") );
                        KG.addResource(o);
                        System.out.println("+" + o);
                        break;

                    case "Document":
                        o = new Document( attributes.getValue("name") );
                        KG.addResource(o);
                        System.out.println("+" + o);
                        break;

                    case "Topic":
                        o = new Topic( attributes.getValue("name") );
                        KG.addResource(o);
                        System.out.println("+" + o);
                        break;

                    case "Viewpoint":
                        Resource o1 = KG.getNamedObject( attributes.getValue("o1") );
                        Resource o2 = KG.getNamedObject( attributes.getValue("o1") );
                        HumanAgent emitter = (HumanAgent) KG.getNamedObject( attributes.getValue("emitter") );
                        ConnectedViewpoint v = ViewpointFactory.newInstance(emitter, o1, o2, ViewpointPolarity.POSITIVE);
                        KG.addViewpoint(v);
                        System.out.println("+" + v);
                        break;
                }

            }*/
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(ViewpointsXMLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if(length > 1)
            tmp = new String(ch, start, length);
        else
            tmp = null;
    }
    
}
