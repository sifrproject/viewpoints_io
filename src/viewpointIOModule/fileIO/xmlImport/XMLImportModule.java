package viewpointIOModule.fileIO.xmlImport;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import kernel.knowledgeGraph.KnowledgeGraph;
import org.xml.sax.SAXException;
import viewpointIOModule.fileIO.FileImportModule;

/**
 *
 * @author WillhelmK
 */
public class XMLImportModule implements FileImportModule {

    protected SAXParser parser;
    protected ViewpointsXMLHandler handler;

    /**
     * 
     * @param handler
     * @throws ParserConfigurationException
     * @throws SAXException 
     */
    public XMLImportModule(ViewpointsXMLHandler handler) throws ParserConfigurationException, SAXException {
        parser = SAXParserFactory.newInstance().newSAXParser();
        this.handler = handler;
    }
    
    /**
     * 
     * @param file
     * @return 
     */
    @Override
    public KnowledgeGraph importFromFile(File file) {
        try {
            parser.parse(file, handler);
            return handler.getKG();
        } catch (SAXException | IOException ex) {
            Logger.getLogger(XMLImportModule.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    public void setHandler(ViewpointsXMLHandler handler) {
        this.handler = handler;
    }

    public ViewpointsXMLHandler getHandler() {
        return handler;
    }

}
