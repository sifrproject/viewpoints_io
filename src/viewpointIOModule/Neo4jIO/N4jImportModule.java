package viewpointIOModule.Neo4jIO;

import java.util.ArrayList;
import kernel.knowledgeGraph.KnowledgeGraph;
import kernel.knowledgeGraph.nodes.superModel.resources.Agent;
import kernel.knowledgeGraph.nodes.superModel.resources.ArtificialAgent;
import kernel.knowledgeGraph.nodes.superModel.resources.HumanAgent;
import kernel.knowledgeGraph.nodes.superModel.resources.Resource;
import kernel.knowledgeGraph.nodes.superModel.viewpoints.ConnectedViewpoint;
import kernel.knowledgeGraph.nodes.superModel.viewpoints.ViewpointFactory;
import kernel.knowledgeGraph.nodes.superModel.viewpoints.ViewpointPolarity;
import kernel.knowledgeGraph.nodes.webSpecificModel.resources.Document;
import kernel.knowledgeGraph.nodes.webSpecificModel.resources.Topic;
import kernel.tools.N4jEdgeType;
import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.impl.util.StringLogger;

/**
 *
 * @author WillhelmK
 */
public abstract class N4jImportModule {

    /**
     * 
     * @param dbService
     * @param KG 
     */
    public static void importFromN4j(GraphDatabaseService dbService, KnowledgeGraph KG) {
        ExecutionResult result;
        ExecutionEngine engine = new ExecutionEngine(dbService, StringLogger.SYSTEM);
        
        try(Transaction importDb = dbService.beginTx()) {
            result = engine.execute("start n=node(*) return n");
            scala.collection.immutable.Vector<Object> results = result.columnAs("n").toVector();
            
            for(int i = 0; i < results.length(); i++) {
                org.neo4j.graphdb.Node node = (org.neo4j.graphdb.Node) results.getElem(i, 0);
                
                switch((String) node.getProperty("type")) {
                    case "HumanAgent":
                        Agent a = new HumanAgent((String) node.getProperty("name"));
                        a.setId((int) node.getProperty("id"));
                        KG.addResource(a);
                        break;
                        
                    case "ArtificialAgent":
                        Agent aa = new ArtificialAgent((String) node.getProperty("name"));
                        aa.setId((int) node.getProperty("id"));
                        KG.addResource(aa);
                        break;
                        
                    case "Topic":
                        Topic t = new Topic((String) node.getProperty("name"));
                        t.setId((int) node.getProperty("id"));
                        KG.addResource(t);
                        break;
                        
                    case "Document":
                        Document d = new Document((String) node.getProperty("name"));
                        d.setId((int) node.getProperty("id"));
                        KG.addResource(d);
                        break;
                }
                
                if(((String) node.getProperty("type")).contains("Viewpoint")) {
                    ConnectedViewpoint v;
                    Agent emitter = null;
                    ArrayList<Resource> o = new ArrayList<>();

                    Iterable<Relationship> expressesRels =  node.getRelationships(N4jEdgeType.EXPRESSES_VIEWPOINT);
                    for(Relationship expressesRel : expressesRels)
                        emitter = (Agent) KG.getNamedObject((String) expressesRel.getOtherNode(node).getProperty("name"));

                    Iterable<Relationship> connectorRels =  node.getRelationships(N4jEdgeType.VIEWPOINT_CONNECTOR);
                    for(Relationship connectorRel : connectorRels)
                        o.add(KG.getNamedObject((String) connectorRel.getOtherNode(node).getProperty("name")));

                    v = ViewpointFactory.newInstance(emitter, o.get(0), o.get(1), (String) node.getProperty("type"), ViewpointPolarity.POSITIVE);
                    emitter.getPerspective().addViewpoint(v);
                    v.setCorrespondingNode(node);
                    KG.addViewpoint(v);
                    break;
                }
                
            }
            
            importDb.success();
        }
    }
    
}
