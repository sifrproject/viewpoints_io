package viewpointIOModule.fileIO;

import java.io.File;
import kernel.knowledgeGraph.KnowledgeGraph;

/**
 *
 * @author WillhelmK
 */
public interface FileImportModule {
    
    /**
     * 
     * @param file
     * @return 
     * @throws java.lang.Exception 
     */
    public KnowledgeGraph importFromFile(File file) throws Exception;
    
}
