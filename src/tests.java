
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import java.io.File;
import javax.swing.JFrame;
import javax.xml.parsers.ParserConfigurationException;
import kernel.knowledgeGraph.KnowledgeGraph;
import kernel.knowledgeGraph.Projection;
import kernel.knowledgeGraph.nodes.superModel.resources.HumanAgent;
import kernel.tools.viz.GraphView;
import org.xml.sax.SAXException;
import viewpointIOModule.fileIO.xmlImport.ViewpointsXMLHandler;
import viewpointIOModule.fileIO.xmlImport.XMLImportModule;

/**
 *
 * @author WillhelmK
 */
public class tests {

    public static void main(String[] args) throws ParserConfigurationException, SAXException {
        /*KnowledgeGraph.USES_NEO4J = false;
        KnowledgeGraph KG = new KnowledgeGraph();
        XMLImportModule importModule = new XMLImportModule(new HALHandler(KG));
        importModule.importFromFile(new File("./xml/Meta-Donnes_HAL_Lirmm_17_09_14.xml"));
        
        XMLExportModule.exportToXML(KG, new File("./xml/KG_LIRMM_v4.xml"));*/
        
        /*GraphDatabaseService dbService = new GraphDatabaseFactory().newEmbeddedDatabase("./db/tests");
        KnowledgeGraph KG = new KnowledgeGraph(dbService);
        N4jImportModule.importFromN4j(dbService, KG);*/
        
        KnowledgeGraph KG = new KnowledgeGraph();
        KnowledgeGraph.USES_NEO4J = false;
        
        XMLImportModule importModule = new XMLImportModule(new ViewpointsXMLHandler(KG));
        importModule.importFromFile(new File("./xml/testClustering.xml"));
        
        System.out.println(KG.getO());
        System.out.println(KG.getViewpoints());
        
        Projection proj = new Projection(KG, 0.0f, Float.MAX_VALUE, HumanAgent.class);
        JFrame frame = new JFrame("viz");
        frame.setSize(800, 600);
        frame.validate();
        frame.setVisible(true);
        
        KKLayout layout = new KKLayout(proj);
        layout.setSize(frame.getSize());
        
        GraphView view = new GraphView(layout, true, true);
        
        //frame.getContentPane().add(view);
        frame.setVisible(true);
        
        //KG.shutdown();
    }
    
}
